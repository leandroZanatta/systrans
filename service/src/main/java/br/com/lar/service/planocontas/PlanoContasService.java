package br.com.lar.service.planocontas;

import java.util.Date;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.dao.PlanoContasDAO;
import br.com.lar.repository.model.PlanoContas;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.sysdesc.util.enumeradores.TipoSaldoEnum;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;

public class PlanoContasService extends AbstractPesquisableServiceImpl<PlanoContas> {

	private PlanoContasDAO planoContasDAO;

	public PlanoContasService() {
		this(new PlanoContasDAO());
	}

	public PlanoContasService(PlanoContasDAO planoContasDAO) {
		super(planoContasDAO, PlanoContas::getIdPlanoContas);

		this.planoContasDAO = planoContasDAO;
	}

	@Override
	public void validar(PlanoContas objetoPersistir) {

		Date data = new Date();

		if (objetoPersistir.getCadastro() == null) {
			objetoPersistir.setCadastro(data);
		}

		objetoPersistir.setManutencao(data);

		if (StringUtil.isNullOrEmpty(objetoPersistir.getDescricao())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_INSIRA_DESCRICAO_VALIDA);
		}

		if (StringUtil.isNullOrEmpty(objetoPersistir.getSaldo())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_SALDO);
		}

		if (LongUtil.isNullOrZero(objetoPersistir.getSituacao())) {
			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_SITUACAO);
		}
	}

	public Long getNextIdentifier(Long idPlanoContas, boolean contaAnalitica) {

		return planoContasDAO.getNextIdentifier(idPlanoContas, contaAnalitica) + 1L;
	}

	public BooleanBuilder getContasAnaliticas() {

		return planoContasDAO.getContasAnaliticas();
	}

	public BooleanBuilder getContasSinteticas() {

		return planoContasDAO.getContasSinteticas();
	}

	public BooleanBuilder getPreFilter(TipoSaldoEnum tipoSaldo, boolean analitica) {

		return planoContasDAO.getPreFilter(tipoSaldo, analitica);
	}

	public BooleanBuilder getContasDevedoras() {

		return planoContasDAO.getContasDevedoras();
	}

	public BooleanBuilder getContasCredoras() {

		return planoContasDAO.getContasCredoras();
	}

	public BooleanBuilder getContasBalanco() {

		return planoContasDAO.getContasBalanco();
	}
}
