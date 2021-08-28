package br.com.lar.service.faturamento.report.consolidado;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import br.com.systrans.util.vo.FaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;

public class FaturamentoConsolidadoHistoricoReportService {

	private FaturamentoCabecalhoDAO faturamentoDAO = new FaturamentoCabecalhoDAO();
	private ContasPagarPagamentoDAO contasPagarPagamentoDAO = new ContasPagarPagamentoDAO();
	private FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO = new FaturamentoEntradaCabecalhoDAO();

	public List<FaturamentoBrutoVO> filtrarFaturamentoHistorico(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		List<FaturamentoBrutoVO> faturamentoBrutoReport = new ArrayList<>();

		List<FaturamentoBrutoReportProjection> faturamentoBrutoReportProjections = faturamentoDAO
				.filtrarFaturamentoBrutoHistorico(pesquisaFaturamentoBrutoVO);

		List<DespesasFinanceirasProjection> despesasFinanceiras = contasPagarPagamentoDAO.filtrarDespesasFinanceiras(pesquisaFaturamentoBrutoVO);

		List<FaturamentoBrutoReportProjection> despesasContabeis = faturamentoEntradaDAO
				.filtrarFaturamentoBrutoEntradasHistorico(pesquisaFaturamentoBrutoVO);

		List<FaturamentoBrutoReportProjection> despesasSociais = faturamentoEntradaDAO
				.filtrarFaturamentoBrutoCentroCustoHistorico(pesquisaFaturamentoBrutoVO);

		if (!ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos())
				&& despesasSociais.stream().anyMatch(projection -> LongUtil.isNullOrZero(projection.getCodigoVeiculo()))) {

			List<FaturamentoVeiculoProjection> faturamentoVeiculoProjections = faturamentoDAO.buscarFaturamentoVeiculo(
					pesquisaFaturamentoBrutoVO.getDataMovimentoInicial(), pesquisaFaturamentoBrutoVO.getDataMovimentoFinal());

			BigDecimal valorTotal = faturamentoVeiculoProjections.stream().map(FaturamentoVeiculoProjection::getValorBruto).reduce(BigDecimal.ZERO,
					BigDecimal::add);

			despesasSociais = atualizarDespesasPorVeiculo(despesasSociais, faturamentoVeiculoProjections, valorTotal);
			despesasContabeis = atualizarDespesasPorVeiculo(despesasContabeis, faturamentoVeiculoProjections, valorTotal);
		}

		BigDecimal valorReceita = getValorTotal(faturamentoBrutoReportProjections, FaturamentoBrutoReportProjection::getValorBruto);

		BigDecimal valorDespesaSocial = getValorTotal(despesasSociais, FaturamentoBrutoReportProjection::getValorBruto);
		BigDecimal valorDespesaContabil = getValorTotal(despesasContabeis, FaturamentoBrutoReportProjection::getValorBruto);

		BigDecimal faturamentoBrutoSocial = valorReceita.subtract(valorDespesaSocial);
		BigDecimal faturamentoBrutoContabil = valorReceita.subtract(valorDespesaContabil);

		BigDecimal valorDespesasFinanceiras = despesasFinanceiras.stream().map(despesas -> despesas.getValorAcrescimo().add(despesas.getValorJuros()))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Map<String, List<FaturamentoBrutoReportProjection>> mapaCreditos = faturamentoBrutoReportProjections.stream()
				.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getHistorico));

		List<DespesasFinanceirasProjection> despesasAcrescimos = despesasFinanceiras.stream()
				.filter(despesas -> BigDecimalUtil.diferente(despesas.getValorAcrescimo(), BigDecimal.ZERO)).collect(Collectors.toList());

		List<DespesasFinanceirasProjection> despesasJuros = despesasFinanceiras.stream()
				.filter(despesas -> BigDecimalUtil.diferente(despesas.getValorJuros(), BigDecimal.ZERO)).collect(Collectors.toList());

		BigDecimal valorAcrescimos = despesasAcrescimos.stream().map(DespesasFinanceirasProjection::getValorAcrescimo).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		BigDecimal valorJuros = despesasJuros.stream().map(DespesasFinanceirasProjection::getValorJuros).reduce(BigDecimal.ZERO, BigDecimal::add);

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("RECEITA BRUTA", valorReceita, valorReceita, 1, null));

		mapaCreditos.forEach((key, value) -> faturamentoBrutoReport
				.add(new FaturamentoBrutoVO(key, getValorTotal(value, FaturamentoBrutoReportProjection::getValorBruto),
						getValorTotal(value, FaturamentoBrutoReportProjection::getValorBruto), 2, null)));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS", valorDespesaContabil, valorDespesaSocial.negate(), 1, null));

		Set<String> listaHistoricos = despesasSociais.stream().map(FaturamentoBrutoReportProjection::getHistorico).collect(Collectors.toSet());

		listaHistoricos.addAll(despesasContabeis.stream().map(FaturamentoBrutoReportProjection::getHistorico).collect(Collectors.toSet()));

		for (String historico : listaHistoricos) {

			BigDecimal valorHistoricoContabil = getValorTotal(
					despesasContabeis.stream().filter(item -> item.getHistorico().equals(historico)).collect(Collectors.toList()),
					FaturamentoBrutoReportProjection::getValorBruto).negate();

			BigDecimal valorHistoricoSocial = getValorTotal(
					despesasSociais.stream().filter(item -> item.getHistorico().equals(historico)).collect(Collectors.toList()),
					FaturamentoBrutoReportProjection::getValorBruto).negate();

			faturamentoBrutoReport.add(new FaturamentoBrutoVO(historico, valorHistoricoContabil, valorHistoricoSocial, 2, null));
		}

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO BRUTO", faturamentoBrutoContabil, faturamentoBrutoSocial, 1, null));
		faturamentoBrutoReport
				.add(new FaturamentoBrutoVO("DESPESAS FINANCEIRAS", valorDespesasFinanceiras.negate(), valorDespesasFinanceiras.negate(), 1, null));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("ACRÉSCIMOS", valorAcrescimos.negate(), valorAcrescimos.negate(), 2, null));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("JUROS", valorJuros.negate(), valorJuros.negate(), 2, null));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO LIQUIDO", faturamentoBrutoContabil.subtract(valorDespesasFinanceiras),
				faturamentoBrutoSocial.subtract(valorDespesasFinanceiras), 1, null));

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
}
