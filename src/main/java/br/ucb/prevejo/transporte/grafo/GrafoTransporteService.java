package br.ucb.prevejo.transporte.grafo;

import br.ucb.prevejo.core.cache.PassiveCacheService;
import br.ucb.prevejo.transporte.areaintegracao.AreaIntegracaoService;
import br.ucb.prevejo.transporte.percursoparada.PercursoParadaService;
import org.springframework.stereotype.Service;

@Service
public class GrafoTransporteService {

    private final AreaIntegracaoService areaService;
    private final PercursoParadaService percursoParadaService;
    private final PassiveCacheService<?, GrafoTransporte> cacheService;

    public GrafoTransporteService(AreaIntegracaoService areaService, PercursoParadaService percursoParadaService, PassiveCacheService cacheService) {
        this.areaService = areaService;
        this.percursoParadaService = percursoParadaService;
        this.cacheService = cacheService;
    }

    public GrafoTransporte instance() {
        return cacheService.getContentByClass(GrafoTransporteCacheContent.class)
                .map(grafo -> grafo.instance())
                .orElseThrow(() -> new RuntimeException("Grafo inicial não construído"));
    }

    public GrafoTransporte build() {
        GrafoTransporte grafo = new GrafoTransporteImpl(percursoParadaService.obterPercursosParadasDTO());
        areaService.obterAreasFetchParadas().forEach(grafo::addArea);
        return grafo;
    }

}
