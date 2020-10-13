package br.com.lar.startup.enumeradores;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public enum TipoHistoricoEnum {

	CONTAS_PAGAR(1L, "Contas á Pagar"),

	CONTAS_RECEBER(2L, "Contas á Receber");

	private final Long codigo;

	private final String descricao;

	private static Map<Long, TipoHistoricoEnum> mapa = new HashMap<>();

	static {

		for (TipoHistoricoEnum tipoVeiculo : TipoHistoricoEnum.values()) {
			mapa.put(tipoVeiculo.getCodigo(), tipoVeiculo);
		}
	}

	private TipoHistoricoEnum(Long codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	@Override
	public String toString() {

		return this.descricao;
	}

	public static TipoHistoricoEnum forValue(Long codigo) {

		return mapa.get(codigo);
	}
}
