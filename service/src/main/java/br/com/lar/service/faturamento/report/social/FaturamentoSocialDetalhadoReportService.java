package br.com.lar.service.faturamento.report.social;

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

public class FaturamentoSocialDetalhadoReportService {

	private FaturamentoCabecalhoDAO faturamentoDAO = new FaturamentoCabecalhoDAO();
	private ContasPagarPagamentoDAO contasPagarPagamentoDAO = new ContasPagarPagamentoDAO();
	private FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO = new FaturamentoEntradaCabecalhoDAO();

	public List<FaturamentoBrutoVO> filtrarFaturamentoSocialDetalhado(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		List<FaturamentoBrutoVO> faturamentoBrutoReport = new ArrayList<>();

		List<FaturamentoBrutoReportProjection> faturamentoBrutoReportProjections = faturamentoDAO
				.filtrarFaturamentoBrutoDetalhado(pesquisaFaturamentoBrutoVO);

		List<DespesasFinanceirasProjection> despesasFinanceiras = contasPagarPagamentoDAO.filtrarDespesasFinanceiras(pesquisaFaturamentoBrutoVO);

		List<FaturamentoBrutoReportProjection> despesasSociais = faturamentoEntradaDAO
				.filtrarFaturamentoBrutoCentroCustoDetalhado(pesquisaFaturamentoBrutoVO);

		if (!ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos())
				&& despesasSociais.stream().anyMatch(projection -> LongUtil.isNullOrZero(projection.getCodigoVeiculo()))) {

			List<FaturamentoVeiculoProjection> faturamentoVeiculoProjections = faturamentoDAO.buscarFaturamentoVeiculo(
					pesquisaFaturamentoBrutoVO.getDataMovimentoInicial(), pesquisaFaturamentoBrutoVO.getDataMovimentoFinal());

			BigDecimal valorTotal = faturamentoVeiculoProjections.stream().map(FaturamentoVeiculoProjection::getValorBruto).reduce(BigDecimal.ZERO,
					BigDecimal::add);

			despesasSociais = atualizarDespesasPorVeiculo(despesasSociais, faturamentoVeiculoProjections, valorTotal);
		}

		BigDecimal valorReceita = getValorTotal(faturamentoBrutoReportProjections, FaturamentoBrutoReportProjection::getValorBruto);

		BigDecimal valorDespesa = getValorTotal(despesasSociais, FaturamentoBrutoReportProjection::getValorBruto);
		BigDecimal faturamentoBruto = valorReceita.subtract(valorDespesa);

		BigDecimal valorDespesasFinanceiras = despesasFinanceiras.stream().map(despesas -> despesas.getValorAcrescimo().add(despesas.getValorJuros()))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Map<String, List<FaturamentoBrutoReportProjection>> mapaCreditos = faturamentoBrutoReportProjections.stream()
				.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getHistorico));

		Map<String, List<FaturamentoBrutoReportProjection>> mapaDebitos = despesasSociais.stream()
				.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getHistorico));

		List<DespesasFinanceirasProjection> despesasAcrescimos = despesasFinanceiras.stream()
				.filter(despesas -> BigDecimalUtil.diferente(despesas.getValorAcrescimo(), BigDecimal.ZERO)).collect(Collectors.toList());

		List<DespesasFinanceirasProjection> despesasJuros = despesasFinanceiras.stream()
				.filter(despesas -> BigDecimalUtil.diferente(despesas.getValorJuros(), BigDecimal.ZERO)).collect(Collectors.toList());

		BigDecimal valorAcrescimos = despesasAcrescimos.stream().map(DespesasFinanceirasProjection::getValorAcrescimo).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		BigDecimal valorJuros = despesasJuros.stream().map(DespesasFinanceirasProjection::getValorJuros).reduce(BigDecimal.ZERO, BigDecimal::add);

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("RECEITA BRUTA", BigDecimal.ZERO, valorReceita, 1));

		mapaCreditos.forEach((key, value) -> {

			faturamentoBrutoReport
					.add(new FaturamentoBrutoVO(key, BigDecimal.ZERO, getValorTotal(value, FaturamentoBrutoReportProjection::getValorBruto), 2));

			if (ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos())
					&& pesquisaFaturamentoBrutoVO.getCodigoVeiculos().size() != 1) {

				Map<String, List<FaturamentoBrutoReportProjection>> mapaCreditosVeiculo = value.stream()
						.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getVeiculo));

				mapaCreditosVeiculo.forEach((placa, credito) -> {

					faturamentoBrutoReport.add(new FaturamentoBrutoVO(placa, BigDecimal.ZERO,
							getValorTotal(credito, FaturamentoBrutoReportProjection::getValorBruto), 3));

				});
			}
		});

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS", BigDecimal.ZERO, valorDespesa.negate(), 1));

		mapaDebitos.forEach((key, value) -> {

			faturamentoBrutoReport.add(
					new FaturamentoBrutoVO(key, BigDecimal.ZERO, getValorTotal(value, FaturamentoBrutoReportProjection::getValorBruto).negate(), 2));

			if (ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos())
					&& pesquisaFaturamentoBrutoVO.getCodigoVeiculos().size() != 1) {

				Map<String, List<FaturamentoBrutoReportProjection>> mapaDebitosVeiculo = value.stream()
						.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getVeiculo));

				mapaDebitosVeiculo.forEach((placa, debito) -> {

					faturamentoBrutoReport.add(new FaturamentoBrutoVO(placa, BigDecimal.ZERO,
							getValorTotal(debito, FaturamentoBrutoReportProjection::getValorBruto), 3));

				});
			}
		});

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO BRUTO", BigDecimal.ZERO, faturamentoBruto, 1));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS FINANCEIRAS", BigDecimal.ZERO, valorDespesasFinanceiras.negate(), 1));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("ACRÉSCIMOS", BigDecimal.ZERO, valorAcrescimos.negate(), 2));

		if (ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos()) && pesquisaFaturamentoBrutoVO.getCodigoVeiculos().size() != 1) {

			despesasAcrescimos.stream().collect(Collectors.groupingBy(DespesasFinanceirasProjection::getVeiculo)).forEach((key, value) -> {

				faturamentoBrutoReport.add(new FaturamentoBrutoVO(key, BigDecimal.ZERO,
						value.stream().map(DespesasFinanceirasProjection::getValorAcrescimo).reduce(BigDecimal.ZERO, BigDecimal::add).negate(), 3));
			});
		}
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("JUROS", BigDecimal.ZERO, valorJuros.negate(), 2));

		if (ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos()) && pesquisaFaturamentoBrutoVO.getCodigoVeiculos().size() != 1) {

			despesasJuros.stream().collect(Collectors.groupingBy(DespesasFinanceirasProjection::getVeiculo)).forEach((key, value) -> {

				faturamentoBrutoReport.add(new FaturamentoBrutoVO(key, BigDecimal.ZERO,
						value.stream().map(DespesasFinanceirasProjection::getValorJuros).reduce(BigDecimal.ZERO, BigDecimal::add).negate(), 3));
			});
		}

		faturamentoBrutoReport
				.add(new FaturamentoBrutoVO("FATURAMENTO LIQUIDO", BigDecimal.ZERO, faturamentoBruto.subtract(valorDespesasFinanceiras), 1));

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

	public List<FaturamentoBrutoMensalVO> filtrarFaturamentoSocialDetalhadoMensal(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {
		// TODO Auto-generated method stub
		return null;
	}
}
