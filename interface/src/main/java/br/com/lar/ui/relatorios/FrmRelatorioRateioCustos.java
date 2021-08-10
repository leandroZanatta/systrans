package br.com.lar.ui.relatorios;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.toedter.calendar.JDateChooser;

import br.com.lar.repository.model.CentroCusto;
import br.com.lar.repository.model.FaturamentoEntradasCabecalho;
import br.com.lar.repository.model.Historico;
import br.com.lar.repository.model.Veiculo;
import br.com.lar.service.alocacaocusto.AlocacaoCustoService;
import br.com.lar.service.centrocusto.CentroCustoService;
import br.com.lar.service.faturamento.FaturamentoEntradaService;
import br.com.lar.service.faturamento.report.FaturamentoReportService;
import br.com.lar.service.historico.HistoricoService;
import br.com.lar.service.veiculo.VeiculoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisaMultiSelect;
import br.com.sysdesc.util.classes.DateUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.systrans.util.vo.AlocacaoCustoVO;
import br.com.systrans.util.vo.PesquisaCentroCustoVO;
import net.miginfocom.swing.MigLayout;

public class FrmRelatorioRateioCustos extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private FaturamentoReportService faturamentoService = new FaturamentoReportService();
	private HistoricoService historicoService = new HistoricoService();
	private VeiculoService veiculoService = new VeiculoService();
	private CentroCustoService centroCustoService = new CentroCustoService();
	private FaturamentoEntradaService faturamentoEntradaService = new FaturamentoEntradaService();
	private AlocacaoCustoService alocacaoCustoService = new AlocacaoCustoService();

	private CampoPesquisaMultiSelect<Historico> pesquisaHistorico;
	private CampoPesquisaMultiSelect<Veiculo> pesquisaVeiculo;
	private CampoPesquisaMultiSelect<CentroCusto> pesquisaCentroCusto;
	private CampoPesquisaMultiSelect<FaturamentoEntradasCabecalho> pesquisaFaturamentoEntrada;
	private JDateChooser dtMovimentoInicial;
	private JDateChooser dtMovimentoFinal;
	private JComboBox<String> cbTipoBalanco;

	public FrmRelatorioRateioCustos(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();

	}

	private void initComponents() {

		setSize(435, 380);
		setClosable(Boolean.TRUE);
		setTitle("RELATÓRIO DE RATEIO DE CUSTOS");

		JPanel container = new JPanel();
		JPanel pnlVencimento = new JPanel();
		JPanel pnlActions = new JPanel();
		JLabel lbDe = new JLabel("De:");
		JLabel lbAte = new JLabel("Até:");
		JLabel lblHistrico = new JLabel("Histórico:");
		JLabel lbVeiculo = new JLabel("Veículo:");
		JLabel lbCentroCusto = new JLabel("Centro Custo:");
		JLabel lblDespesa = new JLabel("Despesa:");
		dtMovimentoInicial = new JDateChooser("dd/MM/yyyy", "##/##/#####", '_');
		dtMovimentoFinal = new JDateChooser("dd/MM/yyyy", "##/##/#####", '_');

		JButton btnGerar = new JButton("Gerar");

		pesquisaHistorico = new CampoPesquisaMultiSelect<Historico>(historicoService, PesquisaEnum.PES_OPERACOES.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected String formatarValorCampoMultiple(List<Historico> objetos) {

				return objetos.stream().map(historico -> historico.getIdHistorico().toString()).collect(Collectors.joining(",", "<", ">"));
			}

			@Override
			protected String formatarValorCampoSingle(Historico objeto) {

				return String.format("%d - %s", objeto.getIdHistorico(), objeto.getDescricao());
			}
		};
		pesquisaVeiculo = new CampoPesquisaMultiSelect<Veiculo>(veiculoService, PesquisaEnum.PES_VEICULOS.getCodigoPesquisa(), getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected String formatarValorCampoMultiple(List<Veiculo> objetos) {

				return objetos.stream().map(veiculo -> veiculo.getIdVeiculo().toString()).collect(Collectors.joining(",", "<", ">"));
			}

			@Override
			protected String formatarValorCampoSingle(Veiculo objeto) {

				return String.format("%d - %s", objeto.getIdVeiculo(), objeto.getPlaca());
			}
		};
		pesquisaCentroCusto = new CampoPesquisaMultiSelect<CentroCusto>(centroCustoService, PesquisaEnum.PES_CENTRO_CUSTO.getCodigoPesquisa(),
				getCodigoUsuario()) {

			private static final long serialVersionUID = 1L;

			@Override
			protected String formatarValorCampoMultiple(List<CentroCusto> objetos) {

				return objetos.stream().map(centroCusto -> centroCusto.getIdCentroCusto().toString()).collect(Collectors.joining(",", "<", ">"));
			}

			@Override
			protected String formatarValorCampoSingle(CentroCusto objeto) {

				return String.format("%d - %s", objeto.getIdCentroCusto(), objeto.getDescricao());
			}

		};

		pesquisaFaturamentoEntrada = new CampoPesquisaMultiSelect<FaturamentoEntradasCabecalho>(faturamentoEntradaService,
				PesquisaEnum.PES_FATURAMENTO_ENTRADA.getCodigoPesquisa(), getCodigoUsuario()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected String formatarValorCampoMultiple(List<FaturamentoEntradasCabecalho> objetos) {

				return objetos.stream().map(centroCusto -> centroCusto.getIdFaturamentoEntradasCabecalho().toString())
						.collect(Collectors.joining(",", "<", ">"));
			}

			@Override
			protected String formatarValorCampoSingle(FaturamentoEntradasCabecalho objeto) {

				return String.format("%d - %s", objeto.getIdFaturamentoEntradasCabecalho(),
						DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, objeto.getDataMovimento()) + " - " + objeto.getHistorico().getDescricao() + " - "
								+ new DecimalFormat("#,###,##0.00").format(objeto.getValorBruto().doubleValue()));
			}
		};

		container.setLayout(null);
		pnlVencimento.setLayout(null);
		pnlVencimento.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Movimento", TitledBorder.CENTER,
				TitledBorder.TOP, null, new Color(0, 0, 0)));

		pnlActions.setBounds(7, 307, 402, 32);
		pnlVencimento.setBounds(7, 189, 402, 52);
		lbDe.setBounds(10, 24, 23, 14);
		lbAte.setBounds(215, 24, 25, 14);
		lblHistrico.setBounds(10, 10, 68, 14);
		lbVeiculo.setBounds(10, 55, 68, 14);
		lbCentroCusto.setBounds(10, 100, 150, 14);
		lblDespesa.setBounds(10, 147, 150, 14);
		pesquisaFaturamentoEntrada.setBounds(10, 163, 399, 22);
		pesquisaHistorico.setBounds(10, 25, 399, 22);
		pesquisaVeiculo.setBounds(10, 70, 399, 22);
		pesquisaCentroCusto.setBounds(10, 115, 399, 22);
		dtMovimentoInicial.setBounds(30, 20, 150, 20);
		dtMovimentoFinal.setBounds(240, 20, 150, 20);

		Calendar calendar = Calendar.getInstance();
		dtMovimentoFinal.setDate(calendar.getTime());
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		dtMovimentoInicial.setDate(calendar.getTime());

		btnGerar.addActionListener(e -> gerarRelatorio());

		getContentPane().add(container, BorderLayout.CENTER);
		container.add(pnlVencimento);
		container.add(pnlActions);

		container.add(lblHistrico);
		container.add(lbVeiculo);
		container.add(lbCentroCusto);
		pnlVencimento.add(lbDe);
		pnlVencimento.add(lbAte);
		container.add(lblDespesa);
		container.add(pesquisaFaturamentoEntrada);
		container.add(pesquisaHistorico);
		container.add(pesquisaVeiculo);
		container.add(pesquisaCentroCusto);
		pnlVencimento.add(dtMovimentoInicial);
		pnlVencimento.add(dtMovimentoFinal);
		pnlActions.add(btnGerar);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Agrupamento", TitledBorder.CENTER, TitledBorder.TOP,
				null, null));
		panel.setBounds(7, 241, 402, 55);
		container.add(panel);
		panel.setLayout(new MigLayout("", "[64px,grow]", "[20px]"));
		cbTipoBalanco = new JComboBox<>();
		panel.add(cbTipoBalanco, "cell 0 0,growx,aligny top");

		cbTipoBalanco.setModel(new DefaultComboBoxModel(new String[] { "Sem Agrupamento", "Histórico", "Veículo" }));

	}

	private void gerarRelatorio() {

		PesquisaCentroCustoVO pesquisaCentroCustoVO = new PesquisaCentroCustoVO();

		pesquisaCentroCustoVO.setCodigoCentroCustos(
				pesquisaCentroCusto.getObjetosPesquisado().stream().mapToLong(CentroCusto::getIdCentroCusto).boxed().collect(Collectors.toList()));
		pesquisaCentroCustoVO.setCodigoHistoricos(
				pesquisaHistorico.getObjetosPesquisado().stream().mapToLong(Historico::getIdHistorico).boxed().collect(Collectors.toList()));
		pesquisaCentroCustoVO.setCodigoVeiculos(
				pesquisaVeiculo.getObjetosPesquisado().stream().mapToLong(Veiculo::getIdVeiculo).boxed().collect(Collectors.toList()));
		pesquisaCentroCustoVO.setDespesas(pesquisaFaturamentoEntrada.getObjetosPesquisado().stream()
				.mapToLong(FaturamentoEntradasCabecalho::getIdFaturamentoEntradasCabecalho).boxed().collect(Collectors.toList()));
		pesquisaCentroCustoVO.setDataMovimentoInicial(dtMovimentoInicial.getDate());
		pesquisaCentroCustoVO.setDataMovimentoFinal(dtMovimentoFinal.getDate());

		List<AlocacaoCustoVO> faturamentoBrutoMensalVOs = alocacaoCustoService.filtrarAlocacaoCusto(pesquisaCentroCustoVO);
	}

	private String montarTitulo() {

		return "RELATÓRIO DE FATURAMENTO MENSAL HISTÓRICO";
	}

	private List<String> montarSubTitulo() {
		List<String> subtitulo = new ArrayList<>();

		if (!StringUtil.isNullOrEmpty(pesquisaHistorico.getText())) {
			subtitulo.add("Histórico: " + pesquisaHistorico.getText().replace("<", "").replace(">", ""));
		}

		if (!StringUtil.isNullOrEmpty(pesquisaVeiculo.getText())) {
			subtitulo.add("Veículo: " + pesquisaVeiculo.getText().replace("<", "").replace(">", ""));
		}

		if (!StringUtil.isNullOrEmpty(pesquisaCentroCusto.getText())) {
			subtitulo.add("Centro de Custo: " + pesquisaCentroCusto.getText().replace("<", "").replace(">", ""));
		}

		if (dtMovimentoFinal.getDate() != null || dtMovimentoInicial.getDate() != null) {

			StringBuilder stringBuilder = new StringBuilder("Data de movimento: ");

			if (dtMovimentoFinal.getDate() != null && dtMovimentoInicial.getDate() != null) {

				stringBuilder.append("De: ").append(DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, dtMovimentoInicial.getDate())).append(" Até: ")
						.append(DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, dtMovimentoFinal.getDate()));
			} else if (dtMovimentoInicial.getDate() != null) {

				stringBuilder.append("A partir De: ").append(DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, dtMovimentoInicial.getDate()));

			} else {
				stringBuilder.append("Até: ").append(DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, dtMovimentoFinal.getDate()));

			}

			subtitulo.add(stringBuilder.toString());
		}

		subtitulo.add("USANDO BALANÇO: " + cbTipoBalanco.getSelectedItem().toString());

		return subtitulo;
	}
}
