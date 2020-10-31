package br.com.lar.ui;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import br.com.lar.repository.model.CaixaCabecalho;
import br.com.lar.service.caixa.CaixaCabecalhoService;
import br.com.lar.service.caixa.ResumoCaixaService;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.vo.ResumoCaixaVO;
import net.miginfocom.swing.MigLayout;

public class FrmFechamentoCaixa extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;
	private JTextFieldMaiusculo txCaixa;
	private JTextField txDataAbertura;
	private JMoneyField txValorDinheiro;
	private JButton btnFechar;

	private CaixaCabecalhoService caixaCabecalhoService = new CaixaCabecalhoService();
	private ResumoCaixaService resumoCaixaService = new ResumoCaixaService();
	private CaixaCabecalho caixaCabecalho;

	public FrmFechamentoCaixa(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(216, 230);
		setClosable(Boolean.TRUE);
		setTitle("FECHAMENTO DE CAIXA");

		painelContent = new JPanel();

		painelContent.setLayout(new MigLayout("", "[grow]", "[][][][][][][grow]"));
		getContentPane().add(painelContent);

		JLabel lblCaixa = new JLabel("Caixa:");
		JLabel lblDataDeAbertura = new JLabel("Data de Abertura:");
		JLabel lblValorEmDinheiro = new JLabel("Valor em Dinheiro:");
		txCaixa = new JTextFieldMaiusculo();
		txDataAbertura = new JTextField();
		txValorDinheiro = new JMoneyField();

		btnFechar = new JButton("Fechar");

		txCaixa.setEditable(false);
		txDataAbertura.setEditable(false);
		btnFechar.addActionListener(e -> fecharCaixa());

		painelContent.add(lblCaixa, "cell 0 0");
		painelContent.add(lblDataDeAbertura, "cell 0 2");
		painelContent.add(lblValorEmDinheiro, "cell 0 4");
		painelContent.add(txCaixa, "cell 0 1,growx");
		painelContent.add(txDataAbertura, "cell 0 3,growx");
		painelContent.add(txValorDinheiro, "cell 0 5,growx");
		painelContent.add(btnFechar, "cell 0 6,alignx center");

		carregarCampos();
	}

	private void fecharCaixa() {

		try {
			caixaCabecalhoService.fecharCaixa(caixaCabecalho);

			JOptionPane.showMessageDialog(null, "Caixa fechado com sucesso");

			FrmApplication.getInstance().setarLabelCaixa();

			dispose();

		} catch (SysDescException e) {
			JOptionPane.showMessageDialog(this, e.getMensagem());
		}
	}

	private void carregarCampos() {

		try {

			caixaCabecalho = caixaCabecalhoService.validarExistenciaCaixaAberto(FrmApplication.getUsuario());

			txCaixa.setText(caixaCabecalho.getCaixa().getDescricao());
			txDataAbertura.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(caixaCabecalho.getDataAbertura()));
			txValorDinheiro.setValue(calcularValorDinheiro());
		} catch (Exception e) {

			JOptionPane.showMessageDialog(this, "Não existe caixa para ser fechado");

			btnFechar.setEnabled(false);
		}

	}

	private BigDecimal calcularValorDinheiro() {

		ResumoCaixaVO resumoCaixaVO = resumoCaixaService.obterResumoCaixaSemValorDinheiro(caixaCabecalho);

		return resumoCaixaVO.getValorMovimentoCredito().subtract(resumoCaixaVO.getValorPagamentosCredito());
	}

}
