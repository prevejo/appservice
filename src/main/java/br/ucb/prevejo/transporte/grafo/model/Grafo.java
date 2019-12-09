package br.ucb.prevejo.transporte.grafo.model;

import java.util.Collection;
import java.util.List;

public interface Grafo<T> {

    public Collection<Vertice<T>> vertices();
    public void addVertice(Vertice<T> vertice);
    public Collection<List<T>> searchPaths(Vertice<T> origem, Vertice<T> destino);

}
