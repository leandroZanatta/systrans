package br.com.lar.service.caixa;

import java.util.Date;

import javax.persistence.EntityManager;

import br.com.lar.repository.dao.CaixaCabecalhoDAO;
import br.com.lar.repository.dao.CaixaDAO;
import br.com.lar.repository.model.Caixa;
import br.com.lar.repository.model.CaixaCabecalho;
import br.com.lar.repository.model.CaixaSaldo;
import br.com.sysdesc.pesquisa.repository.model.Usuario;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;

public class CaixaCabecalhoService extends AbstractPesquisableServiceImpl<CaixaCabecalho> {

	private ResumoCaixaService resumoCaixaService = new ResumoCaixaService();
	private CaixaCabecalhoDAO caixaCabecalhoDAO;
	private CaixaDAO caixaDAO = new CaixaDAO();

	public CaixaCabecalhoService() {
		this(new CaixaCabecalhoDAO());
	}

	public CaixaCabecalhoService(CaixaCabecalhoDAO caixaCabecalhoDAO) {
		super(caixaCabecalhoDAO, CaixaCabecalho::getIdCaixaCabecalho);

		this.caixaCabecalhoDAO = caixaCabecalhoDAO;
	}

	public CaixaCabecalho validarCaixaAberto(Usuario usuario) {

		Caixa caixa = caixaDAO.buscarCaixaPorUsuario(usuario.getIdUsuario());

		if (caixa == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_USUARIO_SEM_CAIXA);
		}

		CaixaCabecalho caixaCabecalho = caixaCabecalhoDAO.obterCaixa(usuario.getIdUsuario());

		if (caixaCabecalho == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_CAIXA_NAO_ENCONTRADO);
		}

		return caixaCabecalho;
	}

	public void consultarAterturaCaixa(Usuario usuario) {

		Caixa caixa = caixaDAO.buscarCaixaPorUsuario(usuario.getIdUsuario());

		if (caixa == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_USUARIO_SEM_CAIXA);
		}

		boolean caixaAberto = caixaCabecalhoDAO.buscarCaixasAbertos(usuario.getIdUsuario());

		if (caixaAberto) {

			throw new SysDescException(MensagemConstants.MENSAGEM_CAIXA_ABERTO);
		}
	}

	public CaixaCabecalho obterCaixa(Usuario usuario) {

		return caixaCabecalhoDAO.obterCaixa(usuario.getIdUsuario());
	}

	public void abrirCaixa(Usuario usuario) {

		consultarAterturaCaixa(usuario);

		CaixaCabecalho caixaCabecalho = new CaixaCabecalho();

		Date dataAbertura = new Date();

		caixaCabecalho.setCaixa(caixaDAO.buscarCaixaPorUsuario(usuario.getIdUsuario()));
		caixaCabecalho.setDataAbertura(dataAbertura);
		caixaCabecalho.setDataMovimento(dataAbertura);

		caixaCabecalhoDAO.salvar(caixaCabecalho);
	}

	public void fecharCaixa(CaixaCabecalho caixaCabecalho) {
		caixaCabecalho.setDataFechamento(new Date());

		CaixaSaldo caixaSaldo = new CaixaSaldo();
		caixaSaldo.setCaixaCabecalho(caixaCabecalho);
		caixaSaldo.setDataMovimento(caixaCabecalho.getDataMovimento());
		caixaSaldo.setValorSaldo(resumoCaixaService.calcularSaldoCaixa(caixaCabecalho.getIdCaixaCabecalho()));

		EntityManager entityManager = caixaCabecalhoDAO.getEntityManager();

		try {
			entityManager.getTransaction().begin();
			entityManager.persist(caixaCabecalho);
			entityManager.persist(caixaSaldo);
		} finally {
			entityManager.getTransaction().commit();
		}
	}

}
