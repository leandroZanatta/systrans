package br.com.systrans.util.enumeradores;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public enum TipoHistoricoOperacaoEnum {

	CREDOR(1L, "Credor"),

	DEVEDOR(2L, "Devedor");

	private final Long codigo;

	private final String descricao;

	private static Map<Long, TipoHistoricoOperacaoEnum> mapa = new HashMap<>();

	static {

		for (TipoHistoricoOperacaoEnum tipoVeiculo : TipoHistoricoOperacaoEnum.values()) {
			mapa.put(tipoVeiculo.getCodigo(), tipoVeiculo);
		}
	}

	private TipoHistoricoOperacaoEnum(Long codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	@Override
	public String toString() {

		return this.descricao;
	}

	public static TipoHistoricoOperacaoEnum forValue(Long codigo) {

		return mapa.get(codigo);
	}
}
