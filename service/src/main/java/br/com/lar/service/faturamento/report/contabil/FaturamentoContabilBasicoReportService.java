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

		BigDecimal valorContabil = atualizarDespesasPorVeiculo(despesasContabeis, faturamentoVeiculoProjections, valorTotal);

		BigDecimal faturamentoBrutoContabil = valorBruto.subtract(valorContabil);

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("RECEITA BRUTA", valorBruto, valorBruto, 1));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS", valorContabil.negate(), BigDecimal.ZERO, 1));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO BRUTO", faturamentoBrutoContabil, BigDecimal.ZERO, 1));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS FINANCEIRAS", despesasFinanceiras.negate(), despesasFinanceiras.negate(), 1));

		faturamentoBrutoReport
				.add(new FaturamentoBrutoVO("FATURAMENTO LIQUIDO", faturamentoBrutoContabil.subtract(despesasFinanceiras), BigDecimal.ZERO, 1));

		return faturamentoBrutoReport;
	}

	private BigDecimal atualizarDespesasPorVeiculo(List<FaturamentoBrutoReportProjection> despesas,
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

		return despesas.stream().map(FaturamentoBrutoReportProjection::getValorBruto).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private List<FaturamentoBrutoVO> filtrarFaturamentoContabilBasicoGeral(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {
		List<FaturamentoBrutoVO> faturamentoBrutoReport = new ArrayList<>();

		BigDecimal valorBruto = IfNull.get(faturamentoDAO.filtrarFaturamentoBrutoBasico(pesquisaFaturamentoBrutoVO), BigDecimal.ZERO);

		BigDecimal despesasFinanceiras = IfNull.get(contasPagarPagamentoDAO.filtrarDespesasFinanceirasGeral(pesquisaFaturamentoBrutoVO),
				BigDecimal.ZERO);

		BigDecimal despesasContabeis = IfNull.get(faturamentoEntradaDAO.filtrarFaturamentoBrutoEntradasBasico(pesquisaFaturamentoBrutoVO),
				BigDecimal.ZERO);

		BigDecimal faturamentoBrutoContabil = valorBruto.subtract(despesasContabeis);

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("RECEITA BRUTA", valorBruto, valorBruto, 1));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS", despesasContabeis.negate(), BigDecimal.ZERO, 1));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO BRUTO", faturamentoBrutoContabil, BigDecimal.ZERO, 1));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS FINANCEIRAS", despesasFinanceiras.negate(), despesasFinanceiras.negate(), 1));

		faturamentoBrutoReport
				.add(new FaturamentoBrutoVO("FATURAMENTO LIQUIDO", faturamentoBrutoContabil.subtract(despesasFinanceiras), BigDecimal.ZERO, 1));

		return faturamentoBrutoReport;
	}

	public List<FaturamentoBrutoMensalVO> filtrarFaturamentoContabilBasicoMensal(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		List<FaturamentoBrutoMensalVO> faturamentoBrutoMensalReport = new ArrayList<>();

		List<ValorBrutoMensalVO> valorBrutoMensals = faturamentoDAO.filtrarFaturamentoBrutoBasicoMensal(pesquisaFaturamentoBrutoVO);

		valorBrutoMensals.forEach(valorMensal -> faturamentoBrutoMensalReport
				.add(new FaturamentoBrutoMensalVO(valorMensal.getMesReferencia(), 1, "RECEITA BRUTA", valorMensal.getValor(), 1)));

		List<ValorBrutoMensalVO> despesasFinanceiras = contasPagarPagamentoDAO.filtrarDespesasFinanceirasGeralMensal(pesquisaFaturamentoBrutoVO);
		List<ValorBrutoMensalVO> despesasContabeis = faturamentoEntradaDAO.filtrarFaturamentoBrutoEntradasBasicoMensal(pesquisaFaturamentoBrutoVO);

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
