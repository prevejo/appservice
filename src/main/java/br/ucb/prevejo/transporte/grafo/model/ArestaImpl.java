package br.ucb.prevejo.transporte.grafo.model;

public class ArestaImpl<T> implements Aresta<T> {

    private Vertice destino;
    private T data;

    public ArestaImpl(Vertice destino, T data) {
        this.destino = destino;
        this.data = data;
    }

    @Override
    public Vertice destino() {
        return destino;
    }

    @Override
    public T data() {
        return data;
    }

}
