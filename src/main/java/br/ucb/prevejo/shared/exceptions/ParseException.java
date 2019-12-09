package br.ucb.prevejo.shared.exceptions;

public class ParseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ParseException() {
    }

    public ParseException(String e) {
        super(e);
    }

    public ParseException(Throwable e) {
        super(e);
    }
}
