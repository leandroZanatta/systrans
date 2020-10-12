package br.com.lar.startup.enumeradores;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public enum TipoVeiculoEnum {

	CAMINHAO(1L, "Caminhão");

	private final Long codigoVeiculo;

	private final String descricao;

	private static Map<Long, TipoVeiculoEnum> mapa = new HashMap<>();

	static {

		for (TipoVeiculoEnum tipoVeiculo : TipoVeiculoEnum.values()) {
			mapa.put(tipoVeiculo.getCodigoVeiculo(), tipoVeiculo);
		}
	}

	private TipoVeiculoEnum(Long codigoVeiculo, String descricao) {
		this.codigoVeiculo = codigoVeiculo;
		this.descricao = descricao;
	}

	@Override
	public String toString() {

		return this.descricao;
	}

	public static TipoVeiculoEnum forValue(Long codigoVeiculo) {

		return mapa.get(codigoVeiculo);
	}
}
