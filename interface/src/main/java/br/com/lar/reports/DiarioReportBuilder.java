package br.com.lar.reports;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import br.com.lar.repository.projection.DiarioReportProjection;
import br.com.sysdesc.pesquisa.ui.components.ReportViewer;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class DiarioReportBuilder {

	private Integer margin = 20;
	private List<DiarioReportProjection> diarioReports = new ArrayList<>();
	private DynamicReport dynamicReport;

	public DiarioReportBuilder build(String title) {

		FastReportBuilder fastReportBuilder = new FastReportBuilder();

		fastReportBuilder.setTitle(title);
		fastReportBuilder.setDetailHeight(15);

		Style detailStyle = new Style();
		detailStyle.setVerticalAlign(VerticalAlign.TOP);

		Style groupTitleStyle = new Style();
		groupTitleStyle.setFont(Font.ARIAL_BIG);

		Style col2Style = new Style();
		col2Style.setFont(Font.ARIAL_MEDIUM_BOLD);
		col2Style.setBorderBottom(Border.THIN());
		col2Style.setVerticalAlign(VerticalAlign.TOP);

		Style headerStyle = new Style();
		headerStyle.setFont(Font.ARIAL_MEDIUM_BOLD);
		headerStyle.setBackgroundColor(Color.gray);
		headerStyle.setTextColor(Color.white);
		headerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
		headerStyle.setVerticalAlign(VerticalAlign.MIDDLE);
		headerStyle.setTransparency(Transparency.OPAQUE);

		Style g1VariablesStyle = new Style();
		g1VariablesStyle.setFont(Font.ARIAL_MEDIUM_BOLD);
		g1VariablesStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
		g1VariablesStyle.setVerticalAlign(VerticalAlign.MIDDLE);
		g1VariablesStyle.setTextColor(new Color(50, 50, 150));

		Style g2VariablesStyle = new Style();
		g2VariablesStyle.setFont(Font.ARIAL_MEDIUM_BOLD);
		g2VariablesStyle.setTextColor(new Color(150, 150, 150));
		g2VariablesStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
		g2VariablesStyle.setVerticalAlign(VerticalAlign.MIDDLE);

		Style titleStyle = new Style();
		titleStyle.setFont(new Font(18, Font._FONT_VERDANA, true));

		Style subGroupStyle = new Style();
		subGroupStyle.setFont(new Font(12, Font._FONT_VERDANA, true));

		Style importeStyle = new Style();
		importeStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
		Style oddRowStyle = new Style();
		oddRowStyle.setBorder(Border.NO_BORDER());
		oddRowStyle.setBackgroundColor(Color.LIGHT_GRAY);
		oddRowStyle.setTransparency(Transparency.OPAQUE);

		DynamicReportBuilder drb = new DynamicReportBuilder();

		drb
				.setTitleStyle(titleStyle)
				.setTitle(title) // defines the title of the report
				.setDetailHeight(15).setLeftMargin(margin)
				.setRightMargin(margin).setTopMargin(margin).setBottomMargin(margin)
				.setPrintBackgroundOnOddRows(false)
				.setGrandTotalLegend("Grand Total")
				.setGrandTotalLegendStyle(g2VariablesStyle)
				.setDefaultStyles(titleStyle, null, headerStyle, detailStyle)
				.setPrintColumnNames(false)
				.setOddRowBackgroundStyle(oddRowStyle);

		AbstractColumn colunaData = ColumnBuilder.getNew()
				.setColumnProperty("dataMovimento", Date.class.getName())
				.setTitle("Data de Movimento:").setWidth(90)
				.setPattern("dd/MM/yyyy")
				.setStyle(subGroupStyle).setHeaderStyle(groupTitleStyle).build();

		AbstractColumn colunaHistorico = ColumnBuilder.getNew()
				.setColumnProperty("descricaoHistorico", String.class.getName())
				.setTitle("Historico").setWidth(85)
				.setStyle(col2Style).setHeaderStyle(col2Style)
				.build();

		AbstractColumn colunaIdentificador = ColumnBuilder.getNew()
				.setColumnProperty("identificador", String.class.getName())
				.setTitle("Identificador").setWidth(85)
				.setStyle(detailStyle).setHeaderStyle(headerStyle)
				.build();

		AbstractColumn colunaDescricaoConta = ColumnBuilder.getNew()
				.setColumnProperty("descricaoConta", String.class.getName())
				.setTitle("Descricao").setWidth(285)
				.setStyle(detailStyle).setHeaderStyle(headerStyle)
				.build();

		AbstractColumn colutaTipoSaldo = ColumnBuilder.getNew()
				.setColumnProperty("tipoSaldo", String.class.getName())
				.setTitle("Tipo").setWidth(80)
				.setStyle(importeStyle).setHeaderStyle(headerStyle)
				.build();

		AbstractColumn colunaValor = ColumnBuilder.getNew()
				.setColumnProperty("valorSaldo", BigDecimal.class.getName())
				.setTitle("Valor").setWidth(90).setPattern("R$ #,##0.00")
				.setStyle(importeStyle).setHeaderStyle(headerStyle)
				.build();

		GroupBuilder gb1 = new GroupBuilder();
		GroupBuilder gb2 = new GroupBuilder(); // Create another group (using another column as criteria)

		DJGroup g1 = gb1.setCriteriaColumn((PropertyColumn) colunaData)
				.addFooterVariable(colunaValor, DJCalculation.SUM, g1VariablesStyle)
				.setGroupLayout(GroupLayout.VALUE_IN_HEADER_WITH_HEADERS)
				.build();

		DJGroup g2 = gb2.setCriteriaColumn((PropertyColumn) colunaHistorico) // and we add the same operations for the columnAmount and
				.addFooterVariable(colunaValor, DJCalculation.SUM, g2VariablesStyle) // columnaQuantity columns
				.setGroupLayout(GroupLayout.VALUE_IN_HEADER)
				.build();

		drb.addColumn(colunaData);
		drb.addColumn(colunaHistorico);
		drb.addColumn(colunaIdentificador);
		drb.addColumn(colunaDescricaoConta);
		drb.addColumn(colutaTipoSaldo);
		drb.addColumn(colunaValor);

		drb.addGroup(g1); // add group g1
		drb.addGroup(g2); // add group g2

		drb.setUseFullPageWidth(true);
		drb.addAutoText(AutoText.AUTOTEXT_PAGE_X_SLASH_Y, AutoText.POSITION_FOOTER, AutoText.ALIGNMENT_RIGHT);

		dynamicReport = drb.build();

		return this;
	}

	public DiarioReportBuilder setData(List<DiarioReportProjection> diarioReports) {

		this.diarioReports = diarioReports;

		return this;
	}

	public void view() throws JRException {

		JRDataSource ds = new JRBeanCollectionDataSource(diarioReports);

		JasperPrint jasperPrint = DynamicJasperHelper.generateJasperPrint(dynamicReport, new ClassicLayoutManager(), ds);

		new ReportViewer(jasperPrint).setVisible(Boolean.TRUE);
	}

}
