package br.com.lar.service.faturamento.report.contabil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import br.com.lar.repository.dao.ContasPagarPagamentoDAO;
import br.com.lar.repository.dao.FaturamentoCabecalhoDAO;
import br.com.lar.repository.dao.FaturamentoEntradaCabecalhoDAO;
import br.com.lar.repository.projection.FaturamentoBrutoReportProjection;
import br.com.lar.repository.projection.FaturamentoVeiculoProjection;
import br.com.sysdesc.util.classes.IfNull;
import br.com.sysdesc.util.classes.ListUtil;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.systrans.util.vo.FaturamentoBrutoMensalVO;
import br.com.systrans.util.vo.FaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;
import br.com.systrans.util.vo.ValorBrutoMensalVO;

public class FaturamentoContabilBasicoReportService {

	private FaturamentoCabecalhoDAO faturamentoDAO = new FaturamentoCabecalhoDAO();
	private ContasPagarPagamentoDAO contasPagarPagamentoDAO = new ContasPagarPagamentoDAO();
	private FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO = new FaturamentoEntradaCabecalhoDAO();

	public List<FaturamentoBrutoVO> filtrarFaturamentoContabilBasico(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		if (ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos())) {

			return filtrarFaturamentoContabilBasicoGeral(pesquisaFaturamentoBrutoVO);
		}

