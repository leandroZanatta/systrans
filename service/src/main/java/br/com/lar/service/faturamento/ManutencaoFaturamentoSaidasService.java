package br.com.lar.service.faturamento;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.swing.JOptionPane;

import br.com.lar.repository.dao.FaturamentoCabecalhoDAO;
import br.com.lar.repository.model.CaixaCabecalho;
import br.com.lar.repository.model.CaixaSaldo;
import br.com.lar.repository.model.FaturamentoCabecalho;
import br.com.lar.service.caixa.CaixaCabecalhoService;
import br.com.lar.service.caixa.ResumoCaixaService;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.systrans.util.vo.FechamentoCaixaVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ManutencaoFaturamentoSaidasService extends AbstractPesquisableServiceImpl<FaturamentoCabecalho> {

	private FaturamentoCabecalhoDAO faturamentoCabecalhoDAO;
	private ResumoCaixaService resumoCaixaService = new ResumoCaixaService();
	private CaixaCabecalhoService caixaCabecalhoService = new CaixaCabecalhoService();

	public ManutencaoFaturamentoSaidasService() {
		this(new FaturamentoCabecalhoDAO());
	}

	public ManutencaoFaturamentoSaidasService(FaturamentoCabecalhoDAO faturamentoCabecalhoDAO) {
		super(faturamentoCabecalhoDAO, FaturamentoCabecalho::getIdFaturamentoCabecalho);

		this.faturamentoCabecalhoDAO = faturamentoCabecalhoDAO;
	}

	public void excluirFaturamento(FaturamentoCabecalho faturamentoCabecalho) {

		if (faturamentoCabecalho.getCaixaCabecalho().getDataFechamento() == null
				|| JOptionPane.showConfirmDialog(null, "O CAIXA DA EXCLUSÃO JÁ FOI FECHADO.\nDESEJA RECALCULAR OS SALDOS DE CAIXA?",
						"VERIFICAÇÃO DE CAIXA", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

			EntityManager entityManager = faturamentoCabecalhoDAO.getEntityManager();

			try {
				entityManager.getTransaction().begin();

				entityManager.remove(entityManager.merge(faturamentoCabecalho));

				CaixaCabecalho caixaCabecalho = faturamentoCabecalho.getCaixaCabecalho();

				while (caixaCabecalho != null && caixaCabecalho.getDataFechamento() != null) {

					FechamentoCaixaVO fechamentoCaixaVO = caixaCabecalhoService.buscarResumoFechamentoCaixa(caixaCabecalho);
					CaixaSaldo caixaSaldo = caixaCabecalho.getCaixaSaldo();

					BigDecimal novoSaldo = fechamentoCaixaVO.getSaldoAtual().add(fechamentoCaixaVO.getValorPagamentos())
							.add(fechamentoCaixaVO.getValorDinheiro());

					caixaSaldo.setValorSaldo(fechamentoCaixaVO.getValorPagamentos().add(fechamentoCaixaVO.getValorDinheiro()));
					caixaSaldo.setValorSaldoAcumulado(novoSaldo);

					entityManager.merge(caixaSaldo);

					caixaCabecalho = caixaCabecalhoService.obterProximoCaixa(caixaCabecalho);
				}

				entityManager.getTransaction().commit();

			} catch (Exception e) {
				e.printStackTrace();
				entityManager.getTransaction().rollback();
			}
		}
	}

}
