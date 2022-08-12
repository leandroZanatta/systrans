package br.com.lar.ui;

import static br.com.lar.repository.model.QConfiguracaoAbastecimento.configuracaoAbastecimento;
import static br.com.sysdesc.util.resources.Resources.FRMABASTECIMENTO_TITLE;
import static br.com.sysdesc.util.resources.Resources.translate;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.model.AbastecimentoPagamento;
import br.com.lar.repository.model.AbastecimentoVeiculo;
import br.com.lar.repository.model.Cliente;
import br.com.lar.repository.model.ConfiguracaoAbastecimento;
import br.com.lar.repository.model.FormasPagamento;
import br.com.lar.repository.model.Veiculo;
import br.com.lar.service.abastecimento.AbastecimentoVeiculoService;
import br.com.lar.service.abastecimento.ConfiguracaoAbastecimentoService;
import br.com.lar.service.cliente.ClienteService;
import br.com.lar.service.veiculo.VeiculoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.lar.tablemodels.AbastecimentoPagamentoTableModel;
import br.com.lar.ui.buttonactions.ButtonActionConfigurarServidor;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JDateFieldColumn;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.components.JNumericField;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.components.JmoneyFieldColumn;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.classes.ContadorUtil;
import br.com.sysdesc.util.classes.DateUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;
import net.miginfocom.swing.MigLayout;

