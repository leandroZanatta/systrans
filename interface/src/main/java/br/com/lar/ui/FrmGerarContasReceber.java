package br.com.lar.ui;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.toedter.calendar.JDateChooser;

import br.com.lar.repository.model.Cliente;
import br.com.lar.repository.model.ContasReceber;
import br.com.lar.repository.model.FormasPagamento;
import br.com.lar.repository.model.Historico;
import br.com.lar.repository.model.Motorista;
import br.com.lar.repository.model.Veiculo;
import br.com.lar.service.cliente.ClienteService;
import br.com.lar.service.contasreceber.ContasReceberService;
import br.com.lar.service.formaspagamento.FormasPagamentoService;
import br.com.lar.service.historico.HistoricoService;
import br.com.lar.service.motorista.MotoristaService;
import br.com.lar.service.veiculo.VeiculoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.components.JTextFieldId;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.pesquisa.ui.components.PanelActions;
import br.com.sysdesc.util.classes.DateUtil;
import br.com.sysdesc.util.enumeradores.TipoStatusEnum;
import net.miginfocom.swing.MigLayout;

public class FrmGerarContasReceber extends AbstractInternalFrame {

	private static final String FORMATO_PESQUISA = "%d - %s";
	private static final long serialVersionUID = 1L;
	private ContasReceberService contasReceberService = new ContasReceberService();
	private FormasPagamentoService formasPagamentoService = new FormasPagamentoService();
	private VeiculoService veiculoService = new VeiculoService();
	private MotoristaService motoristaService = new MotoristaService();
	private ClienteService clienteService = new ClienteService();
	private HistoricoService historicoService = new HistoricoService();
	private JPanel container;
	private JLabel lbCodigo;
	private JTextFieldId txCodigo;
	private JLabel lbFornecedor;
	private CampoPesquisa<Cliente> pesquisaCliente;
	private CampoPesquisa<FormasPagamento> pesquisaPagamento;
	private CampoPesquisa<Veiculo> pesquisaVeiculo;
	private CampoPesquisa<Motorista> pesquisaMotorista;
	private CampoPesquisa<Historico> pesquisaHistorico;
	private PanelActions<ContasReceber> panelActions;
	private JLabel lbPagamento;
	private JLabel lbValorParcela;
	private JMoneyField txValorParcela;
	private JLabel lbAcrescimo;
	private JLabel lbDesconto;
	private JMoneyField txAcrescimo;
	private JMoneyField txDesconto;
	private JLabel lbVencimento;
	private JDateChooser dtVencimento;
	private JDateChooser dtMovimento;
	private JLabel lblDataDeMovimento;
	private JLabel lblVeculo;
	private JLabel lblMotorista;
	private JLabel lblDocumento;
	private JTextFieldMaiusculo txDocumento;
	private JLabel lblHistorico;

	public FrmGerarContasReceber(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();

	}

