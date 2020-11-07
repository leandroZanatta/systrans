package br.com.lar.ui;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import com.mysema.query.BooleanBuilder;
import com.toedter.calendar.JDateChooser;

import br.com.lar.repository.model.CentroCusto;
import br.com.lar.repository.model.Cliente;
import br.com.lar.repository.model.FaturamentoEntrada;
import br.com.lar.repository.model.FaturamentoEntradaPagamento;
import br.com.lar.repository.model.FormasPagamento;
import br.com.lar.repository.model.Historico;
import br.com.lar.repository.model.Motorista;
import br.com.lar.repository.model.Veiculo;
import br.com.lar.service.caixa.CaixaCabecalhoService;
import br.com.lar.service.centrocusto.CentroCustoService;
import br.com.lar.service.cliente.ClienteService;
import br.com.lar.service.faturamento.FaturamentoEntradaService;
import br.com.lar.service.formaspagamento.FormasPagamentoService;
import br.com.lar.service.historico.HistoricoService;
import br.com.lar.service.motorista.MotoristaService;
import br.com.lar.service.veiculo.VeiculoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.lar.tablemodels.FaturamentoEntradasPagamentoTableModel;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.components.JNumericField;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.sysdesc.util.classes.BigDecimalUtil;
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
	private CampoPesquisa<Veiculo> pesquisaVeiculo;
	private CampoPesquisa<Motorista> pesquisaMotorista;
	private CampoPesquisa<FormasPagamento> pesquisaFormasPagamento;
	private CampoPesquisa<CentroCusto> pesquisaCentroCusto;
	private JDateChooser dtMovimento;
	private JMoneyField txValorBruto;
	private JMoneyField txValorTotal;
	private JMoneyField txValorPagamento;
	private JMoneyField txValorDesconto;
	private JMoneyField txValorAcrescimo;
	private JTextFieldMaiusculo txDocumento;
	private JNumericField txNumeroParcelas;
	private JNumericField txDiaPagamento;
	private JTable table;
	private JTabbedPane tabbedPane;
	private PanelActions<FaturamentoEntrada> panelActions;
	private FaturamentoEntradaService faturamentoEntradaService = new FaturamentoEntradaService();
	private HistoricoService historicoService = new HistoricoService();
	private ClienteService clienteService = new ClienteService();
	private VeiculoService veiculoService = new VeiculoService();
	private MotoristaService motoristaService = new MotoristaService();
	private CentroCustoService centroCustoService = new CentroCustoService();
	private FormasPagamentoService formasPagamentoService = new FormasPagamentoService();
	private CaixaCabecalhoService caixaCabecalhoService = new CaixaCabecalhoService();
	private FaturamentoEntradasPagamentoTableModel pagamentoTableModel = new FaturamentoEntradasPagamentoTableModel();

	public FrmLancamentoEntradas(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(550, 450);
		setClosable(Boolean.TRUE);
		setTitle("FATURAMENTO - ENTRADA - LANÇAMENTOS");

		painelContent = new JPanel();
		tabbedPane = new JTabbedPane(SwingConstants.TOP);

		tabbedPane.addChangeListener(e -> checarCamposObrigatorios());

		painelContent.setLayout(new MigLayout("", "[grow]", "[][][][][grow][]"));

		getContentPane().add(painelContent);
		painelContent.add(tabbedPane, "cell 0 4,grow");

		montarPainelPrincipal();
		montarPainelGeral();
		montarPainelPagamentos();

		panelActions = new PanelActions<FaturamentoEntrada>(this, faturamentoEntradaService,
				PesquisaEnum.PES_FATURAMENTO_ENTRADA.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void carregarObjeto(FaturamentoEntrada objeto) {
				txCodigo.setValue(objeto.getIdFaturamentoEntrada());
				pesquisaHistorico.setValue(objeto.getHistorico());
				pesquisaCliente.setValue(objeto.getCliente());
				dtMovimento.setDate(objeto.getDataMovimento());
				txDocumento.setText(objeto.getNumeroDocumento());
				txValorAcrescimo.setValue(objeto.getValorAcrescimo());
				txValorBruto.setValue(objeto.getValorBruto());
				txValorPagamento.setValue(objeto.getValorBruto());
				txValorDesconto.setValue(objeto.getValorDesconto());

				calcularValorTotal();

				pesquisaVeiculo.setValue(objeto.getVeiculo());
				pesquisaMotorista.setValue(objeto.getMotorista());

				pagamentoTableModel.setRows(objeto.getFaturamentoEntradaPagamentos());
			}

			@Override
			public boolean preencherObjeto(FaturamentoEntrada objetoPesquisa) {

				objetoPesquisa.setCaixaCabecalho(caixaCabecalhoService.obterCaixaDoDia(FrmApplication.getUsuario()));
				objetoPesquisa.setIdFaturamentoEntrada(txCodigo.getValue());
				objetoPesquisa.setHistorico(pesquisaHistorico.getObjetoPesquisado());
				objetoPesquisa.setCliente(pesquisaCliente.getObjetoPesquisado());
				objetoPesquisa.setDataMovimento(dtMovimento.getDate());
				objetoPesquisa.setNumeroDocumento(txDocumento.getText());
				objetoPesquisa.setValorAcrescimo(txValorAcrescimo.getValue());
				objetoPesquisa.setValorBruto(txValorBruto.getValue());
				objetoPesquisa.setValorDesconto(txValorDesconto.getValue());
				objetoPesquisa.setVeiculo(pesquisaVeiculo.getObjetoPesquisado());
				objetoPesquisa.setMotorista(pesquisaMotorista.getObjetoPesquisado());
				objetoPesquisa.setCentroCusto(pesquisaCentroCusto.getObjetoPesquisado());

				objetoPesquisa.setFaturamentoEntradaPagamentos(pagamentoTableModel.getRows());
				return true;
			}

		};

		panelActions.addSaveListener(faturamento -> txCodigo.setValue(faturamento.getIdFaturamentoEntrada()));
		panelActions.addNewListener(faturamento -> dtMovimento.setDate(new Date()));
		painelContent.add(panelActions, "cell 0 5,growx");

	}

	private void checarCamposObrigatorios() {

		if (tabbedPane.getSelectedIndex() == 1) {

			if (pesquisaHistorico.getObjetoPesquisado() == null) {
				JOptionPane.showMessageDialog(this, "Selecione um histórico");

				tabbedPane.setSelectedIndex(0);

				pesquisaHistorico.requestFocus();

				return;
			}

			if (!BigDecimalUtil.maior(txValorBruto.getValue(), BigDecimal.ZERO)) {
				JOptionPane.showMessageDialog(this, "Insira o valor do faturamento");

				tabbedPane.setSelectedIndex(0);

				txValorBruto.requestFocus();

				return;
			}

			if (dtMovimento.getDate() == null) {
				JOptionPane.showMessageDialog(this, "Insira a data de Movimento");

				tabbedPane.setSelectedIndex(0);

				dtMovimento.requestFocus();

				return;
			}

			txValorPagamento.setValue(txValorBruto.getValue());

			calcularValorTotal();
		}
	}

	private void calcularValorTotal() {

		txValorTotal.setValue(txValorPagamento.getValue().add(txValorAcrescimo.getValue()).subtract(txValorDesconto.getValue()));
	}

	private void montarPainelPagamentos() {
		JPanel painelPagamentos = new JPanel();

		tabbedPane.addTab("Pagamentos", null, painelPagamentos, null);
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

			if (tabbedPane.getSelectedIndex() == 1) {

				boolean permitePrazo = formaPagamento != null && formaPagamento.isFlagPermitePagamentoPrazo();

				txNumeroParcelas.setEnabled(permitePrazo);
				txDiaPagamento.setEnabled(permitePrazo);

				txNumeroParcelas.setValue(1L);
				txDiaPagamento.setValue(DateUtil.getDayOfMonth(dtMovimento.getDate()).longValue());
			}
		});

		JButton btnGerar = new JButton("Gerar");
		table = new JTable(pagamentoTableModel);
		JScrollPane scrollPane = new JScrollPane(table);

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

				FaturamentoEntradaPagamento faturamentoEntradaPagamento = new FaturamentoEntradaPagamento();
				faturamentoEntradaPagamento.setFaturamentoEntrada(panelActions.getObjetoPesquisa());
				faturamentoEntradaPagamento.setFormasPagamento(pesquisaFormasPagamento.getObjetoPesquisado());
				faturamentoEntradaPagamento.setDataLancamento(new Date());
				faturamentoEntradaPagamento.setNumeroParcela(parcela);
				faturamentoEntradaPagamento.setValorParcela(obterValorParcela(parcela, valorPagamento));

				if (faturamentoEntradaPagamento.getFormasPagamento().isFlagPermitePagamentoPrazo()) {

					dataVencimento = DateUtil.setDay(
							DateUtil.addDays(dataVencimento, faturamentoEntradaPagamento.getFormasPagamento().getNumeroDiasPagamento()),
							txDiaPagamento.getValue());
				}

				faturamentoEntradaPagamento.setDataVencimento(dataVencimento);

				pagamentoTableModel.addRow(faturamentoEntradaPagamento);
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

	private void montarPainelGeral() {
		JPanel painelGeral = new JPanel();
		painelGeral.setLayout(new MigLayout("", "[grow][161.00][grow]", "[][][][][][][][][][]"));
		tabbedPane.addTab("Geral", null, painelGeral, null);

		JLabel lblCliente = new JLabel("Cliente:");
		JLabel lblDocumento = new JLabel("Documento:");
		JLabel lblDataDoMovimento = new JLabel("Data do Movimento:");
		JLabel lblValorDoFrete = new JLabel("Valor do Bruto:");
		JLabel lblCentroDeCutos = new JLabel("Centro de Custos:");
		JLabel lblVeculo = new JLabel("Veículo:");
		JLabel lblMotorista = new JLabel("Motorista:");

		txDocumento = new JTextFieldMaiusculo();
		dtMovimento = new JDateChooser("dd/MM/yyyy HH:mm:ss", "##/##/##### ##:##:##", '_');
		txValorBruto = new JMoneyField();
		pesquisaCliente = new CampoPesquisa<Cliente>(clienteService, PesquisaEnum.PES_CLIENTES.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Cliente objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdCliente(), objeto.getNome());
			}
		};

		pesquisaVeiculo = new CampoPesquisa<Veiculo>(veiculoService, PesquisaEnum.PES_VEICULOS.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Veiculo objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdVeiculo(), objeto.getPlaca());
			}

		};
		pesquisaMotorista = new CampoPesquisa<Motorista>(motoristaService, PesquisaEnum.PES_MOTORISTA.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Motorista objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdMotorista(), objeto.getFuncionario().getCliente().getNome());
			}
		};

		pesquisaVeiculo.addChangeListener(veiculo -> pesquisaMotorista.setValue(veiculo == null ? null : veiculo.getMotorista()));

		pesquisaCentroCusto = new CampoPesquisa<CentroCusto>(centroCustoService, PesquisaEnum.PES_CENTRO_CUSTO.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(CentroCusto objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdCentroCusto(), objeto.getDescricao());
			}
		};

		painelGeral.add(lblCentroDeCutos, "cell 0 0");
		painelGeral.add(lblDocumento, "cell 0 8");
		painelGeral.add(lblDataDoMovimento, "cell 1 8");
		painelGeral.add(lblValorDoFrete, "cell 2 8");
		painelGeral.add(lblCliente, "cell 0 2");
		painelGeral.add(lblVeculo, "cell 0 4");
		painelGeral.add(lblMotorista, "cell 0 6");
		painelGeral.add(pesquisaVeiculo, "cell 0 5 3 1,growx");
		painelGeral.add(pesquisaCentroCusto, "cell 0 1 3 1,growx");
		painelGeral.add(pesquisaMotorista, "cell 0 7 3 1,growx");
		painelGeral.add(pesquisaCliente, "cell 0 3 3 1,growx");
		painelGeral.add(txDocumento, "cell 0 9,growx");
		painelGeral.add(dtMovimento, "cell 1 9,growx");
		painelGeral.add(txValorBruto, "cell 2 9,growx");

	}

	private void montarPainelPrincipal() {
		JLabel lblCdigo = new JLabel("Código:");
		JLabel lblHistorico = new JLabel("Histórico:");

		txCodigo = new JTextFieldId();
		pesquisaHistorico = new CampoPesquisa<Historico>(historicoService, PesquisaEnum.PES_OPERACOES.getCodigoPesquisa(),
				getCodigoUsuario(), historicoService.getHistoricosDevedores()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Historico objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdHistorico(), objeto.getDescricao());
			}
		};

		painelContent.add(lblCdigo, "cell 0 0");
		painelContent.add(txCodigo, "cell 0 1,,width 50:100:100");
		painelContent.add(lblHistorico, "cell 0 2");
		painelContent.add(pesquisaHistorico, "cell 0 3,growx");

	}

}
