package br.com.lar.service.contasreceber;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import br.com.lar.repository.model.ContasPagar;
import br.com.lar.repository.model.ContasReceber;
import br.com.lar.repository.model.Faturamento;
import br.com.lar.repository.model.FaturamentoEntrada;
import br.com.lar.repository.model.FaturamentoEntradaPagamento;
import br.com.lar.repository.model.FaturamentoPagamento;
import br.com.sysdesc.util.enumeradores.TipoStatusEnum;

public class FaturamentoContasReceber {

	public List<ContasPagar> registrarContasPagar(FaturamentoEntrada faturamento) {
		List<ContasPagar> contasPagars = new ArrayList<>();

		List<FaturamentoEntradaPagamento> faturamentoAPrazo = faturamento.getFaturamentoEntradaPagamentos().stream()
				.filter(pagamento -> pagamento.getFormasPagamento().isFlagPermitePagamentoPrazo())
				.collect(Collectors.toList());

		faturamentoAPrazo.forEach(pagamento -> {

			ContasPagar contasPagar = new ContasPagar();
			contasPagar.setBaixado(false);
			contasPagar.setCaixaCabecalho(faturamento.getCaixaCabecalho());
			contasPagar.setCliente(faturamento.getCliente());
			contasPagar.setCodigoStatus(TipoStatusEnum.ATIVO.getCodigo());
			contasPagar.setDataCadastro(new Date());
			contasPagar.setDataManutencao(new Date());
			contasPagar.setDataMovimento(pagamento.getDataLancamento());
			contasPagar.setDataVencimento(pagamento.getDataVencimento());
			contasPagar.setDocumento(faturamento.getNumeroDocumento());
			contasPagar.setFormasPagamento(pagamento.getFormasPagamento());
			contasPagar.setHistorico(faturamento.getHistorico());
			contasPagar.setMotorista(faturamento.getMotorista());
			contasPagar.setVeiculo(faturamento.getVeiculo());
			contasPagar.setValorAcrescimo(BigDecimal.ZERO);
			contasPagar.setValorDesconto(BigDecimal.ZERO);
			contasPagar.setValorJuros(BigDecimal.ZERO);
			contasPagar.setValorPago(BigDecimal.ZERO);
			contasPagar.setValorParcela(pagamento.getValorParcela());

			contasPagars.add(contasPagar);
		});

		return contasPagars;
	}

	public List<ContasReceber> registrarContasReceber(Faturamento faturamento) {

		List<ContasReceber> contasRecebers = new ArrayList<>();

		List<FaturamentoPagamento> faturamentoAPrazo = faturamento.getFaturamentoPagamentos().stream()
				.filter(pagamento -> pagamento.getFormasPagamento().isFlagPermitePagamentoPrazo())
				.collect(Collectors.toList());

		faturamentoAPrazo.forEach(pagamento -> {

			ContasReceber contasReceber = new ContasReceber();
			contasReceber.setBaixado(false);
			contasReceber.setCaixaCabecalho(faturamento.getCaixaCabecalho());
			contasReceber.setCliente(faturamento.getCliente());
			contasReceber.setCodigoStatus(TipoStatusEnum.ATIVO.getCodigo());
			contasReceber.setDataCadastro(new Date());
			contasReceber.setDataManutencao(new Date());
			contasReceber.setDataMovimento(pagamento.getDataLancamento());
			contasReceber.setDataVencimento(pagamento.getDataVencimento());
			contasReceber.setDocumento(faturamento.getNumeroDocumento());
			contasReceber.setFormasPagamento(pagamento.getFormasPagamento());
			contasReceber.setHistorico(faturamento.getHistorico());

			if (faturamento.getFaturamentoTransporte() != null) {
				contasReceber.setMotorista(faturamento.getFaturamentoTransporte().getMotorista());
				contasReceber.setVeiculo(faturamento.getFaturamentoTransporte().getVeiculo());
			}
			contasReceber.setValorAcrescimo(BigDecimal.ZERO);
			contasReceber.setValorDesconto(BigDecimal.ZERO);
			contasReceber.setValorJuros(BigDecimal.ZERO);
			contasReceber.setValorPago(BigDecimal.ZERO);
			contasReceber.setValorParcela(pagamento.getValorParcela());

			contasRecebers.add(contasReceber);
		});

		return contasRecebers;
	}

}
