package br.com.lar.ui;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.com.lar.service.caixa.ResumoCaixaService;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JMoneyField;
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

	public FrmResumoCaixa(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(370, 249);
		setClosable(Boolean.TRUE);
		setTitle("RESUMO DE CAIXA");

		painelContent = new JPanel();

		painelContent.setLayout(new MigLayout("", "[238.00,grow][grow]", "[][][][][][][][grow]"));
		getContentPane().add(painelContent);

		JLabel lblTotalMovimentado = new JLabel("Total Movimentado Crédito:");
		painelContent.add(lblTotalMovimentado, "cell 0 0,alignx left");

		txMovimentoCredito = new JMoneyField();
		txMovimentoCredito.setForeground(Color.BLUE);
		painelContent.add(txMovimentoCredito, "cell 1 0,growx");

		JLabel lblTotalMovimentadoDebito = new JLabel("Total Movimentado Débito:");
		painelContent.add(lblTotalMovimentadoDebito, "cell 0 1,alignx left");

		txMovimentoDebito = new JMoneyField();
		txMovimentoDebito.setForeground(Color.RED);
		painelContent.add(txMovimentoDebito, "cell 1 1,growx");

		lblTotalDeCrditos = new JLabel("Total de Créditos:");
		painelContent.add(lblTotalDeCrditos, "cell 0 2,alignx left");

		txTotalCredito = new JMoneyField();
		txTotalCredito.setForeground(Color.BLUE);
		painelContent.add(txTotalCredito, "cell 1 2,growx");

		lblTotalDeDbitos = new JLabel("Total de Débitos:");
		painelContent.add(lblTotalDeDbitos, "cell 0 3,alignx left");

		txTotalDebito = new JMoneyField();
		txTotalDebito.setForeground(Color.RED);
		painelContent.add(txTotalDebito, "cell 1 3,growx");

		lblSaldoMovimentado = new JLabel("Saldo Movimentado:");
		painelContent.add(lblSaldoMovimentado, "cell 0 4,alignx left");

		txSaldoMovimentado = new JMoneyField();
		painelContent.add(txSaldoMovimentado, "cell 1 4,growx");

		lblSaldoDeContas = new JLabel("Saldo de Contas:");
		painelContent.add(lblSaldoDeContas, "cell 0 5,alignx left");

		txSaldoContas = new JMoneyField();
		painelContent.add(txSaldoContas, "cell 1 5,growx");

		lblSobrafalta = new JLabel("Sobra/Falta:");
		painelContent.add(lblSobrafalta, "cell 0 6,alignx left");

		txSobraFalta = new JMoneyField();
		painelContent.add(txSobraFalta, "cell 1 6,growx");

		panel = new JPanel();
		painelContent.add(panel, "cell 0 7 2 1,growx,aligny bottom");

		btnFechar = new JButton("Fechar");
		btnFechar.addActionListener(e -> dispose());
		panel.add(btnFechar);

		preencherCampos();
	}

	private void preencherCampos() {
		ResumoCaixaVO resumoCaixaVO = resumoCaixaService.obterResumoCaixa(FrmApplication.getUsuario());
		txMovimentoCredito.setValue(resumoCaixaVO.getValorMovimentoCredito());
		txTotalCredito.setValue(resumoCaixaVO.getValorPagamentosCredito());

		txSaldoMovimentado.setValue(txMovimentoCredito.getValue().subtract(txMovimentoDebito.getValue()));
		txSaldoContas.setValue(txTotalCredito.getValue().subtract(txTotalDebito.getValue()));
		txSobraFalta.setValue(txSaldoContas.getValue().subtract(txSaldoMovimentado.getValue()));
	}

}
