package br.ucb.prevejo.shared.exceptions;

public class HttpRequestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public HttpRequestException() {
    }

    public HttpRequestException(Throwable e) {
        super(e);
    }
}