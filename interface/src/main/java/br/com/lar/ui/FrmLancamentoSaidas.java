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

import br.com.lar.repository.model.Cliente;
import br.com.lar.repository.model.Faturamento;
import br.com.lar.repository.model.FaturamentoPagamento;
import br.com.lar.repository.model.FaturamentoTransporte;
import br.com.lar.repository.model.FormasPagamento;
import br.com.lar.repository.model.Historico;
import br.com.lar.repository.model.Motorista;
import br.com.lar.repository.model.Veiculo;
import br.com.lar.service.caixa.CaixaCabecalhoService;
import br.com.lar.service.cliente.ClienteService;
import br.com.lar.service.faturamento.FaturamentoSaidaService;
import br.com.lar.service.formaspagamento.FormasPagamentoService;
import br.com.lar.service.historico.HistoricoService;
import br.com.lar.service.motorista.MotoristaService;
import br.com.lar.service.veiculo.VeiculoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.lar.tablemodels.FaturamentoPagamentoTableModel;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.components.JNumericField;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.classes.ContadorUtil;
import br.com.sysdesc.util.classes.DateUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.systrans.util.constants.MensagemConstants;
import net.miginfocom.swing.MigLayout;

public class FrmLancamentoSaidas extends AbstractInternalFrame {
	private static final String TEMPLATE_PESQUISA = "%d - %s";

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;
	private JTextFieldId txCodigo;
	private CampoPesquisa<Historico> pesquisaHistorico;
	private CampoPesquisa<Cliente> pesquisaCliente;
	private CampoPesquisa<Veiculo> pesquisaVeiculo;
	private CampoPesquisa<Motorista> pesquisaMotorista;
	private CampoPesquisa<FormasPagamento> pesquisaFormasPagamento;
	private JDateChooser dtMovimento;
	private JMoneyField txValorBruto;
	private JMoneyField txValorTotal;
	private JMoneyField txValorPagamento;
	private JMoneyField txValorDesconto;
	private JMoneyField txValorAcrescimo;
	private JTextFieldMaiusculo txDocumento;
	private JNumericField txOdometroInicial;
	private JNumericField txOdometroFinal;
	private JNumericField txNumeroParcelas;
	private JNumericField txDiaPagamento;
	private JTable table;
	private JTabbedPane tabbedPane;
	private PanelActions<Faturamento> panelActions;
	private FaturamentoSaidaService faturamentoService = new FaturamentoSaidaService();
	private HistoricoService historicoService = new HistoricoService();
	private ClienteService clienteService = new ClienteService();
	private VeiculoService veiculoService = new VeiculoService();
	private MotoristaService motoristaService = new MotoristaService();
	private FaturamentoPagamentoTableModel pagamentoTableModel = new FaturamentoPagamentoTableModel();
	private FormasPagamentoService formasPagamentoService = new FormasPagamentoService();
	private CaixaCabecalhoService caixaCabecalhoService = new CaixaCabecalhoService();

	public FrmLancamentoSaidas(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(550, 450);
		setClosable(Boolean.TRUE);
		setTitle("FATURAMENTO - LANÇAMENTOS");

		painelContent = new JPanel();
		tabbedPane = new JTabbedPane(SwingConstants.TOP);

		tabbedPane.addChangeListener(e -> checarCamposObrigatorios());

		painelContent.setLayout(new MigLayout("", "[grow]", "[][][][][grow][]"));

		getContentPane().add(painelContent);
		painelContent.add(tabbedPane, "cell 0 4,grow");

		montarPainelPrincipal();
		montarPainelGeral();
		montarPainelTransporte();
		montarPainelPagamentos();

		panelActions = new PanelActions<Faturamento>(this, faturamentoService, PesquisaEnum.PES_FATURAMENTO.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void posicionarBotoes() {

				ContadorUtil contadorUtil = new ContadorUtil();

				posicionarBotao(contadorUtil, btPrimeiro, Boolean.TRUE);
				posicionarBotao(contadorUtil, btRetroceder, Boolean.TRUE);

				posicionarBotao(contadorUtil, btSalvar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btEditar, Boolean.FALSE);
				posicionarBotao(contadorUtil, btNovo, Boolean.TRUE);
				posicionarBotao(contadorUtil, btBuscar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btCancelar, Boolean.TRUE);

				posicionarBotao(contadorUtil, btAvancar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btUltimo, Boolean.TRUE);

			}

			@Override
			public void carregarObjeto(Faturamento objeto) {
				txCodigo.setValue(objeto.getIdFaturamento());
				pesquisaHistorico.setValue(objeto.getHistorico());
				pesquisaCliente.setValue(objeto.getCliente());
				dtMovimento.setDate(objeto.getDataMovimento());
				txDocumento.setText(objeto.getNumeroDocumento());
				txValorAcrescimo.setValue(objeto.getValorAcrescimo());
				txValorBruto.setValue(objeto.getValorBruto());
				txValorPagamento.setValue(objeto.getValorBruto());
				txValorDesconto.setValue(objeto.getValorDesconto());

				calcularValorTotal();

				Motorista motorista = null;
				Veiculo veiculo = null;
				Long odometroInicial = null;
				Long odometroFinal = null;

				if (objeto.getFaturamentoTransporte() != null) {
					motorista = objeto.getFaturamentoTransporte().getMotorista();
					veiculo = objeto.getFaturamentoTransporte().getVeiculo();
					odometroInicial = objeto.getFaturamentoTransporte().getOdometroInicial();
					odometroFinal = objeto.getFaturamentoTransporte().getOdometroFinal();
				}

				pesquisaVeiculo.setValue(veiculo);
				pesquisaMotorista.setValue(motorista);
				txOdometroInicial.setValue(odometroInicial);
				txOdometroFinal.setValue(odometroFinal);

				pagamentoTableModel.setRows(objeto.getFaturamentoPagamentos());
			}

			@Override
			public boolean preencherObjeto(Faturamento objetoPesquisa) {

				objetoPesquisa.setCaixaCabecalho(caixaCabecalhoService.obterCaixaDoDia(FrmApplication.getUsuario()));
				objetoPesquisa.setIdFaturamento(txCodigo.getValue());
				objetoPesquisa.setHistorico(pesquisaHistorico.getObjetoPesquisado());
				objetoPesquisa.setCliente(pesquisaCliente.getObjetoPesquisado());
				objetoPesquisa.setDataMovimento(dtMovimento.getDate());
				objetoPesquisa.setNumeroDocumento(txDocumento.getText());
				objetoPesquisa.setValorAcrescimo(txValorAcrescimo.getValue());
				objetoPesquisa.setValorBruto(txValorBruto.getValue());
				objetoPesquisa.setValorDesconto(txValorDesconto.getValue());

				FaturamentoTransporte faturamentoTransporte = null;

				if (pesquisaMotorista.getObjetoPesquisado() != null) {

					faturamentoTransporte = objetoPesquisa.getFaturamentoTransporte() != null ? objetoPesquisa.getFaturamentoTransporte()
							: new FaturamentoTransporte();
					faturamentoTransporte.setFaturamento(objetoPesquisa);
					faturamentoTransporte.setMotorista(pesquisaMotorista.getObjetoPesquisado());
					faturamentoTransporte.setVeiculo(pesquisaVeiculo.getObjetoPesquisado());
					faturamentoTransporte.setOdometroFinal(txOdometroInicial.getValue());
					faturamentoTransporte.setOdometroInicial(txOdometroFinal.getValue());
				}

				objetoPesquisa.setFaturamentoTransporte(faturamentoTransporte);
				objetoPesquisa.setFaturamentoPagamentos(pagamentoTableModel.getRows());

				return true;
			}

		};

		panelActions.addSaveListener(faturamento -> txCodigo.setValue(faturamento.getIdFaturamento()));
		panelActions.addNewListener(faturamento -> dtMovimento.setDate(new Date()));
		painelContent.add(panelActions, "cell 0 5,growx");

	}

