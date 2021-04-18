package br.com.lar.service.faturamento.report.consolidado;

import java.util.List;

import br.com.systrans.util.vo.FaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;

public class FaturamentoConsolidadoReportService {

	private FaturamentoConsolidadoBasicoReportService faturamentoConsolidadoBasicoReportService = new FaturamentoConsolidadoBasicoReportService();
	private FaturamentoConsolidadoHistoricoReportService faturamentoConsolidadoHistoricoReportService = new FaturamentoConsolidadoHistoricoReportService();
	private FaturamentoConsolidadoDetalhadoReportService faturamentoConsolidadoDetalhadoReportService = new FaturamentoConsolidadoDetalhadoReportService();

	public List<FaturamentoBrutoVO> filtrarFaturamentoConsolidado(
			PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		if (pesquisaFaturamentoBrutoVO.getCodigoRelatorio() == 0) {

			return faturamentoConsolidadoBasicoReportService
					.filtrarFaturamentoContabilBasico(pesquisaFaturamentoBrutoVO);
		}

		if (pesquisaFaturamentoBrutoVO.getCodigoRelatorio() == 1) {

			return faturamentoConsolidadoHistoricoReportService.filtrarFaturamentoHistorico(pesquisaFaturamentoBrutoVO);
		}

		return faturamentoConsolidadoDetalhadoReportService.filtrarFaturamentoDetalhado(pesquisaFaturamentoBrutoVO);
	}
}
