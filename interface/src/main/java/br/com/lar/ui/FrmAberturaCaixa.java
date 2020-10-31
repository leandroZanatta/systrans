package br.com.lar.ui;

import java.awt.Color;
import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import br.com.lar.service.caixa.CaixaCabecalhoService;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.classes.IfNull;
import br.com.sysdesc.util.exception.SysDescException;
import net.miginfocom.swing.MigLayout;

public class FrmAberturaCaixa extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;
	private JMoneyField txSaldo;
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
		txSaldo = new JMoneyField();
		btAbrirCaixa = new JButton("Abrir");

		btAbrirCaixa.addActionListener(e -> abrirCaixa());
		painelContent.setLayout(new MigLayout("", "[grow]", "[][][grow,bottom]"));

		painelContent.add(lblSaldo, "cell 0 0");
		painelContent.add(txSaldo, "cell 0 1,growx");
		painelContent.add(btAbrirCaixa, "cell 0 2,alignx center");
		getContentPane().add(painelContent);

		txSaldo.setValue(IfNull.get(caixaCabecalhoService.buscarUltimoSaldoCaixa(FrmApplication.getUsuario()), BigDecimal.ZERO));

		setarCorSaldo(txSaldo);
	}

	private void setarCorSaldo(JMoneyField moneyField) {
		BigDecimal valor = moneyField.getValue();

		Color cor = Color.BLACK;

		if (BigDecimalUtil.maior(valor, BigDecimal.ZERO)) {
			cor = Color.BLUE;
		} else if (BigDecimalUtil.menor(valor, BigDecimal.ZERO)) {
			cor = Color.RED;
		}

		moneyField.setForeground(cor);
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
