package br.ucb.prevejo.transporte.grafo.model;

import java.util.Collection;

public interface Vertice<T> {

    public Aresta<T> apontar(Vertice<T> vertice, T value);
    public Collection<Aresta<T>> arestas();

}
