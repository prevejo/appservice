package br.ucb.prevejo.shared.util;

import java.text.Normalizer;
import java.util.Base64;

public class StringUtil {
	
	/**
	 * Tenta realizar o parse de uma string em double.
	 * @param str String.
	 * @return Double; null caso a string não possua
	 */
	public static Double tryDoubleParse(String str) {
		Double d = null;
		
		if (str != null && !str.isEmpty()) {
			try {
				d = Double.parseDouble(str);
			} catch(NumberFormatException e) {}
		}
		
		return d;
	}
	
	/**
	 * Gera uma string contendo dígitos aletórios.
	 * @param maxDigitos Quantidade de dígitos.
	 * @return String.
	 */
	public static String gerarRandomStr(int maxDigitos) {
		String resultado = "";
		
		int seed;
		
		for (int i = 0; i < 6; i++) {
			seed = (int) (Math.random() * 123);
			while (!((seed >= 48 && seed <= 57) || (seed >= 97 && seed <= 122))) {
				seed = (int) (Math.random() * 100);
			}
			resultado += (char) seed;
		}
		
		return resultado;
	}
	
	/**
	 * Obtem os dígitos dentro de uma string.
	 * @param str String.
	 * @return String contendo somente os dígitos numéricos; null caso a string seja nula.
	 */
	public static String getDigits(String str) {
		if (str != null) {
			char[] digitos = new char[str.length()];
			int len = 0;
			
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				
				if (Character.isDigit(c)) {
					digitos[len++] = c;
				}
			}
			
			return new String(digitos, 0, len);
		}
		
		return null;
	}
	
	/**
	 * Remove a acentuação de um campo.
	 * @param campo Campo.
	 * @return Campo sem acentuação.
	 */
	public static String removeAcentuacao(String campo) {
		return Normalizer.normalize(campo, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").trim();
	}
	
	/**
	 * Transforma um array de bytes em uma Data URI.
	 * @param bytes Bytes.
	 * @param mimeType Mime type dos dados.
	 * @return String contendo a data uri.
	 */
	public static String toDataURI(byte[] bytes, String mimeType) {
		return "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(bytes); 
	}
	
	/**
	 * Verifica se dois nomes de humanos correspondem a ao outro.
	 * @param nome1 Nome 1.
	 * @param nome2 Nome 2.
	 * @return Nomes iguais.
	 */
	public static boolean equalsNomes(String nome1, String nome2) {
		nome1 = removeAcentuacao(nome1).replace(" ", "").toUpperCase();
		nome2 = removeAcentuacao(nome2).replace(" ", "").toUpperCase();
		
		return nome1.equals(nome2);
	}
	
	/**
	 * Verifica se uma string é igual a pelo menos uma em um array.
	 * @param str String.
	 * @param array Array de strings.
	 * @return True, array contém a dada string; false, caso contrário.
	 */
	public static boolean isIn(String str, String[] array) {
		boolean in = false;
		
		for (String string : array) {
			if (str.equals(string)) {
				in = true;
				break;
			}
		}
		
		return in;
	}
	
	/**
	 * Obtem o objeto cujo representação em string confere com uma dada string.
	 * @param str String.
	 * @param objects Conjunto de objetos.
	 * @return
	 */
	public static Object valueOf(String str, Object[] objects) {
		Object obj = null;
		
		if (str != null && !str.isEmpty()) {
			str = removeAcentuacao(str.toUpperCase().trim().replace(" ", "_"));
			
			for (Object o : objects) {
				if (o.toString().contains(str)) {
					obj = o;
					break;
				}
			}
		}
		
		return obj;
	}

	/**
	 * Diz se uma string é nula ou vazia.
	 * @param str String.
	 * @return true, se a string é nula ou vazia.
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}

	public static boolean isEmpty(Object obj) {
		return isEmpty(obj.toString());
	}

}
