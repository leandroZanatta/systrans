package br.com.lar.service.faturamento;

import br.com.lar.repository.dao.FaturamentoDAO;
import br.com.lar.repository.model.Faturamento;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;

public class FaturamentoService extends AbstractPesquisableServiceImpl<Faturamento> {

	public FaturamentoService() {
		super(new FaturamentoDAO(), Faturamento::getIdFaturamento);
	}

}
