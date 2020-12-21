package br.com.lar.ui;

import java.awt.BorderLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.com.lar.repository.model.ContasPagar;
import br.com.lar.repository.model.DiarioDetalhe;
import br.com.lar.repository.model.FormasPagamento;
import br.com.lar.repository.model.OperacaoFinanceiro;
import br.com.lar.service.formaspagamento.FormasPagamentoService;
import br.com.lar.service.operacao.OperacaoFinanceiroService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.lar.startup.enumeradores.ProgramasEnum;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.systrans.util.enumeradores.TipoHistoricoOperacaoEnum;
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
	private OperacaoFinanceiroService operacaoFinanceiroService = new OperacaoFinanceiroService();

	private List<ContasPagar> contasPagars;
	private final Long codigoUsuario;
	private FormasPagamentoService formasPagamentoService = new FormasPagamentoService();

	public FrmBaixarContasPagar(Long codigoUsuario, List<ContasPagar> contasPagars) {
		this.contasPagars = contasPagars;
		this.codigoUsuario = codigoUsuario;

		initComponents();
	}

	private void initComponents() {

		setSize(448, 172);
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

		FocusAdapter focusLost = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				calcularValorTotal();
			}
		};

		btBaixar.addActionListener((e) -> baixarContas());
		btCancelar.addActionListener(e -> dispose());

		BigDecimal valorParcelas = contasPagars.stream().map(contaPagar -> contaPagar.getValorParcela().add(contaPagar.getValorAcrescimo())
				.subtract(contaPagar.getValorDesconto()).subtract(contaPagar.getValorPago())).reduce(BigDecimal.ZERO, BigDecimal::add);

		getContentPane().add(container, BorderLayout.CENTER);
		container.setLayout(new MigLayout("", "[grow][grow][grow][grow][grow]", "[][][][][grow]"));
		container.add(lblFormaDePagamento, "cell 0 0");
		container.add(pesquisaFormaPagamento, "cell 0 1 5 1,grow");
		JLabel lbValorParcelas = new JLabel("Valor Parcelas:");
		container.add(lbValorParcelas, "cell 0 2,alignx left,aligny top");

		JLabel lblDescontos = new JLabel("Descontos:");

		container.add(lblDescontos, "cell 1 2");
		JLabel lblAcrscimos = new JLabel("Acréscimos:");
		container.add(lblAcrscimos, "cell 2 2");

		JLabel lblJuros = new JLabel("Juros:");
		container.add(lblJuros, "cell 3 2");
		JLabel lblValorPagar = new JLabel("Valor á Pagar:");
		container.add(lblValorPagar, "cell 4 2");
		txValorParcelas = new JMoneyField();

		txValorParcelas.setEnabled(false);
		txValorParcelas.setValue(valorParcelas);
		container.add(txValorParcelas, "cell 0 3,growx");
		txDesconto = new JMoneyField();
		txDesconto.addFocusListener(focusLost);
		container.add(txDesconto, "cell 1 3,growx");
		txAcrescimo = new JMoneyField();
		txAcrescimo.addFocusListener(focusLost);
		container.add(txAcrescimo, "cell 2 3,growx");

		txValorJuros = new JMoneyField();
		txValorJuros.addFocusListener(focusLost);
		container.add(txValorJuros, "cell 3 3,growx");
		txValorPagar = new JMoneyField();
		container.add(txValorPagar, "cell 4 3,growx");
		container.add(panel, "cell 0 4 5 1,growx,aligny top");
		JButton btnConfigurar = new JButton("Configurar");
		btnConfigurar.addActionListener(e -> abrirConfiguracao());
		panel.add(btBaixar);
		panel.add(btnConfigurar);
		panel.add(btCancelar);

		calcularValorTotal();
	}

	private void baixarContas() {

		this.contasPagars.forEach(contaPagar -> {

			DiarioDetalhe diarioDetahes = contaPagar.getDiarioDetalhe();

			FormasPagamento formasPagamento = pesquisaFormaPagamento.getObjetoPesquisado();

			OperacaoFinanceiro operacaoFinanceiro = operacaoFinanceiroService.buscarPorPlanoCredorEFormaPagamento(
					diarioDetahes.getPlanoContas().getIdPlanoContas(),
					formasPagamento.getIdFormaPagamento());

			if (operacaoFinanceiro == null) {

			}

		});

	}

	private void calcularValorTotal() {

		txValorPagar.setValue(txValorParcelas.getValue().add(txAcrescimo.getValue()).subtract(txDesconto.getValue()).add(txValorJuros.getValue()));
	}

	private void abrirConfiguracao() {

		FrmOperacaoFinanceira frmOperacaoFinanceira = new FrmOperacaoFinanceira(ProgramasEnum.PAGAMENTO_OPERACOES.getCodigo(), codigoUsuario,
				TipoHistoricoOperacaoEnum.CREDOR);

		FrmApplication.getInstance().posicionarFrame(frmOperacaoFinanceira, null);
	}

}
