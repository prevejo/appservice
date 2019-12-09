package br.ucb.prevejo.transporte.grafo;

import br.ucb.prevejo.transporte.areaentity.AreaEntity;
import br.ucb.prevejo.transporte.areaintegracao.AreaIntegracao;
import br.ucb.prevejo.transporte.parada.Parada;
import br.ucb.prevejo.transporte.grafo.dto.AreaLigacao;
import br.ucb.prevejo.transporte.grafo.dto.AreaVertice;
import br.ucb.prevejo.transporte.grafo.dto.ArestaLigacao;
import br.ucb.prevejo.transporte.grafo.model.Grafo;

import java.util.Collection;

public interface GrafoTransporte extends Grafo<ArestaLigacao> {

    public Collection<AreaLigacao> ligacoes();
    public AreaVertice addArea(Collection<Parada> paradas);
    public AreaVertice addArea(Parada parada);
    public AreaVertice addArea(AreaIntegracao areaIntegracao);
    public AreaVertice addArea(AreaEntity area);
    public GrafoTransporte instance();

}
