package br.com.lar.service.faturamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import br.com.lar.repository.dao.FaturamentoCabecalhoDAO;
import br.com.lar.repository.dao.FaturamentoEntradaCabecalhoDAO;
import br.com.lar.repository.projection.FaturamentoBrutoReportProjection;
import br.com.systrans.util.vo.FaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;

public class FaturamentoService {

	private FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO = new FaturamentoEntradaCabecalhoDAO();
	private FaturamentoCabecalhoDAO faturamentoDAO = new FaturamentoCabecalhoDAO();

	public List<FaturamentoBrutoVO> filtrarFaturamentoBruto(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		List<FaturamentoBrutoVO> faturamentoBrutoReport = new ArrayList<>();

		List<FaturamentoBrutoReportProjection> faturamentoBrutoSaidaReportProjections = faturamentoDAO
				.filtrarFaturamentoBruto(pesquisaFaturamentoBrutoVO);

		List<FaturamentoBrutoReportProjection> faturamentoBrutoEntradaReportProjections = faturamentoEntradaDAO
				.filtrarFaturamentoBruto(pesquisaFaturamentoBrutoVO);

		BigDecimal valorReceita = getValorTotal(faturamentoBrutoSaidaReportProjections, FaturamentoBrutoReportProjection::getValorBruto);
		BigDecimal valorDespesa = getValorTotal(faturamentoBrutoEntradaReportProjections, FaturamentoBrutoReportProjection::getValorBruto);

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("RECEITA BRUTA", valorReceita, 1));

		Map<String, List<FaturamentoBrutoReportProjection>> mapaCreditos = faturamentoBrutoSaidaReportProjections.stream()
				.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getHistorico));

		mapaCreditos.forEach((key, value) -> {

			faturamentoBrutoReport.add(new FaturamentoBrutoVO(key, getValorTotal(value, FaturamentoBrutoReportProjection::getValorBruto), 2));

			value.stream()
					.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getVeiculo)).forEach((veiculo, lista) -> faturamentoBrutoReport
							.add(new FaturamentoBrutoVO(veiculo, getValorTotal(lista, FaturamentoBrutoReportProjection::getValorBruto), 3)));

		});

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS", valorDespesa.negate(), 1));

		Map<String, List<FaturamentoBrutoReportProjection>> mapaDebitos = faturamentoBrutoEntradaReportProjections.stream()
				.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getHistorico));

		mapaDebitos.forEach((key, value) -> {

			faturamentoBrutoReport
					.add(new FaturamentoBrutoVO(key, getValorTotal(value, FaturamentoBrutoReportProjection::getValorBruto).negate(), 2));

			value.stream()
					.collect(Collectors.groupingBy(FaturamentoBrutoReportProjection::getVeiculo)).forEach((veiculo, lista) -> faturamentoBrutoReport
							.add(new FaturamentoBrutoVO(veiculo, getValorTotal(lista, FaturamentoBrutoReportProjection::getValorBruto).negate(), 3)));

		});

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO BRUTO", valorReceita.subtract(valorDespesa), 1));

		return faturamentoBrutoReport;
	}

	private BigDecimal getValorTotal(List<FaturamentoBrutoReportProjection> lista, Function<FaturamentoBrutoReportProjection, BigDecimal> funcao) {

		return lista.stream().map(funcao).reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
