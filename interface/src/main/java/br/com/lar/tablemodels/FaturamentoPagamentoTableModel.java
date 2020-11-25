package br.com.lar.tablemodels;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

import br.com.lar.repository.model.FaturamentoPagamento;
import br.com.sysdesc.components.AbstractInternalFrameTable;
import br.com.sysdesc.util.classes.DateUtil;

public class FaturamentoPagamentoTableModel extends AbstractInternalFrameTable {

	private boolean editable = Boolean.FALSE;
	private static final long serialVersionUID = 1L;
	private List<FaturamentoPagamento> rows = new ArrayList<>();
	private List<String> colunas = new ArrayList<>();

	public FaturamentoPagamentoTableModel() {
		this(new ArrayList<>());
	}

	public FaturamentoPagamentoTableModel(List<FaturamentoPagamento> rows) {
		this.rows = rows;

		colunas.add("Forma Pagamento");
		colunas.add("Lançamento");
		colunas.add("Vencimento");
		colunas.add("Parcela");
		colunas.add("Valor");

	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		FaturamentoPagamento faturamentoPagamento = rows.get(rowIndex);

		switch (columnIndex) {

		case 0:
			return faturamentoPagamento.getFormasPagamento().getDescricao();

		case 1:
			return DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, faturamentoPagamento.getDataLancamento());

		case 2:
			return faturamentoPagamento.getDataVencimento();

		case 3:
			return faturamentoPagamento.getNumeroParcela();

		case 4:
			return faturamentoPagamento.getValorParcela();

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

		return columnIndex == 2 ? Date.class : columnIndex == 4 ? BigDecimal.class : String.class;
	}

	@Override
	public boolean isCellEditable(int row, int column) {

		return editable && (column == 2 || column == 4);
	}

	public FaturamentoPagamento getRow(int selectedRow) {
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

	public List<FaturamentoPagamento> getRows() {
		return rows;
	}

	public void setRows(List<FaturamentoPagamento> rows) {
		this.rows = Lists.newArrayList(rows);

		this.rows.sort(Comparator.comparing(FaturamentoPagamento::getDataVencimento));

		fireTableDataChanged();
	}

	public void addRow(FaturamentoPagamento row) {

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

		return this.rows.stream().map(FaturamentoPagamento::getValorParcela).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

}