		return filtrarFaturamentoContabilBasicoVeiculo(pesquisaFaturamentoBrutoVO);
	}

	private List<FaturamentoBrutoVO> filtrarFaturamentoContabilBasicoVeiculo(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		List<FaturamentoBrutoVO> faturamentoBrutoReport = new ArrayList<>();

		BigDecimal valorBruto = IfNull.get(faturamentoDAO.filtrarFaturamentoBrutoBasico(pesquisaFaturamentoBrutoVO), BigDecimal.ZERO);

		BigDecimal despesasFinanceiras = IfNull.get(contasPagarPagamentoDAO.filtrarDespesasFinanceirasGeral(pesquisaFaturamentoBrutoVO),
				BigDecimal.ZERO);

		List<FaturamentoVeiculoProjection> faturamentoVeiculoProjections = faturamentoDAO
				.buscarFaturamentoVeiculo(pesquisaFaturamentoBrutoVO.getDataMovimentoInicial(), pesquisaFaturamentoBrutoVO.getDataMovimentoFinal());

		BigDecimal valorTotal = faturamentoVeiculoProjections.stream().map(FaturamentoVeiculoProjection::getValorBruto).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		List<FaturamentoBrutoReportProjection> despesasContabeis = faturamentoEntradaDAO
				.filtrarFaturamentoBrutoEntradasHistorico(pesquisaFaturamentoBrutoVO);

		BigDecimal valorContabil = atualizarDespesasPorVeiculo(despesasContabeis, faturamentoVeiculoProjections, valorTotal,
				pesquisaFaturamentoBrutoVO.getCodigoVeiculos());

		BigDecimal faturamentoBrutoContabil = valorBruto.subtract(valorContabil);
		BigDecimal percentualDespesa = valorContabil.divide(valorBruto, 4, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100d)).setScale(2,
				RoundingMode.HALF_EVEN);
		BigDecimal percentualFaturamentoBruto = faturamentoBrutoContabil.divide(valorBruto, 4, RoundingMode.HALF_EVEN)
				.multiply(BigDecimal.valueOf(100d)).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal percentualDespesasFinanceiras = despesasFinanceiras.divide(valorBruto, 4, RoundingMode.HALF_EVEN)
				.multiply(BigDecimal.valueOf(100d)).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal percentualFaturamentoLiquido = faturamentoBrutoContabil.subtract(despesasFinanceiras).divide(valorBruto, 4, RoundingMode.HALF_EVEN)
				.multiply(BigDecimal.valueOf(100d)).setScale(2, RoundingMode.HALF_EVEN);

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("RECEITA BRUTA", valorBruto, valorBruto, 1, BigDecimal.valueOf(100d)));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS", valorContabil.negate(), BigDecimal.ZERO, 1, percentualDespesa.negate()));

		faturamentoBrutoReport
				.add(new FaturamentoBrutoVO("FATURAMENTO BRUTO", faturamentoBrutoContabil, BigDecimal.ZERO, 1, percentualFaturamentoBruto));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS FINANCEIRAS", despesasFinanceiras.negate(), despesasFinanceiras.negate(), 1,
				percentualDespesasFinanceiras.negate()));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO LIQUIDO", faturamentoBrutoContabil.subtract(despesasFinanceiras),
				BigDecimal.ZERO, 1, percentualFaturamentoLiquido));

		return faturamentoBrutoReport;
	}

	private BigDecimal atualizarDespesasPorVeiculo(List<FaturamentoBrutoReportProjection> despesas,
			List<FaturamentoVeiculoProjection> faturamentoVeiculo, BigDecimal valorTotal, List<Long> veiculosIdentificados) {

		BigDecimal valorTotalSemPlaca = despesas.stream().filter(projection -> LongUtil.isNullOrZero(projection.getCodigoVeiculo()))
				.map(FaturamentoBrutoReportProjection::getValorBruto).reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal valorPlacas = faturamentoVeiculo.stream().filter(item -> veiculosIdentificados.contains(item.getCodigoVeiculo()))
				.map(item -> item.getValorBruto()).reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal valorTotalComPlaca = despesas.stream().filter(projection -> !LongUtil.isNullOrZero(projection.getCodigoVeiculo()))
				.map(FaturamentoBrutoReportProjection::getValorBruto).reduce(BigDecimal.ZERO, BigDecimal::add);

		return valorPlacas.multiply(valorTotalSemPlaca).divide(valorTotal, 2, RoundingMode.HALF_EVEN).add(valorTotalComPlaca);
	}

	private List<FaturamentoBrutoVO> filtrarFaturamentoContabilBasicoGeral(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {
		List<FaturamentoBrutoVO> faturamentoBrutoReport = new ArrayList<>();

		BigDecimal valorBruto = IfNull.get(faturamentoDAO.filtrarFaturamentoBrutoBasico(pesquisaFaturamentoBrutoVO), BigDecimal.ZERO);

		BigDecimal despesasFinanceiras = IfNull.get(contasPagarPagamentoDAO.filtrarDespesasFinanceirasGeral(pesquisaFaturamentoBrutoVO),
				BigDecimal.ZERO);

		BigDecimal despesasContabeis = IfNull.get(faturamentoEntradaDAO.filtrarFaturamentoBrutoEntradasBasico(pesquisaFaturamentoBrutoVO),
				BigDecimal.ZERO);

		BigDecimal faturamentoBrutoContabil = valorBruto.subtract(despesasContabeis);
		BigDecimal percentualDespesa = despesasContabeis.divide(valorBruto, 4, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100d)).setScale(2,
				RoundingMode.HALF_EVEN);
		BigDecimal percentualFaturamentoBruto = faturamentoBrutoContabil.divide(valorBruto, 4, RoundingMode.HALF_EVEN)
				.multiply(BigDecimal.valueOf(100d)).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal percentualDespesasFinanceiras = despesasFinanceiras.divide(valorBruto, 4, RoundingMode.HALF_EVEN)
				.multiply(BigDecimal.valueOf(100d)).setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal percentualFaturamentoLiquido = faturamentoBrutoContabil.subtract(despesasFinanceiras).divide(valorBruto, 4, RoundingMode.HALF_EVEN)
				.multiply(BigDecimal.valueOf(100d)).setScale(2, RoundingMode.HALF_EVEN);

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("RECEITA BRUTA", valorBruto, valorBruto, 1, BigDecimal.valueOf(100d)));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS", despesasContabeis.negate(), BigDecimal.ZERO, 1, percentualDespesa.negate()));

		faturamentoBrutoReport
				.add(new FaturamentoBrutoVO("FATURAMENTO BRUTO", faturamentoBrutoContabil, BigDecimal.ZERO, 1, percentualFaturamentoBruto));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS FINANCEIRAS", despesasFinanceiras.negate(), despesasFinanceiras.negate(), 1,
				percentualDespesasFinanceiras.negate()));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO LIQUIDO", faturamentoBrutoContabil.subtract(despesasFinanceiras),
				BigDecimal.ZERO, 1, percentualFaturamentoLiquido));

		return faturamentoBrutoReport;
	}

	public List<FaturamentoBrutoMensalVO> filtrarFaturamentoContabilBasicoMensal(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		List<FaturamentoBrutoMensalVO> faturamentoBrutoMensalReport = new ArrayList<>();

		List<ValorBrutoMensalVO> valorBrutoMensals = faturamentoDAO.filtrarFaturamentoBrutoBasicoMensal(pesquisaFaturamentoBrutoVO);

		valorBrutoMensals.forEach(valorMensal -> faturamentoBrutoMensalReport
				.add(new FaturamentoBrutoMensalVO(valorMensal.getMesReferencia(), 1, "RECEITA BRUTA", valorMensal.getValor(), 1)));

		List<ValorBrutoMensalVO> despesasFinanceiras = contasPagarPagamentoDAO.filtrarDespesasFinanceirasGeralMensal(pesquisaFaturamentoBrutoVO);
		List<ValorBrutoMensalVO> despesasContabeis;

		if (!pesquisaFaturamentoBrutoVO.getCodigoVeiculos().isEmpty()) {

			despesasContabeis = new ArrayList<>();

			Map<Integer, List<ValorBrutoMensalVO>> despesasContabeisGeralMensal = faturamentoEntradaDAO
					.filtrarFaturamentoBrutoEntradasBasicoMensalVeiculo(pesquisaFaturamentoBrutoVO).stream()
					.collect(Collectors.groupingBy(ValorBrutoMensalVO::getMesReferencia));

			despesasContabeisGeralMensal.forEach((mes, valorDespesas) -> {

				List<ValorBrutoMensalVO> valoresComPlaca = valorDespesas.stream().filter(item -> !item.getCodigoVeiculo().equals(0L))
						.collect(Collectors.toList());

				List<ValorBrutoMensalVO> valoresSemPlaca = valorDespesas.stream().filter(item -> item.getCodigoVeiculo().equals(0L))
						.collect(Collectors.toList());

				if (!valoresSemPlaca.isEmpty()) {

					BigDecimal valorFaturamentoMensal = faturamentoDAO.obterFaturamentoBrutoMensal(mes);

					pesquisaFaturamentoBrutoVO.getCodigoVeiculos().forEach(veiculo -> {

						BigDecimal valorFaturamentoveiculo = faturamentoDAO.buscarFaturamentoVeiculoMensal(mes, veiculo);

						valoresSemPlaca.forEach(faturamentoSemPlaca -> {

							BigDecimal valorPlaca = valorFaturamentoveiculo.multiply(faturamentoSemPlaca.getValor()).divide(valorFaturamentoMensal, 8,
									RoundingMode.HALF_EVEN);

							valoresComPlaca.add(new ValorBrutoMensalVO(mes, valorPlaca, veiculo));
						});
					});
				}

				valoresComPlaca.stream().collect(Collectors.groupingBy(ValorBrutoMensalVO::getMesReferencia)).forEach((mesReferencia, valores) -> {

					despesasContabeis.add(new ValorBrutoMensalVO(mes, valores.stream().map(ValorBrutoMensalVO::getValor)
							.reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_EVEN), 0L));

				});
			});

		} else {

			despesasContabeis = faturamentoEntradaDAO.filtrarFaturamentoBrutoEntradasBasicoMensal(pesquisaFaturamentoBrutoVO);
		}

		despesasContabeis.forEach(despesaMensal -> faturamentoBrutoMensalReport
				.add(new FaturamentoBrutoMensalVO(despesaMensal.getMesReferencia(), 2, "DESPESAS", despesaMensal.getValor().negate(), 1)));

		despesasFinanceiras.forEach(valorMensal -> faturamentoBrutoMensalReport
				.add(new FaturamentoBrutoMensalVO(valorMensal.getMesReferencia(), 4, "DESPESAS FINANCEIRAS", valorMensal.getValor(), 1)));

		for (int mes = 1; mes <= 12; mes++) {

			criarTotalizadores(valorBrutoMensals, despesasContabeis, despesasFinanceiras, mes, faturamentoBrutoMensalReport);
		}

		return faturamentoBrutoMensalReport;
	}

	private void criarTotalizadores(List<ValorBrutoMensalVO> bruto, List<ValorBrutoMensalVO> despesas, List<ValorBrutoMensalVO> financeiras, int mes,
			List<FaturamentoBrutoMensalVO> report) {

		Optional<ValorBrutoMensalVO> faturamentoMes = bruto.stream().filter(item -> item.getMesReferencia() == mes).findFirst();
		Optional<ValorBrutoMensalVO> despesasMes = despesas.stream().filter(item -> item.getMesReferencia() == mes).findFirst();
		Optional<ValorBrutoMensalVO> financeirasMes = financeiras.stream().filter(item -> item.getMesReferencia() == mes).findFirst();

		if (faturamentoMes.isPresent() || despesasMes.isPresent()) {

			BigDecimal valor = getValorMes(faturamentoMes).subtract(getValorMes(despesasMes));

			report.add(new FaturamentoBrutoMensalVO(mes, 3, "FATURAMENTO BRUTO", valor, 1));
		}

		if (faturamentoMes.isPresent() || despesasMes.isPresent() || financeirasMes.isPresent()) {

			BigDecimal valor = getValorMes(faturamentoMes).subtract(getValorMes(despesasMes)).subtract(getValorMes(financeirasMes));

			report.add(new FaturamentoBrutoMensalVO(mes, 5, "FATURAMENTO LIQUIDO", valor, 1));
		}
	}

	private BigDecimal getValorMes(Optional<ValorBrutoMensalVO> optional) {

		if (!optional.isPresent()) {

			return BigDecimal.ZERO;
		}

		return optional.get().getValor();
	}
}