	private void checarCamposObrigatorios() {

		if (tabbedPane.getSelectedIndex() == 2) {

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

			if (tabbedPane.getSelectedIndex() == 2) {

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

				FaturamentoPagamento faturamentoPagamento = new FaturamentoPagamento();
				faturamentoPagamento.setFaturamento(panelActions.getObjetoPesquisa());
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

	private void montarPainelTransporte() {
		JPanel painelTransporte = new JPanel();

		tabbedPane.addTab("Transporte", null, painelTransporte, null);
		painelTransporte.setLayout(new MigLayout("", "[grow][grow]", "[][][][][][]"));

		JLabel lblVeculo = new JLabel("Veículo:");
		JLabel lblMotorista = new JLabel("Motorista:");
		JLabel lblOdmetroInicial = new JLabel("Odômetro Inicial:");
		JLabel lblOdmetroFinal = new JLabel("Odômetro Final:");

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
		txOdometroInicial = new JNumericField();
		txOdometroFinal = new JNumericField();

		pesquisaVeiculo.addChangeListener(veiculo -> pesquisaMotorista.setValue(veiculo == null ? null : veiculo.getMotorista()));

		painelTransporte.add(lblVeculo, "cell 0 0");
		painelTransporte.add(lblMotorista, "cell 0 2");
		painelTransporte.add(lblOdmetroInicial, "cell 0 4");
		painelTransporte.add(lblOdmetroFinal, "cell 1 4");
		painelTransporte.add(pesquisaVeiculo, "cell 0 1 2 1,growx");
		painelTransporte.add(pesquisaMotorista, "cell 0 3 2 1,growx");
		painelTransporte.add(txOdometroInicial, "cell 0 5,growx");
		painelTransporte.add(txOdometroFinal, "cell 1 5,growx");
	}

	private void montarPainelGeral() {
		JPanel painelGeral = new JPanel();
		painelGeral.setLayout(new MigLayout("", "[grow][161.00][grow]", "[][][][]"));
		tabbedPane.addTab("Geral", null, painelGeral, null);

		JLabel lblCliente = new JLabel("Cliente:");
		JLabel lblDocumento = new JLabel("Documento:");
		JLabel lblDataDoMovimento = new JLabel("Data do Movimento:");
		JLabel lblValorDoFrete = new JLabel("Valor do Bruto:");

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

		painelGeral.add(lblCliente, "cell 0 0");
		painelGeral.add(lblDocumento, "cell 0 2");
		painelGeral.add(lblDataDoMovimento, "cell 1 2");
		painelGeral.add(lblValorDoFrete, "cell 2 2");
		painelGeral.add(pesquisaCliente, "cell 0 1 3 1,growx");
		painelGeral.add(txDocumento, "cell 0 3,growx");
		painelGeral.add(dtMovimento, "cell 1 3,growx");
		painelGeral.add(txValorBruto, "cell 2 3,growx");

	}

	private void montarPainelPrincipal() {
		JLabel lblCdigo = new JLabel("Código:");
		JLabel lblHistorico = new JLabel("Histórico:");

		txCodigo = new JTextFieldId();
		pesquisaHistorico = new CampoPesquisa<Historico>(historicoService, PesquisaEnum.PES_OPERACOES.getCodigoPesquisa(),
				getCodigoUsuario(), historicoService.getHistoricosCredores()) {

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
