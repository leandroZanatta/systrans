package br.com.lar.service.faturamento.report.contabil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import br.com.lar.repository.dao.ContasPagarPagamentoDAO;
import br.com.lar.repository.dao.FaturamentoCabecalhoDAO;
import br.com.lar.repository.dao.FaturamentoEntradaCabecalhoDAO;
import br.com.lar.repository.projection.DespesasFinanceirasProjection;
import br.com.lar.repository.projection.FaturamentoBrutoReportProjection;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.systrans.util.vo.FaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;

public class FaturamentoContabilHistoricoReportService {

	private FaturamentoCabecalhoDAO faturamentoDAO = new FaturamentoCabecalhoDAO();
	private ContasPagarPagamentoDAO contasPagarPagamentoDAO = new ContasPagarPagamentoDAO();
	private FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO = new FaturamentoEntradaCabecalhoDAO();

	public List<FaturamentoBrutoVO> filtrarFaturamentoContabilHistorico(
			PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		List<FaturamentoBrutoVO> faturamentoBrutoReport = new ArrayList<>();

		List<FaturamentoBrutoReportProjection> faturamentoBrutoReportProjections = faturamentoDAO
				.filtrarFaturamentoBrutoHistorico(pesquisaFaturamentoBrutoVO);

		List<DespesasFinanceirasProjection> despesasFinanceiras = contasPagarPagamentoDAO
				.filtrarDespesasFinanceiras(pesquisaFaturamentoBrutoVO);

		List<FaturamentoBrutoReportProjection> despesasContabeis = faturamentoEntradaDAO
				.filtrarFaturamentoBrutoEntradasHistorico(pesquisaFaturamentoBrutoVO);

		BigDecimal valorReceita = getValorTotal(faturamentoBrutoReportProjections,
				FaturamentoBrutoReportProjection::getValorBruto);

		BigDecimal valorDespesa = getValorTotal(despesasContabeis, FaturamentoBrutoReportProjection::getValorBruto);
		BigDecimal faturamentoBruto = valorReceita.subtract(valorDespesa);

		BigDecimal valorDespesasFinanceiras = despesasFinanceiras.stream()
				.map(despesas -> despesas.getValorAcrescimo().add(despesas.getValorJuros()))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Map<String, List<FaturamentoBrutoReportProjection>> mapaCreditos = faturamentoBrutoReportProjections.stream()
				.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getHistorico));

		Map<String, List<FaturamentoBrutoReportProjection>> mapaDebitos = despesasContabeis.stream()
				.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getHistorico));

		List<DespesasFinanceirasProjection> despesasAcrescimos = despesasFinanceiras.stream()
				.filter(despesas -> BigDecimalUtil.diferente(despesas.getValorAcrescimo(), BigDecimal.ZERO))
				.collect(Collectors.toList());

		List<DespesasFinanceirasProjection> despesasJuros = despesasFinanceiras.stream()
				.filter(despesas -> BigDecimalUtil.diferente(despesas.getValorJuros(), BigDecimal.ZERO))
				.collect(Collectors.toList());

		BigDecimal valorAcrescimos = despesasAcrescimos.stream().map(DespesasFinanceirasProjection::getValorAcrescimo)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal valorJuros = despesasJuros.stream().map(DespesasFinanceirasProjection::getValorJuros)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("RECEITA BRUTA", valorReceita, BigDecimal.ZERO, 1));

		mapaCreditos.forEach((key, value) -> faturamentoBrutoReport.add(new FaturamentoBrutoVO(key,
				getValorTotal(value, FaturamentoBrutoReportProjection::getValorBruto), BigDecimal.ZERO, 2)));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS", valorDespesa.negate(), BigDecimal.ZERO, 1));

		mapaDebitos.forEach((key, value) -> faturamentoBrutoReport.add(new FaturamentoBrutoVO(key,
				getValorTotal(value, FaturamentoBrutoReportProjection::getValorBruto).negate(), BigDecimal.ZERO, 2)));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO BRUTO", faturamentoBruto, BigDecimal.ZERO, 1));
		faturamentoBrutoReport.add(
				new FaturamentoBrutoVO("DESPESAS FINANCEIRAS", valorDespesasFinanceiras.negate(), BigDecimal.ZERO, 1));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("ACRÉSCIMOS", valorAcrescimos.negate(), BigDecimal.ZERO, 2));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("JUROS", valorJuros.negate(), BigDecimal.ZERO, 2));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO LIQUIDO",
				faturamentoBruto.subtract(valorDespesasFinanceiras), BigDecimal.ZERO, 1));

		return faturamentoBrutoReport;

	}

	private BigDecimal getValorTotal(List<FaturamentoBrutoReportProjection> lista,
			Function<FaturamentoBrutoReportProjection, BigDecimal> funcao) {

		return lista.stream().map(funcao).reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
