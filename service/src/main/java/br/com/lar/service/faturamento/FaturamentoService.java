package br.com.lar.service.faturamento;

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
import br.com.systrans.util.vo.FaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;

public class FaturamentoService {

	private FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO = new FaturamentoEntradaCabecalhoDAO();
	private FaturamentoCabecalhoDAO faturamentoDAO = new FaturamentoCabecalhoDAO();
	private ContasPagarPagamentoDAO contasPagarPagamentoDAO = new ContasPagarPagamentoDAO();

	public List<FaturamentoBrutoVO> filtrarFaturamentoBruto(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		List<FaturamentoBrutoReportProjection> faturamentoBrutoSaidaReportProjections = faturamentoDAO
				.filtrarFaturamentoBruto(pesquisaFaturamentoBrutoVO);

		List<FaturamentoBrutoReportProjection> faturamentoBrutoEntradaReportProjections = faturamentoEntradaDAO
				.filtrarFaturamentoBruto(pesquisaFaturamentoBrutoVO);

		List<DespesasFinanceirasProjection> contasPagarPagamentos = contasPagarPagamentoDAO.filtrarDespesasFinanceiras(pesquisaFaturamentoBrutoVO);

		if (!ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos()) &&
				faturamentoBrutoEntradaReportProjections.stream().anyMatch(projection -> LongUtil.isNullOrZero(projection.getCodigoVeiculo()))) {

			List<FaturamentoBrutoReportProjection> faturamentoSemVeiculoProjections = faturamentoBrutoEntradaReportProjections.stream()
					.filter(projection -> LongUtil.isNullOrZero(projection.getCodigoVeiculo())).collect(Collectors.toList());

			Map<Long, List<FaturamentoBrutoReportProjection>> mapaFaturamentoVeiculosIdentificados = faturamentoBrutoEntradaReportProjections.stream()
					.filter(projection -> !LongUtil.isNullOrZero(projection.getCodigoVeiculo()))
					.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getCodigoVeiculo));

			List<FaturamentoVeiculoProjection> faturamentoVeiculoProjections = faturamentoDAO
					.buscarFaturamentoVeiculo(pesquisaFaturamentoBrutoVO.getDataMovimentoInicial(),
							pesquisaFaturamentoBrutoVO.getDataMovimentoFinal());

			BigDecimal valorTotal = faturamentoVeiculoProjections.stream().map(FaturamentoVeiculoProjection::getValorBruto)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			List<FaturamentoBrutoReportProjection> faturamentoBrutoRateados = new ArrayList<>();

			mapaFaturamentoVeiculosIdentificados.forEach((key, value) -> {

				Optional<FaturamentoVeiculoProjection> optional = faturamentoVeiculoProjections.stream()
						.filter(item -> item.getCodigoVeiculo().equals(key)).findFirst();

				faturamentoSemVeiculoProjections.forEach(semVeiculo -> {

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

			faturamentoBrutoEntradaReportProjections = faturamentoBrutoEntradaReportProjections.stream()
					.filter(projection -> !LongUtil.isNullOrZero(projection.getCodigoVeiculo())).collect(Collectors.toList());

			faturamentoBrutoEntradaReportProjections.addAll(faturamentoBrutoRateados);
		}

		if (pesquisaFaturamentoBrutoVO.getCodigoRelatorio() == 0) {

			return gerarFaturamentoBasico(faturamentoBrutoSaidaReportProjections, faturamentoBrutoEntradaReportProjections, contasPagarPagamentos);
		}

		if (pesquisaFaturamentoBrutoVO.getCodigoRelatorio() == 1 || !ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos())) {

			return gerarFaturamentoHistorico(faturamentoBrutoSaidaReportProjections, faturamentoBrutoEntradaReportProjections, contasPagarPagamentos);
		}

		return gerarFaturamentoDetalhado(faturamentoBrutoSaidaReportProjections, faturamentoBrutoEntradaReportProjections, contasPagarPagamentos);
	}

	private List<FaturamentoBrutoVO> gerarFaturamentoDetalhado(List<FaturamentoBrutoReportProjection> faturamentoBrutoSaidaReportProjections,
			List<FaturamentoBrutoReportProjection> faturamentoBrutoEntradaReportProjections,
			List<DespesasFinanceirasProjection> contasPagarPagamentos) {

		List<FaturamentoBrutoVO> faturamentoBrutoReport = new ArrayList<>();

		BigDecimal valorReceita = getValorTotal(faturamentoBrutoSaidaReportProjections, FaturamentoBrutoReportProjection::getValorBruto);
		BigDecimal valorDespesa = getValorTotal(faturamentoBrutoEntradaReportProjections, FaturamentoBrutoReportProjection::getValorBruto);
		BigDecimal faturamentoBruto = valorReceita.subtract(valorDespesa);
		BigDecimal valorDespesasFinanceiras = contasPagarPagamentos.stream()
				.map(despesas -> despesas.getValorAcrescimo().add(despesas.getValorJuros())).reduce(BigDecimal.ZERO, BigDecimal::add);
		Map<String, List<FaturamentoBrutoReportProjection>> mapaCreditos = faturamentoBrutoSaidaReportProjections.stream()
				.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getHistorico));
		Map<String, List<FaturamentoBrutoReportProjection>> mapaDebitos = faturamentoBrutoEntradaReportProjections.stream()
				.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getHistorico));
		List<DespesasFinanceirasProjection> despesasAcrescimos = contasPagarPagamentos.stream()
				.filter(despesas -> BigDecimalUtil.diferente(despesas.getValorAcrescimo(), BigDecimal.ZERO)).collect(Collectors.toList());
		List<DespesasFinanceirasProjection> despesasJuros = contasPagarPagamentos.stream()
				.filter(despesas -> BigDecimalUtil.diferente(despesas.getValorJuros(), BigDecimal.ZERO)).collect(Collectors.toList());

		BigDecimal valorAcrescimos = despesasAcrescimos.stream()
				.map(DespesasFinanceirasProjection::getValorAcrescimo).reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal valorJuros = despesasJuros.stream()
				.map(DespesasFinanceirasProjection::getValorJuros).reduce(BigDecimal.ZERO, BigDecimal::add);

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("RECEITA BRUTA", valorReceita, 1));

		mapaCreditos.forEach((key, value) -> {

			faturamentoBrutoReport
					.add(new FaturamentoBrutoVO(key, getValorTotal(value, FaturamentoBrutoReportProjection::getValorBruto), 2));

			value.stream()
					.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getVeiculo))
					.forEach((veiculo, lista) -> faturamentoBrutoReport
							.add(new FaturamentoBrutoVO(veiculo, getValorTotal(lista, FaturamentoBrutoReportProjection::getValorBruto), 3)));
		});

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS", valorDespesa.negate(), 1));

		mapaDebitos.forEach((key, value) -> {
			faturamentoBrutoReport
					.add(new FaturamentoBrutoVO(key, getValorTotal(value, FaturamentoBrutoReportProjection::getValorBruto).negate(), 2));

			value.stream().collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getVeiculo))
					.forEach((veiculo, lista) -> faturamentoBrutoReport
							.add(new FaturamentoBrutoVO(veiculo, getValorTotal(lista, FaturamentoBrutoReportProjection::getValorBruto).negate(), 3)));
		});

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO BRUTO", faturamentoBruto, 1));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS FINANCEIRAS", valorDespesasFinanceiras.negate(), 1));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("ACRÉSCIMOS", valorAcrescimos.negate(), 2));

		despesasAcrescimos.stream().collect(Collectors.groupingBy(DespesasFinanceirasProjection::getVeiculo)).forEach((key, value) ->

		faturamentoBrutoReport.add(new FaturamentoBrutoVO(key,
				value.stream().map(DespesasFinanceirasProjection::getValorAcrescimo).reduce(BigDecimal.ZERO, BigDecimal::add)
						.negate(),
				3)));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("JUROS", valorJuros.negate(), 2));

		despesasJuros.stream().collect(Collectors.groupingBy(DespesasFinanceirasProjection::getVeiculo))
				.forEach((key, value) -> faturamentoBrutoReport
						.add(new FaturamentoBrutoVO(key,
								value.stream().map(DespesasFinanceirasProjection::getValorJuros).reduce(BigDecimal.ZERO, BigDecimal::add)
										.negate(),
								3)));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO LIQUIDO", faturamentoBruto.subtract(valorDespesasFinanceiras), 1));

		return faturamentoBrutoReport;
	}

	private List<FaturamentoBrutoVO> gerarFaturamentoHistorico(List<FaturamentoBrutoReportProjection> faturamentoBrutoSaidaReportProjections,
			List<FaturamentoBrutoReportProjection> faturamentoBrutoEntradaReportProjections,
			List<DespesasFinanceirasProjection> contasPagarPagamentos) {

		List<FaturamentoBrutoVO> faturamentoBrutoReport = new ArrayList<>();

		BigDecimal valorReceita = getValorTotal(faturamentoBrutoSaidaReportProjections, FaturamentoBrutoReportProjection::getValorBruto);
		BigDecimal valorDespesa = getValorTotal(faturamentoBrutoEntradaReportProjections, FaturamentoBrutoReportProjection::getValorBruto);
		BigDecimal faturamentoBruto = valorReceita.subtract(valorDespesa);
		BigDecimal valorDespesasFinanceiras = contasPagarPagamentos.stream()
				.map(despesas -> despesas.getValorAcrescimo().add(despesas.getValorJuros())).reduce(BigDecimal.ZERO, BigDecimal::add);
		Map<String, List<FaturamentoBrutoReportProjection>> mapaCreditos = faturamentoBrutoSaidaReportProjections.stream()
				.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getHistorico));
		Map<String, List<FaturamentoBrutoReportProjection>> mapaDebitos = faturamentoBrutoEntradaReportProjections.stream()
				.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getHistorico));
		List<DespesasFinanceirasProjection> despesasAcrescimos = contasPagarPagamentos.stream()
				.filter(despesas -> BigDecimalUtil.diferente(despesas.getValorAcrescimo(), BigDecimal.ZERO)).collect(Collectors.toList());
		List<DespesasFinanceirasProjection> despesasJuros = contasPagarPagamentos.stream()
				.filter(despesas -> BigDecimalUtil.diferente(despesas.getValorJuros(), BigDecimal.ZERO)).collect(Collectors.toList());

		BigDecimal valorAcrescimos = despesasAcrescimos.stream()
				.map(DespesasFinanceirasProjection::getValorAcrescimo).reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal valorJuros = despesasJuros.stream()
				.map(DespesasFinanceirasProjection::getValorJuros).reduce(BigDecimal.ZERO, BigDecimal::add);

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("RECEITA BRUTA", valorReceita, 1));

		mapaCreditos.forEach((key, value) -> faturamentoBrutoReport
				.add(new FaturamentoBrutoVO(key, getValorTotal(value, FaturamentoBrutoReportProjection::getValorBruto), 2)));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS", valorDespesa.negate(), 1));

		mapaDebitos.forEach((key, value) -> faturamentoBrutoReport
				.add(new FaturamentoBrutoVO(key, getValorTotal(value, FaturamentoBrutoReportProjection::getValorBruto).negate(), 2)));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO BRUTO", faturamentoBruto, 1));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS FINANCEIRAS", valorDespesasFinanceiras.negate(), 1));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("ACRÉSCIMOS", valorAcrescimos.negate(), 2));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("JUROS", valorJuros.negate(), 2));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO LIQUIDO", faturamentoBruto.subtract(valorDespesasFinanceiras), 1));

		return faturamentoBrutoReport;
	}

	private List<FaturamentoBrutoVO> gerarFaturamentoBasico(List<FaturamentoBrutoReportProjection> faturamentoBrutoSaidaReportProjections,
			List<FaturamentoBrutoReportProjection> faturamentoBrutoEntradaReportProjections,
			List<DespesasFinanceirasProjection> contasPagarPagamentos) {

		List<FaturamentoBrutoVO> faturamentoBrutoReport = new ArrayList<>();

		BigDecimal valorReceita = getValorTotal(faturamentoBrutoSaidaReportProjections, FaturamentoBrutoReportProjection::getValorBruto);
		BigDecimal valorDespesa = getValorTotal(faturamentoBrutoEntradaReportProjections, FaturamentoBrutoReportProjection::getValorBruto);
		BigDecimal faturamentoBruto = valorReceita.subtract(valorDespesa);
		BigDecimal valorDespesasFinanceiras = contasPagarPagamentos.stream()
				.map(despesas -> despesas.getValorAcrescimo().add(despesas.getValorJuros())).reduce(BigDecimal.ZERO, BigDecimal::add);

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("RECEITA BRUTA", valorReceita, 1));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS", valorDespesa.negate(), 1));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO BRUTO", faturamentoBruto, 1));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS FINANCEIRAS", valorDespesasFinanceiras.negate(), 1));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO LIQUIDO", faturamentoBruto.subtract(valorDespesasFinanceiras), 1));

		return faturamentoBrutoReport;
	}

	private BigDecimal getValorTotal(List<FaturamentoBrutoReportProjection> lista, Function<FaturamentoBrutoReportProjection, BigDecimal> funcao) {

		return lista.stream().map(funcao).reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
