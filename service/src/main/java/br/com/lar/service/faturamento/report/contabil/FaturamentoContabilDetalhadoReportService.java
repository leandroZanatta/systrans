package br.com.lar.service.faturamento.report.contabil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import br.com.lar.repository.dao.ContasPagarPagamentoDAO;
import br.com.lar.repository.dao.FaturamentoCabecalhoDAO;
import br.com.lar.repository.dao.FaturamentoEntradaCabecalhoDAO;
import br.com.lar.repository.projection.DespesasFinanceirasProjection;
import br.com.lar.repository.projection.FaturamentoBrutoReportProjection;
import br.com.lar.repository.projection.FaturamentoVeiculoProjection;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.classes.ListUtil;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.systrans.util.vo.FaturamentoBrutoMensalVO;
import br.com.systrans.util.vo.FaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;

public class FaturamentoContabilDetalhadoReportService {

	private FaturamentoCabecalhoDAO faturamentoDAO = new FaturamentoCabecalhoDAO();
	private ContasPagarPagamentoDAO contasPagarPagamentoDAO = new ContasPagarPagamentoDAO();
	private FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO = new FaturamentoEntradaCabecalhoDAO();

	public List<FaturamentoBrutoVO> filtrarFaturamentoContabilDetalhado(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		List<FaturamentoBrutoVO> faturamentoBrutoReport = new ArrayList<>();

		List<FaturamentoBrutoReportProjection> faturamentoBrutoReportProjections = faturamentoDAO
				.filtrarFaturamentoBrutoDetalhado(pesquisaFaturamentoBrutoVO);

		List<DespesasFinanceirasProjection> despesasFinanceiras = contasPagarPagamentoDAO.filtrarDespesasFinanceiras(pesquisaFaturamentoBrutoVO);

		List<FaturamentoBrutoReportProjection> despesasContabeis = faturamentoEntradaDAO
				.filtrarFaturmamentoBrutoEntradasDetalhado(pesquisaFaturamentoBrutoVO);

		if (!ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos())
				&& despesasContabeis.stream().anyMatch(projection -> LongUtil.isNullOrZero(projection.getCodigoVeiculo()))) {

			List<FaturamentoVeiculoProjection> faturamentoVeiculoProjections = faturamentoDAO.buscarFaturamentoVeiculo(
					pesquisaFaturamentoBrutoVO.getDataMovimentoInicial(), pesquisaFaturamentoBrutoVO.getDataMovimentoFinal());

			BigDecimal valorTotal = faturamentoVeiculoProjections.stream().map(FaturamentoVeiculoProjection::getValorBruto).reduce(BigDecimal.ZERO,
					BigDecimal::add);

			despesasContabeis = atualizarDespesasPorVeiculo(despesasContabeis, faturamentoVeiculoProjections, valorTotal);
		}

		BigDecimal valorReceita = getValorTotal(faturamentoBrutoReportProjections, FaturamentoBrutoReportProjection::getValorBruto);

		BigDecimal valorDespesa = getValorTotal(despesasContabeis, FaturamentoBrutoReportProjection::getValorBruto);
		BigDecimal faturamentoBruto = valorReceita.subtract(valorDespesa);

