package br.com.lar.upgrade.util.classes;

import java.nio.charset.Charset;

public class StringUtil {

	public static final String STRING_VAZIA = "";
	public static final Charset CHARSET = Charset.forName("UTF-8");

	public static Boolean isNullOrEmpty(String valor) {
		return valor == null || valor.equals(STRING_VAZIA);
	}

	public static String arrayToString(char[] password) {
		StringBuilder sb = new StringBuilder();

		for (char c : password) {
			sb.append(c);
		}
		return sb.toString();
	}

	public static boolean isNumeric(String text) {
		try {
			Double.valueOf(text);
		} catch (Exception e) {
			return false;
		}

		return true;
	}
}
