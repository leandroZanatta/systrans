package br.com.lar.tablemodels;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import br.com.sysdesc.util.vo.PermissaoPesquisaVO;

public class PesquisaTreeTableModel extends AbstractTreeTableModel {
	private final static String[] COLUMN_NAMES = { "Pesquisa", "Visualizar" };

	public PesquisaTreeTableModel(PermissaoPesquisaVO permissaoPrograma) {
		super(permissaoPrograma);
	}

	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	public Object getValueAt(Object node, int column) {
		switch (column) {
		case 0:
			return ((PermissaoPesquisaVO) node).getDescricao();
		case 1:
			return ((PermissaoPesquisaVO) node).getFlagLeitura();
		default:
			return null;
		}
	}

	public Object getChild(Object parent, int index) {
		return ((PermissaoPesquisaVO) parent).getChilds().get(index);
	}

	public int getChildCount(Object parent) {
		return ((PermissaoPesquisaVO) parent).getChilds().size();
	}

	public int getIndexOfChild(Object parent, Object child) {
		return ((PermissaoPesquisaVO) parent).getChilds().indexOf(child);
	}

	@Override
	public boolean isCellEditable(Object node, int column) {

		return column != 0;
	}

	@Override
	public void setValueAt(Object value, Object node, int column) {

		if (column != 0) {
			PermissaoPesquisaVO permissao = (PermissaoPesquisaVO) node;

			if (column == 1) {
				permissao.setFlagLeitura((Boolean) value);

				updateChildsLeitura(permissao, (Boolean) value);
			}
		}

	}

	@Override
	public boolean isLeaf(Object node) {
		return ((PermissaoPesquisaVO) node).getChilds().isEmpty();
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return column == 0 ? String.class : Boolean.class;
	}

	public void updateChildsLeitura(PermissaoPesquisaVO permissaoPesquisaVO, Boolean value) {

		permissaoPesquisaVO.setFlagLeitura(value);

		if (!permissaoPesquisaVO.getChilds().isEmpty()) {

			permissaoPesquisaVO.getChilds().forEach(child -> updateChildsLeitura(child, value));
		}
	}

}
