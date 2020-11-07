package br.com.lar.tablemodels;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;

import br.com.lar.repository.model.FaturamentoEntradaPagamento;
import br.com.sysdesc.components.AbstractInternalFrameTable;
import br.com.sysdesc.util.classes.DateUtil;

public class FaturamentoEntradasPagamentoTableModel extends AbstractInternalFrameTable {

	private static final long serialVersionUID = 1L;
	private List<FaturamentoEntradaPagamento> rows = new ArrayList<>();
	private List<String> colunas = new ArrayList<>();
	private NumberFormat numberFormat = NumberFormat.getNumberInstance();

	public FaturamentoEntradasPagamentoTableModel() {
		this(new ArrayList<>());
	}

	public FaturamentoEntradasPagamentoTableModel(List<FaturamentoEntradaPagamento> rows) {
		this.rows = rows;

		colunas.add("Forma Pagamento");
		colunas.add("Lançamento");
		colunas.add("Vencimento");
		colunas.add("Parcela");
		colunas.add("Valor");

		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setGroupingUsed(true);

	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		FaturamentoEntradaPagamento faturamentoPagamento = rows.get(rowIndex);

		switch (columnIndex) {

		case 0:
			return faturamentoPagamento.getFormasPagamento().getDescricao();

		case 1:
			return DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, faturamentoPagamento.getDataLancamento());

		case 2:
			return DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, faturamentoPagamento.getDataVencimento());

		case 3:
			return faturamentoPagamento.getNumeroParcela();

		case 4:
			return numberFormat.format(faturamentoPagamento.getValorParcela());

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

		return Boolean.FALSE;
	}

	public FaturamentoEntradaPagamento getRow(int selectedRow) {
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

	public List<FaturamentoEntradaPagamento> getRows() {
		return rows;
	}

	public void setRows(List<FaturamentoEntradaPagamento> rows) {
		this.rows = Lists.newArrayList(rows);

		this.rows.sort(Comparator.comparing(FaturamentoEntradaPagamento::getDataVencimento));

		fireTableDataChanged();
	}

	public void addRow(FaturamentoEntradaPagamento row) {

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

	}

	public BigDecimal getTotalPagamentos() {

		return this.rows.stream().map(FaturamentoEntradaPagamento::getValorParcela).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

}