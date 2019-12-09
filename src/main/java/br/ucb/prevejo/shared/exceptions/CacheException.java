package br.ucb.prevejo.shared.exceptions;

public class CacheException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public CacheException(Throwable e) {
		super(e);
	}

	public CacheException(String msg) {
		super(msg);
	}

}
