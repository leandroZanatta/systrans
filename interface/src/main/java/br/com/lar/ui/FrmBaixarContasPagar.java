package br.com.lar.ui;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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

		setSize(800, 261);
		setClosable(Boolean.TRUE);
		setTitle("BAIXA DE CONTAS Á PAGAR");

		JPanel container = new JPanel();
		JPanel panel = new JPanel();
		JLabel lblFormaDePagamento = new JLabel("Forma de Pagamento:");
		JButton btBaixar = new JButton("Baixar");
		JButton btCancelar = new JButton("Cancelar");

		pesquisaFormaPagamento = new CampoPesquisa<FormasPagamento>(formasPagamentoService, PesquisaEnum.PES_FORMAS_PAGAMENTO.getCodigoPesquisa(),
				this.codigoUsuario) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(FormasPagamento objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdFormaPagamento(), objeto.getDescricao());
			}
		};

		btBaixar.addActionListener((e) -> baixarContas());
		btCancelar.addActionListener(e -> dispose());

		getContentPane().add(container, BorderLayout.CENTER);
		container.setLayout(new MigLayout("", "[grow][grow][grow][grow][grow]", "[grow][][][][][grow]"));

		JScrollPane scrollPane = new JScrollPane();
		container.add(scrollPane, "cell 0 0 5 1,grow");

		table = new JTable(contasPagarPagamentoTableModel);
		new JmoneyFieldColumn(table, 4);
		new JmoneyFieldColumn(table, 5);
		new JmoneyFieldColumn(table, 6);
		new JmoneyFieldColumn(table, 7);

		contasPagarPagamentoTableModel.addChangeListener(valorTotal -> {
			calcularValorTotal(valorTotal);
		});

		scrollPane.setViewportView(table);
		container.add(lblFormaDePagamento, "cell 0 1");
		container.add(pesquisaFormaPagamento, "cell 0 2 5 1,grow");
		JLabel lbValorParcelas = new JLabel("Valor Parcelas:");
		container.add(lbValorParcelas, "cell 0 3,alignx left,aligny top");

		JLabel lblDescontos = new JLabel("Descontos:");

		container.add(lblDescontos, "cell 1 3");
		JLabel lblAcrscimos = new JLabel("Acréscimos:");
		container.add(lblAcrscimos, "cell 2 3");

		JLabel lblJuros = new JLabel("Juros:");
		container.add(lblJuros, "cell 3 3");
		JLabel lblValorPagar = new JLabel("Valor á Pagar:");
		container.add(lblValorPagar, "cell 4 3");
		txValorParcelas = new JMoneyField();

		txValorParcelas.setEnabled(false);
		container.add(txValorParcelas, "cell 0 4,growx");
		txDesconto = new JMoneyField();
		txDesconto.setEnabled(false);
		container.add(txDesconto, "cell 1 4,growx");
		txAcrescimo = new JMoneyField();
		txAcrescimo.setEnabled(false);
		container.add(txAcrescimo, "cell 2 4,growx");

		txValorJuros = new JMoneyField();
		txValorJuros.setEnabled(false);
		container.add(txValorJuros, "cell 3 4,growx");
		txValorPagar = new JMoneyField();
		txValorPagar.setEnabled(false);
		container.add(txValorPagar, "cell 4 4,growx");
		container.add(panel, "cell 0 5 5 1,growx,aligny top");
		JButton btnConfigurar = new JButton("Configurar");
		btnConfigurar.addActionListener(e -> abrirConfiguracao());
		panel.add(btBaixar);
		panel.add(btnConfigurar);
		panel.add(btCancelar);

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

	private void abrirConfiguracao() {

		FrmOperacaoFinanceira frmOperacaoFinanceira = new FrmOperacaoFinanceira(ProgramasEnum.PAGAMENTO_OPERACOES.getCodigo(), codigoUsuario,
				TipoHistoricoOperacaoEnum.CREDOR);

		FrmApplication.getInstance().posicionarFrame(frmOperacaoFinanceira, null);
	}

}
