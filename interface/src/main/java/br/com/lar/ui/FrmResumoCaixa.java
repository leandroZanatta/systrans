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
	private JPanel panel;
	private JLabel lblTotalDeCrditos;
	private JLabel lblTotalDeDbitos;
	private JLabel lblSaldoMovimentado;
	private JLabel lblSaldoDeContas;
	private JLabel lblSobrafalta;
	private JLabel lblDataDoCaixa;
	private JLabel lbDescricao;
	private JLabel lblTotalMovimentado;
	private JLabel lblTotalMovimentadoDebito;
	private JMoneyField txMovimentoCredito;
	private JMoneyField txMovimentoDebito;
	private JMoneyField txTotalCredito;
	private JMoneyField txTotalDebito;
	private JMoneyField txSaldoMovimentado;
	private JMoneyField txSaldoContas;
	private JMoneyField txSobraFalta;
	private JDateChooser dtCaixa;
	private JButton btnFechar;
	private ResumoCaixaService resumoCaixaService = new ResumoCaixaService();

	public FrmResumoCaixa(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(370, 318);
		setClosable(Boolean.TRUE);
		setTitle("RESUMO DE CAIXA");

		painelContent = new JPanel();
		lbDescricao = new JLabel("");
		lblDataDoCaixa = new JLabel("Data do Caixa:");
		dtCaixa = new JDateChooser("dd/MM/yyyy", "##/##/####", '_');
		lblTotalMovimentado = new JLabel("Total Movimentado Crédito:");
		txMovimentoCredito = new JMoneyField();
		lblTotalMovimentadoDebito = new JLabel("Total Movimentado Débito:");
		txMovimentoDebito = new JMoneyField();
		lblTotalDeCrditos = new JLabel("Total de Créditos:");
		txTotalCredito = new JMoneyField();
		lblTotalDeDbitos = new JLabel("Total de Débitos:");
		lblSaldoMovimentado = new JLabel("Saldo Movimentado:");
		txSaldoMovimentado = new JMoneyField();
		lblSaldoDeContas = new JLabel("Saldo de Contas:");
		txSaldoContas = new JMoneyField();
		lblSobrafalta = new JLabel("Sobra/Falta:");
		txSobraFalta = new JMoneyField();
		panel = new JPanel();
		txTotalDebito = new JMoneyField();
		btnFechar = new JButton("Fechar");

		painelContent.setLayout(new MigLayout("", "[238.00,grow][grow]", "[][][][][][][][][][grow]"));

		dtCaixa.setEnabled(false);
		txMovimentoCredito.setEditable(false);
		txMovimentoDebito.setEditable(false);
		txMovimentoCredito.setForeground(Color.BLUE);
		txMovimentoDebito.setForeground(Color.RED);
		txTotalCredito.setForeground(Color.BLUE);
		txTotalCredito.setEditable(false);
		txTotalDebito.setEditable(false);
		txTotalDebito.setForeground(Color.RED);
		txSaldoMovimentado.setEditable(false);
		txSaldoContas.setEditable(false);
		txSobraFalta.setEditable(false);
		btnFechar.addActionListener(e -> dispose());

		painelContent.add(lbDescricao, "cell 0 0 2 1,alignx center");
		painelContent.add(lblDataDoCaixa, "cell 0 1,alignx left");
		painelContent.add(dtCaixa, "cell 1 1,growx");
		painelContent.add(lblTotalMovimentado, "cell 0 2,alignx left");
		painelContent.add(txMovimentoCredito, "cell 1 2,growx");
		painelContent.add(lblTotalMovimentadoDebito, "cell 0 3,alignx left");
		painelContent.add(txMovimentoDebito, "cell 1 3,growx");
		painelContent.add(lblTotalDeCrditos, "cell 0 4,alignx left");
		painelContent.add(txTotalCredito, "cell 1 4,growx");
		painelContent.add(lblTotalDeDbitos, "cell 0 5,alignx left");
		painelContent.add(txTotalDebito, "cell 1 5,growx");
		painelContent.add(lblSaldoMovimentado, "cell 0 6,alignx left");
		painelContent.add(txSaldoMovimentado, "cell 1 6,growx");
		painelContent.add(lblSaldoDeContas, "cell 0 7,alignx left");
		painelContent.add(txSaldoContas, "cell 1 7,growx");
		painelContent.add(lblSobrafalta, "cell 0 8,alignx left");
		painelContent.add(txSobraFalta, "cell 1 8,growx");
		painelContent.add(panel, "cell 0 9 2 1,growx,aligny bottom");

		panel.add(btnFechar);

		getContentPane().add(painelContent);

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
