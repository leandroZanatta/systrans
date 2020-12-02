package br.com.lar.ui;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mysema.query.BooleanBuilder;

import br.com.lar.repository.model.ContasPagar;
import br.com.lar.repository.model.FormasPagamento;
import br.com.lar.repository.model.Operacao;
import br.com.lar.service.contaspagar.ContasPagarService;
import br.com.lar.service.formaspagamento.FormasPagamentoService;
import br.com.lar.service.operacao.OperacaoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import net.miginfocom.swing.MigLayout;

public class FrmBaixarContasPagar extends JInternalFrame {
	private static final String TEMPLATE_PESQUISA = "%d - %s";

	private static final long serialVersionUID = 1L;
	private JMoneyField txValorParcelas;
	private JMoneyField txDesconto;
	private JMoneyField txAcrescimo;
	private JMoneyField txValorTotal;
	private JMoneyField txValorPagar;
	private CampoPesquisa<Operacao> pesquisaHistorico;
	private CampoPesquisa<FormasPagamento> pesquisaFormaPagamento;

	private List<ContasPagar> contasPagars;
	private final Long codigoUsuario;
	private final Long codigoConta;
	private ContasPagarService contasPagarService = new ContasPagarService();
	private OperacaoService operacaoService = new OperacaoService();
	private FormasPagamentoService formasPagamentoService = new FormasPagamentoService();

	public FrmBaixarContasPagar(Long codigoUsuario, List<ContasPagar> contasPagars, Long codigoConta) {
		this.contasPagars = contasPagars;
		this.codigoUsuario = codigoUsuario;
		this.codigoConta = codigoConta;
		initComponents();
	}

	private void initComponents() {

		setSize(502, 275);
		setClosable(Boolean.TRUE);
		setTitle("BAIXA DE CONTAS Á PAGAR");

		JPanel container = new JPanel();
		JPanel panel = new JPanel();

		JLabel lblDescontos = new JLabel("Descontos");
		JLabel lblAcrscimos = new JLabel("Acréscimos:");
		JLabel lblValorTotal = new JLabel("Valor Total:");
		JLabel lblValorPagar = new JLabel("Valor á Pagar:");
		JLabel lblHistrico = new JLabel("Histórico:");
		JLabel lblFormaDePagamento = new JLabel("Forma de Pagamento:");
		JLabel lbValorParcelas = new JLabel("Valor Parcelas:");
		txDesconto = new JMoneyField();
		txAcrescimo = new JMoneyField();
		txValorTotal = new JMoneyField();
		txValorPagar = new JMoneyField();
		txValorParcelas = new JMoneyField();
		JButton btBaixar = new JButton("Baixar");
		JButton btCancelar = new JButton("Cancelar");

		pesquisaHistorico = new CampoPesquisa<Operacao>(operacaoService, PesquisaEnum.PES_HISTORICOOPERACOES.getCodigoPesquisa(),
				this.codigoUsuario) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Operacao objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdOperacao(), objeto.getDescricao());
			}

			@Override
			public BooleanBuilder getPreFilter() {

				return operacaoService.buscarOperacaoContasPagar(codigoConta);
			}

		};

		pesquisaFormaPagamento = new CampoPesquisa<FormasPagamento>(formasPagamentoService, PesquisaEnum.PES_FORMAS_PAGAMENTO.getCodigoPesquisa(),
				this.codigoUsuario) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(FormasPagamento objeto) {
				return String.format(TEMPLATE_PESQUISA, objeto.getIdFormaPagamento(), objeto.getDescricao());
			}

			@Override
			public BooleanBuilder getPreFilter() {

				return formasPagamentoService.buscarPagamentosAVistaComHistorico(pesquisaHistorico.getObjetoPesquisado().getIdOperacao());
			}

		};

		btBaixar.addActionListener((e) -> baixarContas());
		btCancelar.addActionListener(e -> dispose());

		BigDecimal valorParcelas = contasPagars.stream().map(contaPagar -> contaPagar.getValorParcela().add(contaPagar.getValorAcrescimo())
				.subtract(contaPagar.getValorDesconto()).subtract(contaPagar.getValorPago())).reduce(BigDecimal.ZERO, BigDecimal::add);

		txValorParcelas.setEnabled(false);
		txValorTotal.setEnabled(false);
		txValorParcelas.setValue(valorParcelas);

		calcularValorTotal();

		getContentPane().add(container, BorderLayout.CENTER);
		container.setLayout(new MigLayout("", "[grow][grow][grow][grow]", "[14px][][grow][][grow][][][][33px,grow]"));

		container.add(lblDescontos, "cell 1 0");
		container.add(lblAcrscimos, "cell 2 0");
		container.add(lblValorTotal, "cell 3 0");
		container.add(lblHistrico, "cell 0 2");
		container.add(lbValorParcelas, "cell 0 0,alignx left,aligny top");
		container.add(lblFormaDePagamento, "cell 0 4");
		container.add(lblValorPagar, "cell 0 6");
		container.add(pesquisaHistorico, "cell 0 3 4 1,grow");
		container.add(pesquisaFormaPagamento, "cell 0 5 4 1,grow");
		container.add(txValorParcelas, "cell 0 1,growx");
		container.add(txDesconto, "cell 1 1,growx");
		container.add(txAcrescimo, "cell 2 1,growx");
		container.add(txValorTotal, "cell 3 1,growx");
		container.add(txValorPagar, "cell 0 7,growx");
		container.add(panel, "cell 0 8 4 1,growx,aligny top");
		panel.add(btBaixar);
		panel.add(btCancelar);

	}

	private void calcularValorTotal() {

		txValorTotal.setValue(txValorParcelas.getValue().add(txAcrescimo.getValue()).subtract(txDesconto.getValue()));

		if (txValorPagar.getValue().compareTo(BigDecimal.ZERO) == 0) {

			txValorPagar.setValue(txValorTotal.getValue());
		}
	}

	private void baixarContas() {
	}
}
