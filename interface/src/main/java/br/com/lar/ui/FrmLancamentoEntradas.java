package br.com.lar.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import com.mysema.query.BooleanBuilder;
import com.toedter.calendar.JDateChooser;

import br.com.lar.repository.model.CentroCusto;
import br.com.lar.repository.model.Cliente;
import br.com.lar.repository.model.FaturamentoEntradaPagamentos;
import br.com.lar.repository.model.FaturamentoEntradasCabecalho;
import br.com.lar.repository.model.FaturamentoEntradasDetalhe;
import br.com.lar.repository.model.FormasPagamento;
import br.com.lar.repository.model.Historico;
import br.com.lar.service.caixa.CaixaCabecalhoService;
import br.com.lar.service.centrocusto.CentroCustoService;
import br.com.lar.service.cliente.ClienteService;
import br.com.lar.service.faturamento.FaturamentoEntradaService;
import br.com.lar.service.formaspagamento.FormasPagamentoService;
import br.com.lar.service.historico.HistoricoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.lar.tablemodels.FaturamentoEntradasPagamentoTableModel;
import br.com.lar.tablemodels.FaturamentoEntradasTableModel;
import br.com.lar.ui.buttonactions.ButtonActionImagem;
import br.com.lar.ui.dialogs.FrmDocumentosContasReceber;
import br.com.lar.ui.dialogs.FrmEntrada;
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

public class FrmLancamentoEntradas extends AbstractInternalFrame {
	private static final String TEMPLATE_PESQUISA = "%d - %s";

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;
	private JTextFieldId txCodigo;
	private CampoPesquisa<Historico> pesquisaHistorico;
	private CampoPesquisa<Cliente> pesquisaCliente;
	private JDateChooser dtMovimento;
	private PanelActions<FaturamentoEntradasCabecalho> panelActions;
	private FaturamentoEntradaService faturamentoEntradaService = new FaturamentoEntradaService();
	private CentroCustoService centroCustoService = new CentroCustoService();
	private FaturamentoEntradasTableModel faturamentoEntradasTableModel = new FaturamentoEntradasTableModel();
	private HistoricoService historicoService = new HistoricoService();
	private ClienteService clienteService = new ClienteService();
	private ButtonActionImagem documentoEscaneados;
	private JLabel lblCliente_1;
	private JLabel lblValorDoFrete_1;
	private JMoneyField txValorBruto;
	private JScrollPane scrollPane_1;
	private JTable table_1;
	private JLabel lblDesconto;
	private JMoneyField txDesconto;
	private JMoneyField txAcrescimo;
	private JLabel lblAcrscimo;
	private JPanel panel;
	private JButton btAdd;
	private JButton btRemove;
	private JMoneyField txValorLiquido;
	private JLabel lblValorTotal;
	private JLabel lblCentroDeCustos;
	private CampoPesquisa<CentroCusto> pesquisaCentroCustos;
	private JTabbedPane tabbePane;
	private JPanel panelPrincipal;
	private JPanel painelPagamentos;
	private CampoPesquisa<FormasPagamento> pesquisaFormasPagamento;
	private JMoneyField txValorTotal;
	private JMoneyField txValorPagamento;
	private JMoneyField txValorDesconto;
	private JMoneyField txValorAcrescimo;
	private JNumericField txNumeroParcelas;
	private JNumericField txDiaPagamento;
	private JTable table;
	private FaturamentoEntradasPagamentoTableModel pagamentoTableModel = new FaturamentoEntradasPagamentoTableModel();
	private FormasPagamentoService formasPagamentoService = new FormasPagamentoService();
	private CaixaCabecalhoService caixaCabecalhoService = new CaixaCabecalhoService();
	private JPanel panel_1;
	private JScrollPane scrollPane_2;
	private JTextPane txObservacoes;

