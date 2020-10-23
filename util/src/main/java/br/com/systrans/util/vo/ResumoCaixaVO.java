package br.com.systrans.util.vo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResumoCaixaVO {

	private BigDecimal valorMovimentoCredito;

	private BigDecimal valorPagamentosCredito;
}
