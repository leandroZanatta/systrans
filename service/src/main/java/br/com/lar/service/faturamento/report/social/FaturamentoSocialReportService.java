package br.com.lar.service.faturamento.report.social;

import java.util.List;

import br.com.systrans.util.vo.FaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;

public class FaturamentoSocialReportService {

	private FaturamentoSocialBasicoReportService faturamentoSocialBasicoReportService = new FaturamentoSocialBasicoReportService();
	private FaturamentoSocialHistoricoReportService faturamentoSocialHistoricoReportService = new FaturamentoSocialHistoricoReportService();
	private FaturamentoSocialDetalhadoReportService faturamentoSocialDetalhadoReportService = new FaturamentoSocialDetalhadoReportService();

	public List<FaturamentoBrutoVO> filtrarFaturamentoSocial(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		if (pesquisaFaturamentoBrutoVO.getCodigoRelatorio() == 0) {

			return faturamentoSocialBasicoReportService.filtrarFaturamentoSocialGeral(pesquisaFaturamentoBrutoVO);
		}

		if (pesquisaFaturamentoBrutoVO.getCodigoRelatorio() == 1) {

			return faturamentoSocialHistoricoReportService
					.filtrarFaturamentoSocialHistorico(pesquisaFaturamentoBrutoVO);
		}

		return faturamentoSocialDetalhadoReportService.filtrarFaturamentoSocialDetalhado(pesquisaFaturamentoBrutoVO);
	}
}
