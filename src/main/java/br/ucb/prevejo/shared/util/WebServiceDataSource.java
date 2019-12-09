package br.ucb.prevejo.shared.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import br.ucb.prevejo.shared.exceptions.ParseException;
import br.ucb.prevejo.shared.exceptions.ServiceException;
import br.ucb.prevejo.shared.intefaces.Parser;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.util.EntityUtils;

/**
 * Implementa um data souce sobre um recurso web.
 * @param <T> Tipo do retorno do recurso.
 */
public class WebServiceDataSource<T> {
	
	private Parser<String, T> parser;
	private Map<String, String> headers = new HashMap<>();
	private String method;
	private String path;
	
	private boolean closeConnection;
	
	private HttpRoute route;
	
	private HttpClientContext context = HttpClientContext.create();
	private HttpClientConnectionManager connMrg = new BasicHttpClientConnectionManager();

	/**
	 * Constrói o data souce.
	 * @param parser Parse que realizará a transformação do retorno da resposta HTTP para o tipo de retorno especificado.
	 * @param hostAddr Endereço do host.
	 * @param hostPort Porta do host.
	 * @param method Método a ser utilizado na requisição.
	 * @param path Path que identifica o recurso no host.
	 */
	public WebServiceDataSource(Parser<String, T> parser, String hostAddr, int hostPort, String method, String path) {
		this.parser = parser;
		this.method = method;
		this.path = path;
		this.route = new HttpRoute(new HttpHost(hostAddr, hostPort));
	}
	
	/**
	 * Configura um header HTTP a ser configurado na requisição.
	 * @param header Header.
	 * @param value Valor.
	 */
	public void setRequestHeader(String header, String value) {
		headers.put(header, value);
	}
	
	/**
	 * Realiza a requisição sobre o recurso.
	 * @return Retorno do recurso.
	 * @throws ServiceException
	 */
	public T get() throws ServiceException {
		try {
			ConnectionRequest connRequest = connMrg.requestConnection(route, null);
			
			HttpClientConnection conn = connRequest.get(10, TimeUnit.SECONDS);
			
			try {
			    if (!conn.isOpen()) {
			        connMrg.connect(conn, route, 10000, context);
			        connMrg.routeComplete(conn, route, context);
			    }
			    
			    HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
			    
			    HttpResponse response = httpexecutor.execute(getRequest(), conn, context);
			    
			    return parser.parse(EntityUtils.toString(response.getEntity()));
			} finally {
				if (closeConnection) {
				    connMrg.releaseConnection(conn, null, 1, TimeUnit.NANOSECONDS);
				    connMrg.closeExpiredConnections();
				} else {
					connMrg.releaseConnection(conn, null, 1, TimeUnit.MINUTES);
				}
			}
		} catch(IOException | HttpException e) {
			e.printStackTrace();
			throw new ServiceException("Erro ao realizar conexão", e);
		} catch(ExecutionException | InterruptedException e) {
			e.printStackTrace();
			throw new ServiceException("Erro ao obter conexão", e);
		} catch(ParseException e) {
			throw new ServiceException(e);
		} catch(Throwable e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * Obtem a requisição a ser realizada.
	 * @return Request.
	 */
	private HttpRequest getRequest() {
		BasicHttpRequest request = new BasicHttpRequest(method, path);
		
		headers.entrySet().forEach(e -> request.setHeader(e.getKey(), e.getValue()));
		
		return request;
	}
	
	/**
	 * Configura se a conexão deve ser fechada ao final de cada requisição.
	 * @param closeConnection True, a conexão será fechada; false, a conexão será reaproveitada durante as várias requisições.
	 */
	public void setCloseConnection(boolean closeConnection) {
		this.closeConnection = closeConnection;
	}
	
}
