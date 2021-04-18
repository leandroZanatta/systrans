package br.com.lar.service.faturamento.report.contabil;

import java.util.List;

import br.com.systrans.util.vo.FaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;

public class FaturamentoContabilReportService {

	private FaturamentoContabilBasicoReportService faturamentoContabilBasicoReportService = new FaturamentoContabilBasicoReportService();
	private FaturamentoContabilHistoricoReportService faturamentoContabilHistoricoReportService = new FaturamentoContabilHistoricoReportService();
	private FaturamentoContabilDetalhadoReportService faturamentoContabilDetalhadoReportService = new FaturamentoContabilDetalhadoReportService();

	public List<FaturamentoBrutoVO> filtrarFaturamentoContabil(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		if (pesquisaFaturamentoBrutoVO.getCodigoRelatorio() == 0) {

			return faturamentoContabilBasicoReportService.filtrarFaturamentoContabilBasico(pesquisaFaturamentoBrutoVO);
		}

		if (pesquisaFaturamentoBrutoVO.getCodigoRelatorio() == 1) {

			return faturamentoContabilHistoricoReportService
					.filtrarFaturamentoContabilHistorico(pesquisaFaturamentoBrutoVO);
		}

		return faturamentoContabilDetalhadoReportService
				.filtrarFaturamentoContabilDetalhado(pesquisaFaturamentoBrutoVO);
	}
}