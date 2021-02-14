package br.com.lar.tablemodels;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.EventListenerList;

import br.com.lar.repository.model.ContasReceber;
import br.com.lar.repository.projection.PagamentoContasProjection;
import br.com.sysdesc.components.AbstractInternalFrameTable;
import br.com.sysdesc.components.listeners.ChangeListener;
import br.com.sysdesc.util.classes.DateUtil;
import br.com.systrans.util.vo.ResumoPagamentosVO;

public class ContasReceberPagamentoTableModel extends AbstractInternalFrameTable {

	private static final long serialVersionUID = 1L;
	private final List<PagamentoContasProjection<ContasReceber>> rows;
	private List<String> colunas = new ArrayList<>();
	private NumberFormat codigoContasReceberFormat = NumberFormat.getNumberInstance();
	private NumberFormat numberFormat = NumberFormat.getNumberInstance();
	protected EventListenerList eventListenerList = new EventListenerList();

	public ContasReceberPagamentoTableModel(List<PagamentoContasProjection<ContasReceber>> rows) {

		this.rows = rows;

		colunas.add("Código");
		colunas.add("Cliente");
		colunas.add("Vencimento");
		colunas.add("Valor da Parcela");
		colunas.add("Descontos");
		colunas.add("Acréscimos");
		colunas.add("Juros");
		colunas.add("Valor Pagar");

		codigoContasReceberFormat.setMaximumFractionDigits(0);
		codigoContasReceberFormat.setMinimumFractionDigits(0);
		codigoContasReceberFormat.setGroupingUsed(true);

		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setGroupingUsed(true);

	}

	public void addChangeListener(ChangeListener<ResumoPagamentosVO> changeListener) {

		eventListenerList.add(ChangeListener.class, changeListener);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		PagamentoContasProjection<ContasReceber> pagamentoContasVO = rows.get(rowIndex);

		switch (columnIndex) {

		case 0:
			return codigoContasReceberFormat.format(pagamentoContasVO.getIdConta());

		case 1:
			return pagamentoContasVO.getCliente();

		case 2:
			return DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, pagamentoContasVO.getVencimento());

		case 3:
			return numberFormat.format(pagamentoContasVO.getValorParcela());

		case 4:
			return pagamentoContasVO.getDecontos();

		case 5:
			return pagamentoContasVO.getAcrescimos();

		case 6:
			return pagamentoContasVO.getJuros();

		case 7:
			return pagamentoContasVO.getValorPagar();

		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		if (columnIndex >= 4) {

			PagamentoContasProjection<ContasReceber> pagamentoContasVO = rows.get(rowIndex);

			switch (columnIndex) {
			case 4:
				pagamentoContasVO.setDecontos((BigDecimal) aValue);
				recalcularValorPagar(pagamentoContasVO);
				break;
			case 5:
				pagamentoContasVO.setAcrescimos((BigDecimal) aValue);
				recalcularValorPagar(pagamentoContasVO);
				break;
			case 6:
				pagamentoContasVO.setJuros((BigDecimal) aValue);
				recalcularValorPagar(pagamentoContasVO);
				break;
			case 7:
				if (validarValor(pagamentoContasVO, (BigDecimal) aValue)) {
					pagamentoContasVO.setDecontos(BigDecimal.ZERO);
					pagamentoContasVO.setAcrescimos(BigDecimal.ZERO);
					pagamentoContasVO.setJuros(BigDecimal.ZERO);
					pagamentoContasVO.setValorPagar((BigDecimal) aValue);
				}
				break;
			default:
				break;
			}

			fireChangeValue();
		}
	}

	private boolean validarValor(PagamentoContasProjection<ContasReceber> pagamentoContasVO, BigDecimal aValue) {

		if (pagamentoContasVO.getValorParcela().compareTo(aValue) <= 0) {

			JOptionPane.showMessageDialog(null, "O VALOR MÁXIMO DA PARCELA É: "
					+ new DecimalFormat("0.00").format(pagamentoContasVO.getValorParcela()) + ", INSIRA UM VALOR MENOR");

			return false;
		}

		return true;
	}

	private void recalcularValorPagar(PagamentoContasProjection<ContasReceber> vo) {

		BigDecimal valorRecalculado = vo.getValorParcela().add(vo.getAcrescimos()).add(vo.getJuros()).subtract(vo.getDecontos());

		vo.setValorPagar(valorRecalculado);

		fireTableDataChanged();
	}

	private void fireChangeValue() {

		Object[] listeners = eventListenerList.getListenerList();

		for (int i = 0; i < listeners.length; i = i + 2) {

			if (listeners[i] == ChangeListener.class) {

				((ChangeListener<ResumoPagamentosVO>) listeners[i + 1]).valueChanged(obterValorTotal());
			}
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

		return columnIndex >= 4 ? BigDecimal.class : String.class;
	}

	@Override
	public boolean isCellEditable(int row, int column) {

		return column >= 4;
	}

	@Override
	public void clear() {

	}

	public ResumoPagamentosVO obterValorTotal() {

		ResumoPagamentosVO resumoPagamentosVO = new ResumoPagamentosVO();

		rows.forEach(pagamento -> {
			resumoPagamentosVO.setValorParcelas(resumoPagamentosVO.getValorParcelas().add(pagamento.getValorParcela()));
			resumoPagamentosVO.setValorDesconto(resumoPagamentosVO.getValorDesconto().add(pagamento.getDecontos()));
			resumoPagamentosVO.setValorAcrescimo(resumoPagamentosVO.getValorAcrescimo().add(pagamento.getAcrescimos()));
			resumoPagamentosVO.setValorJuros(resumoPagamentosVO.getValorJuros().add(pagamento.getJuros()));
			resumoPagamentosVO.setValorPagar(resumoPagamentosVO.getValorPagar().add(pagamento.getValorPagar()));
		});

		return resumoPagamentosVO;
	}

	public List<PagamentoContasProjection<ContasReceber>> getRows() {

		return this.rows;
	}
}