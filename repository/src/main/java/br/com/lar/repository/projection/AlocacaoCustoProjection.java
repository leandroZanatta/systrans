package br.com.lar.repository.projection;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AlocacaoCustoProjection {

	private Long codigo;

	private Integer periodo;

	private BigDecimal valorParcela;

	private String placa;

	private String historico;
}
