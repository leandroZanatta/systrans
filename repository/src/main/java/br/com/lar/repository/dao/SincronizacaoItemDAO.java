package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QSincronizacaoItem.sincronizacaoItem;

import java.util.List;

import com.mysema.query.types.Projections;

import br.com.lar.repository.model.SincronizacaoItem;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;
import br.com.systrans.util.dto.SincronizacaoTabelaDTO;

public class SincronizacaoItemDAO extends PesquisableDAOImpl<SincronizacaoItem> {

	private static final long serialVersionUID = 1L;

	public SincronizacaoItemDAO() {
		super(sincronizacaoItem, sincronizacaoItem.idSincronizacao);
	}

	public List<SincronizacaoTabelaDTO> obterDadosSincronizacao() {

		return sqlFrom().groupBy(sincronizacaoItem.codigoTabela).list(Projections.fields(SincronizacaoTabelaDTO.class,
				sincronizacaoItem.codigoTabela.as("codigoTabela"), sincronizacaoItem.sincronizacaoVersao.max().as("versaoSincronizacao")));
	}

}
