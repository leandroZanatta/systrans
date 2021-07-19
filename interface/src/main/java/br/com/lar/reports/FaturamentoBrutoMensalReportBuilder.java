package br.com.lar.reports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle;
import br.com.lar.reports.models.FaturamentoMensalReport;
import br.com.lar.reports.models.condictions.FaturamentoBrutoCondiction;
import br.com.sysdesc.pesquisa.ui.components.ReportViewer;
import br.com.systrans.util.vo.FaturamentoBrutoMensalVO;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FaturamentoBrutoMensalReportBuilder {

	private Integer margin = 20;
	private List<FaturamentoMensalReport> data = new ArrayList<>();
	private DynamicReport dynamicReport;

	public FaturamentoBrutoMensalReportBuilder build(String title, List<String> subtitle, Integer tipoBalanco) {

		DynamicReportBuilder drb = new DynamicReportBuilder();

		Style titleStyle = new Style("titleStyle");
		titleStyle.setFont(new Font(12, Font._FONT_VERDANA, true));
		titleStyle.setHorizontalAlign(HorizontalAlign.CENTER);

		Style textPrincipal = new Style("principalStyle");
		textPrincipal.setFont(new Font(8, "Times New Roman", true));
		textPrincipal.setHorizontalAlign(HorizontalAlign.RIGHT);
		textPrincipal.setBorderBottom(Border.THIN());

		Style textHistorico = new Style("historicoStyle");
		textHistorico.setFont(new Font(8, "Times New Roman", false));
		textHistorico.setHorizontalAlign(HorizontalAlign.RIGHT);
		textHistorico.setBorderBottom(Border.DOTTED());

		Style textVeiculo = new Style("veiculoStyle");
		textVeiculo.setFont(new Font(8, "Times New Roman", false));
		textVeiculo.setHorizontalAlign(HorizontalAlign.RIGHT);
		textVeiculo.setBorderBottom(Border.DOTTED());

		Style textPaddingPrincipal = new Style("principalStyle");
		textPaddingPrincipal.setFont(new Font(8, "Times New Roman", true));
		textPaddingPrincipal.setBorderBottom(Border.THIN());

		Style headerSyle = new Style("headerSyle");
		headerSyle.setFont(new Font(8, "Times New Roman", false));
		headerSyle.setHorizontalAlign(HorizontalAlign.RIGHT);

		Style headerTextSyle = new Style("headerSyle");
		headerTextSyle.setFont(new Font(8, "Times New Roman", false));

		Style textPaddingHistorico = new Style("historicopaddingStyle", "textHistorico");
		textPaddingHistorico.setPaddingLeft(15);
		textHistorico.setFont(new Font(8, "Times New Roman", false));
		textPaddingHistorico.setBorderBottom(Border.DOTTED());

		Style textPaddingVeiculo = new Style("veiculopaddingStyle", "textVeiculo");
		textPaddingVeiculo.setPaddingLeft(30);
		textVeiculo.setFont(new Font(8, "Times New Roman", false));
		textPaddingVeiculo.setBorderBottom(Border.DOTTED());

		drb.setTitle(title).setTitleStyle(titleStyle)
				.setSubtitle(subtitle.stream().map(item -> item.toUpperCase()).collect(Collectors.joining("\\n"))).setDetailHeight(15)
				.setLeftMargin(margin).setRightMargin(margin).setTopMargin(margin).setBottomMargin(margin).setPrintBackgroundOnOddRows(false);

		ArrayList<ConditionalStyle> listCondStyle = new ArrayList<>();
		listCondStyle.add(new ConditionalStyle(new FaturamentoBrutoCondiction(1), textPaddingPrincipal));
		listCondStyle.add(new ConditionalStyle(new FaturamentoBrutoCondiction(2), textPaddingHistorico));
		listCondStyle.add(new ConditionalStyle(new FaturamentoBrutoCondiction(3), textPaddingVeiculo));

		ArrayList<ConditionalStyle> listValueCondStyle = new ArrayList<>();
		listValueCondStyle.add(new ConditionalStyle(new FaturamentoBrutoCondiction(1), textPrincipal));
		listValueCondStyle.add(new ConditionalStyle(new FaturamentoBrutoCondiction(2), textHistorico));
		listValueCondStyle.add(new ConditionalStyle(new FaturamentoBrutoCondiction(3), textVeiculo));

		drb.addColumn(ColumnBuilder.getNew().setTitle("DESCRIÇÃO").setHeaderStyle(headerTextSyle)
				.setColumnProperty("descricao", String.class.getName()).setWidth(200).addConditionalStyles(listCondStyle).build());

		drb.addColumn(ColumnBuilder.getNew().setTitle("JANEIRO").setHeaderStyle(headerSyle).setColumnProperty("valorJan", BigDecimal.class.getName())
				.addConditionalStyles(listValueCondStyle).setPattern("#,##0.00").setWidth(75).build());
		drb.addColumn(
				ColumnBuilder.getNew().setTitle("FEVEREIRO").setHeaderStyle(headerSyle).setColumnProperty("valorFev", BigDecimal.class.getName())
						.addConditionalStyles(listValueCondStyle).setPattern("#,##0.00").setWidth(75).build());
		drb.addColumn(ColumnBuilder.getNew().setTitle("MARÇO").setHeaderStyle(headerSyle).setColumnProperty("valorMar", BigDecimal.class.getName())
				.addConditionalStyles(listValueCondStyle).setPattern("#,##0.00").setWidth(75).build());
		drb.addColumn(ColumnBuilder.getNew().setTitle("ABRIL").setHeaderStyle(headerSyle).setColumnProperty("valorAbr", BigDecimal.class.getName())
				.addConditionalStyles(listValueCondStyle).setPattern("#,##0.00").setWidth(75).build());
		drb.addColumn(ColumnBuilder.getNew().setTitle("MAIO").setHeaderStyle(headerSyle).setColumnProperty("valorMai", BigDecimal.class.getName())
				.addConditionalStyles(listValueCondStyle).setPattern("#,##0.00").setWidth(75).build());
		drb.addColumn(ColumnBuilder.getNew().setTitle("JUNHO").setHeaderStyle(headerSyle).setColumnProperty("valorJun", BigDecimal.class.getName())
				.addConditionalStyles(listValueCondStyle).setPattern("#,##0.00").setWidth(75).build());
		drb.addColumn(ColumnBuilder.getNew().setTitle("JULHO").setHeaderStyle(headerSyle).setColumnProperty("valorJul", BigDecimal.class.getName())
				.addConditionalStyles(listValueCondStyle).setPattern("#,##0.00").setWidth(75).build());
		drb.addColumn(ColumnBuilder.getNew().setTitle("AGOSTO").setHeaderStyle(headerSyle).setColumnProperty("valorAgo", BigDecimal.class.getName())
				.addConditionalStyles(listValueCondStyle).setPattern("#,##0.00").setWidth(75).build());
		drb.addColumn(ColumnBuilder.getNew().setTitle("SETEMBRO").setHeaderStyle(headerSyle).setColumnProperty("valorSet", BigDecimal.class.getName())
				.addConditionalStyles(listValueCondStyle).setPattern("#,##0.00").setWidth(75).build());
		drb.addColumn(ColumnBuilder.getNew().setTitle("OUTUBRO").setHeaderStyle(headerSyle).setColumnProperty("valorOut", BigDecimal.class.getName())
				.addConditionalStyles(listValueCondStyle).setPattern("#,##0.00").setWidth(75).build());
		drb.addColumn(ColumnBuilder.getNew().setTitle("NOVEMBRO").setHeaderStyle(headerSyle).setColumnProperty("valorNov", BigDecimal.class.getName())
				.addConditionalStyles(listValueCondStyle).setPattern("#,##0.00").setWidth(75).build());
		drb.addColumn(ColumnBuilder.getNew().setTitle("DEZEMBRO").setHeaderStyle(headerSyle).setColumnProperty("valorDez", BigDecimal.class.getName())
				.addConditionalStyles(listValueCondStyle).setPattern("#,##0.00").setWidth(75).build());
		drb.addColumn(ColumnBuilder.getNew().setTitle("TOTAL").setHeaderStyle(headerSyle).setColumnProperty("valorTotal", BigDecimal.class.getName())
				.addConditionalStyles(listValueCondStyle).setPattern("#,##0.00").setWidth(75).build());
		drb.addColumn(ColumnBuilder.getNew().setColumnProperty("agrupamento", Integer.class.getName()).setWidth(0).build());

		drb.setUseFullPageWidth(true);
		drb.addAutoText(AutoText.AUTOTEXT_PAGE_X_SLASH_Y, AutoText.POSITION_FOOTER, AutoText.ALIGNMENT_RIGHT);
		drb.setPageSizeAndOrientation(Page.Page_A4_Landscape());

		dynamicReport = drb.build();

		return this;
	}

	public FaturamentoBrutoMensalReportBuilder setData(List<FaturamentoBrutoMensalVO> data) {

		List<FaturamentoMensalReport> retorno = new ArrayList<>();

		for (int ordem = 1; ordem <= 5; ordem++) {

			montarReportOrdenado(data, retorno, ordem);
		}

		this.data = retorno;

		return this;
	}

	private void montarReportOrdenado(List<FaturamentoBrutoMensalVO> data, List<FaturamentoMensalReport> retorno, int ordem) {
		List<FaturamentoBrutoMensalVO> retornoOrdem = data.stream().filter(faturamento -> faturamento.getOrdem() == ordem)
				.collect(Collectors.toList());

		Map<Integer, List<FaturamentoBrutoMensalVO>> mapaAgrupamento = retornoOrdem.stream()
				.collect(Collectors.groupingBy(FaturamentoBrutoMensalVO::getAgrupamento));

		mapaAgrupamento.forEach((agrupamento, listaAgrupamento) -> {

			Map<String, List<FaturamentoBrutoMensalVO>> mapaDescricao = listaAgrupamento.stream()
					.collect(Collectors.groupingBy(FaturamentoBrutoMensalVO::getDescricao));

			mapaDescricao.forEach((decricao, listaDescricao) -> retorno.add(montarAgrupamento(agrupamento, decricao, listaDescricao)));
		});
	}

	private FaturamentoMensalReport montarAgrupamento(Integer agrupamento, String decricao, List<FaturamentoBrutoMensalVO> listaDescricao) {

		FaturamentoMensalReport faturamentoMensalReport = new FaturamentoMensalReport();
		faturamentoMensalReport.setAgrupamento(agrupamento);
		faturamentoMensalReport.setDescricao(decricao);

		preencherValor(faturamentoMensalReport, FaturamentoMensalReport::setValorJan, 1, listaDescricao);
		preencherValor(faturamentoMensalReport, FaturamentoMensalReport::setValorFev, 2, listaDescricao);
		preencherValor(faturamentoMensalReport, FaturamentoMensalReport::setValorMar, 3, listaDescricao);
		preencherValor(faturamentoMensalReport, FaturamentoMensalReport::setValorAbr, 4, listaDescricao);
		preencherValor(faturamentoMensalReport, FaturamentoMensalReport::setValorMai, 5, listaDescricao);
		preencherValor(faturamentoMensalReport, FaturamentoMensalReport::setValorJun, 6, listaDescricao);
		preencherValor(faturamentoMensalReport, FaturamentoMensalReport::setValorJul, 7, listaDescricao);
		preencherValor(faturamentoMensalReport, FaturamentoMensalReport::setValorAgo, 8, listaDescricao);
		preencherValor(faturamentoMensalReport, FaturamentoMensalReport::setValorSet, 9, listaDescricao);
		preencherValor(faturamentoMensalReport, FaturamentoMensalReport::setValorOut, 10, listaDescricao);
		preencherValor(faturamentoMensalReport, FaturamentoMensalReport::setValorNov, 11, listaDescricao);
		preencherValor(faturamentoMensalReport, FaturamentoMensalReport::setValorDez, 12, listaDescricao);

		faturamentoMensalReport.setValorTotal(faturamentoMensalReport.getValorJan().add(faturamentoMensalReport.getValorFev())
				.add(faturamentoMensalReport.getValorMar()).add(faturamentoMensalReport.getValorAbr()).add(faturamentoMensalReport.getValorMai())
				.add(faturamentoMensalReport.getValorJun()).add(faturamentoMensalReport.getValorJul()).add(faturamentoMensalReport.getValorAgo())
				.add(faturamentoMensalReport.getValorSet()).add(faturamentoMensalReport.getValorOut()).add(faturamentoMensalReport.getValorNov())
				.add(faturamentoMensalReport.getValorDez()));

		return faturamentoMensalReport;
	}

	private void preencherValor(FaturamentoMensalReport model, BiConsumer<FaturamentoMensalReport, BigDecimal> setterFunction, int i,
			List<FaturamentoBrutoMensalVO> lista) {

		Optional<FaturamentoBrutoMensalVO> optional = lista.stream().filter(item -> item.getMesReferencia() == i).findFirst();

		if (optional.isPresent()) {
			setterFunction.accept(model, optional.get().getValor());
		}

	}

	public void view() throws JRException {

		JRDataSource ds = new JRBeanCollectionDataSource(data);

		JasperPrint jasperPrint = DynamicJasperHelper.generateJasperPrint(dynamicReport, new ClassicLayoutManager(), ds);

		new ReportViewer(jasperPrint).setVisible(Boolean.TRUE);
	}

}
