package br.com.lar.ui.relatorios;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.toedter.calendar.JDateChooser;

import br.com.lar.reports.ContasReceberReportBuilder;
import br.com.lar.repository.model.Cliente;
import br.com.lar.repository.model.ContasReceber;
import br.com.lar.repository.model.FormasPagamento;
import br.com.lar.service.cliente.ClienteService;
import br.com.lar.service.contasreceber.ContasReceberService;
import br.com.lar.service.formaspagamento.FormasPagamentoService;
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
import br.com.systrans.util.vo.PesquisaContasVO;
import net.sf.jasperreports.engine.JRException;

public class FrmRelatorioContasReceber extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private ContasReceberService contasReceberService = new ContasReceberService();
	private FormasPagamentoService formasPagamentoService = new FormasPagamentoService();
	private ClienteService clienteService = new ClienteService();
	private JNumericField txCodigo;
	private CampoPesquisa<Cliente> pesquisaCliente;
	private JCheckBox chBaixado;
	private JDateChooser dtVencimentoInicial;
	private JDateChooser dtVencimentoFinal;
	private CampoPesquisa<FormasPagamento> pesquisaPagamento;
	private JMoneyField txValorInicial;
	private JMoneyField txValorFinal;
	private JTextFieldMaiusculo txDocumento;
	private JCheckBox chVencido;

	public FrmRelatorioContasReceber(Long permissaoPrograma, Long codigoUsuario) {
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

		setSize(800, 195);
		setClosable(Boolean.TRUE);
		setTitle("Relatório de Contas á Receber");

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		JPanel container = new JPanel();
		JPanel pnlVencimento = new JPanel();
		JPanel pnlPagamento = new JPanel();
		JPanel pnlActions = new JPanel();

		JLabel lbCodigo = new JLabel("Código:");
		JLabel lbFornecedor = new JLabel("Cliente:");
		JLabel lbDocumento = new JLabel("Documento:");
		JLabel lbPagamento = new JLabel("Pagamento:");
		JLabel lbDe = new JLabel("De:");
		JLabel lbAte = new JLabel("Até:");
		JLabel pbPgDe = new JLabel("De:");
		JLabel lbPgAte = new JLabel("Até:");

		txValorInicial = new JMoneyField();
		txCodigo = new JNumericField();
		dtVencimentoInicial = new JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
		dtVencimentoFinal = new JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
		txValorFinal = new JMoneyField();
		chBaixado = new JCheckBox("Baixado");
		chVencido = new JCheckBox("Vencido");
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

		container.setLayout(null);
		pnlVencimento.setLayout(null);
		pnlPagamento.setLayout(null);
		pnlVencimento
				.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Vencimento", TitledBorder.CENTER, TitledBorder.TOP,
						null, new Color(0, 0, 0)));
		pnlPagamento
				.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Pagamento", TitledBorder.CENTER, TitledBorder.TOP,
						null, new Color(0, 0, 0)));

		pnlActions.setBounds(10, 118, 767, 32);
		pnlVencimento.setBounds(7, 55, 280, 52);
		pnlPagamento.setBounds(285, 55, 235, 52);
		lbCodigo.setBounds(10, 10, 55, 14);
		lbFornecedor.setBounds(255, 10, 68, 14);
		lbDocumento.setBounds(10, 35, 68, 14);
		lbPagamento.setBounds(255, 34, 68, 14);
		lbDe.setBounds(10, 24, 23, 14);
		lbAte.setBounds(145, 24, 25, 14);
		pbPgDe.setBounds(10, 24, 22, 14);
		lbPgAte.setBounds(130, 24, 25, 14);
		txCodigo.setBounds(77, 7, 86, 20);
		pesquisaCliente.setBounds(322, 6, 455, 22);
		pesquisaPagamento.setBounds(322, 31, 455, 22);
		dtVencimentoInicial.setBounds(38, 21, 100, 20);
		dtVencimentoFinal.setBounds(170, 21, 100, 20);
		txValorInicial.setBounds(37, 21, 90, 20);
		txValorFinal.setBounds(155, 21, 70, 20);
		chBaixado.setBounds(525, 68, 76, 23);
		txDocumento.setBounds(77, 32, 168, 20);
		chVencido.setBounds(603, 68, 97, 23);

		btnGerar.addActionListener(e -> gerarRelatorio());

		getContentPane().add(container, BorderLayout.CENTER);
		container.add(pnlVencimento);
		container.add(pnlPagamento);
		container.add(pnlActions);

		container.add(lbCodigo);
		container.add(lbFornecedor);
		container.add(lbDocumento);
		container.add(lbPagamento);
		pnlVencimento.add(lbDe);
		pnlVencimento.add(lbAte);
		pnlPagamento.add(pbPgDe);
		pnlPagamento.add(lbPgAte);
		container.add(txCodigo);
		container.add(pesquisaCliente);
		container.add(pesquisaPagamento);
		pnlVencimento.add(dtVencimentoInicial);
		pnlVencimento.add(dtVencimentoFinal);
		pnlPagamento.add(txValorInicial);
		pnlPagamento.add(txValorFinal);
		container.add(chBaixado);
		container.add(txDocumento);
		container.add(chVencido);
		pnlActions.add(btnGerar);

	}

	private void gerarRelatorio() {

		try {

			PesquisaContasVO pesquisaContasVO = new PesquisaContasVO();
			pesquisaContasVO.setCodigoConta(txCodigo.getValue());
			pesquisaContasVO.setCodigoCliente(getValueObject(pesquisaCliente.getObjetoPesquisado(), Cliente::getIdCliente));
			pesquisaContasVO
					.setCodigoFormaPagamento(getValueObject(pesquisaPagamento.getObjetoPesquisado(), FormasPagamento::getIdFormaPagamento));
			pesquisaContasVO.setDataVencimentoInicial(dtVencimentoInicial.getDate());
			pesquisaContasVO.setDataVencimentoFinal(dtVencimentoFinal.getDate());
			pesquisaContasVO.setDocumentoBaixado(chBaixado.isSelected());
			pesquisaContasVO.setValorParcelaInicial(txValorInicial.getValue());
			pesquisaContasVO.setValorParcelaFinal(txValorFinal.getValue());
			pesquisaContasVO.setCodigoDocumento(txDocumento.getText());
			pesquisaContasVO.setDocumentoVencido(chVencido.isSelected());

			List<ContasReceber> contasRecebers = contasReceberService.filtrarContasReceber(pesquisaContasVO);

			new ContasReceberReportBuilder().build("Relatório de Contas á Receber", montarSubTitulo()).setData(contasRecebers).view();
		} catch (JRException e) {

			JOptionPane.showMessageDialog(this, "Ocorreu um erro ao Gerar relatório de contas á receber");
		}
	}

	private List<String> montarSubTitulo() {
		List<String> subtitulo = new ArrayList<>();

		if (!LongUtil.isNullOrZero(txCodigo.getValue())) {

			subtitulo.add("Código: " + txCodigo.getValue());
		}

		if (pesquisaCliente.getObjetoPesquisado() != null) {

			subtitulo.add("Cliente: " + pesquisaCliente.getObjetoPesquisado().getNome());
		}

		if (!StringUtil.isNullOrEmpty(txDocumento.getText())) {

			subtitulo.add("Documento: " + txDocumento.getText());
		}

		if (pesquisaPagamento.getObjetoPesquisado() != null) {

			subtitulo.add("Forma de Pagamento: " + pesquisaPagamento.getObjetoPesquisado().getDescricao());
		}

		if (dtVencimentoFinal.getDate() != null || dtVencimentoInicial.getDate() != null) {

			StringBuilder stringBuilder = new StringBuilder("Data de movimento: ");

			if (dtVencimentoFinal.getDate() != null && dtVencimentoInicial.getDate() != null) {

				stringBuilder.append("De: ").append(DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, dtVencimentoInicial.getDate()))
						.append(" Até: ").append(DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, dtVencimentoFinal.getDate()));
			} else if (dtVencimentoInicial.getDate() != null) {

				stringBuilder.append("A partir De: ")
						.append(DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, dtVencimentoInicial.getDate()));

			} else {
				stringBuilder.append("Até: ")
						.append(DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, dtVencimentoFinal.getDate()));

			}

			subtitulo.add(stringBuilder.toString());
		}

		if (!BigDecimalUtil.isNullOrZero(txValorFinal.getValue()) || !BigDecimalUtil.isNullOrZero(txValorInicial.getValue())) {

			NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

			StringBuilder stringBuilder = new StringBuilder("Valor da parcela: ");

			if (!BigDecimalUtil.isNullOrZero(txValorFinal.getValue()) && !BigDecimalUtil.isNullOrZero(txValorInicial.getValue())) {

				stringBuilder.append("De: ").append(numberFormat.format(txValorInicial.getValue())).append(" Até: ")
						.append(numberFormat.format(txValorFinal.getValue()));

			} else if (!BigDecimalUtil.isNullOrZero(txValorInicial.getValue())) {

				stringBuilder.append("A partir De: ").append(numberFormat.format(txValorInicial.getValue()));

			} else {
				stringBuilder.append("Até: ").append(numberFormat.format(txValorFinal.getValue()));

			}

			subtitulo.add(stringBuilder.toString());
		}

		if (chBaixado.isSelected()) {
			subtitulo.add("Apenas documentos baixados");
		}

		if (chVencido.isSelected()) {

			subtitulo.add("Apenas documentos vencidos");
		}

		return subtitulo;
	}
}
