package br.com.lar.reports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import br.com.lar.reports.models.FaturamentoBrutoReport;
import br.com.sysdesc.pesquisa.ui.components.ReportViewer;
import br.com.systrans.util.vo.FaturamentoBrutoVO;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FaturamentoBrutoReportBuilder {

	private Integer margin = 20;
	private List<FaturamentoBrutoReport> data = new ArrayList<>();
	private DynamicReport dynamicReport;

	public FaturamentoBrutoReportBuilder build(String title) {

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

		AbstractColumn valorBruto = ColumnBuilder.getNew().setColumnProperty("valor", BigDecimal.class.getName())
				.setPattern("#,##0.00")
				.setWidth(75).setStyle(valueStyle).build();

		drb.setTitle(title).setTitleStyle(titleStyle).setDetailHeight(15).setLeftMargin(margin).setRightMargin(margin).setTopMargin(margin)
				.setBottomMargin(margin).setPrintBackgroundOnOddRows(false);

		drb.addColumn(ColumnBuilder.getNew().setStyle(textStyle).setColumnProperty("descricao", String.class.getName())
				.setWidth(500).build());

		drb.addColumn(valorBruto);

		drb.setUseFullPageWidth(true);
		drb.addAutoText(AutoText.AUTOTEXT_PAGE_X_SLASH_Y, AutoText.POSITION_FOOTER, AutoText.ALIGNMENT_RIGHT);

		dynamicReport = drb.build();

		return this;
	}

	public FaturamentoBrutoReportBuilder setData(FaturamentoBrutoVO data) {

		this.data = mapearData(data);

		return this;
	}

	public void view() throws JRException {

		JRDataSource ds = new JRBeanCollectionDataSource(data);

		JasperPrint jasperPrint = DynamicJasperHelper.generateJasperPrint(dynamicReport, new ClassicLayoutManager(), ds);

		new ReportViewer(jasperPrint).setVisible(Boolean.TRUE);
	}

	private List<FaturamentoBrutoReport> mapearData(FaturamentoBrutoVO data) {

		List<FaturamentoBrutoReport> faturamentoBrutoReport = new ArrayList<>();

		FaturamentoBrutoReport receitas = new FaturamentoBrutoReport();
		receitas.setDescricao("RECEITA BRUTA");
		receitas.setValor(data.getTotalReceitas());
		faturamentoBrutoReport.add(receitas);

		FaturamentoBrutoReport despesas = new FaturamentoBrutoReport();
		despesas.setDescricao("DESPESAS");
		despesas.setValor(data.getTotalDespesas().negate());
		faturamentoBrutoReport.add(despesas);

		FaturamentoBrutoReport bruto = new FaturamentoBrutoReport();
		bruto.setDescricao("FATURAMENTO BRUTO");
		bruto.setValor(data.getValorFaturamento());
		faturamentoBrutoReport.add(bruto);

		return faturamentoBrutoReport;
	}

}
