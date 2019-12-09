package br.ucb.prevejo.previsao.operacao;

import java.util.Collection;

import br.ucb.prevejo.core.cache.CacheContent;
import br.ucb.prevejo.core.config.WebServiceConfig;
import br.ucb.prevejo.previsao.instanteoperacao.InstanteOperacao;
import br.ucb.prevejo.previsao.instanteoperacao.parser.OperadoraParser;
import br.ucb.prevejo.previsao.operacao.EnumOperadora;
import br.ucb.prevejo.previsao.operacao.Operacao;
import br.ucb.prevejo.shared.exceptions.CacheException;
import br.ucb.prevejo.shared.exceptions.ServiceException;
import br.ucb.prevejo.shared.intefaces.Parser;
import br.ucb.prevejo.shared.util.WebServiceDataSource;

public class OperacaoCacheContent implements CacheContent<EnumOperadora, Operacao> {
	private static final long serialVersionUID = 1L;

	private volatile Operacao operacao;
	private Integer cacheSeconds;
	protected WebServiceDataSource<Collection<InstanteOperacao>> wsDataSource;
	private boolean useLocalHost;
	private WebServiceConfig config;
	private EnumOperadora operadora;
	private OperadoraParser parser;

	public OperacaoCacheContent(WebServiceConfig config, EnumOperadora operadora, OperadoraParser parser) {
		this.config = config;
		this.operadora = operadora;
		this.parser = parser;
	}

	public String getId() {
		return getConfig().getId();
	}
	
	@Override
	public EnumOperadora getKey() {
		return getOperadora();
	}

	@Override
	public int getCacheSeconds() {
		return cacheSeconds;
	}
	
	public void setCacheSeconds(Integer cacheSeconds) {
		this.cacheSeconds = cacheSeconds;
	}
	
	public void setUseLocalHost(boolean useLocalHost) {
		this.useLocalHost = useLocalHost;
	}

	@Override
	public Operacao getContent() {
		return operacao;
	}
	
	@Override
	public void refresh() throws CacheException {
		if (wsDataSource == null) {
			wsDataSource = new WebServiceDataSource<>(getParser(), useLocalHost ? "127.0.0.1" : getConfig().getHost(), getConfig().getPort(), "GET", getConfig().getPath());
			wsDataSource.setRequestHeader("Host", getConfig().getHostHeader());
			wsDataSource.setCloseConnection(getConfig().getCloseConnectionMode());
		}
		
		try {
			operacao = new Operacao(getKey(), wsDataSource.get());
		} catch(ServiceException e) {
			throw new CacheException(e);
		} catch(Throwable e) {
			throw new CacheException(e);
		}
	}

	public WebServiceConfig getConfig() {
		return config;
	}

	public EnumOperadora getOperadora() {
		return operadora;
	}

	public OperadoraParser getParser() {
		return parser;
	}
}