	public FrmLancamentoEntradas(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(600, 500);
		setClosable(Boolean.TRUE);
		setTitle("FATURAMENTO - ENTRADA - LANÇAMENTOS");

		painelContent = new JPanel();
		tabbePane = new JTabbedPane();
		panelPrincipal = new JPanel();
		painelPagamentos = new JPanel();

		painelContent.setLayout(new MigLayout("", "[grow]", "[][][grow][]"));
		panelPrincipal.setLayout(new MigLayout("", "[grow][grow][120.00,grow][grow][grow]", "[][20.00][16.00,grow][][][][][][grow][][grow][]"));

		tabbePane.addTab("Despesas", null, panelPrincipal, null);
		tabbePane.addTab("Pagamento", null, painelPagamentos, null);

		tabbePane.addChangeListener(e -> checarCamposObrigatorios());

		getContentPane().add(painelContent);
		painelContent.add(tabbePane, "cell 0 2,grow");

		panel_1 = new JPanel();
		tabbePane.addTab("Observações", null, panel_1, null);
		panel_1.setLayout(new BorderLayout(0, 0));

		scrollPane_2 = new JScrollPane();
		panel_1.add(scrollPane_2, BorderLayout.CENTER);

		txObservacoes = new JTextPane();
		scrollPane_2.setViewportView(txObservacoes);

		montarPainelPrincipal();
		montarPainelPagamentos();

		Action actionAlterarSalario = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				abrirScanner();
			}

		};

		panelActions = new PanelActions<FaturamentoEntradasCabecalho>(this, faturamentoEntradaService,
				PesquisaEnum.PES_FATURAMENTO_ENTRADA.getCodigoPesquisa(), documentoEscaneados) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void posicionarBotoes() {

				ContadorUtil contadorUtil = new ContadorUtil();

				posicionarBotao(contadorUtil, btPrimeiro, Boolean.TRUE);
				posicionarBotao(contadorUtil, btRetroceder, Boolean.TRUE);

				posicionarBotao(contadorUtil, documentoEscaneados, Boolean.TRUE);
				posicionarBotao(contadorUtil, btSalvar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btEditar, Boolean.FALSE);
				posicionarBotao(contadorUtil, btNovo, Boolean.TRUE);
				posicionarBotao(contadorUtil, btBuscar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btCancelar, Boolean.TRUE);

				posicionarBotao(contadorUtil, btAvancar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btUltimo, Boolean.TRUE);

			}

			@Override
			protected void registrarEventosBotoesPagina() {
				registrarEvento(documentoEscaneados, actionAlterarSalario);
			}

			@Override
			public void carregarObjeto(FaturamentoEntradasCabecalho objeto) {

				txCodigo.setValue(objeto.getIdFaturamentoEntradasCabecalho());
				dtMovimento.setDate(objeto.getDataMovimento());
				faturamentoEntradasTableModel.setRows(objeto.getFaturamentoEntradasDetalhes());
				pesquisaCliente.setValue(objeto.getCliente());
				pesquisaHistorico.setValue(objeto.getHistorico());
				pesquisaCentroCustos.setValue(objeto.getCentroCusto());

				pagamentoTableModel.setRows(objeto.getFaturamentoEntradaPagamentos());
				txObservacoes.setText(objeto.getObservacao());

				atualizarTotais();
			}

			@Override
			public boolean preencherObjeto(FaturamentoEntradasCabecalho objetoPesquisa) {
				objetoPesquisa.setIdFaturamentoEntradasCabecalho(txCodigo.getValue());
				objetoPesquisa.setValorBruto(txValorLiquido.getValue());
				objetoPesquisa.setDataMovimento(dtMovimento.getDate());
				objetoPesquisa.setFaturamentoEntradasDetalhes(faturamentoEntradasTableModel.getRows());
				objetoPesquisa.setCliente(pesquisaCliente.getObjetoPesquisado());
				objetoPesquisa.setHistorico(pesquisaHistorico.getObjetoPesquisado());
				objetoPesquisa.setCentroCusto(pesquisaCentroCustos.getObjetoPesquisado());

				objetoPesquisa.setFaturamentoEntradaPagamentos(pagamentoTableModel.getRows());
				objetoPesquisa.setCaixaCabecalho(caixaCabecalhoService.obterCaixaDoDia(FrmApplication.getUsuario()));
				objetoPesquisa.setObservacao(txObservacoes.getText());

				return true;
			}

		};

		panelActions.addSaveListener(faturamento -> {
			txCodigo.setValue(faturamento.getIdFaturamentoEntradasCabecalho());
			tabbePane.setSelectedIndex(0);
		});
		panelActions.addNewListener(faturamento -> dtMovimento.setDate(new Date()));

		painelContent.add(panelActions, "cell 0 14,growx");

	}

	private void montarPainelPrincipal() {
		lblCliente_1 = new JLabel("Cliente:");
		pesquisaCliente = new CampoPesquisa<Cliente>(clienteService, PesquisaEnum.PES_CLIENTES.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Cliente objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdCliente(), objeto.getNome());
			}
		};

		JLabel lblCdigo = new JLabel("Código:");
		JLabel lblHistorico = new JLabel("Histórico:");

		table_1 = new JTable(faturamentoEntradasTableModel);
		scrollPane_1 = new JScrollPane(table_1);
		txCodigo = new JTextFieldId();
		pesquisaHistorico = new CampoPesquisa<Historico>(historicoService, PesquisaEnum.PES_OPERACOES.getCodigoPesquisa(), getCodigoUsuario(),
				historicoService.getHistoricosDevedores()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Historico objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdHistorico(), objeto.getDescricao());
			}
		};

		pesquisaCentroCustos = new CampoPesquisa<CentroCusto>(centroCustoService, PesquisaEnum.PES_CENTRO_CUSTO.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			public String formatarValorCampo(CentroCusto objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdCentroCusto(), objeto.getDescricao());
			}
		};

		painelContent.add(lblCdigo, "cell 0 0");
		painelContent.add(txCodigo, "cell 0 1,,width 50:100:100");

		JLabel lblDataDoMovimento = new JLabel("Data do Movimento:");

		lblValorDoFrete_1 = new JLabel("Valor do Bruto:");

		lblDesconto = new JLabel("Desconto:");

		lblAcrscimo = new JLabel("Acréscimo:");

		lblValorTotal = new JLabel("Valor Total:");
		dtMovimento = new JDateChooser("dd/MM/yyyy HH:mm:ss", "##/##/##### ##:##:##", '_');

		txValorBruto = new JMoneyField();
		txValorBruto.setEnabled(false);

		txDesconto = new JMoneyField();
		txDesconto.setEnabled(false);

		txAcrescimo = new JMoneyField();
		txAcrescimo.setEnabled(false);

		txValorLiquido = new JMoneyField();
		txValorLiquido.setEnabled(false);
		panel = new JPanel();

		btAdd = new JButton("+");
		btAdd.addActionListener((e) -> adicionarDespesa());

		btRemove = new JButton("-");
		btRemove.addActionListener(e -> removeRow());
		documentoEscaneados = new ButtonActionImagem();

		lblCentroDeCustos = new JLabel("Centro de Custos:");

		panelPrincipal.add(lblCliente_1, "cell 0 1");
		panelPrincipal.add(pesquisaCliente, "cell 0 2 5 1,growx");

		panelPrincipal.add(dtMovimento, "cell 0 8");
		panelPrincipal.add(lblAcrscimo, "cell 3 7");
		panelPrincipal.add(lblDesconto, "cell 2 7");
		panelPrincipal.add(lblValorTotal, "cell 4 7");
		panelPrincipal.add(txValorBruto, "cell 1 8,growx");
		panelPrincipal.add(txDesconto, "cell 2 8,growx");
		panelPrincipal.add(lblHistorico, "cell 0 3");
		panelPrincipal.add(pesquisaHistorico, "cell 0 4 5 1,growx");
		panelPrincipal.add(lblCentroDeCustos, "cell 0 5");
		panelPrincipal.add(pesquisaCentroCustos, "cell 0 6 5 1,grow");
		panelPrincipal.add(lblDataDoMovimento, "cell 0 7");
		panelPrincipal.add(lblValorDoFrete_1, "cell 1 7");
		panelPrincipal.add(panel, "cell 0 11 5 1,alignx right,growy");
		panelPrincipal.add(txAcrescimo, "cell 3 8,growx");
		panelPrincipal.add(txValorLiquido, "cell 4 8,growx");
		panelPrincipal.add(scrollPane_1, "cell 0 10 5 1,grow");

		panel.add(btRemove);
		panel.add(btAdd);
	}

	private void checarCamposObrigatorios() {

		if (tabbePane.getSelectedIndex() == 1) {

			if (pesquisaHistorico.getObjetoPesquisado() == null) {
				JOptionPane.showMessageDialog(this, "Selecione um histórico");

				tabbePane.setSelectedIndex(0);

				pesquisaHistorico.requestFocus();

				return;
			}

			if (!BigDecimalUtil.maior(txValorBruto.getValue(), BigDecimal.ZERO)) {
				JOptionPane.showMessageDialog(this, "Insira o valor do faturamento");

				tabbePane.setSelectedIndex(0);

				txValorBruto.requestFocus();

				return;
			}

			if (dtMovimento.getDate() == null) {
				JOptionPane.showMessageDialog(this, "Insira a data de Movimento");

				tabbePane.setSelectedIndex(0);

				dtMovimento.requestFocus();

				return;
			}

			txValorPagamento.setValue(txValorBruto.getValue());

			calcularValorTotal();
		}
	}

	private void montarPainelPagamentos() {

		painelPagamentos.setLayout(new MigLayout("", "[grow][grow][grow][grow][][]", "[][][][][][]"));

		JLabel lblValorPagamento = new JLabel("Valor Pagamento:");
		JLabel lblDesconto = new JLabel("Desconto:");
		JLabel lblAcrescimo = new JLabel("Acréscimo:");
		JLabel lblValorTotal = new JLabel("Valor Total");
		JLabel lblFormaDePagamento = new JLabel("Forma de Pagamento:");
		JLabel lblNmeroDeParcelas = new JLabel("Nº Parcelas:");
		JLabel lblDiaDeVencimento = new JLabel("Dia:");

		txValorPagamento = new JMoneyField();
		txValorDesconto = new JMoneyField();
		txValorAcrescimo = new JMoneyField();
		txValorTotal = new JMoneyField();
		txNumeroParcelas = new JNumericField();
		txDiaPagamento = new JNumericField();
		pesquisaFormasPagamento = new CampoPesquisa<FormasPagamento>(formasPagamentoService, PesquisaEnum.PES_FORMAS_PAGAMENTO.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(FormasPagamento objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdFormaPagamento(), objeto.getDescricao());
			}

			@Override
			public BooleanBuilder getPreFilter() {

				return formasPagamentoService.buscarPagamentosComHistorico(pesquisaHistorico.getObjetoPesquisado().getIdHistorico());
			}

		};

		pesquisaFormasPagamento.addChangeListener(formaPagamento -> {

			if (tabbePane.getSelectedIndex() == 1) {

				boolean permitePrazo = formaPagamento != null && formaPagamento.isFlagPermitePagamentoPrazo();

				txNumeroParcelas.setEnabled(permitePrazo);
				txDiaPagamento.setEnabled(permitePrazo);

				txNumeroParcelas.setValue(1L);

				if (dtMovimento.getDate() != null) {

					txDiaPagamento.setValue(DateUtil.getDayOfMonth(dtMovimento.getDate()).longValue());
				}
			}
		});

		JButton btnGerar = new JButton("Gerar");
		table = new JTable(pagamentoTableModel);

		InputMap inputMap = table.getInputMap(WHEN_FOCUSED);
		ActionMap actionMap = table.getActionMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
		actionMap.put("delete", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent evt) {
				if (table.getSelectedRowCount() == 1) {
					pagamentoTableModel.deleteRow(table.getSelectedRow());
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(table);

		new JmoneyFieldColumn(table, 4);
		new JDateFieldColumn(table, 2);

		txValorPagamento.setEnabled(false);
		txValorTotal.setEnabled(false);
		txDiaPagamento.setEnabled(false);
		txNumeroParcelas.setEnabled(false);
		txValorAcrescimo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				calcularValorTotal();
			}
		});
		txValorDesconto.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				calcularValorTotal();
			}
		});

		table.getColumnModel().getColumn(0).setPreferredWidth(300);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
		table.getColumnModel().getColumn(3).setPreferredWidth(80);
		table.getColumnModel().getColumn(4).setPreferredWidth(100);

		btnGerar.addActionListener(e -> gerarPagamento());
		painelPagamentos.add(lblValorPagamento, "cell 0 0");
		painelPagamentos.add(lblDesconto, "cell 1 0");
		painelPagamentos.add(lblAcrescimo, "cell 2 0");
		painelPagamentos.add(lblValorTotal, "cell 3 0");
		painelPagamentos.add(lblFormaDePagamento, "cell 0 2");
		painelPagamentos.add(lblNmeroDeParcelas, "cell 3 2");
		painelPagamentos.add(lblDiaDeVencimento, "cell 4 2");
		painelPagamentos.add(txValorPagamento, "cell 0 1,growx");
		painelPagamentos.add(txValorDesconto, "cell 1 1,growx");
		painelPagamentos.add(txValorAcrescimo, "cell 2 1,growx");
		painelPagamentos.add(txValorTotal, "cell 3 1 3 1,growx");
		painelPagamentos.add(pesquisaFormasPagamento, "cell 0 3 3 1,growx");
		painelPagamentos.add(txNumeroParcelas, "cell 3 3,growx");
		painelPagamentos.add(txDiaPagamento, "cell 4 3,growx");
		painelPagamentos.add(btnGerar, "cell 5 3");
		painelPagamentos.add(scrollPane, "cell 0 5 6 1,grow");

	}

	private void gerarPagamento() {

		BigDecimal valorPagamento = txValorTotal.getValue().subtract(pagamentoTableModel.getTotalPagamentos());

		try {

			validarPagamento(valorPagamento);

			Date dataVencimento = dtMovimento.getDate();

			for (Long parcela = 1L; parcela <= txNumeroParcelas.getValue(); parcela++) {

				FaturamentoEntradaPagamentos faturamentoPagamento = new FaturamentoEntradaPagamentos();
				faturamentoPagamento.setFaturamentoEntradasCabecalho(panelActions.getObjetoPesquisa());
				faturamentoPagamento.setFormasPagamento(pesquisaFormasPagamento.getObjetoPesquisado());
				faturamentoPagamento.setDataLancamento(new Date());
				faturamentoPagamento.setNumeroParcela(parcela);
				faturamentoPagamento.setValorParcela(obterValorParcela(parcela, valorPagamento));

				if (faturamentoPagamento.getFormasPagamento().isFlagPermitePagamentoPrazo()) {

					dataVencimento = DateUtil.setDay(
							DateUtil.addDays(dataVencimento, faturamentoPagamento.getFormasPagamento().getNumeroDiasPagamento()),
							txDiaPagamento.getValue());
				}

				faturamentoPagamento.setDataVencimento(dataVencimento);

				pagamentoTableModel.addRow(faturamentoPagamento);
			}

			pesquisaFormasPagamento.setValue(null);

		} catch (SysDescException e) {

			JOptionPane.showMessageDialog(this, e.getMensagem());
		}

	}

	private void validarPagamento(BigDecimal valorPagamento) {

		if (pesquisaFormasPagamento.getObjetoPesquisado() == null) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SELECIONE_FORMA_PAGAMAMENTO);
		}

		if (BigDecimalUtil.menor(valorPagamento, BigDecimal.ZERO)) {

			throw new SysDescException(MensagemConstants.MENSAGEM_PAGAMENTOS_MAIOR_FATURAMENTO);
		}

		if (BigDecimalUtil.isNullOrZero(valorPagamento)) {

			throw new SysDescException(MensagemConstants.MENSAGEM_SEM_FATURAMENTO_PAGAMENTOS);
		}
	}

	private BigDecimal obterValorParcela(Long parcela, BigDecimal valorPagamento) {

		if (txNumeroParcelas.getValue().equals(1L)) {

			return valorPagamento;
		}

		if (parcela.equals(txNumeroParcelas.getValue())) {

			return txValorTotal.getValue().subtract(pagamentoTableModel.getTotalPagamentos()).setScale(2, RoundingMode.HALF_EVEN);
		}

		return valorPagamento.divide(BigDecimal.valueOf(txNumeroParcelas.getValue()), 2, RoundingMode.HALF_EVEN);
	}

	private void removeRow() {

		if (table_1.getSelectedRowCount() != 1) {
			JOptionPane.showMessageDialog(this, "Selecione uma despesa para excluir");

			return;
		}

		faturamentoEntradasTableModel.remove(table_1.getSelectedRow());
	}

	private void calcularValorTotal() {

		txValorTotal.setValue(txValorPagamento.getValue().add(txValorAcrescimo.getValue()).subtract(txValorDesconto.getValue()));
	}

	private void atualizarTotais() {

		txValorBruto.setValue(faturamentoEntradasTableModel.getTotalPagamentos());
		txDesconto.setValue(faturamentoEntradasTableModel.getTotalDescontos());
		txAcrescimo.setValue(faturamentoEntradasTableModel.getTotalAcrecimos());

		txValorLiquido.setValue(txValorBruto.getValue().add(txAcrescimo.getValue()).subtract(txDesconto.getValue()));
	}

	private void adicionarDespesa() {

		FaturamentoEntradasDetalhe faturamentoEntradasDetalhe = new FaturamentoEntradasDetalhe();
		faturamentoEntradasDetalhe.setFaturamentoEntradasCabecalho(panelActions.getObjetoPesquisa());

		FrmEntrada frmEntrada = new FrmEntrada(faturamentoEntradasDetalhe, getCodigoUsuario());

		frmEntrada.setVisible(true);

		if (frmEntrada.isOk()) {

			faturamentoEntradasTableModel.addRow(faturamentoEntradasDetalhe);

			atualizarTotais();
		}
	}

	private void abrirScanner() {

		new FrmDocumentosContasReceber(panelActions.getObjetoPesquisa()).setVisible(true);
	}

}
