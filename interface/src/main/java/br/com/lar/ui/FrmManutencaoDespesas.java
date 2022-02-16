package br.com.lar.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;

import com.toedter.calendar.JDateChooser;

import br.com.lar.repository.model.CentroCusto;
import br.com.lar.repository.model.Cliente;
import br.com.lar.repository.model.FaturamentoEntradasCabecalho;
import br.com.lar.repository.model.Veiculo;
import br.com.lar.service.centrocusto.CentroCustoService;
import br.com.lar.service.cliente.ClienteService;
import br.com.lar.service.faturamento.ManutencaoFaturamentoEntradaService;
import br.com.lar.service.veiculo.VeiculoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.lar.tablemodels.DetalhesEntradaTableModel;
import br.com.lar.tablemodels.FaturamentoEntradasPagamentoTableModel;
import br.com.lar.tablemodels.FaturamentoEntradasTableModel;
import br.com.lar.ui.buttonactions.ButtonActionExcluir;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisaColumn;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.sysdesc.util.classes.ContadorUtil;
import net.miginfocom.swing.MigLayout;

public class FrmManutencaoDespesas extends AbstractInternalFrame {
	private static final String TEMPLATE_PESQUISA = "%d - %s";

	private static final long serialVersionUID = 1L;

	private ManutencaoFaturamentoEntradaService manutencaoFaturamentoEntradaService = new ManutencaoFaturamentoEntradaService();
	private FaturamentoEntradasPagamentoTableModel pagamentoTableModel = new FaturamentoEntradasPagamentoTableModel();
	private FaturamentoEntradasTableModel faturamentoEntradasTableModel = new FaturamentoEntradasTableModel();
	private DetalhesEntradaTableModel detalhesEntradaTableModel = new DetalhesEntradaTableModel();
	private CentroCustoService centroCustoService = new CentroCustoService();
	private ClienteService clienteService = new ClienteService();
	private VeiculoService veiculoService = new VeiculoService();

	private JPanel painelContent;
	private JTextFieldId txCodigo;
	private CampoPesquisa<Cliente> pesquisaCliente;
	private JDateChooser dtMovimento;
	private PanelActions<FaturamentoEntradasCabecalho> panelActions;
	private JLabel lblCliente_1;
	private JLabel lblCentroDeCustos;
	private CampoPesquisa<CentroCusto> pesquisaCentroCustos;
	private JTabbedPane tabbedPane;
	private JPanel panel;
	private JPanel pnlDetalhes;
	private JScrollPane scrollPane;
	private JTable tbDetalhes;
	private CampoPesquisa<Veiculo> pesquisaVeiculo;
	private JPanel panel_1;
	private JScrollPane scrollPane_1;
	private JTextPane txObservacao;

