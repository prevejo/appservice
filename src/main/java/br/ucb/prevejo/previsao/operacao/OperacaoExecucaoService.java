package br.ucb.prevejo.previsao.operacao;

import br.ucb.prevejo.core.AppShutdownListener;
import br.ucb.prevejo.core.cache.ActiveCacheProvider;
import br.ucb.prevejo.core.cache.CacheContent;
import br.ucb.prevejo.core.cache.CacheProvider;
import br.ucb.prevejo.core.cache.PassiveCacheProvider;
import br.ucb.prevejo.core.resources.WebServiceResources;
import br.ucb.prevejo.previsao.instanteoperacao.InstanteOperacao;
import br.ucb.prevejo.previsao.instanteoperacao.InstanteOperacaoService;
import br.ucb.prevejo.previsao.operacao.veiculo.VeiculoOperacao;
import br.ucb.prevejo.previsao.operacao.veiculo.VeiculosMap;
import br.ucb.prevejo.previsao.instanteoperacao.parser.OperadoraParser;
import br.ucb.prevejo.previsao.operacao.register.OperacaoRegister;
import br.ucb.prevejo.shared.intefaces.LocatedEntity;
import br.ucb.prevejo.shared.util.Geo;
import br.ucb.prevejo.transporte.percurso.Percurso;
import br.ucb.prevejo.transporte.percurso.PercursoDTO;
import br.ucb.prevejo.transporte.percurso.PercursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperacaoExecucaoService implements AppShutdownListener {
    private static final int MAX_VEICULO_DISTANCE_TO_PERCURSO = 30;
    private static final int MAX_VEICULO_PAST_MINUTES = 5;

    @Value("${operacao.cache.expire_seconds}")
    private Integer cacheSeconds;

    @Value("#{'${operacao.use_localhost}'.split(',')}")
    private List<String> useLocalHostList;

    @Value("${operacao.save_location_enable}")
    private Boolean saveLocation;

    @Value("${operacao.active_cache_provider}")
    private Boolean useActiveCacheProvider;

    @Value("${operacao.disabled}")
    private List<String> disabledServices;

    @Autowired
    private Collection<OperadoraParser> parsers;

    private CacheProvider<EnumOperadora, Operacao> cacheProvider;
    private OperacaoRegister register;
    private final VeiculosMap veiculosMap = new VeiculosMap();

    private final PercursoService percursoService;
    private final InstanteOperacaoService instanteOperacaoService;

    public OperacaoExecucaoService(PercursoService percursoService, InstanteOperacaoService instanteOperacaoService) {
        this.percursoService = percursoService;
        this.instanteOperacaoService = instanteOperacaoService;
    }

    @PostConstruct
    private void init() {
        new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            cacheProvider = instanceCacheProvider();    
        }).start();

        this.register = new OperacaoRegister(percursoService::obterPercursosDTOFromCache, this::registrarInstantes);
        this.register.setInstantesFlushConsumer(this::updateVeiculosMap);
    }

    public Collection<Operacao> obterOperacoes() {
        return cacheProvider.getContents().stream().filter(c -> c != null).collect(Collectors.toList());
    }

    public Collection<LocatedEntity> obterHistoricoCorrente(PercursoDTO percurso) {
        return veiculosMap.getHistorico(percurso).stream()
                .filter(h -> h.getInstanteOperacaoCurrent().getInstante().isBehind(Duration.of(MAX_VEICULO_PAST_MINUTES, ChronoUnit.MINUTES)))
                .map(h -> h.toLocatedEntity())
                .collect(Collectors.toList());
    }

    public Collection<VeiculoOperacao> obterVeiculosEmOperacao(Percurso percurso) {
        return veiculosMap.getHistorico(percurso.toDTO()).stream()
                .filter(h -> h.getInstanteOperacaoCurrent().getInstante().isBehind(Duration.of(MAX_VEICULO_PAST_MINUTES, ChronoUnit.MINUTES)))
                .map(h ->VeiculoOperacao.build(
                        h.getInstanteOperacaoCurrent(),
                        Geo.splitLineString(h.toLocatedEntity(), percurso.getGeo(), MAX_VEICULO_DISTANCE_TO_PERCURSO).orElse(null)
                )).collect(Collectors.toList());
    }

    public Collection<Operacao> obterOperacoesByPercurso(PercursoDTO percurso) {
        return obterOperacoes().stream()
                .map(op -> op.filter(inst -> inst.assignTo(percurso)).filterRecentOnes(Duration.of(MAX_VEICULO_PAST_MINUTES, ChronoUnit.MINUTES)))
                .filter(op -> !op.getVeiculos().isEmpty())
                .collect(Collectors.toList());
    }


    private void registrarOperacao(Operacao operacao) {
        register.register(Arrays.asList(operacao));
    }

    private void registrarInstantes(Collection<InstanteOperacao> instantes) {
        if (saveLocation) {
            instanteOperacaoService.registarInstantes(instantes);
        }
    }

    private void updateVeiculosMap(Collection<InstanteOperacao> instantes) {
        veiculosMap.updateMap(instantes);
    }

    private CacheProvider<EnumOperadora, Operacao> instanceCacheProvider() {
        CacheProvider<EnumOperadora, Operacao> provider = useActiveCacheProvider
                ? instanceActiveCacheProvider()
                : instancePassiveCacheProvider();

        instanceCacheContents().forEach(provider::addCacheContent);

        if (provider instanceof ActiveCacheProvider) {
            ((ActiveCacheProvider) provider).startRefresh();
        }

        return provider;
    }

    private Collection<CacheContent<EnumOperadora, Operacao>> instanceCacheContents() {
        return parsers.stream()
                .filter(parser -> !disabledServices.contains(parser.getOperadora().getWebServiceConfigId()))
                .map(parser -> {
                    OperacaoCacheContent cc = new OperacaoCacheContent(
                        WebServiceResources.findConfig(parser.getOperadora().getWebServiceConfigId()),
                        parser.getOperadora(),
                        parser
                    );
                    cc.setUseLocalHost(useLocalHostList.contains(cc.getId()));
                    cc.setCacheSeconds(cacheSeconds);

                    return cc;
                }).collect(Collectors.toList());
    }

    private CacheProvider<EnumOperadora, Operacao> instanceActiveCacheProvider() {
        ActiveCacheProvider<EnumOperadora, Operacao> cp = new ActiveCacheProvider<>();

        cp.setRefreshListener(contentRefreshed -> registrarOperacao(contentRefreshed));

        cp.startRefresh();

        return cp;
    }

    private CacheProvider<EnumOperadora, Operacao> instancePassiveCacheProvider() {
        PassiveCacheProvider<EnumOperadora, Operacao> cp = new PassiveCacheProvider<>();
        cp.setReturnOnException(true);

        return cp;
    }

    @Override
    public void onShutdown() {
        if (cacheProvider instanceof ActiveCacheProvider) {
            ((ActiveCacheProvider<EnumOperadora, Operacao>) cacheProvider).startRefresh();
        }
    }

}
