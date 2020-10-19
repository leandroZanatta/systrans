package br.com.lar.service.caixa;

import java.util.Date;

import br.com.lar.repository.dao.CaixaCabecalhoDAO;
import br.com.lar.repository.dao.CaixaDAO;
import br.com.lar.repository.model.Caixa;
import br.com.lar.repository.model.CaixaCabecalho;
import br.com.sysdesc.pesquisa.repository.model.Usuario;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.constants.MensagemConstants;
import br.com.sysdesc.util.exception.SysDescException;

public class CaixaCabecalhoService extends AbstractPesquisableServiceImpl<CaixaCabecalho> {

	private CaixaCabecalhoDAO caixaCabecalhoDAO;
	private CaixaDAO caixaDAO = new CaixaDAO();

	public CaixaCabecalhoService() {
		this(new CaixaCabecalhoDAO());
	}

	public CaixaCabecalhoService(CaixaCabecalhoDAO caixaCabecalhoDAO) {
		super(caixaCabecalhoDAO, CaixaCabecalho::getIdCaixaCabecalho);

		this.caixaCabecalhoDAO = caixaCabecalhoDAO;
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

	public void abrirCaixa(Usuario usuario) {

		consultarAterturaCaixa(usuario);

		CaixaCabecalho caixaCabecalho = new CaixaCabecalho();

		Date dataAbertura = new Date();

		caixaCabecalho.setCaixa(caixaDAO.buscarCaixaPorUsuario(usuario.getIdUsuario()));
		caixaCabecalho.setDataAbertura(dataAbertura);
		caixaCabecalho.setDataMovimento(dataAbertura);

		caixaCabecalhoDAO.salvar(caixaCabecalho);
	}

}
