package br.ucb.prevejo.previsao.instanteoperacao.parser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import br.ucb.prevejo.previsao.instanteoperacao.Instante;
import br.ucb.prevejo.previsao.instanteoperacao.InstanteOperacao;
import br.ucb.prevejo.previsao.instanteoperacao.Veiculo;
import br.ucb.prevejo.previsao.operacao.EnumOperadora;
import br.ucb.prevejo.shared.exceptions.ParseException;
import br.ucb.prevejo.shared.intefaces.Parser;
import br.ucb.prevejo.shared.model.Velocidade;
import br.ucb.prevejo.shared.util.DateAndTime;
import br.ucb.prevejo.shared.util.Geo;
import br.ucb.prevejo.shared.util.StringUtil;
import br.ucb.prevejo.transporte.percurso.EnumSentido;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

@Component("urbi")
public class UrbiParser implements OperadoraParser {

	private DateTimeFormatter DF = DateAndTime.buildFormater("dd/MM/yyyy HH:mm:ss");

	@Override
	public EnumOperadora getOperadora() {
		return EnumOperadora.URBI;
	}
	
	@Override
	public Collection<InstanteOperacao> parse(String str) throws ParseException {
		JSONParser parser = new JSONParser();
		
		try {
			JSONObject obj = (JSONObject) parser.parse(str);
			
			JSONArray array = (JSONArray) obj.get("Dados");
			
			List<InstanteOperacao> list = new ArrayList<>(array.size());
			
			for (Object o : array) {
				JSONArray arrayPosicao = (JSONArray) o;
				
				String strData = (String) arrayPosicao.get(1);
				
				if (StringUtil.isEmpty(strData)) {
					continue;
				}

				String numero = (String) arrayPosicao.get(0);
				LocalDateTime horario = LocalDateTime.parse(strData, DF);
				Point point = Geo.makePoint((String)arrayPosicao.get(2), (String)arrayPosicao.get(3));
				String linha = (String) arrayPosicao.get(5);
				EnumSentido sentido = EnumSentido.valueByNumeral(arrayPosicao.get(6).toString());
				BigDecimal direcao = !StringUtil.isEmpty(arrayPosicao.get(4))
						? new BigDecimal(arrayPosicao.get(4).toString().replace(",", ".")) : null;
				Velocidade velocidade = !StringUtil.isEmpty(arrayPosicao.get(7))
						? Velocidade.metrosPorSegundo(new BigDecimal(arrayPosicao.get(7).toString().replace(",", "."))) : null;
				String imei = !StringUtil.isEmpty(arrayPosicao.get(8)) ? (String) arrayPosicao.get(8) : null;
				
				list.add(new InstanteOperacao(
						new Veiculo(numero, getOperadora()),
						linha,
						sentido,
						new Instante(horario, point, direcao, velocidade)
				));
			}
			
			return list;
		} catch(org.json.simple.parser.ParseException e) {
			throw new ParseException(e);
		} catch (DateTimeParseException e) {
			throw new ParseException(e);
		} catch(Throwable e) {
			throw new ParseException(e);
		}
	}

}
