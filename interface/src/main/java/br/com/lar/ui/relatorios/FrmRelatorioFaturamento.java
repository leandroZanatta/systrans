package br.com.lar.ui.relatorios;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.toedter.calendar.JDateChooser;

import br.com.lar.reports.FaturamentoBrutoReportBuilder;
import br.com.lar.repository.model.CentroCusto;
import br.com.lar.repository.model.Historico;
import br.com.lar.repository.model.Veiculo;
import br.com.lar.service.centrocusto.CentroCustoService;
import br.com.lar.service.faturamento.FaturamentoService;
import br.com.lar.service.historico.HistoricoService;
import br.com.lar.service.veiculo.VeiculoService;
import br.com.lar.startup.enumeradores.PesquisaEnum;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.pesquisa.ui.components.CampoPesquisaMultiSelect;
import br.com.sysdesc.util.classes.DateUtil;
import br.com.sysdesc.util.classes.StringUtil;
import br.com.systrans.util.vo.FaturamentoBrutoVO;
import br.com.systrans.util.vo.PesquisaFaturamentoBrutoVO;
import net.sf.jasperreports.engine.JRException;

public class FrmRelatorioFaturamento extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private FaturamentoService faturamentoService = new FaturamentoService();
	private HistoricoService historicoService = new HistoricoService();
	private VeiculoService veiculoService = new VeiculoService();
	private CentroCustoService centroCustoService = new CentroCustoService();
	private CampoPesquisaMultiSelect<Historico> pesquisaHistorico;
	private CampoPesquisaMultiSelect<Veiculo> pesquisaVeiculo;
	private CampoPesquisaMultiSelect<CentroCusto> pesquisaCentroCusto;
	private JDateChooser dtMovimentoInicial;
	private JDateChooser dtMovimentoFinal;

	public FrmRelatorioFaturamento(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();

	}

	private void initComponents() {

		setSize(366, 280);
		setClosable(Boolean.TRUE);
		setTitle("Relatório de Faturamento Bruto");

		JPanel container = new JPanel();
		JPanel pnlVencimento = new JPanel();
		JPanel pnlActions = new JPanel();
		JLabel lbDe = new JLabel("De:");
		JLabel lbAte = new JLabel("Até:");
		JLabel lblHistrico = new JLabel("Histórico:");
		JLabel lbVeiculo = new JLabel("Veículo:");
		JLabel lbCentroCusto = new JLabel("Centro Custo:");
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

		container.setLayout(null);
		pnlVencimento.setLayout(null);
		pnlVencimento
				.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Movimento", TitledBorder.CENTER, TitledBorder.TOP,
						null, new Color(0, 0, 0)));

		pnlActions.setBounds(7, 210, 340, 32);
		pnlVencimento.setBounds(7, 147, 340, 52);
		lbDe.setBounds(10, 24, 23, 14);
		lbAte.setBounds(172, 24, 25, 14);
		lblHistrico.setBounds(10, 10, 68, 14);
		lbVeiculo.setBounds(10, 55, 68, 14);
		lbCentroCusto.setBounds(10, 100, 150, 14);
		pesquisaHistorico.setBounds(10, 25, 335, 22);
		pesquisaVeiculo.setBounds(10, 70, 335, 22);
		pesquisaCentroCusto.setBounds(10, 115, 335, 22);
		dtMovimentoInicial.setBounds(38, 21, 124, 20);
		dtMovimentoFinal.setBounds(205, 21, 124, 20);

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
		container.add(pesquisaHistorico);
		container.add(pesquisaVeiculo);
		container.add(pesquisaCentroCusto);
		pnlVencimento.add(dtMovimentoInicial);
		pnlVencimento.add(dtMovimentoFinal);
		pnlActions.add(btnGerar);
	}

	private void gerarRelatorio() {

		try {

			PesquisaFaturamentoBrutoVO pesquisaFaturamentoBrutoVO = new PesquisaFaturamentoBrutoVO();

			pesquisaFaturamentoBrutoVO.setCodigoCentroCustos(pesquisaCentroCusto.getObjetosPesquisado().stream()
					.mapToLong(CentroCusto::getIdCentroCusto).boxed().collect(Collectors.toList()));
			pesquisaFaturamentoBrutoVO.setCodigoHistoricos(pesquisaHistorico.getObjetosPesquisado().stream()
					.mapToLong(Historico::getIdHistorico).boxed().collect(Collectors.toList()));
			pesquisaFaturamentoBrutoVO.setCodigoVeiculos(pesquisaVeiculo.getObjetosPesquisado().stream()
					.mapToLong(Veiculo::getIdVeiculo).boxed().collect(Collectors.toList()));
			pesquisaFaturamentoBrutoVO.setDataMovimentoInicial(dtMovimentoInicial.getDate());
			pesquisaFaturamentoBrutoVO.setDataMovimentoFinal(dtMovimentoFinal.getDate());

			List<FaturamentoBrutoVO> faturamentoBrutoVOs = faturamentoService.filtrarFaturamentoBruto(pesquisaFaturamentoBrutoVO);

			new FaturamentoBrutoReportBuilder().build("Relatório de Faturamento", montarSubTitulo())
					.setData(faturamentoBrutoVOs).view();

		} catch (JRException e) {
			JOptionPane.showMessageDialog(this, "Ocorreu um erro ao Gerar relatório de contas á pagar");
		}
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

		return subtitulo;
	}
}
