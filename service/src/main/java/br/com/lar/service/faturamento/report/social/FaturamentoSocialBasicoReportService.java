package br.com.lar.service.faturamento.report.social;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.lar.repository.dao.ContasPagarPagamentoDAO;
import br.com.lar.repository.dao.FaturamentoCabecalhoDAO;
import br.com.lar.repository.dao.FaturamentoEntradaCabecalhoDAO;
import br.com.sysdesc.util.classes.IfNull;
import br.com.systrans.util.vo.FaturamentoBrutoMensalVO;
import br.com.systrans.util.vo.FaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;

public class FaturamentoSocialBasicoReportService {

	private FaturamentoCabecalhoDAO faturamentoDAO = new FaturamentoCabecalhoDAO();
	private ContasPagarPagamentoDAO contasPagarPagamentoDAO = new ContasPagarPagamentoDAO();
	private FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO = new FaturamentoEntradaCabecalhoDAO();

	public List<FaturamentoBrutoVO> filtrarFaturamentoSocialGeral(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		List<FaturamentoBrutoVO> faturamentoBrutoReport = new ArrayList<>();

		BigDecimal valorBruto = IfNull.get(faturamentoDAO.filtrarFaturamentoBrutoBasico(pesquisaFaturamentoBrutoVO), BigDecimal.ZERO);

		BigDecimal despesasFinanceiras = IfNull.get(contasPagarPagamentoDAO.filtrarDespesasFinanceirasGeral(pesquisaFaturamentoBrutoVO),
				BigDecimal.ZERO);

		BigDecimal despesasSociais = IfNull.get(faturamentoEntradaDAO.filtrarFaturamentoBrutoCentroCustoGeral(pesquisaFaturamentoBrutoVO),
				BigDecimal.ZERO);

		BigDecimal faturamentoBrutoSocial = valorBruto.subtract(despesasSociais);

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("RECEITA BRUTA", valorBruto, valorBruto, 1));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS", BigDecimal.ZERO, despesasSociais.negate(), 1));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO BRUTO", BigDecimal.ZERO, faturamentoBrutoSocial, 1));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS FINANCEIRAS", despesasFinanceiras.negate(), despesasFinanceiras.negate(), 1));

		faturamentoBrutoReport
				.add(new FaturamentoBrutoVO("FATURAMENTO LIQUIDO", BigDecimal.ZERO, faturamentoBrutoSocial.subtract(despesasFinanceiras), 1));

		return faturamentoBrutoReport;
	}

	public List<FaturamentoBrutoMensalVO> filtrarFaturamentoSocialGeralMensal(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {
		// TODO Auto-generated method stub
		return null;
	}
}
