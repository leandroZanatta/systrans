package br.com.lar.ui.relatorios;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.toedter.calendar.JDateChooser;

import br.com.lar.reports.FaturamentoReportBuilder;
import br.com.lar.repository.model.CentroCusto;
import br.com.lar.repository.model.Cliente;
import br.com.lar.repository.model.FormasPagamento;
import br.com.lar.repository.model.Historico;
import br.com.lar.repository.model.Veiculo;
import br.com.lar.repository.projection.FaturamentoProjection;
import br.com.lar.service.centrocusto.CentroCustoService;
import br.com.lar.service.cliente.ClienteService;
import br.com.lar.service.faturamento.FaturamentoEntradaService;
import br.com.lar.service.formaspagamento.FormasPagamentoService;
import br.com.lar.service.historico.HistoricoService;
import br.com.lar.service.veiculo.VeiculoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.components.JMoneyField;
import br.com.sysdesc.components.JNumericField;
import br.com.sysdesc.components.JTextFieldMaiusculo;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisa;
import br.com.sysdesc.util.classes.BigDecimalUtil;
import br.com.sysdesc.util.classes.DateUtil;
import br.com.sysdesc.util.classes.LongUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.systrans.util.vo.PesquisaFaturamentoVO;
import net.sf.jasperreports.engine.JRException;

