package br.com.systrans.util.enumeradores;

import java.util.HashMap;
import java.util.Map;

public enum TipoContaEnum {

	DESCONTOS(1L, "Descontos"),

	ACRESCIMOS(2L, "Acréscimos"),

	JUROS(3L, "Juros");

	private static Map<Long, TipoContaEnum> mapa = new HashMap<>();

	static {

		for (TipoContaEnum enumerador : TipoContaEnum.values()) {
			mapa.put(enumerador.getCodigo(), enumerador);
		}
	}

	private final Long codigo;
	private final String descricao;

	public Long getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	TipoContaEnum(Long codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	@Override
	public String toString() {

		return this.descricao;
	}

	public static TipoContaEnum findByCodigo(Long tipoCliente) {

		return mapa.get(tipoCliente);
	}
}
