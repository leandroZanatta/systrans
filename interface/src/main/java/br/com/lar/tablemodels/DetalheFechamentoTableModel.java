package br.com.lar.tablemodels;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.sysdesc.components.AbstractInternalFrameTable;
import br.com.systrans.util.vo.DetalheFechamentoVO;

public class DetalheFechamentoTableModel extends AbstractInternalFrameTable {

	private static final long serialVersionUID = 1L;
	private List<DetalheFechamentoVO> rows = new ArrayList<>();
	private List<String> colunas = new ArrayList<>();
	private NumberFormat numberFormat = NumberFormat.getNumberInstance();

	public DetalheFechamentoTableModel() {
		colunas.add("Forma de Pagamento");
		colunas.add("Valor");

		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setGroupingUsed(true);

	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		DetalheFechamentoVO detalheFechamentoVO = rows.get(rowIndex);

		switch (columnIndex) {

		case 0:
			return detalheFechamentoVO.getNomeConta();

		case 1:
			return numberFormat.format(detalheFechamentoVO.getValorConta().doubleValue());

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

	public DetalheFechamentoVO getRow(int selectedRow) {
		return rows.get(selectedRow);
	}

	public void setRows(List<DetalheFechamentoVO> rows) {
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

	}

}