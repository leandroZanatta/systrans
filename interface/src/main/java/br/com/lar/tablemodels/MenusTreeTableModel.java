package br.com.lar.tablemodels;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import br.com.sysdesc.util.vo.PermissaoProgramaVO;

public class MenusTreeTableModel extends AbstractTreeTableModel {
	private final static String[] COLUMN_NAMES = { "Programa", "Visualizar", "Editar", "Excluir" };

	public MenusTreeTableModel(PermissaoProgramaVO permissaoPrograma) {
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
			return ((PermissaoProgramaVO) node).getDescricao();
		case 1:
			return ((PermissaoProgramaVO) node).getFlagLeitura();
		case 2:
			return ((PermissaoProgramaVO) node).getFlagCadastro();
		case 3:
			return ((PermissaoProgramaVO) node).getFlagExclusao();
		default:
			return null;
		}
	}

	public Object getChild(Object parent, int index) {
		return ((PermissaoProgramaVO) parent).getChilds().get(index);
	}

	public int getChildCount(Object parent) {
		return ((PermissaoProgramaVO) parent).getChilds().size();
	}

	public int getIndexOfChild(Object parent, Object child) {
		return ((PermissaoProgramaVO) parent).getChilds().indexOf(child);
	}

	@Override
	public boolean isCellEditable(Object node, int column) {

		return column != 0;
	}

	@Override
	public void setValueAt(Object value, Object node, int column) {

		if (column != 0) {
			PermissaoProgramaVO permissao = (PermissaoProgramaVO) node;

			if (column == 1) {
				permissao.setFlagLeitura((Boolean) value);

				updateChildsLeitura(permissao, (Boolean) value);
			}

			if (column == 2) {
				permissao.setFlagCadastro((Boolean) value);

				updateChildsCadastro(permissao, (Boolean) value);
			}

			if (column == 3) {
				permissao.setFlagExclusao((Boolean) value);

				updateChildsExclusao(permissao, (Boolean) value);
			}
		}

	}

	@Override
	public boolean isLeaf(Object node) {
		return ((PermissaoProgramaVO) node).getChilds().isEmpty();
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return column == 0 ? String.class : Boolean.class;
	}

	public void updateChildsLeitura(PermissaoProgramaVO permissaoProgramaVO, Boolean value) {

		permissaoProgramaVO.setFlagLeitura(value);

		if (!permissaoProgramaVO.getChilds().isEmpty()) {

			permissaoProgramaVO.getChilds().forEach(child -> updateChildsLeitura(child, value));
		}
	}

	public void updateChildsCadastro(PermissaoProgramaVO permissaoProgramaVO, Boolean value) {

		permissaoProgramaVO.setFlagCadastro(value);

		if (!permissaoProgramaVO.getChilds().isEmpty()) {

			permissaoProgramaVO.getChilds().forEach(child -> updateChildsCadastro(child, value));
		}
	}

	public void updateChildsExclusao(PermissaoProgramaVO permissaoProgramaVO, Boolean value) {

		permissaoProgramaVO.setFlagExclusao(value);

		if (!permissaoProgramaVO.getChilds().isEmpty()) {

			permissaoProgramaVO.getChilds().forEach(child -> updateChildsExclusao(child, value));
		}
	}

}
