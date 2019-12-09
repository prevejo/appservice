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
import br.ucb.prevejo.shared.util.DateAndTime;
import br.ucb.prevejo.transporte.parada.Parada;
import br.ucb.prevejo.transporte.parada.ParadaService;
import br.ucb.prevejo.transporte.percurso.EnumSentido;
import br.ucb.prevejo.transporte.percurso.Percurso;
import br.ucb.prevejo.transporte.percurso.PercursoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class EstimativaService implements AppShutdownListener {

    private final InstanteOperacaoService service;
    private final PercursoService percursoService;
    private final ParadaService paradaService;
    private final OperacaoExecucaoService operacaoExecucaoService;
    private final EstimativaCache cache;
    private final HttpClient httpClient;
    private final ObjectMapper mapper = buildMapper();

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

    public EstimativaPercurso estimar(int percursoId, String codParada) {
        return percursoService.obterPercursoFetchLinha(percursoId)
                .flatMap(percurso -> paradaService.obterPorCodigo(codParada)
                        .map(parada -> estimar(percurso, parada))
                ).orElse(null);
    }

    public EstimativaPercurso estimar(String numLinha, EnumSentido sentidoLinha, String codParada) {
        return percursoService.obterPercursoFetchLinha(numLinha, sentidoLinha)
                .flatMap(percurso -> paradaService.obterPorCodigo(codParada)
                        .map(parada -> estimar(percurso, parada))
                ).orElse(null);
    }

    public String estimarFromService(String numLinha, EnumSentido sentidoLinha, String codParada) {
        return percursoService.obterPercursoFetchLinha(numLinha, sentidoLinha)
                .flatMap(percurso -> paradaService.obterPorCodigo(codParada)
                        .map(parada -> estimarFromService(new EstimativaRequest(percurso, parada)))
                ).orElse(null);
    }

    public String estimarFromService(EstimativaRequest estRequest) {
        ServiceRequest request = new ServiceRequest();

        request.setNumero(estRequest.getPercurso().getLinha().getNumero());
        request.setSentido(estRequest.getPercurso().getSentido().toString());
        request.setParada(estRequest.getParadaEmbarque().getCod());
        request.setVeiculos(operacaoExecucaoService.obterHistoricoCorrente(estRequest.getPercurso().toDTO()));

        return estimar(request);
    }

    public String estimar(ServiceRequest request) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            mapper.writeValue(baos, request);
        } catch (IOException e) { throw new RuntimeException(e); }

        return httpClient.post(str -> str, apiPrevisao, new String(baos.toByteArray()));
    }

    public EstimativaPercurso estimar(Percurso percurso, Parada paradaEmbarque) {
        LocalDateTime now = LocalDateTime.now();
        try {
            return estimar(new EstimativaRequest(percurso, paradaEmbarque));
        } finally {
            System.out.println("<-> Time: " + DateAndTime.timeBetween(now, LocalDateTime.now(), ChronoUnit.MILLIS));
        }
    }

    private EstimativaPercurso estimar(EstimativaRequest request) {
        Collection<? extends  LocatedEntity> veiculos = operacaoExecucaoService.obterHistoricoCorrente(request.getPercurso().toDTO());

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

        return historicoOperacao.calcularEstimativa(request.getPercurso(), request.getParadaEmbarque(), veiculos);
    }

    @Override
    public void onShutdown() {
        cache.stopRefreshWorkers();
    }

    private static ObjectMapper buildMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addSerializer(new VeiculoInstanteSerializer()));
        mapper.registerModule(new SimpleModule().addSerializer(new GeometrySerializer()));
        mapper.registerModule(new SimpleModule().addSerializer(new TimeSerializer()));
        return mapper;
    }

}
