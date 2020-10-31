package br.com.lar.ui;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.toedter.calendar.JDateChooser;

import br.com.lar.service.caixa.ResumoCaixaService;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.vo.ResumoCaixaVO;
import net.miginfocom.swing.MigLayout;

public class FrmResumoCaixa extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;
	private JMoneyField txMovimentoCredito;
	private JMoneyField txMovimentoDebito;
	private JLabel lblTotalDeCrditos;
	private JLabel lblTotalDeDbitos;
	private JLabel lblSaldoMovimentado;
	private JLabel lblSaldoDeContas;
	private JLabel lblSobrafalta;
	private JMoneyField txTotalCredito;
	private JMoneyField txTotalDebito;
	private JMoneyField txSaldoMovimentado;
	private JMoneyField txSaldoContas;
	private JMoneyField txSobraFalta;
	private JPanel panel;
	private JButton btnFechar;
	private ResumoCaixaService resumoCaixaService = new ResumoCaixaService();
	private JLabel lblDataDoCaixa;
	private JLabel lbDescricao;
	private JDateChooser dtCaixa;

	public FrmResumoCaixa(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(370, 318);
		setClosable(Boolean.TRUE);
		setTitle("RESUMO DE CAIXA");

		painelContent = new JPanel();

		painelContent.setLayout(new MigLayout("", "[238.00,grow][grow]", "[][][][][][][][][][grow]"));
		getContentPane().add(painelContent);

		lbDescricao = new JLabel("");
		painelContent.add(lbDescricao, "cell 0 0 2 1,alignx center");

		lblDataDoCaixa = new JLabel("Data do Caixa:");
		painelContent.add(lblDataDoCaixa, "cell 0 1,alignx left");

		dtCaixa = new JDateChooser("dd/MM/yyyy", "##/##/####", '_');
		dtCaixa.setEnabled(false);
		painelContent.add(dtCaixa, "cell 1 1,growx");

		JLabel lblTotalMovimentado = new JLabel("Total Movimentado Crédito:");
		painelContent.add(lblTotalMovimentado, "cell 0 2,alignx left");

		txMovimentoCredito = new JMoneyField();
		txMovimentoCredito.setEditable(false);
		txMovimentoCredito.setForeground(Color.BLUE);
		painelContent.add(txMovimentoCredito, "cell 1 2,growx");

		JLabel lblTotalMovimentadoDebito = new JLabel("Total Movimentado Débito:");
		painelContent.add(lblTotalMovimentadoDebito, "cell 0 3,alignx left");

		txMovimentoDebito = new JMoneyField();
		txMovimentoDebito.setEditable(false);
		txMovimentoDebito.setForeground(Color.RED);
		painelContent.add(txMovimentoDebito, "cell 1 3,growx");

		lblTotalDeCrditos = new JLabel("Total de Créditos:");
		painelContent.add(lblTotalDeCrditos, "cell 0 4,alignx left");

		txTotalCredito = new JMoneyField();
		txTotalCredito.setEditable(false);
		txTotalCredito.setForeground(Color.BLUE);
		painelContent.add(txTotalCredito, "cell 1 4,growx");

		lblTotalDeDbitos = new JLabel("Total de Débitos:");
		painelContent.add(lblTotalDeDbitos, "cell 0 5,alignx left");

		txTotalDebito = new JMoneyField();
		txTotalDebito.setEditable(false);
		txTotalDebito.setForeground(Color.RED);
		painelContent.add(txTotalDebito, "cell 1 5,growx");

		lblSaldoMovimentado = new JLabel("Saldo Movimentado:");
		painelContent.add(lblSaldoMovimentado, "cell 0 6,alignx left");

		txSaldoMovimentado = new JMoneyField();
		txSaldoMovimentado.setEditable(false);
		painelContent.add(txSaldoMovimentado, "cell 1 6,growx");

		lblSaldoDeContas = new JLabel("Saldo de Contas:");
		painelContent.add(lblSaldoDeContas, "cell 0 7,alignx left");

		txSaldoContas = new JMoneyField();
		txSaldoContas.setEditable(false);
		painelContent.add(txSaldoContas, "cell 1 7,growx");

		lblSobrafalta = new JLabel("Sobra/Falta:");
		painelContent.add(lblSobrafalta, "cell 0 8,alignx left");

		txSobraFalta = new JMoneyField();
		txSobraFalta.setEditable(false);
		painelContent.add(txSobraFalta, "cell 1 8,growx");

		panel = new JPanel();
		painelContent.add(panel, "cell 0 9 2 1,growx,aligny bottom");

		btnFechar = new JButton("Fechar");
		btnFechar.addActionListener(e -> dispose());
		panel.add(btnFechar);

		preencherCampos();
	}

	private void preencherCampos() {

		try {

			ResumoCaixaVO resumoCaixaVO = resumoCaixaService.obterResumoCaixa(FrmApplication.getUsuario());
			lbDescricao.setText(resumoCaixaVO.getDescricao());
			dtCaixa.setDate(resumoCaixaVO.getDataCaixa());

			txMovimentoCredito.setValue(resumoCaixaVO.getValorMovimentoCredito());
			txMovimentoDebito.setValue(resumoCaixaVO.getValorMovimentoDebito());
			txTotalCredito.setValue(resumoCaixaVO.getValorPagamentosCredito());
			txTotalDebito.setValue(resumoCaixaVO.getValorPagamentosDebito());

			txSaldoMovimentado.setValue(txMovimentoCredito.getValue().subtract(txMovimentoDebito.getValue()));
			txSaldoContas.setValue(txTotalCredito.getValue().subtract(txTotalDebito.getValue()));
			txSobraFalta.setValue(txSaldoContas.getValue().subtract(txSaldoMovimentado.getValue()));

		} catch (SysDescException e) {

			JOptionPane.showMessageDialog(this, e.getMensagem());
		}
	}

}
