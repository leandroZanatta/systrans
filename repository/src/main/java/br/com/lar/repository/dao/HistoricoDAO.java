package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QHistorico.historico;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.model.Historico;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class HistoricoDAO extends PesquisableDAOImpl<Historico> {

	private static final long serialVersionUID = 1L;

	public HistoricoDAO() {
		super(historico, historico.idHistorico);
	}

	public BooleanBuilder getHistoricosCredores() {

		return new BooleanBuilder(historico.tipoHistorico.eq(1L));
	}

	public BooleanBuilder getHistoricosDevedores() {

		return new BooleanBuilder(historico.tipoHistorico.eq(2L));
	}
}
