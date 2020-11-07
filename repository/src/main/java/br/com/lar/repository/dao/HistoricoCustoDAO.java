package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QHistoricoCusto.historicoCusto;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.model.Historico;
import br.com.lar.repository.model.HistoricoCusto;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.sysdesc.util.classes.LongUtil;

public class HistoricoCustoDAO extends PesquisableDAOImpl<HistoricoCusto> {

	private static final long serialVersionUID = 1L;

	public HistoricoCustoDAO() {
		super(historicoCusto, historicoCusto.idHistoricoCusto);
	}

	public boolean existeHistorico(Long idHistoricoCusto, Historico historico) {

		BooleanBuilder booleanBuilder = new BooleanBuilder(historicoCusto.codigoHistorico.eq(historico.getIdHistorico()));

		if (!LongUtil.isNullOrZero(idHistoricoCusto)) {

			booleanBuilder.and(historicoCusto.idHistoricoCusto.ne(idHistoricoCusto));
		}

		return from().where(booleanBuilder).exists();
	}

}
