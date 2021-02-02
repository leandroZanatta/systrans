package br.com.lar.ui;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import com.toedter.calendar.JDateChooser;

import br.com.lar.repository.model.CentroCusto;
import br.com.lar.repository.model.Cliente;
import br.com.lar.repository.model.FaturamentoEntradasCabecalho;
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
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.sysdesc.util.classes.ContadorUtil;
import net.miginfocom.swing.MigLayout;

public class FrmManutencaoDespesas extends AbstractInternalFrame {
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
	private JLabel lblCliente_1;
	private JLabel lblCentroDeCustos;
	private CampoPesquisa<CentroCusto> pesquisaCentroCustos;
	private FaturamentoEntradasPagamentoTableModel pagamentoTableModel = new FaturamentoEntradasPagamentoTableModel();
	private FormasPagamentoService formasPagamentoService = new FormasPagamentoService();
	private CaixaCabecalhoService caixaCabecalhoService = new CaixaCabecalhoService();
	private JTabbedPane tabbedPane;
	private JPanel panel;
	private JPanel pnlDetalhes;
	private JPanel pnlAlocacaoCustos;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JTable table;
	private JTable table_1;

	public FrmManutencaoDespesas(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(600, 500);
		setClosable(Boolean.TRUE);
		setTitle("MANUTENÇÃO DE DESPESAS");

		painelContent = new JPanel();

		painelContent.setLayout(new MigLayout("", "[grow][][grow][]", "[][][][][grow][]"));

		getContentPane().add(painelContent);
		txCodigo = new JTextFieldId();
		painelContent.add(txCodigo, "cell 1 0,width 50:100:100");

		JLabel lblDataDoMovimento = new JLabel("Data do Movimento:");
		painelContent.add(lblDataDoMovimento, "cell 2 0,alignx right");
		dtMovimento = new JDateChooser("dd/MM/yyyy HH:mm:ss", "##/##/##### ##:##:##", '_');
		painelContent.add(dtMovimento, "cell 3 0");
		lblCliente_1 = new JLabel("Cliente:");
		painelContent.add(lblCliente_1, "cell 0 1");
		pesquisaCliente = new CampoPesquisa<Cliente>(clienteService, PesquisaEnum.PES_CLIENTES.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Cliente objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdCliente(), objeto.getNome());
			}
		};
		painelContent.add(pesquisaCliente, "cell 1 1 3 1,grow");
		JLabel lblHistorico = new JLabel("Histórico:");
		painelContent.add(lblHistorico, "cell 0 2");
		pesquisaHistorico = new CampoPesquisa<Historico>(historicoService, PesquisaEnum.PES_OPERACOES.getCodigoPesquisa(),
				getCodigoUsuario(), historicoService.getHistoricosDevedores()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Historico objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdHistorico(), objeto.getDescricao());
			}
		};
		painelContent.add(pesquisaHistorico, "cell 1 2 3 1,grow");

		lblCentroDeCustos = new JLabel("Centro de Custos:");
		painelContent.add(lblCentroDeCustos, "cell 0 3");

		pesquisaCentroCustos = new CampoPesquisa<CentroCusto>(centroCustoService, PesquisaEnum.PES_CENTRO_CUSTO.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			public String formatarValorCampo(CentroCusto objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdCentroCusto(), objeto.getDescricao());
			}
		};
		painelContent.add(pesquisaCentroCustos, "cell 1 3 3 1,grow");

		JLabel lblCdigo = new JLabel("Código:");

		painelContent.add(lblCdigo, "cell 0 0");

		panelActions = new PanelActions<FaturamentoEntradasCabecalho>(this, faturamentoEntradaService,
				PesquisaEnum.PES_FATURAMENTO_ENTRADA.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void posicionarBotoes() {

				ContadorUtil contadorUtil = new ContadorUtil();

				posicionarBotao(contadorUtil, btPrimeiro, Boolean.TRUE);
				posicionarBotao(contadorUtil, btRetroceder, Boolean.TRUE);

				posicionarBotao(contadorUtil, btSalvar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btEditar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btNovo, Boolean.FALSE);
				posicionarBotao(contadorUtil, btBuscar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btCancelar, Boolean.TRUE);

				posicionarBotao(contadorUtil, btAvancar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btUltimo, Boolean.TRUE);

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

			}

			@Override
			public boolean preencherObjeto(FaturamentoEntradasCabecalho objetoPesquisa) {
				objetoPesquisa.setIdFaturamentoEntradasCabecalho(txCodigo.getValue());
				objetoPesquisa.setDataMovimento(dtMovimento.getDate());
				objetoPesquisa.setFaturamentoEntradasDetalhes(faturamentoEntradasTableModel.getRows());
				objetoPesquisa.setCliente(pesquisaCliente.getObjetoPesquisado());
				objetoPesquisa.setHistorico(pesquisaHistorico.getObjetoPesquisado());
				objetoPesquisa.setCentroCusto(pesquisaCentroCustos.getObjetoPesquisado());

				objetoPesquisa.setFaturamentoEntradaPagamentos(pagamentoTableModel.getRows());
				objetoPesquisa.setCaixaCabecalho(caixaCabecalhoService.obterCaixaDoDia(FrmApplication.getUsuario()));
				return true;
			}

		};

		panelActions.addSaveListener(faturamento -> txCodigo.setValue(faturamento.getIdFaturamentoEntradasCabecalho()));
		panelActions.addNewListener(faturamento -> dtMovimento.setDate(new Date()));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		painelContent.add(tabbedPane, "cell 0 4 4 1,grow");

		panel = new JPanel();
		tabbedPane.addTab("Detalhe", null, panel, null);
		panel.setLayout(new MigLayout("", "[grow]", "[grow][grow]"));

		pnlDetalhes = new JPanel();
		pnlDetalhes.setBorder(new TitledBorder(null, "Detalhes", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		panel.add(pnlDetalhes, "cell 0 0,grow");
		pnlDetalhes.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		pnlDetalhes.add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		scrollPane.setViewportView(table);

		pnlAlocacaoCustos = new JPanel();
		pnlAlocacaoCustos.setBorder(new TitledBorder(null, "Aloca\u00E7\u00E3o de Custos", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		panel.add(pnlAlocacaoCustos, "cell 0 1,grow");
		pnlAlocacaoCustos.setLayout(new BorderLayout(0, 0));

		scrollPane_1 = new JScrollPane();
		pnlAlocacaoCustos.add(scrollPane_1, BorderLayout.CENTER);

		table_1 = new JTable();
		scrollPane_1.setViewportView(table_1);

		painelContent.add(panelActions, "cell 0 5 4 1,grow");

	}

}
