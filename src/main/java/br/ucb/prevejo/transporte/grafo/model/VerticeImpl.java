package br.ucb.prevejo.transporte.grafo.model;

import java.util.ArrayList;
import java.util.Collection;

public class VerticeImpl<T> implements Vertice<T> {

    private Collection<Aresta<T>> arestas;

    public VerticeImpl() {
        this(new ArrayList<>());
    }

    public VerticeImpl(Collection<Aresta<T>> arestas) {
        this.arestas = arestas;
    }

    @Override
    public Aresta<T> apontar(Vertice<T> vertice, T value) {
        Aresta<T> aresta = new ArestaImpl<T>(vertice, value);
        this.arestas.add(aresta);
        return aresta;
    }

    @Override
    public Collection<Aresta<T>> arestas() {
        return arestas;
    }

}
