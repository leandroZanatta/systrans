package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QHistorico.historico;
import static br.com.lar.repository.model.QOperacao.operacao;

import java.util.List;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.model.Operacao;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.systrans.util.enumeradores.TipoHistoricoOperacaoEnum;

public class OperacaoDAO extends PesquisableDAOImpl<Operacao> {

	private static final long serialVersionUID = 1L;

	public OperacaoDAO() {
		super(operacao, operacao.idOperacao);
	}

	public boolean validarBuscarOperacao(Operacao objetoPersistir) {

		BooleanBuilder booleanBuilder = new BooleanBuilder();

		booleanBuilder.and(operacao.codigoFormaPagamento.eq(objetoPersistir.getFormasPagamento().getIdFormaPagamento()));
		booleanBuilder.and(operacao.codigoHistorico.eq(objetoPersistir.getHistorico().getIdHistorico()));

		if (!LongUtil.isNullOrZero(objetoPersistir.getIdOperacao())) {
			booleanBuilder.and(operacao.idOperacao.ne(objetoPersistir.getIdOperacao()));
		}

		return from().where(booleanBuilder).exists();
	}

	public List<Operacao> buscarOperacao(Long codigoHistorico, List<Long> codigoPagamentos) {

		return from().where(operacao.codigoHistorico.eq(codigoHistorico).and(operacao.codigoFormaPagamento.in(codigoPagamentos))).list(operacao);
	}

	public BooleanBuilder buscarHistoricoCredorPorPlanoContas(Long idPlanoContas) {

		return new BooleanBuilder(operacao.codigoContaDevedora.eq(idPlanoContas)
				.and(subQuery().from(historico).where(historico.idHistorico.eq(operacao.codigoHistorico)
						.and(historico.tipoHistorico.eq(TipoHistoricoOperacaoEnum.DEVEDOR.getCodigo()))).exists()));
	}

}
