package br.ucb.prevejo.shared.exceptions;

public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ServiceException() {
    }

    public ServiceException(Throwable e) {
        super(e);
    }

    public ServiceException(String msg, Throwable e) {
        super(msg, e);
    }
}