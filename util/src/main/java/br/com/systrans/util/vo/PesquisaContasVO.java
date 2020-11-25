package br.com.systrans.util.vo;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class PesquisaContasVO {

	private Long codigoConta;

	private Long codigoCliente;

	private String codigoDocumento;

	private Long codigoFormaPagamento;

	private Date dataVencimentoInicial;

	private Date dataVencimentoFinal;

	private BigDecimal valorParcelaInicial;

	private BigDecimal valorParcelaFinal;

	private boolean documentoBaixado;

	private boolean documentoVencido;

}