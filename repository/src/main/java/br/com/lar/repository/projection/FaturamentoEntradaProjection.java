package br.com.lar.repository.projection;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class FaturamentoEntradaProjection {

	private Long idFaturamentoEntradasCabecalho;

	private String cliente;

	private String veiculo;

	private Date dataMovimento;

	private BigDecimal valorBruto;
}