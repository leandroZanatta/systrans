package br.com.lar.service.faturamento.report.contabil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.lar.repository.dao.ContasPagarPagamentoDAO;
import br.com.lar.repository.dao.FaturamentoCabecalhoDAO;
import br.com.lar.repository.dao.FaturamentoEntradaCabecalhoDAO;
import br.com.sysdesc.util.classes.IfNull;
import br.com.systrans.util.vo.FaturamentoBrutoMensalVO;
import br.com.systrans.util.vo.FaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;
import br.com.systrans.util.vo.ValorBrutoMensalVO;

public class FaturamentoContabilBasicoReportService {

	private FaturamentoCabecalhoDAO faturamentoDAO = new FaturamentoCabecalhoDAO();
	private ContasPagarPagamentoDAO contasPagarPagamentoDAO = new ContasPagarPagamentoDAO();
	private FaturamentoEntradaCabecalhoDAO faturamentoEntradaDAO = new FaturamentoEntradaCabecalhoDAO();

	public List<FaturamentoBrutoVO> filtrarFaturamentoContabilBasico(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		List<FaturamentoBrutoVO> faturamentoBrutoReport = new ArrayList<>();

		BigDecimal valorBruto = IfNull.get(faturamentoDAO.filtrarFaturamentoBrutoBasico(pesquisaFaturamentoBrutoVO), BigDecimal.ZERO);

		BigDecimal despesasFinanceiras = IfNull.get(contasPagarPagamentoDAO.filtrarDespesasFinanceirasGeral(pesquisaFaturamentoBrutoVO),
				BigDecimal.ZERO);

		BigDecimal despesasContabeis = IfNull.get(faturamentoEntradaDAO.filtrarFaturamentoBrutoEntradasBasico(pesquisaFaturamentoBrutoVO),
				BigDecimal.ZERO);

		BigDecimal faturamentoBrutoContabil = valorBruto.subtract(despesasContabeis);

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("RECEITA BRUTA", valorBruto, valorBruto, 1));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS", despesasContabeis.negate(), BigDecimal.ZERO, 1));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("FATURAMENTO BRUTO", faturamentoBrutoContabil, BigDecimal.ZERO, 1));

		faturamentoBrutoReport.add(new FaturamentoBrutoVO("DESPESAS FINANCEIRAS", despesasFinanceiras.negate(), despesasFinanceiras.negate(), 1));

		faturamentoBrutoReport
				.add(new FaturamentoBrutoVO("FATURAMENTO LIQUIDO", faturamentoBrutoContabil.subtract(despesasFinanceiras), BigDecimal.ZERO, 1));

		return faturamentoBrutoReport;
	}

	public List<FaturamentoBrutoMensalVO> filtrarFaturamentoContabilBasicoMensal(PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO) {

		List<FaturamentoBrutoMensalVO> faturamentoBrutoMensalReport = new ArrayList<>();

		List<ValorBrutoMensalVO> valorBrutoMensals = faturamentoDAO.filtrarFaturamentoBrutoBasicoMensal(pesquisaFaturamentoBrutoVO);

		valorBrutoMensals.forEach(valorMensal -> faturamentoBrutoMensalReport
				.add(new FaturamentoBrutoMensalVO(valorMensal.getMesReferencia(), 1, "RECEITA BRUTA", valorMensal.getValor(), 1)));

		List<ValorBrutoMensalVO> despesasFinanceiras = contasPagarPagamentoDAO.filtrarDespesasFinanceirasGeralMensal(pesquisaFaturamentoBrutoVO);
		List<ValorBrutoMensalVO> despesasContabeis = faturamentoEntradaDAO.filtrarFaturamentoBrutoEntradasBasicoMensal(pesquisaFaturamentoBrutoVO);

		despesasContabeis.forEach(despesaMensal -> faturamentoBrutoMensalReport
				.add(new FaturamentoBrutoMensalVO(despesaMensal.getMesReferencia(), 2, "DESPESAS", despesaMensal.getValor().negate(), 1)));

		despesasFinanceiras.forEach(valorMensal -> faturamentoBrutoMensalReport
				.add(new FaturamentoBrutoMensalVO(valorMensal.getMesReferencia(), 4, "DESPESAS FINANCEIRAS", valorMensal.getValor(), 1)));

		for (int mes = 1; mes <= 12; mes++) {

			criarTotalizadores(valorBrutoMensals, despesasContabeis, despesasFinanceiras, mes, faturamentoBrutoMensalReport);
		}

		return faturamentoBrutoMensalReport;
	}

	private void criarTotalizadores(List<ValorBrutoMensalVO> bruto, List<ValorBrutoMensalVO> despesas, List<ValorBrutoMensalVO> financeiras, int mes,
			List<FaturamentoBrutoMensalVO> report) {

		Optional<ValorBrutoMensalVO> faturamentoMes = bruto.stream().filter(item -> item.getMesReferencia() == mes).findFirst();
		Optional<ValorBrutoMensalVO> despesasMes = despesas.stream().filter(item -> item.getMesReferencia() == mes).findFirst();
		Optional<ValorBrutoMensalVO> financeirasMes = financeiras.stream().filter(item -> item.getMesReferencia() == mes).findFirst();

		if (faturamentoMes.isPresent() || despesasMes.isPresent()) {

			BigDecimal valor = getValorMes(faturamentoMes).subtract(getValorMes(despesasMes));

			report.add(new FaturamentoBrutoMensalVO(mes, 3, "FATURAMENTO BRUTO", valor, 1));
		}

		if (faturamentoMes.isPresent() || despesasMes.isPresent() || financeirasMes.isPresent()) {

			BigDecimal valor = getValorMes(faturamentoMes).subtract(getValorMes(despesasMes)).subtract(getValorMes(financeirasMes));

			report.add(new FaturamentoBrutoMensalVO(mes, 5, "FATURAMENTO LIQUIDO", valor, 1));
		}
	}

	private BigDecimal getValorMes(Optional<ValorBrutoMensalVO> optional) {

		if (!optional.isPresent()) {

			return BigDecimal.ZERO;
		}

		return optional.get().getValor();
	}
}
