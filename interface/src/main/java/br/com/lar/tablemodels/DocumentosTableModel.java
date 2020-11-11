package br.com.lar.tablemodels;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import br.com.lar.repository.model.DocumentoEntrada;
import br.com.sysdesc.components.AbstractInternalFrameTable;

public class DocumentosTableModel extends AbstractInternalFrameTable {

	private static final long serialVersionUID = 1L;
	private List<DocumentoEntrada> rows;
	private List<String> colunas = new ArrayList<>();

	public DocumentosTableModel() {
		this(new ArrayList<>());
	}

	public DocumentosTableModel(List<DocumentoEntrada> rows) {
		this.rows = rows;

		colunas.add("Código");
		colunas.add("Local");
		colunas.add("Visualizar");
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		DocumentoEntrada documentoEntrada = rows.get(rowIndex);

		switch (columnIndex) {

		case 0:
			return documentoEntrada.getIdDocumentoEntrada();

		case 1:
			return documentoEntrada.getLocal();

		default:
			return null;
		}
	}

	@Override
	public int getColumnCount() {
		return colunas.size();
	}

	@Override
	public String getColumnName(int column) {
		return colunas.get(column);
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		return String.class;
	}

	@Override
	public boolean isCellEditable(int row, int column) {

		return column == 2;
	}

	public DocumentoEntrada getRow(int selectedRow) {
		return rows.get(selectedRow);
	}

	public void remove(int selectedRow) {
		rows.remove(selectedRow);
		fireTableDataChanged();
	}

	public void removeAll() {
		rows = new ArrayList<>();
		fireTableDataChanged();
	}

	public List<DocumentoEntrada> getRows() {
		return rows;
	}

	public void setRows(List<DocumentoEntrada> rows) {
		this.rows = Lists.newArrayList(rows);
		fireTableDataChanged();
	}

	public void addRow(DocumentoEntrada documentoEntrada) {

		this.rows.add(documentoEntrada);

		fireTableDataChanged();

	}

	@Override
	public void clear() {
		this.rows = new ArrayList<>();

		fireTableDataChanged();
	}

	@Override
	public void setEnabled(Boolean enabled) {

	}

}