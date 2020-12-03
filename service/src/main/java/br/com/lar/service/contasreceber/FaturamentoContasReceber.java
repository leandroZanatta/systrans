package br.com.lar.service.contasreceber;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import br.com.lar.repository.model.ContasPagar;
import br.com.lar.repository.model.ContasPagarVeiculo;
import br.com.lar.repository.model.ContasReceber;
import br.com.lar.repository.model.ContasReceberVeiculo;
import br.com.lar.repository.model.FaturamentoCabecalho;
import br.com.lar.repository.model.FaturamentoDetalhe;
import br.com.lar.repository.model.FaturamentoEntradaPagamentos;
import br.com.lar.repository.model.FaturamentoEntradasCabecalho;
import br.com.lar.repository.model.FaturamentoEntradasDetalhe;
import br.com.lar.repository.model.FaturamentoPagamentos;
import br.com.sysdesc.util.enumeradores.TipoStatusEnum;
import br.com.systrans.util.RateioUtil;

public class FaturamentoContasReceber {

	public List<ContasPagar> registrarContasPagar(FaturamentoEntradasCabecalho faturamento, List<FaturamentoEntradasDetalhe> detalhes) {
		List<ContasPagar> contasPagars = new ArrayList<>();

		List<FaturamentoEntradaPagamentos> faturamentoAPrazo = faturamento.getFaturamentoEntradaPagamentos().stream()
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
			contasPagar.setFormasPagamento(pagamento.getFormasPagamento());
			contasPagar.setHistorico(faturamento.getHistorico());
			contasPagar.setValorAcrescimo(BigDecimal.ZERO);
			contasPagar.setValorDesconto(BigDecimal.ZERO);
			contasPagar.setValorJuros(BigDecimal.ZERO);
			contasPagar.setValorPago(BigDecimal.ZERO);
			contasPagar.setValorParcela(pagamento.getValorParcela());

			List<ContasPagarVeiculo> contasPagarVeiculos = detalhes.stream().map(detalhe -> {

				BigDecimal valorDetalhe = detalhe.getValorBruto().subtract(detalhe.getValorDesconto()).add(detalhe.getValorAcrescimo());

				ContasPagarVeiculo contasPagarVeiculo = new ContasPagarVeiculo();
				contasPagarVeiculo.setContasPagar(contasPagar);
				contasPagarVeiculo.setDocumento(detalhe.getNumeroDocumento());
				contasPagarVeiculo.setMotorista(detalhe.getMotorista());
				contasPagarVeiculo.setVeiculo(detalhe.getVeiculo());
				contasPagarVeiculo.setValorParcela(
						valorDetalhe.multiply(pagamento.getValorParcela()).divide(faturamento.getValorBruto(), 2,
								RoundingMode.HALF_EVEN));

				return contasPagarVeiculo;
			}).collect(Collectors.toList());

			RateioUtil.efetuarRateio(contasPagarVeiculos, ContasPagarVeiculo::getValorParcela, ContasPagarVeiculo::setValorParcela,
					pagamento.getValorParcela());

			contasPagar.setContasPagarVeiculos(contasPagarVeiculos);

			contasPagars.add(contasPagar);
		});

		return contasPagars;
	}

	public List<ContasReceber> registrarContasReceber(FaturamentoCabecalho faturamento, List<FaturamentoDetalhe> faturamentoDetalhes) {

		List<ContasReceber> contasRecebers = new ArrayList<>();

		List<FaturamentoPagamentos> faturamentoAPrazo = faturamento.getFaturamentoPagamentos().stream()
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
			contasReceber.setFormasPagamento(pagamento.getFormasPagamento());
			contasReceber.setHistorico(faturamento.getHistorico());
			contasReceber.setValorAcrescimo(BigDecimal.ZERO);
			contasReceber.setValorDesconto(BigDecimal.ZERO);
			contasReceber.setValorJuros(BigDecimal.ZERO);
			contasReceber.setValorPago(BigDecimal.ZERO);
			contasReceber.setValorParcela(pagamento.getValorParcela());

			List<ContasReceberVeiculo> contasReceberVeiculos = faturamentoDetalhes.stream().map(detalhe -> {

				BigDecimal valorDetalhe = detalhe.getValorBruto().subtract(detalhe.getValorDesconto()).add(detalhe.getValorAcrescimo());

				ContasReceberVeiculo contasReceberVeiculo = new ContasReceberVeiculo();
				contasReceberVeiculo.setContasReceber(contasReceber);
				contasReceberVeiculo.setDocumento(detalhe.getNumeroDocumento());
				contasReceberVeiculo.setMotorista(detalhe.getMotorista());
				contasReceberVeiculo.setVeiculo(detalhe.getVeiculo());
				contasReceberVeiculo.setValorParcela(
						valorDetalhe.multiply(pagamento.getValorParcela()).divide(faturamento.getValorBruto(), 2,
								RoundingMode.HALF_EVEN));

				return contasReceberVeiculo;
			}).collect(Collectors.toList());

			RateioUtil.efetuarRateio(contasReceberVeiculos, ContasReceberVeiculo::getValorParcela, ContasReceberVeiculo::setValorParcela,
					pagamento.getValorParcela());

			contasReceber.setContasReceberVeiculos(contasReceberVeiculos);

			contasRecebers.add(contasReceber);
		});

		return contasRecebers;
	}

}
