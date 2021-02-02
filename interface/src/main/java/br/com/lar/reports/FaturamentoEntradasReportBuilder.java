package br.com.lar.reports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import br.com.lar.reports.models.FaturamentoReport;
import br.com.lar.repository.projection.FaturamentoEntradaProjection;
import br.com.sysdesc.pesquisa.ui.components.ReportViewer;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FaturamentoEntradasReportBuilder {

	private Integer margin = 20;
	private List<FaturamentoReport> data = new ArrayList<>();
	private DynamicReport dynamicReport;

	public FaturamentoEntradasReportBuilder build(String title, List<String> subtitle) {

		DynamicReportBuilder drb = new DynamicReportBuilder();

		Style titleStyle = new Style("titleStyle");
		titleStyle.setFont(new Font(18, Font._FONT_VERDANA, true));
		titleStyle.setHorizontalAlign(HorizontalAlign.CENTER);

		Style valueStyle = new Style("valueStyle");
		valueStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
		valueStyle.setFont(new Font(10, "Times New Roman", false));

		Style textStyle = new Style("textStyle");
		textStyle.setFont(new Font(10, "Times New Roman", false));

		Style totalStyle = new Style("totalStyle");
		totalStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
		totalStyle.setFont(Font.ARIAL_MEDIUM_BOLD);

		AbstractColumn valorBruto = ColumnBuilder.getNew().setColumnProperty("valorTotal", BigDecimal.class.getName()).setTitle("Valor Bruto")
				.setPattern("#,##0.00")
				.setWidth(75).setStyle(valueStyle).build();

		drb.setTitle(title).setTitleStyle(titleStyle).setSubtitle(subtitle.stream().collect(Collectors.joining("\\n"))).setDetailHeight(15)
				.setLeftMargin(margin).setRightMargin(margin).setTopMargin(margin)
				.setBottomMargin(margin)
				.setPrintBackgroundOnOddRows(false).setGrandTotalLegendStyle(totalStyle).setGrandTotalLegend("Totais");
		drb.addColumn(ColumnBuilder.getNew().setStyle(valueStyle).setColumnProperty("codigoConta", Long.class.getName()).setTitle("Código")
				.setWidth(50).build());
		drb.addColumn(ColumnBuilder.getNew().setStyle(textStyle).setColumnProperty("cliente", String.class.getName()).setTitle("Cliente")
				.setWidth(300).build());
		drb.addColumn(ColumnBuilder.getNew().setStyle(textStyle).setColumnProperty("veiculo", String.class.getName()).setTitle("Veículo")
				.setWidth(100).build());
		drb.addColumn(ColumnBuilder.getNew().setStyle(textStyle).setColumnProperty("dataMovimento", Date.class.getName()).setTitle("Data Movimento")
				.setPattern("dd/MM/yyyy HH:mm:ss")
				.setWidth(120).build());

		drb.addColumn(valorBruto);

		drb.setUseFullPageWidth(true);
		drb.addAutoText(AutoText.AUTOTEXT_PAGE_X_SLASH_Y, AutoText.POSITION_FOOTER, AutoText.ALIGNMENT_RIGHT);

		drb.addGlobalFooterVariable(valorBruto, DJCalculation.SUM, totalStyle);

		dynamicReport = drb.build();

		return this;
	}

	public FaturamentoEntradasReportBuilder setData(List<FaturamentoEntradaProjection> faturamentoEntradasCabecalhos) {

		this.data = faturamentoEntradasCabecalhos.stream().map(this::mapearData).collect(Collectors.toList());

		this.data.sort(Comparator.comparing(FaturamentoReport::getDataMovimento));

		return this;
	}

	public void view() throws JRException {

		JRDataSource ds = new JRBeanCollectionDataSource(data);

		JasperPrint jasperPrint = DynamicJasperHelper.generateJasperPrint(dynamicReport, new ClassicLayoutManager(), ds);

		new ReportViewer(jasperPrint).setVisible(Boolean.TRUE);
	}

	private FaturamentoReport mapearData(FaturamentoEntradaProjection faturamentoEntradas) {

		FaturamentoReport faturmaentoReport = new FaturamentoReport();

		faturmaentoReport.setCodigoConta(faturamentoEntradas.getIdFaturamentoEntradasCabecalho());
		faturmaentoReport.setVeiculo(faturamentoEntradas.getVeiculo());
		faturmaentoReport.setCliente(cortar(faturamentoEntradas.getCliente(), 40));
		faturmaentoReport.setDataMovimento(faturamentoEntradas.getDataMovimento());
		faturmaentoReport.setValorTotal(faturamentoEntradas.getValorBruto());

		return faturmaentoReport;
	}

	private String cortar(String texto, int tamanho) {

		return texto.length() > tamanho ? texto.substring(0, tamanho) : texto;
	}

}
