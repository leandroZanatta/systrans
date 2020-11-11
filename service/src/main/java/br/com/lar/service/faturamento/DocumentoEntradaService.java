package br.com.lar.service.faturamento;

import br.com.lar.repository.dao.DocumentoEntradaDAO;
import br.com.lar.repository.model.DocumentoEntrada;
import br.com.sysdesc.pesquisa.service.impl.AbstractPesquisableServiceImpl;

public class DocumentoEntradaService extends AbstractPesquisableServiceImpl<DocumentoEntrada> {

	private DocumentoEntradaDAO documentoEntradaDAO;

	public DocumentoEntradaService() {
		this(new DocumentoEntradaDAO());
	}

	public DocumentoEntradaService(DocumentoEntradaDAO documentoEntradaDAO) {
		super(documentoEntradaDAO, DocumentoEntrada::getIdDocumentoEntrada);

		this.documentoEntradaDAO = documentoEntradaDAO;
	}

	public void excluir(DocumentoEntrada documentoEntrada) {

		documentoEntradaDAO.remove(documentoEntrada);

	}

}
