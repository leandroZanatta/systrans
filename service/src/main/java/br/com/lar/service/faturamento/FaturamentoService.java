package br.com.lar.service.faturamento;

import java.math.BigDecimal;
import java.util.List;

import br.com.lar.repository.dao.FaturamentoCabecalhoDAO;
import br.com.lar.repository.dao.FaturamentoEntradaCabecalhoDAO;
import br.com.lar.repository.projection.FaturamentoBrutoReportProjection;
import br.com.systrans.util.vo.FaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;

public class FaturamentoService {

	private FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO = new FaturamentoEntradaCabecalhoDAO();
	private FaturamentoCabecalhoDAO faturamentoDAO = new FaturamentoCabecalhoDAO();

	public FaturamentoBrutoVO filtrarFaturamentoBruto(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		List<FaturamentoBrutoReportProjection> faturamentoBrutoSaidaReportProjections = faturamentoDAO
				.filtrarFaturamentoBruto(pesquisaFaturamentoBrutoVO);

		List<FaturamentoBrutoReportProjection> faturamentoBrutoEntradaReportProjections = faturamentoEntradaDAO
				.filtrarFaturamentoBruto(pesquisaFaturamentoBrutoVO);

		FaturamentoBrutoVO faturamentoBrutoVO = new FaturamentoBrutoVO();
		faturamentoBrutoVO.setTotalReceitas(faturamentoBrutoSaidaReportProjections.stream().map(FaturamentoBrutoReportProjection::getValorBruto)
				.reduce(BigDecimal.ZERO, BigDecimal::add));
		faturamentoBrutoVO.setTotalDespesas(faturamentoBrutoEntradaReportProjections.stream().map(FaturamentoBrutoReportProjection::getValorBruto)
				.reduce(BigDecimal.ZERO, BigDecimal::add));

		faturamentoBrutoVO.setValorFaturamento(faturamentoBrutoVO.getTotalReceitas().subtract(faturamentoBrutoVO.getTotalDespesas()));

		return faturamentoBrutoVO;
	}
}
