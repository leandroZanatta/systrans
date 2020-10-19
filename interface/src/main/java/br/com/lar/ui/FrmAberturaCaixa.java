package br.com.lar.ui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import br.com.lar.service.caixa.CaixaCabecalhoService;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.util.exception.SysDescException;
import net.miginfocom.swing.MigLayout;

public class FrmAberturaCaixa extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;
	private JTextField txSaldo;
	private JButton btAbrirCaixa;
	private CaixaCabecalhoService caixaCabecalhoService = new CaixaCabecalhoService();

	public FrmAberturaCaixa(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(202, 126);
		setClosable(Boolean.TRUE);
		setTitle("ABERTURA DE CAIXA");

		painelContent = new JPanel();
		JLabel lblSaldo = new JLabel("Saldo:");
		txSaldo = new JTextField();
		btAbrirCaixa = new JButton("Abrir");

		btAbrirCaixa.addActionListener(e -> abrirCaixa());
		painelContent.setLayout(new MigLayout("", "[grow]", "[][][grow,bottom]"));

		painelContent.add(lblSaldo, "cell 0 0");
		painelContent.add(txSaldo, "cell 0 1,growx");
		painelContent.add(btAbrirCaixa, "cell 0 2,alignx center");
		getContentPane().add(painelContent);

	}

	private void abrirCaixa() {

		try {
			caixaCabecalhoService.abrirCaixa(FrmApplication.getUsuario());

			FrmApplication.getInstance().setarLabelCaixa();

			dispose();

		} catch (SysDescException e) {

			JOptionPane.showMessageDialog(this, e.getMensagem());
		}
	}

}
