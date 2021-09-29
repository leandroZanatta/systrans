package br.com.lar.repository.projection;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class FaturamentoProjection {

	private Long id;

	private String cliente;

	private String veiculo;

	private String historico;

	private String observacao;

	private Date dataMovimento;

	private BigDecimal valorBruto;
}