		BigDecimal valorDespesasFinanceiras = despesasFinanceiras.stream().map(despesas -> despesas.getValorAcrescimo().add(despesas.getValorJuros()))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Map<String, List<FaturamentoBrutoReportProjection>> mapaCreditos = faturamentoBrutoReportProjections.stream()
				.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getHistorico));

		Map<String, List<FaturamentoBrutoReportProjection>> mapaDebitos = despesasContabeis.stream()
				.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getHistorico));

		List<DespesasFinanceirasProjection> despesasAcrescimos = despesasFinanceiras.stream()
				.filter(despesas -> BigDecimalUtil.diferente(despesas.getValorAcrescimo(), BigDecimal.ZERO)).collect(Collectors.toList());

		List<DespesasFinanceirasProjection> despesasJuros = despesasFinanceiras.stream()
				.filter(despesas -> BigDecimalUtil.diferente(despesas.getValorJuros(), BigDecimal.ZERO)).collect(Collectors.toList());

		BigDecimal valorAcrescimos = despesasAcrescimos.stream().map(DespesasFinanceirasProjection::getValorAcrescimo).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		BigDecimal valorJuros = despesasJuros.stream().map(DespesasFinanceirasProjection::getValorJuros).reduce(BigDecimal.ZERO, BigDecimal::add);

		FaturamentoBrutoVO receita = new FaturamentoBrutoVO("RECEITA BRUTA", valorReceita, BigDecimal.ZERO, 1, null);
		faturamentoBrutoReport.add(receita);

		mapaCreditos.forEach((key, value) -> {

			BigDecimal valorReceitaHistorico = getValorTotal(value, FaturamentoBrutoReportProjection::getValorBruto);

			FaturamentoBrutoVO receitaHistorico = new FaturamentoBrutoVO(key, valorReceitaHistorico, BigDecimal.ZERO, 2, receita);

			faturamentoBrutoReport.add(receitaHistorico);

			if (ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos())
					&& pesquisaFaturamentoBrutoVO.getCodigoVeiculos().size() != 1) {

				Map<String, List<FaturamentoBrutoReportProjection>> mapaCreditosVeiculo = value.stream()
						.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getVeiculo));

				mapaCreditosVeiculo.forEach((placa, credito) -> {

					BigDecimal valorPlacaReceita = getValorTotal(credito, FaturamentoBrutoReportProjection::getValorBruto);

					faturamentoBrutoReport.add(new FaturamentoBrutoVO(placa, valorPlacaReceita, BigDecimal.ZERO, 3,
							pesquisaFaturamentoBrutoVO.getTipoPercentual() == 0 ? receitaHistorico : receita));

				});
			}
		});

		FaturamentoBrutoVO despesa = new FaturamentoBrutoVO("DESPESAS", valorDespesa.negate(), BigDecimal.ZERO, 1, receita);
		despesa.setPercentual(BigDecimal.valueOf(100d));
		;
		faturamentoBrutoReport.add(despesa);

		mapaDebitos.forEach((key, value) -> {

			BigDecimal valorDespesaHistorico = getValorTotal(value, FaturamentoBrutoReportProjection::getValorBruto);
			FaturamentoBrutoVO despesaHistorico = new FaturamentoBrutoVO(key, valorDespesaHistorico.negate(), BigDecimal.ZERO, 2,
					pesquisaFaturamentoBrutoVO.getTipoPercentual() == 0 ? despesa : receita);

			faturamentoBrutoReport.add(despesaHistorico);

			if (ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos())
					&& pesquisaFaturamentoBrutoVO.getCodigoVeiculos().size() != 1) {

				Map<String, List<FaturamentoBrutoReportProjection>> mapaDebitosVeiculo = value.stream()
						.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getVeiculo));

				mapaDebitosVeiculo.forEach((placa, debito) -> {

					BigDecimal valorPlacaDespesa = getValorTotal(debito, FaturamentoBrutoReportProjection::getValorBruto);

					faturamentoBrutoReport.add(new FaturamentoBrutoVO(placa, valorPlacaDespesa.negate(), BigDecimal.ZERO, 3,
							pesquisaFaturamentoBrutoVO.getTipoPercentual() == 0 ? despesaHistorico : receita));
				});
			}
		});

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO BRUTO", faturamentoBruto, BigDecimal.ZERO, 1, receita));

		FaturamentoBrutoVO financeiras = new FaturamentoBrutoVO("DESPESAS FINANCEIRAS", valorDespesasFinanceiras.negate(), BigDecimal.ZERO, 1,
				receita);

		faturamentoBrutoReport.add(financeiras);

		FaturamentoBrutoVO acrescimos = new FaturamentoBrutoVO("ACRÉSCIMOS", valorAcrescimos.negate(), BigDecimal.ZERO, 2,
				pesquisaFaturamentoBrutoVO.getTipoPercentual() == 0 ? financeiras : receita);

		faturamentoBrutoReport.add(acrescimos);

		if (ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos()) && pesquisaFaturamentoBrutoVO.getCodigoVeiculos().size() != 1) {

			despesasAcrescimos.stream().collect(Collectors.groupingBy(DespesasFinanceirasProjection::getVeiculo))

					.forEach((key, value) -> {

						BigDecimal valorPlacaAcrescimos = value.stream().map(DespesasFinanceirasProjection::getValorAcrescimo).reduce(BigDecimal.ZERO,
								BigDecimal::add);

						faturamentoBrutoReport.add(new FaturamentoBrutoVO(key, valorPlacaAcrescimos.negate(), BigDecimal.ZERO, 3,
								pesquisaFaturamentoBrutoVO.getTipoPercentual() == 0 ? acrescimos : receita));
					});
		}

		FaturamentoBrutoVO juros = new FaturamentoBrutoVO("JUROS", valorJuros.negate(), BigDecimal.ZERO, 2,
				pesquisaFaturamentoBrutoVO.getTipoPercentual() == 0 ? financeiras : receita);

		faturamentoBrutoReport.add(juros);

		if (ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos()) && pesquisaFaturamentoBrutoVO.getCodigoVeiculos().size() != 1) {

			despesasJuros.stream().collect(Collectors.groupingBy(DespesasFinanceirasProjection::getVeiculo)).forEach((key, value) -> {

				BigDecimal valorPlacaJuros = value.stream().map(DespesasFinanceirasProjection::getValorJuros).reduce(BigDecimal.ZERO,
						BigDecimal::add);

				faturamentoBrutoReport.add(new FaturamentoBrutoVO(key, valorPlacaJuros.negate(), BigDecimal.ZERO, 3,
						pesquisaFaturamentoBrutoVO.getTipoPercentual() == 0 ? juros : receita));
			});
		}

		faturamentoBrutoReport
				.add(new FaturamentoBrutoVO("FATURAMENTO LIQUIDO", faturamentoBruto.subtract(valorDespesasFinanceiras), BigDecimal.ZERO, 1, receita));

		return faturamentoBrutoReport;

	}

	private List<FaturamentoBrutoReportProjection> atualizarDespesasPorVeiculo(List<FaturamentoBrutoReportProjection> despesas,
			List<FaturamentoVeiculoProjection> faturamentoVeiculo, BigDecimal valorTotal) {

		List<FaturamentoBrutoReportProjection> despesasSemVeiculo = despesas.stream()
				.filter(projection -> LongUtil.isNullOrZero(projection.getCodigoVeiculo())).collect(Collectors.toList());

		Map<Long, List<FaturamentoBrutoReportProjection>> mapaVeiculosIdentificados = despesas.stream()
				.filter(projection -> !LongUtil.isNullOrZero(projection.getCodigoVeiculo()))
				.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getCodigoVeiculo));

		List<FaturamentoBrutoReportProjection> faturamentoBrutoRateados = new ArrayList<>();

		mapaVeiculosIdentificados.forEach((key, value) -> {

			Optional<FaturamentoVeiculoProjection> optional = faturamentoVeiculo.stream().filter(item -> item.getCodigoVeiculo().equals(key))
					.findFirst();

			despesasSemVeiculo.forEach(semVeiculo -> {

				FaturamentoBrutoReportProjection faturamentoRateado = new FaturamentoBrutoReportProjection();
				faturamentoRateado.setCentroCusto(semVeiculo.getCentroCusto());
				faturamentoRateado.setHistorico(semVeiculo.getHistorico());
				faturamentoRateado.setCodigoVeiculo(value.get(0).getCodigoVeiculo());
				faturamentoRateado.setVeiculo(value.get(0).getVeiculo());

				if (optional.isPresent()) {

					faturamentoRateado.setValorBruto(semVeiculo.getValorBruto().divide(valorTotal, 8, RoundingMode.HALF_EVEN)
							.multiply(optional.get().getValorBruto()).setScale(2, RoundingMode.HALF_EVEN));
				} else {

					faturamentoRateado.setValorBruto(BigDecimal.ZERO);
				}

				faturamentoBrutoRateados.add(faturamentoRateado);
			});

		});

		despesas = despesas.stream().filter(projection -> !LongUtil.isNullOrZero(projection.getCodigoVeiculo())).collect(Collectors.toList());

		despesas.addAll(faturamentoBrutoRateados);

		return despesas;
	}

	private BigDecimal getValorTotal(List<FaturamentoBrutoReportProjection> lista, Function<FaturamentoBrutoReportProjection, BigDecimal> funcao) {

		return lista.stream().map(funcao).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public List<FaturamentoBrutoMensalVO> filtrarFaturamentoContabilDetalhadoMensal(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {
		// TODO Auto-generated method stub
		return null;
	}
}