	private void initComponents() {

		setSize(600, 380);
		setClosable(Boolean.TRUE);
		setTitle("CADASTRO DE CONTAS Á RECEBER");

		container = new JPanel();
		getContentPane().add(container, BorderLayout.CENTER);
		container
				.setLayout(new MigLayout("", "[grow][100px:100px:100px,grow][100px:100px:100px,grow][100px:100px:100px,grow][100px:100px:100px,grow]",
						"[][][][][][][][][][][22px][][33px,grow]"));

		lbCodigo = new JLabel("Código:");
		lblVeculo = new JLabel("Veículo:");
		lblMotorista = new JLabel("Motorista:");
		lblHistorico = new JLabel("Histórico:");
		lbVencimento = new JLabel("Vencimento:");
		lbValorParcela = new JLabel("Valor Parcela:");
		lbAcrescimo = new JLabel("Acréscimo:");
		lbDesconto = new JLabel("Desconto:");
		lblDocumento = new JLabel("Documento:");
		lbFornecedor = new JLabel("Fornecedor:");
		lbPagamento = new JLabel("Pagamento:");
		lblDataDeMovimento = new JLabel("Data de Movimento:");

		txCodigo = new JTextFieldId();
		txDocumento = new JTextFieldMaiusculo();
		dtMovimento = new JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
		dtVencimento = new JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
		txValorParcela = new JMoneyField();
		txAcrescimo = new JMoneyField();
		txDesconto = new JMoneyField();

		pesquisaVeiculo = new CampoPesquisa<Veiculo>(veiculoService, PesquisaEnum.PES_VEICULOS.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Veiculo objeto) {
				return String.format(FORMATO_PESQUISA, objeto.getIdVeiculo(), objeto.getPlaca());
			}
		};
		pesquisaMotorista = new CampoPesquisa<Motorista>(motoristaService, PesquisaEnum.PES_MOTORISTA.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Motorista objeto) {
				return String.format(FORMATO_PESQUISA, objeto.getIdMotorista(), objeto.getFuncionario().getCliente().getNome());
			}
		};

		pesquisaHistorico = new CampoPesquisa<Historico>(historicoService, PesquisaEnum.PES_HISTORICO.getCodigoPesquisa(), getCodigoUsuario(),
				historicoService.getHistoricosCredores()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Historico objeto) {
				return String.format(FORMATO_PESQUISA, objeto.getIdHistorico(), objeto.getDescricao());
			}
		};

		pesquisaCliente = new CampoPesquisa<Cliente>(clienteService, PesquisaEnum.PES_CLIENTES.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Cliente objeto) {
				return String.format(FORMATO_PESQUISA, objeto.getIdCliente(), objeto.getNome());
			}
		};

		pesquisaPagamento = new CampoPesquisa<FormasPagamento>(formasPagamentoService, PesquisaEnum.PES_FORMAS_PAGAMENTO.getCodigoPesquisa(),
				getCodigoUsuario(), formasPagamentoService.pesquisarApenasAPrazo()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(FormasPagamento objeto) {
				return String.format(FORMATO_PESQUISA, objeto.getIdFormaPagamento(), objeto.getDescricao());
			}
		};

		pesquisaPagamento.addChangeListener(pagamento -> {

			if (pagamento != null && dtVencimento.getDate() == null) {
				dtVencimento.setDate(DateUtil.addDays(dtMovimento.getDate(), pagamento.getNumeroDiasPagamento()));
			}
		});
		pesquisaVeiculo.addChangeListener(veiculo -> pesquisaMotorista.setValue(veiculo == null ? null : veiculo.getMotorista()));
		txCodigo.setColumns(10);

		container.add(lbCodigo, "cell 0 0,growx,aligny center");
		container.add(lblDocumento, "cell 4 0");
		container.add(txCodigo, "cell 0 1,alignx left,aligny center");
		container.add(txDocumento, "cell 4 1,growx");
		container.add(lbFornecedor, "cell 0 2,alignx left,aligny center");
		container.add(pesquisaPagamento, "cell 0 5 5 1,grow");
		container.add(lblVeculo, "cell 0 6");
		container.add(lblMotorista, "cell 2 6");
		container.add(pesquisaVeiculo, "cell 0 7 2 1,growx");
		container.add(pesquisaMotorista, "cell 2 7 3 1,growx");
		container.add(lblHistorico, "cell 0 8");
		container.add(pesquisaCliente, "cell 0 3 5 1,grow");
		container.add(lbPagamento, "cell 0 4,alignx left,aligny center");
		container.add(pesquisaHistorico, "cell 0 9 5 1,growx");
		container.add(lblDataDeMovimento, "cell 0 10");
		container.add(lbVencimento, "cell 1 10");
		container.add(lbValorParcela, "cell 2 10");
		container.add(lbAcrescimo, "cell 3 10");
		container.add(lbDesconto, "cell 4 10,alignx left");
		container.add(dtMovimento, "cell 0 11,growx");
		container.add(dtVencimento, "cell 1 11,growx");
		container.add(txValorParcela, "cell 2 11,growx");
		container.add(txAcrescimo, "cell 3 11,growx");
		container.add(txDesconto, "cell 4 11,growx");

		panelActions = new PanelActions<ContasReceber>(this, contasReceberService, PesquisaEnum.PES_CONTAS_RECEBER.getCodigoPesquisa()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void carregarObjeto(ContasReceber objetoPesquisa) {
				txCodigo.setValue(objetoPesquisa.getIdContasReceber());
				txValorParcela.setValue(objetoPesquisa.getValorParcela());
				txAcrescimo.setValue(objetoPesquisa.getValorAcrescimo());
				txDesconto.setValue(objetoPesquisa.getValorDesconto());
				txDocumento.setText(objetoPesquisa.getDocumento());
				dtMovimento.setDate(objetoPesquisa.getDataMovimento());
				dtVencimento.setDate(objetoPesquisa.getDataVencimento());
				pesquisaCliente.setValue(objetoPesquisa.getCliente());
				pesquisaPagamento.setValue(objetoPesquisa.getFormasPagamento());
				pesquisaVeiculo.setValue(objetoPesquisa.getVeiculo());
				pesquisaMotorista.setValue(objetoPesquisa.getMotorista());
				pesquisaHistorico.setValue(objetoPesquisa.getHistorico());
			}

			@Override
			public boolean preencherObjeto(ContasReceber objetoPesquisa) {
				objetoPesquisa.setIdContasReceber(txCodigo.getValue());
				objetoPesquisa.setDataMovimento(dtMovimento.getDate());
				objetoPesquisa.setDataVencimento(dtVencimento.getDate());
				objetoPesquisa.setValorParcela(txValorParcela.getValue());
				objetoPesquisa.setValorAcrescimo(txAcrescimo.getValue());
				objetoPesquisa.setValorDesconto(txDesconto.getValue());
				objetoPesquisa.setDocumento(txDocumento.getText());
				objetoPesquisa.setDataManutencao(new Date());
				objetoPesquisa.setCliente(pesquisaCliente.getObjetoPesquisado());
				objetoPesquisa.setFormasPagamento(pesquisaPagamento.getObjetoPesquisado());
				objetoPesquisa.setVeiculo(pesquisaVeiculo.getObjetoPesquisado());
				objetoPesquisa.setMotorista(pesquisaMotorista.getObjetoPesquisado());
				objetoPesquisa.setHistorico(pesquisaHistorico.getObjetoPesquisado());

				return true;
			}
		};
		container.add(panelActions, "cell 0 12 5 1,growx,aligny bottom");
		panelActions.addSaveListener(objeto -> txCodigo.setValue(objeto.getIdContasReceber()));
		panelActions.addNewListener(conta -> {
			conta.setValorJuros(BigDecimal.ZERO);
			conta.setBaixado(false);
			conta.setValorPago(BigDecimal.ZERO);
			conta.setDataCadastro(new Date());
			conta.setCodigoStatus(TipoStatusEnum.ATIVO.getCodigo());

			dtMovimento.setDate(new Date());
		});
	}
}
