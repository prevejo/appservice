package br.ucb.prevejo.transporte.grafo.model;

public interface Aresta<T> {

    public Vertice<T> destino();
    public T data();

}
