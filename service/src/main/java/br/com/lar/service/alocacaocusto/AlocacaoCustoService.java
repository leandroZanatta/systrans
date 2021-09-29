package br.com.lar.service.alocacaocusto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import br.com.lar.repository.dao.AlocacaoCustoDAO;
import br.com.lar.repository.model.AlocacaoCusto;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.systrans.util.vo.FaturamentoBrutoMensalVO;
import br.com.systrans.util.vo.PesquisaCentroCustoVO;

public class AlocacaoCustoService extends AbstractPesquisableServiceImpl<AlocacaoCusto> {

	private AlocacaoCustoDAO alocacaoCustoDAO;
	private Calendar calendar = Calendar.getInstance();

	public AlocacaoCustoService() {
		this(new AlocacaoCustoDAO());
	}

	public AlocacaoCustoService(AlocacaoCustoDAO alocacaoCustoDAO) {
		super(alocacaoCustoDAO, AlocacaoCusto::getIdAlocacaoCusto);

		this.alocacaoCustoDAO = alocacaoCustoDAO;
	}

	public List<FaturamentoBrutoMensalVO> filtrarAlocacaoCusto(PesquisaCentroCustoVO pesquisaCentroCustoVO, Integer tipoAgrupamento) {

		switch (tipoAgrupamento) {
		case 0:
			return realizarAgrupamentoGeral(alocacaoCustoDAO.filtrarAlocacaoCusto(pesquisaCentroCustoVO), 1);
		case 1:
			return realizarAgrupamentoHistorico(alocacaoCustoDAO.filtrarAlocacaoCusto(pesquisaCentroCustoVO));

		default:
			return Collections.emptyList();
		}
	}

	private List<FaturamentoBrutoMensalVO> realizarAgrupamentoHistorico(List<AlocacaoCusto> alocacaoCustos) {

		List<FaturamentoBrutoMensalVO> faturamentoReport = new ArrayList<>();

		Map<String, List<AlocacaoCusto>> custosHistorico = alocacaoCustos.stream().collect(Collectors.groupingBy(this::agruparHistorico));

		Integer ordenacao = 1;

		for (Entry<String, List<AlocacaoCusto>> entry : custosHistorico.entrySet()) {

			Map<Integer, List<AlocacaoCusto>> custosMensais = entry.getValue().stream().collect(Collectors.groupingBy(this::agruparMes));

			for (int i = 1; i <= 12; i++) {

				BigDecimal valorMensal = obterValor(custosMensais, i);

				faturamentoReport.add(new FaturamentoBrutoMensalVO(i, ordenacao, entry.getKey(), valorMensal, 2));
			}

			ordenacao++;
		}

		faturamentoReport.addAll(realizarAgrupamentoGeral(alocacaoCustos, ordenacao));

		return faturamentoReport;
	}

	private List<FaturamentoBrutoMensalVO> realizarAgrupamentoGeral(List<AlocacaoCusto> alocacaoCustos, Integer ordem) {

		List<FaturamentoBrutoMensalVO> faturamentoReport = new ArrayList<>();

		Map<Integer, List<AlocacaoCusto>> custosMensais = alocacaoCustos.stream().collect(Collectors.groupingBy(this::agruparMes));

		for (int i = 1; i <= 12; i++) {

			BigDecimal valorMensal = obterValor(custosMensais, i);

			faturamentoReport.add(new FaturamentoBrutoMensalVO(i, ordem, "Total", valorMensal, 1));
		}

		return faturamentoReport;
	}

	private BigDecimal obterValor(Map<Integer, List<AlocacaoCusto>> custosMensais, int i) {

		if (!custosMensais.containsKey(i - 1)) {
			return BigDecimal.ZERO;
		}

		return custosMensais.get(i - 1).stream().map(AlocacaoCusto::getValorParcela).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private String agruparHistorico(AlocacaoCusto alocacaoCusto) {

		return alocacaoCusto.getHistoricoCusto().getHistorico().getDescricao();
	}

	private Integer agruparMes(AlocacaoCusto alocacaoCusto) {
		calendar.setTime(alocacaoCusto.getPeriodo());

		return calendar.get(Calendar.MONTH);
	}
}
