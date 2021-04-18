package br.com.lar.service.faturamento.report;

import java.util.List;

import br.com.lar.service.faturamento.report.consolidado.FaturamentoConsolidadoReportService;
import br.com.lar.service.faturamento.report.contabil.FaturamentoContabilReportService;
import br.com.lar.service.faturamento.report.social.FaturamentoSocialReportService;
import br.com.systrans.util.vo.FaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;

public class FaturamentoReportService {

	private FaturamentoSocialReportService faturamentoSocialReportService = new FaturamentoSocialReportService();
	private FaturamentoContabilReportService faturamentoContabilReportService = new FaturamentoContabilReportService();
	private FaturamentoConsolidadoReportService faturamentoConsolidadoReportService = new FaturamentoConsolidadoReportService();

	public List<FaturamentoBrutoVO> filtrarFaturamentoBruto(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		if (pesquisaFaturamentoBrutoVO.getTipoBalanco() == 0) {

			return faturamentoContabilReportService.filtrarFaturamentoContabil(pesquisaFaturamentoBrutoVO);
		}

		if (pesquisaFaturamentoBrutoVO.getTipoBalanco() == 1) {

			return faturamentoSocialReportService.filtrarFaturamentoSocial(pesquisaFaturamentoBrutoVO);
		}

		return faturamentoConsolidadoReportService.filtrarFaturamentoConsolidado(pesquisaFaturamentoBrutoVO);
	}
}