public class FrmRelatorioDespesas extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private FaturamentoEntradaService faturamentoEntradaService = new FaturamentoEntradaService();
	private FormasPagamentoService formasPagamentoService = new FormasPagamentoService();
	private HistoricoService historicoService = new HistoricoService();
	private VeiculoService veiculoService = new VeiculoService();
	private CentroCustoService centroCustoService = new CentroCustoService();
	private ClienteService clienteService = new ClienteService();
	private JNumericField txCodigo;
	private CampoPesquisa<Cliente> pesquisaCliente;
	private CampoPesquisa<FormasPagamento> pesquisaPagamento;
	private CampoPesquisa<Historico> pesquisaHistorico;
	private CampoPesquisa<Veiculo> pesquisaVeiculo;
	private CampoPesquisa<CentroCusto> pesquisaCentroCusto;
	private JDateChooser dtMovimentoInicial;
	private JDateChooser dtMovimentoFinal;
	private JMoneyField txValorInicial;
	private JMoneyField txValorFinal;
	private JTextFieldMaiusculo txDocumento;

	public FrmRelatorioDespesas(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();

	}

	private <K, T> T getValueObject(K objeto, Function<K, T> propertie) {

		if (objeto == null) {
			return null;
		}

		return propertie.apply(objeto);
	}

	private void initComponents() {

		setSize(564, 327);
		setClosable(Boolean.TRUE);
		setTitle("Relatório de Despesas");

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		JPanel container = new JPanel();
		JPanel pnlVencimento = new JPanel();
		JPanel pnlPagamento = new JPanel();
		JPanel pnlActions = new JPanel();

		JLabel lbCodigo = new JLabel("Código:");
		JLabel lbFornecedor = new JLabel("Fornecedor:");
		JLabel lbDocumento = new JLabel("Documento:");
		JLabel lbPagamento = new JLabel("Pagamento:");
		JLabel lbDe = new JLabel("De:");
		JLabel lbAte = new JLabel("Até:");
		JLabel pbPgDe = new JLabel("De:");
		JLabel lbPgAte = new JLabel("Até:");
		JLabel lblHistrico = new JLabel("Histórico:");
		JLabel lbVeiculo = new JLabel("Veículo:");
		JLabel lbCentroCusto = new JLabel("Centro Custo:");

		txValorInicial = new JMoneyField();
		txCodigo = new JNumericField();
		dtMovimentoInicial = new JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
		dtMovimentoFinal = new JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
		txValorFinal = new JMoneyField();
		txDocumento = new JTextFieldMaiusculo();

		JButton btnGerar = new JButton("Gerar");

		pesquisaCliente = new CampoPesquisa<Cliente>(clienteService, PesquisaEnum.PES_CLIENTES.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(Cliente objeto) {
				return String.format("%d - %s", objeto.getIdCliente(), objeto.getNome());
			}
		};

		pesquisaPagamento = new CampoPesquisa<FormasPagamento>(formasPagamentoService, PesquisaEnum.PES_FORMAS_PAGAMENTO.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String formatarValorCampo(FormasPagamento objeto) {
				return String.format("%d - %s", objeto.getIdFormaPagamento(), objeto.getDescricao());
			}
		};

		pesquisaHistorico = new CampoPesquisa<Historico>(historicoService, PesquisaEnum.PES_OPERACOES.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			public String formatarValorCampo(Historico objeto) {

				return String.format("%d - %s", objeto.getIdHistorico(), objeto.getDescricao());
			}
		};
		pesquisaVeiculo = new CampoPesquisa<Veiculo>(veiculoService, PesquisaEnum.PES_VEICULOS.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			public String formatarValorCampo(Veiculo objeto) {

				return String.format("%d - %s", objeto.getIdVeiculo(), objeto.getPlaca());
			}
		};
		pesquisaCentroCusto = new CampoPesquisa<CentroCusto>(centroCustoService, PesquisaEnum.PES_CENTRO_CUSTO.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			public String formatarValorCampo(CentroCusto objeto) {

				return String.format("%d - %s", objeto.getIdCentroCusto(), objeto.getDescricao());
			}
		};

		container.setLayout(null);
		pnlVencimento.setLayout(null);
		pnlPagamento.setLayout(null);
		pnlVencimento
				.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Movimento", TitledBorder.CENTER, TitledBorder.TOP,
						null, new Color(0, 0, 0)));
		pnlPagamento
				.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Valor Despesa", TitledBorder.CENTER,
						TitledBorder.TOP, null, new Color(0, 0, 0)));

		pnlActions.setBounds(7, 258, 536, 32);
		pnlVencimento.setBounds(7, 195, 280, 52);
		pnlPagamento.setBounds(291, 195, 252, 52);
		lbCodigo.setBounds(10, 10, 47, 14);
		lbFornecedor.setBounds(10, 40, 68, 14);
		lbDocumento.setBounds(297, 10, 68, 14);
		lbPagamento.setBounds(10, 70, 68, 14);
		lbDe.setBounds(10, 24, 23, 14);
		lbAte.setBounds(145, 24, 25, 14);
		pbPgDe.setBounds(10, 24, 22, 14);
		lbPgAte.setBounds(130, 24, 25, 14);
		lblHistrico.setBounds(10, 105, 68, 14);
		lbVeiculo.setBounds(10, 135, 68, 14);
		lbCentroCusto.setBounds(10, 170, 68, 14);
		pesquisaHistorico.setBounds(88, 101, 455, 22);
		pesquisaVeiculo.setBounds(88, 134, 455, 22);
		pesquisaCentroCusto.setBounds(88, 167, 455, 22);
		txCodigo.setBounds(88, 7, 86, 20);
		pesquisaCliente.setBounds(88, 35, 455, 22);
		pesquisaPagamento.setBounds(88, 68, 455, 22);
		dtMovimentoInicial.setBounds(38, 21, 100, 20);
		dtMovimentoFinal.setBounds(170, 21, 100, 20);
		txValorInicial.setBounds(37, 21, 90, 20);
		txValorFinal.setBounds(155, 21, 87, 20);
		txDocumento.setBounds(375, 7, 168, 20);

		btnGerar.addActionListener(e -> gerarRelatorio());

		getContentPane().add(container, BorderLayout.CENTER);
		container.add(pnlVencimento);
		container.add(pnlPagamento);
		container.add(pnlActions);

		container.add(lblHistrico);
		container.add(lbVeiculo);
		container.add(lbCentroCusto);
		container.add(lbCodigo);
		container.add(lbFornecedor);
		container.add(lbDocumento);
		container.add(lbPagamento);
		pnlVencimento.add(lbDe);
		pnlVencimento.add(lbAte);
		pnlPagamento.add(pbPgDe);
		pnlPagamento.add(lbPgAte);
		container.add(pesquisaHistorico);
		container.add(pesquisaVeiculo);
		container.add(pesquisaCentroCusto);
		container.add(txCodigo);
		container.add(pesquisaCliente);
		container.add(pesquisaPagamento);
		pnlVencimento.add(dtMovimentoInicial);
		pnlVencimento.add(dtMovimentoFinal);
		pnlPagamento.add(txValorInicial);
		pnlPagamento.add(txValorFinal);
		container.add(txDocumento);
		pnlActions.add(btnGerar);
	}

	private void gerarRelatorio() {

		try {

			PesquisaFaturamentoVO pesquisaFaturamentoVO = new PesquisaFaturamentoVO();
			pesquisaFaturamentoVO.setCodigoConta(txCodigo.getValue());
			pesquisaFaturamentoVO.setCodigoFornecedor(getValueObject(pesquisaCliente.getObjetoPesquisado(), Cliente::getIdCliente));
			pesquisaFaturamentoVO
					.setCodigoFormaPagamento(getValueObject(pesquisaPagamento.getObjetoPesquisado(), FormasPagamento::getIdFormaPagamento));
			pesquisaFaturamentoVO
					.setCodigoCentroCusto(getValueObject(pesquisaCentroCusto.getObjetoPesquisado(), CentroCusto::getIdCentroCusto));
			pesquisaFaturamentoVO
					.setCodigoHistorico(getValueObject(pesquisaHistorico.getObjetoPesquisado(), Historico::getIdHistorico));
			pesquisaFaturamentoVO
					.setCodigoVeiculo(getValueObject(pesquisaVeiculo.getObjetoPesquisado(), Veiculo::getIdVeiculo));
			pesquisaFaturamentoVO.setDataMovimentoInicial(dtMovimentoInicial.getDate());
			pesquisaFaturamentoVO.setDataMovimentoFinal(dtMovimentoFinal.getDate());
			pesquisaFaturamentoVO.setValorInicial(txValorInicial.getValue());
			pesquisaFaturamentoVO.setValorFinal(txValorFinal.getValue());
			pesquisaFaturamentoVO.setCodigoDocumento(txDocumento.getText());

			List<FaturamentoProjection> faturamentoEntradasCabecalhos = faturamentoEntradaService.filtrarFaturamento(pesquisaFaturamentoVO);

			new FaturamentoReportBuilder()
					.build("Relatório de Despesas", montarSubTitulo(), !LongUtil.isNullOrZero(pesquisaFaturamentoVO.getCodigoVeiculo()))
					.setData(faturamentoEntradasCabecalhos).view();

		} catch (JRException e) {
			JOptionPane.showMessageDialog(this, "Ocorreu um erro ao Gerar relatório de contas á pagar");
		}
	}

	private List<String> montarSubTitulo() {

		List<String> subtitulo = new ArrayList<>();

		Cliente cliente = pesquisaCliente.getObjetoPesquisado();
		FormasPagamento formasPagamento = pesquisaPagamento.getObjetoPesquisado();
		Historico historico = pesquisaHistorico.getObjetoPesquisado();
		Veiculo veiculo = pesquisaVeiculo.getObjetoPesquisado();
		CentroCusto centroCusto = pesquisaCentroCusto.getObjetoPesquisado();

		if (!LongUtil.isNullOrZero(txCodigo.getValue())) {
			subtitulo.add("Código: " + txCodigo.getValue());
		}

		if (!StringUtil.isNullOrEmpty(txDocumento.getText())) {
			subtitulo.add("Documento: " + txDocumento.getText());
		}

		if (cliente != null) {
			subtitulo.add("Fornecedor: " + cliente.getNome());
		}

		if (formasPagamento != null) {
			subtitulo.add("Forma de Pagamento: " + formasPagamento.getDescricao());
		}

		if (historico != null) {
			subtitulo.add("Histórico: " + historico.getDescricao());
		}

		if (veiculo != null) {
			subtitulo.add("Veículo: " + veiculo.getPlaca());
		}

		if (centroCusto != null) {
			subtitulo.add("Centro de Custo: " + centroCusto.getDescricao());
		}

		if (dtMovimentoFinal.getDate() != null || dtMovimentoInicial.getDate() != null) {

			StringBuilder stringBuilder = new StringBuilder("Data de movimento: ");

			if (dtMovimentoFinal.getDate() != null && dtMovimentoInicial.getDate() != null) {

				stringBuilder.append("De: ").append(DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, dtMovimentoInicial.getDate()))
						.append(" Até: ").append(DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, dtMovimentoFinal.getDate()));
			} else if (dtMovimentoInicial.getDate() != null) {

				stringBuilder.append("A partir De: ")
						.append(DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, dtMovimentoInicial.getDate()));

			} else {
				stringBuilder.append("Até: ")
						.append(DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, dtMovimentoFinal.getDate()));

			}

			subtitulo.add(stringBuilder.toString());
		}

		if (!BigDecimalUtil.isNullOrZero(txValorInicial.getValue()) || !BigDecimalUtil.isNullOrZero(txValorFinal.getValue())) {

			NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

			StringBuilder stringBuilder = new StringBuilder("Valor Faturamento: ");

			if (!BigDecimalUtil.isNullOrZero(txValorFinal.getValue()) && !BigDecimalUtil.isNullOrZero(txValorInicial.getValue())) {

				stringBuilder.append("Entre: ").append(numberFormat.format(txValorInicial.getValue())).append(" e: ")
						.append(numberFormat.format(txValorFinal.getValue()));

			} else if (!BigDecimalUtil.isNullOrZero(txValorInicial.getValue())) {

				stringBuilder.append("A partir De: ").append(numberFormat.format(txValorInicial.getValue()));

			} else {
				stringBuilder.append("Até: ").append(numberFormat.format(txValorFinal.getValue()));

			}

			subtitulo.add(stringBuilder.toString());
		}

		return subtitulo;
	}
}
