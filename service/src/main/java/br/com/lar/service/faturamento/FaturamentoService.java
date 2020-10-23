package br.com.lar.service.faturamento;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.lar.repository.dao.FaturamentoDAO;
import br.com.lar.repository.model.CaixaDetalhe;
import br.com.lar.repository.model.ContasReceber;
import br.com.lar.repository.model.DiarioCabecalho;
import br.com.lar.repository.model.Faturamento;
import br.com.lar.service.caixa.CaixaService;
import br.com.lar.service.caixa.FaturamentoCaixa;
import br.com.lar.service.contasreceber.FaturamentoContasReceber;
import br.com.lar.service.diario.DiarioService;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.ListUtil;

public class FaturamentoService extends AbstractPesquisableServiceImpl<Faturamento> {

	private FaturamentoDAO faturamentoDAO;
	private FaturamentoContasReceber faturamentoContasReceber = new FaturamentoContasReceber();
	private DiarioService faturamentoDiario = new DiarioService();
	private FaturamentoCaixa faturamentoCaixa = new FaturamentoCaixa();
	private CaixaService caixaService = new CaixaService();

	public FaturamentoService() {
		this(new FaturamentoDAO());
	}

	public FaturamentoService(FaturamentoDAO faturamentoDAO) {
		super(faturamentoDAO, Faturamento::getIdFaturamento);

		this.faturamentoDAO = faturamentoDAO;
	}

	@Override
	public void salvar(Faturamento objetoPersistir) {

		caixaService.verificarCaixaAberto(objetoPersistir.getCaixaCabecalho());

		DiarioCabecalho diarioCabecalho = faturamentoDiario.registrarDiarioFaturamento(objetoPersistir);

		List<ContasReceber> contasReceber = faturamentoContasReceber.registrarContasReceber(objetoPersistir);

		List<CaixaDetalhe> caixaDetalhes = faturamentoCaixa.registrarCaixaFaturamento(objetoPersistir);

		EntityManager entityManager = faturamentoDAO.getEntityManager();

		try {

			entityManager.getTransaction().begin();

			entityManager.persist(objetoPersistir);

			entityManager.persist(diarioCabecalho);

			if (!ListUtil.isNullOrEmpty(contasReceber)) {

				contasReceber.forEach(entityManager::persist);
			}

			caixaDetalhes.forEach(entityManager::persist);

		} finally {
			entityManager.getTransaction().commit();
		}

	}
}
