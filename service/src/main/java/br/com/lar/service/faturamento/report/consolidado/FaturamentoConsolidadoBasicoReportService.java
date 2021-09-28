package br.com.lar.service.faturamento.report.consolidado;

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
import br.com.systrans.util.vo.FaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;

public class FaturamentoConsolidadoBasicoReportService {

	private FaturamentoCabecalhoDAO faturamentoDAO = new FaturamentoCabecalhoDAO();
	private ContasPagarPagamentoDAO contasPagarPagamentoDAO = new ContasPagarPagamentoDAO();
	private FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO = new FaturamentoEntradaCabecalhoDAO();

	public List<FaturamentoBrutoVO> filtrarFaturamentoContabilBasico(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		if (ListUtil.isNullOrEmpty(pesquisaFaturamentoBrutoVO.getCodigoVeiculos())) {

			return filtrarFaturamentoConsolidadoBasicoGeral(pesquisaFaturamentoBrutoVO);
		}

		return filtrarFaturamentoConsolidadoBasicoVeiculo(pesquisaFaturamentoBrutoVO);
	}

	private List<FaturamentoBrutoVO> filtrarFaturamentoConsolidadoBasicoVeiculo(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {
		List<FaturamentoBrutoVO> faturamentoBrutoReport = new ArrayList<>();

		List<FaturamentoVeiculoProjection> faturamentoVeiculoProjections = faturamentoDAO
				.buscarFaturamentoVeiculo(pesquisaFaturamentoBrutoVO.getDataMovimentoInicial(), pesquisaFaturamentoBrutoVO.getDataMovimentoFinal());

		BigDecimal valorTotal = faturamentoVeiculoProjections.stream().map(FaturamentoVeiculoProjection::getValorBruto).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		BigDecimal valorBruto = IfNull.get(faturamentoDAO.filtrarFaturamentoBrutoBasico(pesquisaFaturamentoBrutoVO), BigDecimal.ZERO);

		BigDecimal despesasFinanceiras = IfNull.get(contasPagarPagamentoDAO.filtrarDespesasFinanceirasGeral(pesquisaFaturamentoBrutoVO),
				BigDecimal.ZERO);

		List<FaturamentoBrutoReportProjection> despesasContabeis = faturamentoEntradaDAO
				.filtrarFaturamentoBrutoEntradasHistorico(pesquisaFaturamentoBrutoVO);

		List<FaturamentoBrutoReportProjection> despesasSociais = faturamentoEntradaDAO
				.filtrarFaturamentoBrutoCentroCustoHistorico(pesquisaFaturamentoBrutoVO);

		BigDecimal valorContabil = atualizarDespesasPorVeiculo(despesasContabeis, faturamentoVeiculoProjections, valorTotal);
		BigDecimal valorSocial = atualizarDespesasPorVeiculo(despesasSociais, faturamentoVeiculoProjections, valorTotal);

		BigDecimal faturamentoBrutoContabil = valorBruto.subtract(valorContabil);
		BigDecimal faturamentoBrutoSocial = valorBruto.subtract(valorSocial);

		FaturamentoBrutoVO receitas = new FaturamentoBrutoVO("RECEITA BRUTA", valorBruto, valorBruto, 1, null);
		FaturamentoBrutoVO despesas = new FaturamentoBrutoVO("DESPESAS", valorContabil.negate(), valorSocial.negate(), 1, receitas);
		FaturamentoBrutoVO bruto = new FaturamentoBrutoVO("FATURAMENTO BRUTO", faturamentoBrutoContabil, faturamentoBrutoSocial, 1, receitas);
		FaturamentoBrutoVO financeiras = new FaturamentoBrutoVO("DESPESAS FINANCEIRAS", despesasFinanceiras.negate(), despesasFinanceiras.negate(), 1,
				pesquisaFaturamentoBrutoVO.getTipoPercentual() == 0 ? bruto : receitas);
		FaturamentoBrutoVO liquido = new FaturamentoBrutoVO("FATURAMENTO LIQUIDO", faturamentoBrutoContabil.subtract(despesasFinanceiras),
				faturamentoBrutoSocial.subtract(despesasFinanceiras), 1, pesquisaFaturamentoBrutoVO.getTipoPercentual() == 0 ? bruto : receitas);

		faturamentoBrutoReport.add(receitas);
		faturamentoBrutoReport.add(despesas);
		faturamentoBrutoReport.add(bruto);
		faturamentoBrutoReport.add(financeiras);
		faturamentoBrutoReport.add(liquido);

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

	private List<FaturamentoBrutoVO> filtrarFaturamentoConsolidadoBasicoGeral(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {
		List<FaturamentoBrutoVO> faturamentoBrutoReport = new ArrayList<>();

		BigDecimal valorBruto = IfNull.get(faturamentoDAO.filtrarFaturamentoBrutoBasico(pesquisaFaturamentoBrutoVO), BigDecimal.ZERO);

		BigDecimal despesasFinanceiras = IfNull.get(contasPagarPagamentoDAO.filtrarDespesasFinanceirasGeral(pesquisaFaturamentoBrutoVO),
				BigDecimal.ZERO);

		BigDecimal despesasContabeis = IfNull.get(faturamentoEntradaDAO.filtrarFaturamentoBrutoEntradasBasico(pesquisaFaturamentoBrutoVO),
				BigDecimal.ZERO);

		BigDecimal despesasSociais = IfNull.get(faturamentoEntradaDAO.filtrarFaturamentoBrutoCentroCustoGeral(pesquisaFaturamentoBrutoVO),
				BigDecimal.ZERO);

		BigDecimal faturamentoBrutoContabil = valorBruto.subtract(despesasContabeis);
		BigDecimal faturamentoBrutoSocial = valorBruto.subtract(despesasSociais);

		FaturamentoBrutoVO receitas = new FaturamentoBrutoVO("RECEITA BRUTA", valorBruto, valorBruto, 1, null);
		FaturamentoBrutoVO despesas = new FaturamentoBrutoVO("DESPESAS", despesasContabeis.negate(), despesasSociais.negate(), 1, receitas);
		FaturamentoBrutoVO bruto = new FaturamentoBrutoVO("FATURAMENTO BRUTO", faturamentoBrutoContabil, faturamentoBrutoSocial, 1, receitas);
		FaturamentoBrutoVO financeiras = new FaturamentoBrutoVO("DESPESAS FINANCEIRAS", despesasFinanceiras.negate(), despesasFinanceiras.negate(), 1,
				pesquisaFaturamentoBrutoVO.getTipoPercentual() == 0 ? bruto : receitas);
		FaturamentoBrutoVO liquido = new FaturamentoBrutoVO("FATURAMENTO LIQUIDO", faturamentoBrutoContabil.subtract(despesasFinanceiras),
				faturamentoBrutoSocial.subtract(despesasFinanceiras), 1, pesquisaFaturamentoBrutoVO.getTipoPercentual() == 0 ? bruto : receitas);

		faturamentoBrutoReport.add(receitas);
		faturamentoBrutoReport.add(despesas);
		faturamentoBrutoReport.add(bruto);
		faturamentoBrutoReport.add(financeiras);
		faturamentoBrutoReport.add(liquido);

		return faturamentoBrutoReport;
	}
}
