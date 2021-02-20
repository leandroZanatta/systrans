package br.com.lar.repository.projection;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FaturamentoBrutoReportProjection {

	private BigDecimal valorBruto;
	private String centroCusto;
	private String historico;
	private String veiculo;
	private Long codigoVeiculo;

}