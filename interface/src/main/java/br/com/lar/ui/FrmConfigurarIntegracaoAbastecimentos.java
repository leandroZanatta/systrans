package br.com.lar.ui;

import javax.swing.JLabel;
import javax.swing.JTextField;

import br.com.sysdesc.components.AbstractInternalFrame;
import net.miginfocom.swing.MigLayout;

public class FrmConfigurarIntegracaoAbastecimentos extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextField textField_1;

	public FrmConfigurarIntegracaoAbastecimentos(Long codigoPrograma, Long codigoUsuario) {
		super(codigoPrograma, codigoUsuario);
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][]"));

		JLabel lblNewLabel = new JLabel("Url:");
		getContentPane().add(lblNewLabel, "cell 0 0,alignx left");

		textField = new JTextField();
		getContentPane().add(textField, "cell 1 0,growx");
		textField.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Tempo Integrar:");
		getContentPane().add(lblNewLabel_1, "cell 0 1,alignx left");

		textField_1 = new JTextField();
		getContentPane().add(textField_1, "cell 1 1,growx");
		textField_1.setColumns(10);

		initComponents();
	}

	private void initComponents() {

		setSize(450, 275);
		setClosable(Boolean.TRUE);
		setTitle("Configurar Integração de Abastecimentos");

	}

}
