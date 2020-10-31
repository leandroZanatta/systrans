package br.com.lar.tablemodels;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import br.com.lar.repository.model.ContasPagar;
import br.com.sysdesc.components.AbstractInternalFrameTable;
import br.com.sysdesc.util.classes.DateUtil;
import br.com.sysdesc.util.enumeradores.TipoStatusEnum;

public class ContasPagarTableModel extends AbstractInternalFrameTable {

	private static final long serialVersionUID = 1L;
	private List<ContasPagar> rows = new ArrayList<>();
	private List<String> colunas = new ArrayList<>();
	private NumberFormat numberFormat = NumberFormat.getNumberInstance();
	private NumberFormat codigoContasReceberFormat = NumberFormat.getNumberInstance();

	public ContasPagarTableModel() {

		colunas.add("Código");
		colunas.add("Cliente");
		colunas.add("Vencimento");
		colunas.add("Valor");
		colunas.add("Acréscimo");
		colunas.add("Desconto");
		colunas.add("Valor Pago");
		colunas.add("Baixado");
		colunas.add("Status");

		codigoContasReceberFormat.setMaximumFractionDigits(0);
		codigoContasReceberFormat.setMinimumFractionDigits(0);
		codigoContasReceberFormat.setGroupingUsed(true);

		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setGroupingUsed(true);

	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		ContasPagar contasPagar = rows.get(rowIndex);

		switch (columnIndex) {

		case 0:
			return codigoContasReceberFormat.format(contasPagar.getIdContasPagar());

		case 1:
			return contasPagar.getCliente().getNome();

		case 2:
			return DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, contasPagar.getDataVencimento());

		case 3:
			return numberFormat.format(contasPagar.getValorParcela());

		case 4:
			return numberFormat.format(contasPagar.getValorAcrescimo());

		case 5:
			return numberFormat.format(contasPagar.getValorDesconto());

		case 6:
			return numberFormat.format(contasPagar.getValorPago());

		case 7:
			return contasPagar.isBaixado() ? "Sim" : "Não";

		case 8:
			return TipoStatusEnum.findByCodigo(contasPagar.getCodigoStatus()).getDescricao();

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

	public ContasPagar getRow(int selectedRow) {
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

	public List<ContasPagar> getRows() {
		return rows;
	}

	public void setRows(List<ContasPagar> rows) {
		this.rows = Lists.newArrayList(rows);
		fireTableDataChanged();
	}

	public void addRow(ContasPagar contasPagar) {

		this.rows.add(contasPagar);

		fireTableDataChanged();

	}

	@Override
	public void clear() {
		this.rows = new ArrayList<>();

		fireTableDataChanged();
	}

}