	public FrmManutencaoDespesas(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(600, 500);
		setClosable(Boolean.TRUE);
		setTitle("MANUTENÇÃO DE DESPESAS");

		painelContent = new JPanel();

		painelContent.setLayout(new MigLayout("", "[grow][][grow][]", "[][][][grow][]"));

		getContentPane().add(painelContent);
		txCodigo = new JTextFieldId();
		painelContent.add(txCodigo, "cell 1 0,width 50:100:100");

		JLabel lblDataDoMovimento = new JLabel("Data do Movimento:");
		painelContent.add(lblDataDoMovimento, "cell 2 0,alignx right");
		dtMovimento = new JDateChooser("dd/MM/yyyy HH:mm:ss", "##/##/##### ##:##:##", '_');
		painelContent.add(dtMovimento, "cell 3 0");
		lblCliente_1 = new JLabel("Cliente:");
		painelContent.add(lblCliente_1, "cell 0 1");

		pesquisaCliente = new CampoPesquisa<Cliente>(clienteService, PesquisaEnum.PES_CLIENTES.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Cliente objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdCliente(), objeto.getNome());
			}
		};

		pesquisaVeiculo = new CampoPesquisa<Veiculo>(veiculoService, PesquisaEnum.PES_VEICULOS.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Veiculo objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdVeiculo(), objeto.getPlaca());
			}
		};

		painelContent.add(pesquisaCliente, "cell 1 1 3 1,grow");

		lblCentroDeCustos = new JLabel("Centro de Custos:");
		painelContent.add(lblCentroDeCustos, "cell 0 2");

		pesquisaCentroCustos = new CampoPesquisa<CentroCusto>(centroCustoService, PesquisaEnum.PES_CENTRO_CUSTO.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			public String formatarValorCampo(CentroCusto objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdCentroCusto(), objeto.getDescricao());
			}
		};
		painelContent.add(pesquisaCentroCustos, "cell 1 2 3 1,grow");

		JLabel lblCdigo = new JLabel("Código:");

		painelContent.add(lblCdigo, "cell 0 0");

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		painelContent.add(tabbedPane, "cell 0 3 4 1,grow");
		panel = new JPanel();
		tabbedPane.addTab("Detalhe", null, panel, null);
		panel.setLayout(new MigLayout("", "[grow]", "[grow][grow]"));

		pnlDetalhes = new JPanel();
		panel.add(pnlDetalhes, "cell 0 0,grow");
		pnlDetalhes.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		pnlDetalhes.add(scrollPane, BorderLayout.CENTER);

		tbDetalhes = new JTable(detalhesEntradaTableModel);
		scrollPane.setViewportView(tbDetalhes);

		CampoPesquisaColumn<Veiculo> campoPesquisaColumn = new CampoPesquisaColumn<>(tbDetalhes, 1, pesquisaVeiculo);
		campoPesquisaColumn.addBeforeChangeRow((row, oldValue, newValue) -> detalhesEntradaTableModel.getRow(row).setVeiculo(newValue));

		tbDetalhes.setRowHeight(24);

		panel_1 = new JPanel();
		tabbedPane.addTab("Observações", null, panel_1, null);
		panel_1.setLayout(new BorderLayout(0, 0));

		scrollPane_1 = new JScrollPane();
		panel_1.add(scrollPane_1, BorderLayout.CENTER);

		txObservacao = new JTextPane();
		scrollPane_1.setViewportView(txObservacao);

		ButtonActionExcluir buttonAction = new ButtonActionExcluir();

		Action actionExcluir = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				excluirDespesa();
			}

		};

		panelActions = new PanelActions<FaturamentoEntradasCabecalho>(this, manutencaoFaturamentoEntradaService,
				PesquisaEnum.PES_FATURAMENTO_ENTRADA.getCodigoPesquisa(), buttonAction) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void posicionarBotoes() {

				ContadorUtil contadorUtil = new ContadorUtil();

				posicionarBotao(contadorUtil, btPrimeiro, Boolean.TRUE);
				posicionarBotao(contadorUtil, btRetroceder, Boolean.TRUE);

				posicionarBotao(contadorUtil, buttonAction, Boolean.TRUE);
				posicionarBotao(contadorUtil, btSalvar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btEditar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btNovo, Boolean.FALSE);
				posicionarBotao(contadorUtil, btBuscar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btCancelar, Boolean.TRUE);

				posicionarBotao(contadorUtil, btAvancar, Boolean.TRUE);
				posicionarBotao(contadorUtil, btUltimo, Boolean.TRUE);

			}

			@Override
			protected void registrarEventosBotoesPagina() {

				registrarEvento(buttonAction, actionExcluir);
			}

			@Override
			public void carregarObjeto(FaturamentoEntradasCabecalho objeto) {

				txCodigo.setValue(objeto.getIdFaturamentoEntradasCabecalho());
				dtMovimento.setDate(objeto.getDataMovimento());
				faturamentoEntradasTableModel.setRows(objeto.getFaturamentoEntradasDetalhes());
				pesquisaCliente.setValue(objeto.getCliente());
				pesquisaCentroCustos.setValue(objeto.getCentroCusto());

				detalhesEntradaTableModel.setRows(objeto.getFaturamentoEntradasDetalhes());
				pagamentoTableModel.setRows(objeto.getFaturamentoEntradaPagamentos());

				txObservacao.setText(objeto.getObservacao());
			}

			@Override
			public boolean preencherObjeto(FaturamentoEntradasCabecalho objetoPesquisa) {

				objetoPesquisa.setCliente(pesquisaCliente.getObjetoPesquisado());
				objetoPesquisa.setFaturamentoEntradasDetalhes(faturamentoEntradasTableModel.getRows());

				objetoPesquisa.setObservacao(txObservacao.getText());

				return true;
			}

			@Override
			protected void limpar() {

				super.limpar();

				manutencaoFaturamentoEntradaService.getGenericDAO().invalidarObjeto();
			}

		};
		panelActions.addSaveListener(faturamento -> txCodigo.setValue(faturamento.getIdFaturamentoEntradasCabecalho()));
		panelActions.addNewListener(faturamento -> dtMovimento.setDate(new Date()));
		painelContent.add(panelActions, "cell 0 4 4 1,grow");

	}

	private void excluirDespesa() {

		if (JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este registro?", "Verificação",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

			manutencaoFaturamentoEntradaService.excluirFaturamento(panelActions.getObjetoPesquisa());

			panelActions.deleteEvent();
		}
	}

}
