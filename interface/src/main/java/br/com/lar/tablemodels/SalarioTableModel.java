package br.com.lar.tablemodels;

import static br.com.sysdesc.util.resources.Resources.TBLSALARIO_DATA_ALTERACAO;
import static br.com.sysdesc.util.resources.Resources.TBLSALARIO_VALOR_SALARIO;
import static br.com.sysdesc.util.resources.Resources.translate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import br.com.lar.repository.model.Salario;
import br.com.sysdesc.components.AbstractInternalFrameTable;
import br.com.sysdesc.util.classes.DateUtil;

public class SalarioTableModel extends AbstractInternalFrameTable {

	private static final long serialVersionUID = 1L;
	private List<Salario> rows = new ArrayList<>();
	private List<String> colunas = new ArrayList<>();
	private NumberFormat numberFormat = NumberFormat.getNumberInstance();

	public SalarioTableModel(List<Salario> rows) {
		this.rows = rows;

		colunas.add(translate(TBLSALARIO_DATA_ALTERACAO));
		colunas.add(translate(TBLSALARIO_VALOR_SALARIO));

		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setGroupingUsed(true);

	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		Salario salario = rows.get(rowIndex);

		switch (columnIndex) {

		case 0:
			return DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, salario.getDataAlteracao());

		case 1:
			return numberFormat.format(salario.getValorSalario().doubleValue());

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

	public Salario getRow(int selectedRow) {
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

	public List<Salario> getRows() {
		return rows;
	}

	public void setRows(List<Salario> rows) {
		this.rows = Lists.newArrayList(rows);
		fireTableDataChanged();
	}

	public void addRow(Salario configuracaoPesquisa) {

		this.rows.add(configuracaoPesquisa);

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