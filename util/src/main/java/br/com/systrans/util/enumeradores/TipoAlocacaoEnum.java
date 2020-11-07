package br.com.systrans.util.enumeradores;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public enum TipoAlocacaoEnum {

	FIXO("F", "Fixa"),

	VARIAVEL("V", "Variável");

	private final String codigo;

	private final String descricao;

	private static Map<String, TipoAlocacaoEnum> mapa = new HashMap<>();

	static {

		for (TipoAlocacaoEnum tipoVeiculo : TipoAlocacaoEnum.values()) {
			mapa.put(tipoVeiculo.getCodigo(), tipoVeiculo);
		}
	}

	private TipoAlocacaoEnum(String codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	@Override
	public String toString() {

		return this.descricao;
	}

	public static TipoAlocacaoEnum forValue(String codigo) {

		return mapa.get(codigo);
	}
}