public class FrmAbastecimento extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JLabel lbCodigo;
	private JLabel lbVeiculo;
	private JLabel lbKmInicial;
	private JLabel lbKmFinal;
	private JLabel lbLitrosAbastecidos;
	private JLabel lbValorTotal;
	private JLabel lbConfiguracaoAbastecimento;
	private JTextFieldId txCodigo;
	private JNumericField txKmInicial;
	private JNumericField txKmFinal;
	private JMoneyField txLitrosAbastecidos;
	private JMoneyField txValorTotal;

	private CampoPesquisa<Veiculo> pesquisaVeiculo;
	private CampoPesquisa<Cliente> pesquisaCliente;
	private CampoPesquisa<ConfiguracaoAbastecimento> pesquisaConfiguracaoAbastecimento;

	private JPanel painelContent;
	private PanelActions<AbastecimentoVeiculo> panelActions;
	private ButtonActionConfigurarServidor configurarServidor;
	private VeiculoService veiculoService = new VeiculoService();
	private ConfiguracaoAbastecimentoService configuracaoAbastecimentoService = new ConfiguracaoAbastecimentoService();
	private AbastecimentoVeiculoService abastecimentoVeiculoService = new AbastecimentoVeiculoService();
	private ClienteService clienteService = new ClienteService();
	private AbastecimentoPagamentoTableModel abastecimentoPagamentoTableModel = new AbastecimentoPagamentoTableModel();

	private JLabel lbFornecedor;
	private JPanel panel;
	private JLabel lbFormaPagamento;
	private JLabel lbParcelas;
	private JLabel lbDiaPagamento;
	private JTextField txFormaPagamento;
	private JNumericField txParcelas;
	private JNumericField txDias;
	private JScrollPane scrollPane;
	private JButton btGerarParcelas;
	private JTable table;
	private JLabel lbDocumento;
	private JTextField txDocumento;
	private JCheckBox chAbastecimentoParcial;

	public FrmAbastecimento(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(550, 500);
		setClosable(Boolean.TRUE);
		setTitle(translate(FRMABASTECIMENTO_TITLE));

		painelContent = new JPanel();
		lbCodigo = new JLabel("Código: ");

		lbConfiguracaoAbastecimento = new JLabel("Configuração de Abastecimento:");
		lbKmInicial = new JLabel("Km Inicial:");
		lbFornecedor = new JLabel("Fornecedor:");

		txCodigo = new JTextFieldId();
		txKmInicial = new JNumericField();

		panel = new JPanel();
		lbFormaPagamento = new JLabel("Forma de Pagamento");
		lbParcelas = new JLabel("Parcelas");
		lbDiaPagamento = new JLabel("Dia:");
		txFormaPagamento = new JTextField();
		txFormaPagamento.setEnabled(false);
		txParcelas = new JNumericField();
		txDias = new JNumericField();
		btGerarParcelas = new JButton("Gerar");

		table = new JTable(abastecimentoPagamentoTableModel);
		scrollPane = new JScrollPane(table);

		InputMap inputMap = table.getInputMap(WHEN_FOCUSED);
		ActionMap actionMap = table.getActionMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
		actionMap.put("delete", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent evt) {
				if (table.getSelectedRowCount() == 1) {
					abastecimentoPagamentoTableModel.deleteRow(table.getSelectedRow());
				}
			}
		});

		btGerarParcelas.addActionListener(e -> gerarPagamento());

		new JmoneyFieldColumn(table, 3);
		new JDateFieldColumn(table, 1);

		panel.setBorder(
				new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Pagamento", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		panel.setLayout(new MigLayout("", "[grow][50px:50px:50px][50px:50px:50px][]", "[][][]"));

		pesquisaCliente = new CampoPesquisa<Cliente>(clienteService, PesquisaEnum.PES_CLIENTES.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Cliente objeto) {

				return String.format("%d - %s", objeto.getIdCliente(), objeto.getNome());
			}
		};

		pesquisaConfiguracaoAbastecimento = new CampoPesquisa<ConfiguracaoAbastecimento>(configuracaoAbastecimentoService,
				PesquisaEnum.PES_CONFIGURACAO_ABASTECIMENTO.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(ConfiguracaoAbastecimento objeto) {

				return String.format("%d - %s - %s", objeto.getIdConfiguracaoAbastecimento(), objeto.getOperacao().getHistorico().getDescricao(),
						objeto.getOperacao().getFormasPagamento().getDescricao());
			}

			@Override
			public com.mysema.query.BooleanBuilder getPreFilter() {

				return new BooleanBuilder(configuracaoAbastecimento.veiculo.eq(pesquisaVeiculo.getObjetoPesquisado()));
			};

			@Override
			public boolean validar() {
				if (pesquisaVeiculo.getObjetoPesquisado() == null) {
					JOptionPane.showMessageDialog(this, "Selecione um veículo!");
					return false;
				}
				return true;
			};
		};

		pesquisaConfiguracaoAbastecimento.addChangeListener(configuracaoAbastecimento -> alterouConfiguracaoAbastecimento(configuracaoAbastecimento));
		configurarServidor = new ButtonActionConfigurarServidor();

		painelContent.setLayout(new MigLayout("", "[][grow][grow][grow]", "[][][][][][][][][][][][][40px:40px:40px]"));
		getContentPane().add(painelContent);

		Action actionAlterarSenha = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				FrmApplication.getInstance().posicionarFrame(new FrmConfigurarAbastecimentos(getCodigoPrograma(), getCodigoUsuario()), null);
			}

		};

		painelContent.add(lbCodigo, "cell 0 0");
		painelContent.add(txCodigo, "cell 0 1,growx");
		painelContent.add(lbFornecedor, "cell 0 2");
		lbValorTotal = new JLabel("Valor Total");
		painelContent.add(lbValorTotal, "cell 0 4");
		painelContent.add(pesquisaCliente, "cell 0 3 4 1,grow");
		lbVeiculo = new JLabel("Veículo:");
		painelContent.add(lbVeiculo, "cell 1 4");
		txValorTotal = new JMoneyField();

		txValorTotal.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {

				gerarLancamentoPagamento();
			}
		});
		painelContent.add(txValorTotal, "cell 0 5,growx");

		pesquisaVeiculo = new CampoPesquisa<Veiculo>(veiculoService, PesquisaEnum.PES_VEICULOS.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Veiculo objeto) {

				return String.format("%d - %s", objeto.getIdVeiculo(), objeto.getPlaca());
			}
		};

		pesquisaVeiculo.addChangeListener(this::alimentarCampos);
		painelContent.add(pesquisaVeiculo, "cell 1 5 3 1,growx");
		painelContent.add(lbConfiguracaoAbastecimento, "cell 0 6,alignx left,aligny center");
		painelContent.add(pesquisaConfiguracaoAbastecimento, "cell 0 7 4 1,grow");
		painelContent.add(lbKmInicial, "cell 0 8,aligny center");
		lbKmFinal = new JLabel("Km Final:");
		painelContent.add(lbKmFinal, "cell 1 8,aligny center");
		lbLitrosAbastecidos = new JLabel("Litros Abastecidos");
		painelContent.add(lbLitrosAbastecidos, "cell 2 8");

		lbDocumento = new JLabel("Documento:");
		painelContent.add(lbDocumento, "cell 3 8");
		painelContent.add(txKmInicial, "cell 0 9,growx");
		txKmFinal = new JNumericField();
		painelContent.add(txKmFinal, "cell 1 9,growx");
		txLitrosAbastecidos = new JMoneyField(3);
		painelContent.add(txLitrosAbastecidos, "cell 2 9,growx");

		txDocumento = new JTextField();
		painelContent.add(txDocumento, "cell 3 9,growx");

		chAbastecimentoParcial = new JCheckBox("Abastecimento Parcial");
		painelContent.add(chAbastecimentoParcial, "cell 0 10");
		painelContent.add(panel, "cell 0 11 4 1,grow");

		panel.add(lbFormaPagamento, "cell 0 0");
		panel.add(lbParcelas, "cell 1 0,alignx left");
		panel.add(lbDiaPagamento, "cell 2 0");
		panel.add(txFormaPagamento, "cell 0 1,growx");
		panel.add(txParcelas, "cell 1 1,growx");
		panel.add(txDias, "cell 2 1,growx");
		panel.add(btGerarParcelas, "cell 3 1,alignx right");
		panel.add(scrollPane, "cell 0 2 4 1,grow");

		panelActions = new PanelActions<AbastecimentoVeiculo>(this, abastecimentoVeiculoService,
				PesquisaEnum.PES_ABASTECIMENTO_VEICULO.getCodigoPesquisa(), Boolean.TRUE, configurarServidor) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void posicionarBotoes() {

				ContadorUtil contadorUtil = new ContadorUtil();

				posicionarBotao(contadorUtil, btPrimeiro, Boolean.TRUE);
				posicionarBotao(contadorUtil, btRetroceder, Boolean.TRUE);
				posicionarBotao(contadorUtil, configurarServidor, Boolean.TRUE);
				posicionarBotao(contadorUtil, btSalvar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btEditar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btNovo, Boolean.TRUE);
				posicionarBotao(contadorUtil, btBuscar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btCancelar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btAvancar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btUltimo, Boolean.TRUE);
			}

			@Override
			protected void registrarEventosBotoesPagina() {
				registrarEvento(configurarServidor, actionAlterarSenha);
			}

			@Override
			public void carregarObjeto(AbastecimentoVeiculo objeto) {

				txCodigo.setValue(objetoPesquisa.getIdAbastecimentoVeiculo());
				pesquisaVeiculo.setValue(objeto.getConfiguracaoAbastecimento().getVeiculo());
				pesquisaConfiguracaoAbastecimento.setValue(objeto.getConfiguracaoAbastecimento());
				txKmInicial.setValue(objeto.getKilometroInicial());
				txKmFinal.setValue(objeto.getKilometroFinal());
				txLitrosAbastecidos.setValue(objeto.getLitrosAbastecidos());
				txValorTotal.setValue(objeto.getValorAbastecimento());
				pesquisaCliente.setValue(objeto.getCliente());
				txDocumento.setText(objeto.getNumeroDocumento());
				chAbastecimentoParcial.setSelected(objeto.getAbastecimentoParcial());

				abastecimentoPagamentoTableModel.setRows(objeto.getAbastecimentoPagamentos());
			}

			@Override
			public boolean preencherObjeto(AbastecimentoVeiculo objetoPesquisa) {
				objetoPesquisa.setIdAbastecimentoVeiculo(txCodigo.getValue());
				objetoPesquisa.setConfiguracaoAbastecimento(pesquisaConfiguracaoAbastecimento.getObjetoPesquisado());
				objetoPesquisa.setKilometroInicial(txKmInicial.getValue());
				objetoPesquisa.setKilometroFinal(txKmFinal.getValue());
				objetoPesquisa.setLitrosAbastecidos(txLitrosAbastecidos.getValue());
				objetoPesquisa.setValorAbastecimento(txValorTotal.getValue());
				objetoPesquisa.setUsuario(FrmApplication.getUsuario());
				objetoPesquisa.setCliente(pesquisaCliente.getObjetoPesquisado());
				objetoPesquisa.setNumeroDocumento(txDocumento.getText());
				objetoPesquisa.setAbastecimentoPagamentos(abastecimentoPagamentoTableModel.getRows());
				objetoPesquisa.setAbastecimentoParcial(chAbastecimentoParcial.isSelected());

				objetoPesquisa.getAbastecimentoPagamentos()
						.forEach(abastecimentoPagamento -> abastecimentoPagamento.setAbastecimentoVeiculo(objetoPesquisa));

				return true;
			}

		};

		painelContent.add(panelActions, "cell 0 12 4 1,growx,aligny bottom");
	}

	private void gerarPagamento() {

		BigDecimal valorPagamento = txValorTotal.getValue().subtract(abastecimentoPagamentoTableModel.getTotalPagamentos());

		try {

			validarPagamento(valorPagamento);

			Date dataVencimento = new Date();
			Date dataLancamento = new Date();

			for (Long parcela = 1L; parcela <= txParcelas.getValue(); parcela++) {

				AbastecimentoPagamento abastecimentoPagamento = new AbastecimentoPagamento();
				abastecimentoPagamento.setDataLancamento(dataLancamento);
				abastecimentoPagamento.setNumeroParcela(parcela);
				abastecimentoPagamento.setValorParcela(obterValorParcela(parcela, txValorTotal.getValue()));

				dataVencimento = DateUtil.setDay(
						DateUtil.addDays(dataVencimento,
								pesquisaConfiguracaoAbastecimento.getObjetoPesquisado().getOperacao().getFormasPagamento().getNumeroDiasPagamento()),
						txDias.getValue());

				abastecimentoPagamento.setDataVencimento(dataVencimento);

				abastecimentoPagamentoTableModel.addRow(abastecimentoPagamento);
			}

		} catch (SysDescException e) {

			JOptionPane.showMessageDialog(this, e.getMensagem());
		}
	}

	private BigDecimal obterValorParcela(Long parcela, BigDecimal valorPagamento) {

		if (txParcelas.getValue().equals(1L)) {

			return valorPagamento;
		}

		if (parcela.equals(txParcelas.getValue())) {

			return txValorTotal.getValue().subtract(abastecimentoPagamentoTableModel.getTotalPagamentos()).setScale(2, RoundingMode.HALF_EVEN);
		}

		return valorPagamento.divide(BigDecimal.valueOf(txParcelas.getValue()), 2, RoundingMode.HALF_EVEN);
	}

	private void validarPagamento(BigDecimal valorPagamento) {

		if (BigDecimalUtil.menor(valorPagamento, BigDecimal.ZERO)) {

			throw new SysDescException(MensagemConstants.MENSAGEM_PAGAMENTOS_MAIOR_FATURAMENTO);
		}

		if (BigDecimalUtil.isNullOrZero(valorPagamento)) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SEM_FATURAMENTO_PAGAMENTOS);
		}
	}

	private void alterouConfiguracaoAbastecimento(ConfiguracaoAbastecimento configuracaoAbastecimento) {

		txFormaPagamento.setText("");
		btGerarParcelas.setEnabled(true);

		if (configuracaoAbastecimento != null) {

			FormasPagamento formasPagamento = configuracaoAbastecimento.getOperacao().getFormasPagamento();

			txFormaPagamento.setText(formasPagamento.getDescricao());
			btGerarParcelas.setEnabled(formasPagamento.isFlagPermitePagamentoPrazo());
			txDias.setEnabled(formasPagamento.isFlagPermitePagamentoPrazo());
			txParcelas.setEnabled(formasPagamento.isFlagPermitePagamentoPrazo());

			gerarLancamentoPagamento();

			if (formasPagamento.isFlagPermitePagamentoPrazo()) {

				txParcelas.setValue(1L);

				txDias.setValue(DateUtil.getDayOfMonth(new Date()).longValue());
			}
		}
	}

	private void gerarLancamentoPagamento() {

		if (!BigDecimalUtil.isNullOrZero(txValorTotal.getValue()) && pesquisaConfiguracaoAbastecimento.getObjetoPesquisado() != null
				&& !pesquisaConfiguracaoAbastecimento.getObjetoPesquisado().getOperacao().getFormasPagamento().isFlagPermitePagamentoPrazo()) {
			abastecimentoPagamentoTableModel.clear();

			AbastecimentoPagamento abastecimentoPagamento = new AbastecimentoPagamento();
			abastecimentoPagamento.setDataLancamento(new Date());
			abastecimentoPagamento.setDataVencimento(new Date());
			abastecimentoPagamento.setNumeroParcela(1L);
			abastecimentoPagamento.setValorParcela(txValorTotal.getValue());

			abastecimentoPagamentoTableModel.addRow(abastecimentoPagamento);
		}
	}

	private void alimentarCampos(Veiculo veiculo) {

		if (veiculo == null) {
			zerarCampos();

			return;
		}

		List<ConfiguracaoAbastecimento> confAbastecimentos = configuracaoAbastecimentoService.buscarConfiguracaoPorVeiculo(veiculo.getIdVeiculo());

		if (confAbastecimentos.isEmpty()) {

			JOptionPane.showMessageDialog(this, "Configuração de abastecimentos não encontrada para o veículo", "VERIFICAÇÃO",
					JOptionPane.WARNING_MESSAGE);

			zerarCampos();

			return;
		}

		if (confAbastecimentos.size() == 1) {

			pesquisaConfiguracaoAbastecimento.setValue(confAbastecimentos.get(0));
		}

		txKmInicial.setValue(abastecimentoVeiculoService.buscarUltimaKilometragemVeiculo(veiculo.getIdVeiculo()));
		txKmFinal.requestFocus();
	}

	private void zerarCampos() {
		txKmInicial.setValue(null);
		txKmFinal.setValue(null);
		pesquisaConfiguracaoAbastecimento.setValue(null);
		txLitrosAbastecidos.setValue(BigDecimal.ZERO);
		txValorTotal.setValue(BigDecimal.ZERO);
	}

}
