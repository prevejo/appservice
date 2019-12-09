package br.ucb.prevejo.shared.intefaces;

import br.ucb.prevejo.shared.exceptions.ParseException;

public interface Parser<T, R> {

    public R parse(T str) throws ParseException;

}
