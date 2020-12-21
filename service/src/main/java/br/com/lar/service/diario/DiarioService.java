package br.com.lar.service.diario;

import java.util.Date;
import java.util.List;

import br.com.lar.repository.dao.DiarioCabecalhoDAO;
import br.com.lar.repository.projection.DiarioReportProjection;
import br.com.lar.repository.projection.ResumoCaixaMovimentoProjection;

public class DiarioService {

	private DiarioCabecalhoDAO diarioCabecalhoDAO = new DiarioCabecalhoDAO();

	public List<DiarioReportProjection> buscarDiarioPeriodo(Date dataInicial, Date dataFinal) {

		return diarioCabecalhoDAO.buscarDiarioPeriodo(dataInicial, dataFinal);
	}

	public List<ResumoCaixaMovimentoProjection> buscarResumoCaixa(Long codigoCaixaCabecalho) {

		return diarioCabecalhoDAO.buscarResumoCaixa(codigoCaixaCabecalho);
	}

}
