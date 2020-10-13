package br.com.lar.ui;

import java.awt.Color;
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import br.com.lar.repository.model.FormasPagamento;
import br.com.lar.service.formaspagamento.FormasPagamentoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.components.JNumericField;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.sysdesc.util.enumeradores.FormaPagamentoEnum;
import net.miginfocom.swing.MigLayout;

public class FrmFormasPagamento extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private final JLabel label = new JLabel("");
	private JTextFieldId txCodigo;
	private JTextFieldMaiusculo txDescricao;
	private JNumericField txNumeroMaximoParcelas;
	private JMoneyField txValorMinimoPagamento;
	private JNumericField txDiasPagamento;
	private JLabel lblCdigo;
	private JLabel lblDescrio;
	private JLabel lblFormasDePagamento;
	private JComboBox<FormaPagamentoEnum> cbFormaPagamento;
	private JPanel panel_1;
	private JCheckBox chPermiteTroco;
	private JCheckBox chUsaTef;
	private JPanel panel;
	private JCheckBox chPermitePagamentoPrazo;
	private JLabel lblNmeroDeParcelas;
	private JLabel lblValorMnimoPagamento;
	private JLabel lblDiasPagamento;
	private PanelActions<FormasPagamento> panelActions;

	private FormasPagamentoService formasPagamentoService = new FormasPagamentoService();

	private JLabel lblBanco;

	public FrmFormasPagamento(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);
		setTitle("CADASTRO DE FORMAS DE PAGAMENTO");

		setSize(460, 485);
		setClosable(Boolean.TRUE);
		getContentPane().setLayout(new MigLayout("", "[280.00px,grow]", "[1px][][][][23.00][][][][][][][grow]"));
		getContentPane().add(label, "cell 0 0,growx,aligny top");

		lblCdigo = new JLabel("Código:");
		lblDescrio = new JLabel("Descrição:");
		lblFormasDePagamento = new JLabel("Forma de pagamento:");
		lblNmeroDeParcelas = new JLabel("Número Máximo de Parcelas:");
		lblDiasPagamento = new JLabel("Dias pagamento:");
		lblValorMnimoPagamento = new JLabel("Valor mínimo pagamento:");
		lblBanco = new JLabel("Banco:");

		txCodigo = new JTextFieldId();
		txDescricao = new JTextFieldMaiusculo();

		cbFormaPagamento = new JComboBox<>();
		chPermiteTroco = new JCheckBox("Permite Troco");
		chPermitePagamentoPrazo = new JCheckBox("Permite Pagamentos á Prazo");

		chUsaTef = new JCheckBox("Usa Tef");
		txNumeroMaximoParcelas = new JNumericField();
		txValorMinimoPagamento = new JMoneyField();
		txDiasPagamento = new JNumericField();

		panel_1 = new JPanel();
		panel = new JPanel();

		panel_1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Permiss\u00F5es",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_1.setLayout(new MigLayout("", "[grow][grow]", "[]"));
		panel.setLayout(new MigLayout("", "[122.00px][grow]", "[23px][][][]"));
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Pagamentos A Prazo",
				TitledBorder.CENTER, TitledBorder.TOP, null, null));

		Arrays.asList(FormaPagamentoEnum.values()).forEach(cbFormaPagamento::addItem);
		chPermitePagamentoPrazo.addActionListener((e) -> bloquearPagamentos());
		cbFormaPagamento.addActionListener((e) -> bloquearComponentePagamentos());

		panel_1.add(chPermiteTroco, "cell 0 0");
		panel_1.add(chUsaTef, "cell 1 0");

		panel.add(lblNmeroDeParcelas, "cell 0 1,alignx trailing");
		panel.add(lblValorMnimoPagamento, "cell 0 2,alignx trailing");
		panel.add(lblDiasPagamento, "cell 0 3,alignx trailing");
		panel.add(chPermitePagamentoPrazo, "cell 0 0 2 1,alignx left,aligny top");
		panel.add(txNumeroMaximoParcelas, "cell 1 1,growx");
		panel.add(txValorMinimoPagamento, "cell 1 2,growx");
		panel.add(txDiasPagamento, "cell 1 3,growx");

		getContentPane().add(cbFormaPagamento, "cell 0 4,grow");
		getContentPane().add(lblCdigo, "cell 0 1");
		getContentPane().add(lblDescrio, "cell 0 5");
		getContentPane().add(lblFormasDePagamento, "cell 0 3");
		getContentPane().add(lblBanco, "cell 0 7");
		getContentPane().add(txCodigo, "cell 0 2,,width 50:100:100");
		getContentPane().add(txDescricao, "cell 0 6,growx");

		getContentPane().add(panel_1, "cell 0 9,grow");
		getContentPane().add(panel, "cell 0 10,grow");

		panelActions = new PanelActions<FormasPagamento>(this, formasPagamentoService,
				PesquisaEnum.PES_FORMAS_PAGAMENTO.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void carregarObjeto(FormasPagamento objeto) {
				txCodigo.setValue(objeto.getIdFormaPagamento());
				txDescricao.setText(objeto.getDescricao());
				cbFormaPagamento.setSelectedItem(FormaPagamentoEnum.forCodigo(objeto.getCodigoFormaPagamento()));
				chPermiteTroco.setSelected(objeto.isFlagPermiteTroco());
				chUsaTef.setSelected(objeto.isFlagUsaTEF());
				chPermitePagamentoPrazo.setSelected(objeto.isFlagPermitePagamentoPrazo());
				txDiasPagamento.setValue(objeto.getNumeroDiasPagamento());
				txValorMinimoPagamento.setValue(objeto.getValorMinimoParcelas());
				txNumeroMaximoParcelas.setValue(objeto.getNumeroMaximoParcelas());
			}

			@Override
			public boolean preencherObjeto(FormasPagamento objetoPesquisa) {

				objetoPesquisa.setIdFormaPagamento(txCodigo.getValue());
				objetoPesquisa.setDescricao(txDescricao.getText());
				objetoPesquisa.setFlagPermiteTroco(chPermiteTroco.isSelected());
				objetoPesquisa.setFlagUsaTEF(chUsaTef.isSelected());
				objetoPesquisa.setFlagPermitePagamentoPrazo(chPermitePagamentoPrazo.isSelected());

				objetoPesquisa.setCodigoFormaPagamento(null);
				objetoPesquisa.setNumeroDiasPagamento(null);
				objetoPesquisa.setValorMinimoParcelas(null);
				objetoPesquisa.setNumeroMaximoParcelas(null);

				if (cbFormaPagamento.getSelectedIndex() >= 0) {

					objetoPesquisa.setCodigoFormaPagamento(
							((FormaPagamentoEnum) cbFormaPagamento.getSelectedItem()).getCodigo());
				}

				if (chPermitePagamentoPrazo.isSelected()) {

					objetoPesquisa.setNumeroDiasPagamento(txDiasPagamento.getValue());
					objetoPesquisa.setValorMinimoParcelas(txValorMinimoPagamento.getValue());
					objetoPesquisa.setNumeroMaximoParcelas(txNumeroMaximoParcelas.getValue());
				}

				return true;
			}
		};

		panelActions.addSaveListener((objeto) -> txCodigo.setValue(objeto.getIdFormaPagamento()));

		getContentPane().add(panelActions, "cell 0 11,growx,aligny bottom");
	}

	private void bloquearComponentePagamentos() {

		Boolean pagamentoAprazo = Boolean.FALSE;

		if (cbFormaPagamento.getSelectedIndex() >= 0) {
			pagamentoAprazo = ((FormaPagamentoEnum) cbFormaPagamento.getSelectedItem()).getPagamentoAPrazo();
		}

		chPermitePagamentoPrazo.setEnabled(pagamentoAprazo);
		txNumeroMaximoParcelas.setEnabled(pagamentoAprazo);
		txValorMinimoPagamento.setEnabled(pagamentoAprazo);
		txDiasPagamento.setEnabled(pagamentoAprazo);
	}

	private void bloquearPagamentos() {
		txNumeroMaximoParcelas.setEnabled(chPermitePagamentoPrazo.isSelected());
		txValorMinimoPagamento.setEnabled(chPermitePagamentoPrazo.isSelected());
		txDiasPagamento.setEnabled(chPermitePagamentoPrazo.isSelected());
	}
}
