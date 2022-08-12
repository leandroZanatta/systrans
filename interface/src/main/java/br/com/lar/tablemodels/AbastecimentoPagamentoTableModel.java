package br.com.lar.tablemodels;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

import br.com.lar.repository.model.AbastecimentoPagamento;
import br.com.sysdesc.components.AbstractInternalFrameTable;
import br.com.sysdesc.util.classes.DateUtil;
import br.com.sysdesc.util.exception.SysDescException;

public class AbastecimentoPagamentoTableModel extends AbstractInternalFrameTable {

	private boolean editable = Boolean.FALSE;
	private static final long serialVersionUID = 1L;
	private List<AbastecimentoPagamento> rows = new ArrayList<>();
	private List<String> colunas = new ArrayList<>();

	public AbastecimentoPagamentoTableModel() {
		this(new ArrayList<>());
	}

	public AbastecimentoPagamentoTableModel(List<AbastecimentoPagamento> rows) {
		this.rows = rows;

		colunas.add("Lançamento");
		colunas.add("Vencimento");
		colunas.add("Parcela");
		colunas.add("Valor");

	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		AbastecimentoPagamento abastecimentoPagamento = rows.get(rowIndex);

		switch (columnIndex) {

		case 0:
			return DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, abastecimentoPagamento.getDataLancamento());

		case 1:
			return abastecimentoPagamento.getDataVencimento();

		case 2:
			return abastecimentoPagamento.getNumeroParcela();

		case 3:
			return abastecimentoPagamento.getValorParcela();

		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		AbastecimentoPagamento abastecimentoPagamento = rows.get(rowIndex);

		switch (columnIndex) {

		case 1:
			abastecimentoPagamento.setDataVencimento((Date) aValue);
			break;

		case 3:
			abastecimentoPagamento.setValorParcela((BigDecimal) aValue);
			break;

		default:
			throw new SysDescException("Campo não pode ser editado");
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

		return columnIndex == 1 ? Date.class : columnIndex == 3 ? BigDecimal.class : String.class;
	}

	@Override
	public boolean isCellEditable(int row, int column) {

		return editable && (column == 1 || column == 3);
	}

	public AbastecimentoPagamento getRow(int selectedRow) {
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

	public List<AbastecimentoPagamento> getRows() {
		return rows;
	}

	public void setRows(List<AbastecimentoPagamento> rows) {
		this.rows = Lists.newArrayList(rows);

		this.rows.sort(Comparator.comparing(AbastecimentoPagamento::getDataVencimento));

		fireTableDataChanged();
	}

	public void addRow(AbastecimentoPagamento row) {

		this.rows.add(row);

		fireTableDataChanged();

	}

	@Override
	public void clear() {
		this.rows = new ArrayList<>();

		fireTableDataChanged();
	}

	@Override
	public void setEnabled(Boolean enabled) {
		this.editable = enabled;
	}

	public BigDecimal getTotalPagamentos() {

		return this.rows.stream().map(AbastecimentoPagamento::getValorParcela).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public void deleteRow(int selectedRow) {
		this.rows.remove(selectedRow);

		Long parcela = 1L;

		for (AbastecimentoPagamento row : rows) {
			row.setNumeroParcela(parcela);

			parcela++;
		}

		fireTableDataChanged();

	}

}