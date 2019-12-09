package br.ucb.prevejo.comunidade.informativo.provider;

import java.util.Collection;

public interface NewsProvider {

    Collection<News> searchInLastDay(String query);

}
