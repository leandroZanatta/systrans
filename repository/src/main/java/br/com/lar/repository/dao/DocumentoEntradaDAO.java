package br.com.lar.repository.dao;

import static br.com.lar.repository.model.QDocumentoEntrada.documentoEntrada;

import br.com.lar.repository.model.DocumentoEntrada;
import br.com.sysdesc.pesquisa.repository.dao.impl.PesquisableDAOImpl;

public class DocumentoEntradaDAO extends PesquisableDAOImpl<DocumentoEntrada> {

	private static final long serialVersionUID = 1L;

	public DocumentoEntradaDAO() {
		super(documentoEntrada, documentoEntrada.idDocumentoEntrada);
	}

}
