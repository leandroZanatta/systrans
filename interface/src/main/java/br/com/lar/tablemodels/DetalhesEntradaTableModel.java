package br.com.lar.tablemodels;

import java.util.ArrayList;
import java.util.List;

import br.com.lar.repository.model.FaturamentoEntradasDetalhe;
import br.com.lar.repository.model.Veiculo;
import br.com.sysdesc.components.AbstractInternalFrameTable;

public class DetalhesEntradaTableModel extends AbstractInternalFrameTable {

	private static final long serialVersionUID = 1L;
	private List<FaturamentoEntradasDetalhe> rows;
	private List<String> colunas = new ArrayList<>();
	private boolean editable = false;

	public DetalhesEntradaTableModel() {
		this(new ArrayList<>());
	}

	public DetalhesEntradaTableModel(List<FaturamentoEntradasDetalhe> rows) {
		this.rows = rows;

		colunas.add("Código");
		colunas.add("Veículo");
		colunas.add("Motorista");
		colunas.add("Valor");
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		FaturamentoEntradasDetalhe faturamentoEntradasDetalhe = rows.get(rowIndex);

		switch (columnIndex) {

		case 0:
			return faturamentoEntradasDetalhe.getIdFaturamentoEntradasDetalhe();

		case 1:
			return faturamentoEntradasDetalhe.getVeiculo();

		case 2:
			return faturamentoEntradasDetalhe.getMotorista();

		case 3:
			return faturamentoEntradasDetalhe.getValorBruto().subtract(faturamentoEntradasDetalhe.getValorDesconto())
					.add(faturamentoEntradasDetalhe.getValorAcrescimo());

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

		return columnIndex == 1 ? Veiculo.class : String.class;
	}

	@Override
	public boolean isCellEditable(int row, int column) {

		return editable && (column == 1 || column == 2);
	}

	public FaturamentoEntradasDetalhe getRow(int selectedRow) {
		return rows.get(selectedRow);
	}

	public List<FaturamentoEntradasDetalhe> getRows() {
		return rows;
	}

	public void setRows(List<FaturamentoEntradasDetalhe> rows) {
		this.rows = rows;
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

}