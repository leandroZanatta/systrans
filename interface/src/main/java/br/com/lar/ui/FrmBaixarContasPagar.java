package br.com.lar.ui;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import br.com.lar.repository.model.ContasPagar;
import br.com.lar.repository.model.FormasPagamento;
import br.com.lar.repository.projection.PagamentoContasProjection;
import br.com.lar.service.caixa.CaixaCabecalhoService;
import br.com.lar.service.contaspagar.ContasPagarService;
import br.com.lar.service.formaspagamento.FormasPagamentoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.lar.startup.enumeradores.ProgramasEnum;
import br.com.lar.tablemodels.ContasPagarPagamentoTableModel;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.components.JmoneyFieldColumn;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.sysdesc.util.resources.Resources;
import br.com.systrans.util.RateioUtil;
import br.com.systrans.util.constants.MensagemConstants;
import br.com.systrans.util.enumeradores.TipoHistoricoOperacaoEnum;
import br.com.systrans.util.vo.ResumoPagamentosVO;
import net.miginfocom.swing.MigLayout;

public class FrmBaixarContasPagar extends JInternalFrame {
	private static final String TEMPLATE_PESQUISA = "%d - %s";

	private static final long serialVersionUID = 1L;
	private JMoneyField txValorParcelas;
	private JMoneyField txDesconto;
	private JMoneyField txAcrescimo;
	private JMoneyField txValorPagar;
	private JMoneyField txValorJuros;
	private CampoPesquisa<FormasPagamento> pesquisaFormaPagamento;
	private ContasPagarService contasPagarService = new ContasPagarService();

	private List<ContasPagar> contasPagars;
	private final Long codigoUsuario;
	private FormasPagamentoService formasPagamentoService = new FormasPagamentoService();
	private ContasPagarPagamentoTableModel contasPagarPagamentoTableModel;
	private CaixaCabecalhoService caixaCabecalhoService = new CaixaCabecalhoService();
	private JTable table;

	public FrmBaixarContasPagar(Long codigoUsuario, List<ContasPagar> contasPagars) {
		this.contasPagars = contasPagars;
		this.codigoUsuario = codigoUsuario;

		contasPagarPagamentoTableModel = this.instanciarContasPagar();

		initComponents();
	}

	private ContasPagarPagamentoTableModel instanciarContasPagar() {

		List<PagamentoContasProjection> rows = contasPagars.stream().map(conta -> {

			BigDecimal valorTotal = calcularValorPagar(conta);

			PagamentoContasProjection pagamentoContasProjection = new PagamentoContasProjection();
			pagamentoContasProjection.setCliente(conta.getCliente().getNome());
			pagamentoContasProjection.setIdContasPagar(conta.getIdContasPagar());
			pagamentoContasProjection.setVencimento(conta.getDataVencimento());
			pagamentoContasProjection.setValorPagar(valorTotal);
			pagamentoContasProjection.setValorParcela(valorTotal);
			pagamentoContasProjection.setContasPagar(conta);

			return pagamentoContasProjection;
		}).collect(Collectors.toList());

		return new ContasPagarPagamentoTableModel(rows);
	}

	private BigDecimal calcularValorPagar(ContasPagar conta) {

		return conta.getValorParcela().add(conta.getValorAcrescimo()).add(conta.getValorJuros()).subtract(conta.getValorPago())
				.subtract(conta.getValorDesconto());
	}

