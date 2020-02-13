package br.ucb.prevejo.previsao.estimativa;

import br.ucb.prevejo.core.AppShutdownListener;
import br.ucb.prevejo.core.GeometrySerializer;
import br.ucb.prevejo.core.TimeSerializer;
import br.ucb.prevejo.previsao.estimativa.model.EstimativaRequest;
import br.ucb.prevejo.previsao.estimativa.model.HistoricoOperacao;
import br.ucb.prevejo.previsao.estimativa.model.ServiceRequest;
import br.ucb.prevejo.previsao.instanteoperacao.InstanteOperacao;
import br.ucb.prevejo.previsao.instanteoperacao.InstanteOperacaoService;
import br.ucb.prevejo.previsao.operacao.OperacaoExecucaoService;
import br.ucb.prevejo.previsao.operacao.veiculo.VeiculoInstanteSerializer;
import br.ucb.prevejo.shared.intefaces.HttpClient;
import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import br.ucb.prevejo.shared.serializer.PercursoServiceRequestSerializer;
import br.ucb.prevejo.transporte.parada.Parada;
import br.ucb.prevejo.transporte.parada.ParadaService;
import br.ucb.prevejo.transporte.percurso.EnumSentido;
import br.ucb.prevejo.transporte.percurso.Percurso;
import br.ucb.prevejo.transporte.percurso.PercursoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class EstimativaService implements AppShutdownListener {

    private final InstanteOperacaoService service;
    private final PercursoService percursoService;
    private final ParadaService paradaService;
    private final OperacaoExecucaoService operacaoExecucaoService;
    private final EstimativaCache cache;
    private final HttpClient httpClient;
    private final ObjectMapper serviceRequestMapper = buildServiceRequestMapper();
    private final ObjectMapper responseMapper = buildResponseMapper();

    @Value("${api.lambda.enabled}")
    private Boolean useApiPrevisao;
    @Value("${api.lambda.previsao}")
    private String apiPrevisao;

    public EstimativaService(InstanteOperacaoService service, PercursoService percursoService, ParadaService paradaService,
                             OperacaoExecucaoService operacaoExecucaoService, HttpClient httpClient) {
        this.service = service;
        this.percursoService = percursoService;
        this.paradaService = paradaService;
        this.operacaoExecucaoService = operacaoExecucaoService;
        this.cache = new EstimativaCache();
        this.httpClient = httpClient;
    }

    public String estimar(int percursoId, String codParada) {
        return percursoService.obterPercursoFetchLinha(percursoId)
                .flatMap(percurso -> paradaService.obterPorCodigo(codParada)
                        .map(parada -> estimar(percurso, parada))
                ).orElse(null);
    }

    public String estimar(String numLinha, EnumSentido sentidoLinha, String codParada) {
        return percursoService.obterPercursoFetchLinha(numLinha, sentidoLinha)
                .flatMap(percurso -> paradaService.obterPorCodigo(codParada)
                        .map(parada -> estimar(percurso, parada))
                ).orElse(null);
    }

    public String estimar(Percurso percurso, Parada paradaEmbarque) {
        return estimar(new EstimativaRequest(percurso, paradaEmbarque));
    }

    private String estimar(EstimativaRequest request) {
        return Boolean.TRUE.equals(useApiPrevisao) ? estimarOnService(request) : estimarOnPremise(request);
    }

    private String estimarOnService(EstimativaRequest estimativaRequest) {
        ServiceRequest request = new ServiceRequest();

        request.setPercurso(estimativaRequest.getPercurso());
        request.setParada(estimativaRequest.getParadaEmbarque());
        request.setVeiculos(operacaoExecucaoService.obterHistoricoCorrente(estimativaRequest.getPercurso().toDTO()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            serviceRequestMapper.writeValue(baos, request);
        } catch (IOException e) { throw new RuntimeException(e); }

        return httpClient.post(str -> str, apiPrevisao, new String(baos.toByteArray()));
    }

    private String estimarOnPremise(EstimativaRequest request) {
        Collection<? extends  LocatedEntity> veiculos = operacaoExecucaoService.obterHistoricoCorrente(request.getPercurso().toDTO())
                /*.stream().filter(v -> ((VeiculoInstante)v).getInstante().getVeiculo().getNumero().equals("440167"))
                .collect(Collectors.toList())*/;

        /*veiculos = Arrays.asList(new LocatedEntity() {
            @Override
            public Point getLocation() {
                return Geo.makePointXY(-48.03572, -15.83966);
            }
            @Override
            public List<Point> getRecordPath() {
                return Arrays.asList(
                    Geo.makePointXY(-48.03951, -15.83668),
                    Geo.makePointXY(-48.03811, -15.83774),
                    Geo.makePointXY(-48.03685, -15.83864),
                    Geo.makePointXY(-48.03572, -15.83966)
                );
            }
        });*/


        HistoricoOperacao historicoOperacao = cache.getHistorico(request).orElseGet(() -> {
            Collection<InstanteOperacao> operacoes = service.obterByPercurso(request.getPercurso().toDTO());
            HistoricoOperacao hp = HistoricoOperacao.build(operacoes);
            cache.add(request, hp);
            return hp;
        });

        EstimativaPercurso estimativaPercurso = historicoOperacao.calcularEstimativa(
                request.getPercurso(),
                request.getParadaEmbarque(),
                veiculos
        );

        try {
            return responseMapper.writeValueAsString(estimativaPercurso);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onShutdown() {
        cache.stopRefreshWorkers();
    }

    private static ObjectMapper buildServiceRequestMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addSerializer(new VeiculoInstanteSerializer()));
        mapper.registerModule(new SimpleModule().addSerializer(new GeometrySerializer()));
        mapper.registerModule(new SimpleModule().addSerializer(new TimeSerializer()));
        mapper.registerModule(new SimpleModule().addSerializer(new PercursoServiceRequestSerializer()));
        return mapper;
    }

    private static ObjectMapper buildResponseMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addSerializer(new GeometrySerializer()));
        mapper.registerModule(new SimpleModule().addSerializer(new TimeSerializer()));
        return mapper;
    }

}
