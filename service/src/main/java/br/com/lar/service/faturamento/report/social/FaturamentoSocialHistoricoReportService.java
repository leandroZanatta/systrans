package br.com.lar.service.faturamento.report.social;

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
import br.com.systrans.util.vo.FaturamentoBrutoMensalVO;
import br.com.systrans.util.vo.FaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;

public class FaturamentoSocialHistoricoReportService {

	private FaturamentoCabecalhoDAO faturamentoDAO = new FaturamentoCabecalhoDAO();
	private ContasPagarPagamentoDAO contasPagarPagamentoDAO = new ContasPagarPagamentoDAO();
	private FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO = new FaturamentoEntradaCabecalhoDAO();

	public List<FaturamentoBrutoVO> filtrarFaturamentoSocialHistorico(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		List<FaturamentoBrutoVO> faturamentoBrutoReport = new ArrayList<>();

		List<FaturamentoBrutoReportProjection> faturamentoBrutoReportProjections = faturamentoDAO
				.filtrarFaturamentoBrutoHistorico(pesquisaFaturamentoBrutoVO);

		List<DespesasFinanceirasProjection> despesasFinanceiras = contasPagarPagamentoDAO.filtrarDespesasFinanceiras(pesquisaFaturamentoBrutoVO);

		List<FaturamentoBrutoReportProjection> despesasSociais = faturamentoEntradaDAO
				.filtrarFaturamentoBrutoCentroCustoHistorico(pesquisaFaturamentoBrutoVO);

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

		mapaCreditos.forEach((key, value) -> faturamentoBrutoReport
				.add(new FaturamentoBrutoVO(key, BigDecimal.ZERO, getValorTotal(value, FaturamentoBrutoReportProjection::getValorBruto), 2)));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS", BigDecimal.ZERO, valorDespesa.negate(), 1));

		mapaDebitos.forEach((key, value) -> faturamentoBrutoReport.add(
				new FaturamentoBrutoVO(key, BigDecimal.ZERO, getValorTotal(value, FaturamentoBrutoReportProjection::getValorBruto).negate(), 2)));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO BRUTO", BigDecimal.ZERO, faturamentoBruto, 1));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS FINANCEIRAS", BigDecimal.ZERO, valorDespesasFinanceiras.negate(), 1));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("ACRÉSCIMOS", BigDecimal.ZERO, valorAcrescimos.negate(), 2));
		faturamentoBrutoReport.add(new FaturamentoBrutoVO("JUROS", BigDecimal.ZERO, valorJuros.negate(), 2));
		faturamentoBrutoReport
				.add(new FaturamentoBrutoVO("FATURAMENTO LIQUIDO", BigDecimal.ZERO, faturamentoBruto.subtract(valorDespesasFinanceiras), 1));

		return faturamentoBrutoReport;

	}

	private BigDecimal getValorTotal(List<FaturamentoBrutoReportProjection> lista, Function<FaturamentoBrutoReportProjection, BigDecimal> funcao) {

		return lista.stream().map(funcao).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public List<FaturamentoBrutoMensalVO> filtrarFaturamentoSocialHistoricoMensal(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {
		// TODO Auto-generated method stub
		return null;
	}
}
