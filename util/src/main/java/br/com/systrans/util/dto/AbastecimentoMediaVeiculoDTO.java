package br.com.systrans.util.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbastecimentoMediaVeiculoDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idAbastecimentoMediaVeiculo;
	private Long codigoVeiculo;
	private Date dataCalculo;
	private Long quantidadeKilometros;
	private BigDecimal litrosAbastecidos;
	private BigDecimal valorAbastecimento;
	private BigDecimal kilometrosPorLitro;
	private BigDecimal reaisPorKilometro;
	private Long sincronizacaoVersao;
}