	private void initComponents() {
		JLabel lbValorParcelas = new JLabel("Valor Parcelas:");
		JButton btnConfigurar = new JButton("Configurar");
		JLabel lblDescontos = new JLabel("Descontos:");
		JLabel lblAcrscimos = new JLabel("Acréscimos:");
		JLabel lblJuros = new JLabel("Juros:");
		JLabel lblValorPagar = new JLabel("Valor á Pagar:");
		JPopupMenu jPopupMenu = new JPopupMenu();
		JMenuItem jMenuItemCP = new JMenuItem("Operação Contas á Pagar");
		JMenuItem jMenuItemOF = new JMenuItem("Operação Financeiro");

		txValorParcelas = new JMoneyField();
		txDesconto = new JMoneyField();
		txAcrescimo = new JMoneyField();
		txValorJuros = new JMoneyField();
		txValorPagar = new JMoneyField();

		JPanel container = new JPanel();
		JPanel panel = new JPanel();
		JLabel lblFormaDePagamento = new JLabel("Forma de Pagamento:");
		JButton btBaixar = new JButton("Baixar");
		JButton btCancelar = new JButton("Cancelar");

		table = new JTable(contasPagarPagamentoTableModel);
		new JmoneyFieldColumn(table, 4);
		new JmoneyFieldColumn(table, 5);
		new JmoneyFieldColumn(table, 6);
		new JmoneyFieldColumn(table, 7);
		JScrollPane scrollPane = new JScrollPane(table);

		pesquisaFormaPagamento = new CampoPesquisa<FormasPagamento>(formasPagamentoService, PesquisaEnum.PES_FORMAS_PAGAMENTO.getCodigoPesquisa(),
				this.codigoUsuario) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(FormasPagamento objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdFormaPagamento(), objeto.getDescricao());
			}
		};

		txValorPagar.setEnabled(false);

		jMenuItemCP.addActionListener(e -> abrirConfiguracaoContasPagar());
		jMenuItemOF.addActionListener(e -> abrirConfiguracaoOperacaoFinanceira());

		btnConfigurar.addActionListener(e -> jPopupMenu.show(btnConfigurar, 0, -45));
		btBaixar.addActionListener((e) -> baixarContas());
		btCancelar.addActionListener(e -> dispose());
		txDesconto.addChangeValue(value -> this.calcularRateioDesconto(value));
		txAcrescimo.addChangeValue(value -> this.calcularRateioAcrescimo(value));
		txValorJuros.addChangeValue(value -> this.calcularRateioJuros(value));
		txValorParcelas.setEnabled(false);

		contasPagarPagamentoTableModel.addChangeListener(valorTotal -> {
			calcularValorTotal(valorTotal);
		});

		getContentPane().add(container, BorderLayout.CENTER);
		container.setLayout(new MigLayout("", "[grow][grow][grow][grow][grow]", "[grow][][][][][grow]"));

		container.add(scrollPane, "cell 0 0 5 1,grow");

		container.add(lblFormaDePagamento, "cell 0 1");
		container.add(lbValorParcelas, "cell 0 3,alignx left,aligny top");
		container.add(lblDescontos, "cell 1 3");
		container.add(lblAcrscimos, "cell 2 3");
		container.add(lblJuros, "cell 3 3");
		container.add(lblValorPagar, "cell 4 3");

		container.add(txDesconto, "cell 1 4,growx");
		container.add(txValorParcelas, "cell 0 4,growx");
		container.add(txAcrescimo, "cell 2 4,growx");
		container.add(txValorJuros, "cell 3 4,growx");
		container.add(txValorPagar, "cell 4 4,growx");
		container.add(pesquisaFormaPagamento, "cell 0 2 5 1,grow");
		container.add(panel, "cell 0 5 5 1,growx,aligny top");

		jPopupMenu.add(jMenuItemCP);
		jPopupMenu.add(jMenuItemOF);

		panel.add(btBaixar);
		panel.add(btnConfigurar);
		panel.add(btCancelar);

		setSize(800, 261);
		setClosable(Boolean.TRUE);
		setTitle("BAIXA DE CONTAS Á PAGAR");

		calcularValorTotal(contasPagarPagamentoTableModel.obterValorTotal());
	}

	private void calcularRateioJuros(BigDecimal value) {

		efetuarRateioConta(value, PagamentoContasProjection::getJuros, PagamentoContasProjection::setJuros);
	}

	private void calcularRateioAcrescimo(BigDecimal value) {

		efetuarRateioConta(value, PagamentoContasProjection::getAcrescimos, PagamentoContasProjection::setAcrescimos);
	}

	private void calcularRateioDesconto(BigDecimal value) {

		efetuarRateioConta(value, PagamentoContasProjection::getDecontos, PagamentoContasProjection::setDecontos);
	}

	private <T> void efetuarRateioConta(BigDecimal value, Function<PagamentoContasProjection, BigDecimal> funcaoGet,
			BiConsumer<PagamentoContasProjection, BigDecimal> funcaoSet) {

		List<PagamentoContasProjection> pagamentoContasProjections = contasPagarPagamentoTableModel.getRows();

		BigDecimal valorTotalParcelas = contasPagarPagamentoTableModel.obterValorTotal().getValorParcelas();

		pagamentoContasProjections.forEach(pagamento -> {

			BigDecimal valorRateio = pagamento.getValorParcela().multiply(value).divide(valorTotalParcelas, 2, RoundingMode.HALF_EVEN);

			funcaoSet.accept(pagamento, valorRateio);

		});

		RateioUtil.efetuarRateio(pagamentoContasProjections, funcaoGet, funcaoSet, value);

		pagamentoContasProjections.forEach(pagamento -> {

			BigDecimal valorRecalculado = pagamento.getValorParcela().add(pagamento.getAcrescimos()).add(pagamento.getJuros())
					.subtract(pagamento.getDecontos());

			pagamento.setValorPagar(valorRecalculado);

		});

		contasPagarPagamentoTableModel.fireTableDataChanged();

		calcularValorTotal(contasPagarPagamentoTableModel.obterValorTotal());
	}

	private void baixarContas() {

		if (pesquisaFormaPagamento.getObjetoPesquisado() == null) {

			JOptionPane.showMessageDialog(this, Resources.translate(MensagemConstants.MENSAGEM_SELECIONE_FORMA_PAGAMAMENTO));

			return;
		}

		try {

			this.contasPagarService.baixarContas(contasPagarPagamentoTableModel.getRows(), pesquisaFormaPagamento.getObjetoPesquisado(),
					caixaCabecalhoService.obterCaixaDoDia(FrmApplication.getUsuario()));

			dispose();

		} catch (SysDescException e) {

			JOptionPane.showMessageDialog(this, e.getMensagem());
		}
	}

	private void calcularValorTotal(ResumoPagamentosVO resumoPagamentosVO) {

		txValorPagar.setValue(resumoPagamentosVO.getValorPagar());
		txValorParcelas.setValue(resumoPagamentosVO.getValorParcelas());
		txAcrescimo.setValue(resumoPagamentosVO.getValorAcrescimo());
		txDesconto.setValue(resumoPagamentosVO.getValorDesconto());
		txValorJuros.setValue(resumoPagamentosVO.getValorJuros());
	}

	private void abrirConfiguracaoOperacaoFinanceira() {

		FrmParametroOperacaoFinanceira frmOperacaoFinanceira = new FrmParametroOperacaoFinanceira(ProgramasEnum.PAGAMENTO_OPERACOES.getCodigo(),
				codigoUsuario,
				TipoHistoricoOperacaoEnum.DEVEDOR);

		FrmApplication.getInstance().posicionarFrame(frmOperacaoFinanceira, null);
	}

	private void abrirConfiguracaoContasPagar() {

		FrmOperacaoFinanceira frmOperacaoFinanceira = new FrmOperacaoFinanceira(ProgramasEnum.PAGAMENTO_OPERACOES.getCodigo(),
				codigoUsuario,
				TipoHistoricoOperacaoEnum.DEVEDOR);

		FrmApplication.getInstance().posicionarFrame(frmOperacaoFinanceira, null);
	}

}
