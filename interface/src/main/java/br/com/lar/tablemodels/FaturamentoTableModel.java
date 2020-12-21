package br.com.lar.tablemodels;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;

import br.com.lar.repository.model.FaturamentoDetalhe;
import br.com.lar.repository.model.Veiculo;
import br.com.sysdesc.components.AbstractInternalFrameTable;
import br.com.sysdesc.util.classes.IfNull;

public class FaturamentoTableModel extends AbstractInternalFrameTable {

	private static final long serialVersionUID = 1L;
	private List<FaturamentoDetalhe> rows = new ArrayList<>();
	private List<String> colunas = new ArrayList<>();

	public FaturamentoTableModel() {
		this(new ArrayList<>());
	}

	public FaturamentoTableModel(List<FaturamentoDetalhe> rows) {
		this.rows = rows;

		colunas.add("Documento");
		colunas.add("Valor");
		colunas.add("Desconto");
		colunas.add("Acréscimo");
		colunas.add("Veiculo");
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		FaturamentoDetalhe faturamentoDetalhe = rows.get(rowIndex);

		switch (columnIndex) {

		case 0:
			return faturamentoDetalhe.getNumeroDocumento();

		case 1:
			return faturamentoDetalhe.getValorBruto();

		case 2:
			return faturamentoDetalhe.getValorDesconto();

		case 3:
			return faturamentoDetalhe.getValorAcrescimo();

		case 4:
			return IfNull.caseNull(faturamentoDetalhe.getVeiculo(), "", Veiculo::getPlaca);

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

		return false;
	}

	public FaturamentoDetalhe getRow(int selectedRow) {
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

	public List<FaturamentoDetalhe> getRows() {
		return rows;
	}

	public void setRows(List<FaturamentoDetalhe> rows) {
		this.rows = Lists.newArrayList(rows);

		this.rows.sort(Comparator.comparing(FaturamentoDetalhe::getIdFaturamentoDetalhe));

		fireTableDataChanged();
	}

	public void addRow(FaturamentoDetalhe row) {

		this.rows.add(row);

		fireTableDataChanged();

	}

	@Override
	public void clear() {
		this.rows = new ArrayList<>();

		fireTableDataChanged();
	}

	public BigDecimal getTotalPagamentos() {

		return totalizar(FaturamentoDetalhe::getValorBruto);
	}

	public BigDecimal getTotalDescontos() {

		return totalizar(FaturamentoDetalhe::getValorDesconto);
	}

	public BigDecimal getTotalAcrecimos() {

		return totalizar(FaturamentoDetalhe::getValorAcrescimo);
	}

	private BigDecimal totalizar(Function<FaturamentoDetalhe, BigDecimal> function) {

		return this.rows.stream().map(detalhe -> function.apply(detalhe))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

}