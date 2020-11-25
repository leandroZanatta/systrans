package br.com.lar.repository.projection;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class DiarioReportProjection {

	private Date dataMovimento;
	private String descricaoHistorico;
	private String identificador;
	private String descricaoConta;
	private String tipoSaldo;
	private BigDecimal valorSaldo;

}