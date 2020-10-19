package br.com.lar.ui;

import javax.swing.JPanel;

import br.com.sysdesc.components.AbstractInternalFrame;
import net.miginfocom.swing.MigLayout;

public class FrmFechamentoCaixa extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;

	public FrmFechamentoCaixa(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(450, 230);
		setClosable(Boolean.TRUE);
		setTitle("FECHAMENTO DE CAIXA");

		painelContent = new JPanel();

		painelContent.setLayout(new MigLayout("", "[grow]", "[][][][][][][grow]"));
		getContentPane().add(painelContent);

	}

}
