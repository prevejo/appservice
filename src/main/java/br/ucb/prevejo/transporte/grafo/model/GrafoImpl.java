package br.ucb.prevejo.transporte.grafo.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GrafoImpl<T> implements Grafo<T> {

    private Collection<Vertice<T>> vertices;

    public GrafoImpl() {
        this(Collections.emptyList());
    }

    public GrafoImpl(Collection<Vertice<T>> vertices) {
        this.vertices = new ArrayList<>(vertices);
    }

    public void addVertice(Vertice<T> vertice) {
        this.vertices.add(vertice);
    }

    @Override
    public Collection<Vertice<T>> vertices() {
        return vertices;
    }

    @Override
    public Collection<List<T>> searchPaths(Vertice<T> origem, Vertice<T> destino) {
        Collection<List<T>> caminhos = new ArrayList<>();

        dfs(origem, destino, caminhos, Collections.emptyList(), new ArrayList<>(), 3);

        return caminhos;
    }

    private void dfs(Vertice<T> origem, Vertice<T> destino, Collection<List<T>> caminhos, List<T> atualCaminho, Collection<Vertice> jaVisitados, int limite) {
        jaVisitados.add(origem);

        for (Aresta<T> aresta : origem.arestas()) {
            if (!jaVisitados.contains(aresta.destino())) {

                List<T> lista = new ArrayList<>(atualCaminho);
                lista.add(aresta.data());

                if (aresta.destino().equals(destino)) {
                    caminhos.add(lista);
                } else if (jaVisitados.size() < limite) {
                    dfs(aresta.destino(), destino, caminhos, lista, new ArrayList<>(jaVisitados), limite);
                }
            }
        }
    }
}
