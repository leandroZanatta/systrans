package br.com.lar.reports;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
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
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle;
import br.com.lar.reports.models.condictions.FaturamentoBrutoCondiction;
import br.com.sysdesc.pesquisa.ui.components.ReportViewer;
import br.com.systrans.util.vo.FaturamentoBrutoVO;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FaturamentoBrutoReportBuilder {

	private Integer margin = 20;
	private List<FaturamentoBrutoVO> data = new ArrayList<>();
	private DynamicReport dynamicReport;
	private Integer tipoBalanco;

	public FaturamentoBrutoReportBuilder build(String title, List<String> subtitle, Integer tipoBalanco) {
		this.tipoBalanco = tipoBalanco;

		DynamicReportBuilder drb = new DynamicReportBuilder();

		Style titleStyle = new Style("titleStyle");
		titleStyle.setFont(new Font(18, Font._FONT_VERDANA, true));
		titleStyle.setHorizontalAlign(HorizontalAlign.CENTER);

		Style textPrincipal = new Style("principalStyle");
		textPrincipal.setFont(new Font(12, "Times New Roman", true));
		textPrincipal.setHorizontalAlign(HorizontalAlign.RIGHT);
		textPrincipal.setBorderBottom(Border.THIN());

		Style textHistorico = new Style("historicoStyle");
		textHistorico.setFont(new Font(12, "Times New Roman", false));
		textHistorico.setHorizontalAlign(HorizontalAlign.RIGHT);
		textHistorico.setBorderBottom(Border.DOTTED());

		Style textVeiculo = new Style("veiculoStyle");
		textVeiculo.setFont(new Font(12, "Times New Roman", false));
		textVeiculo.setHorizontalAlign(HorizontalAlign.RIGHT);
		textVeiculo.setBorderBottom(Border.DOTTED());

		Style textPaddingPrincipal = new Style("principalStyle");
		textPaddingPrincipal.setFont(new Font(12, "Times New Roman", true));
		textPaddingPrincipal.setBorderBottom(Border.THIN());

		Style textPaddingHistorico = new Style("historicopaddingStyle", "textHistorico");
		textPaddingHistorico.setPaddingLeft(15);
		textHistorico.setFont(new Font(11, "Times New Roman", false));
		textPaddingHistorico.setBorderBottom(Border.DOTTED());

		Style textPaddingVeiculo = new Style("veiculopaddingStyle", "textVeiculo");
		textPaddingVeiculo.setPaddingLeft(30);
		textVeiculo.setFont(new Font(11, "Times New Roman", false));
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

		drb.addColumn(ColumnBuilder.getNew().setColumnProperty("descricao", String.class.getName()).setWidth(500).addConditionalStyles(listCondStyle)
				.build());

		if (tipoBalanco == 0 || tipoBalanco == 2) {
			drb.addColumn(ColumnBuilder.getNew().setColumnProperty("valorContabil", BigDecimal.class.getName())
					.addConditionalStyles(listValueCondStyle).setPattern("#,##0.00").setWidth(100).build());
		}
		if (tipoBalanco == 1 || tipoBalanco == 2) {

			drb.addColumn(ColumnBuilder.getNew().setColumnProperty("valorSocial", BigDecimal.class.getName()).addConditionalStyles(listValueCondStyle)
					.setPattern("#,##0.00").setWidth(100).build());
		}

		drb.addColumn(ColumnBuilder.getNew().setColumnProperty("agrupamento", Integer.class.getName()).setWidth(0).build());
		if (tipoBalanco == 0 || tipoBalanco == 1) {

			drb.addColumn(ColumnBuilder.getNew().setColumnProperty("percentual", BigDecimal.class.getName()).addConditionalStyles(listValueCondStyle)
					.setPattern("#,##0.00").setWidth(100).build());
		}
		drb.setUseFullPageWidth(true);
		drb.addAutoText(AutoText.AUTOTEXT_PAGE_X_SLASH_Y, AutoText.POSITION_FOOTER, AutoText.ALIGNMENT_RIGHT);

		dynamicReport = drb.build();

		return this;
	}

	public FaturamentoBrutoReportBuilder setData(List<FaturamentoBrutoVO> data) {

		this.data = data;

		for (FaturamentoBrutoVO faturamentoItem : this.data) {

			switch (tipoBalanco) {
			case 0:
				faturamentoItem.setPercentual(faturamentoItem.getParent() == null ? BigDecimal.valueOf(100d)
						: faturamentoItem.getValorContabil().divide(faturamentoItem.getParent().getValorContabil(), 4, RoundingMode.HALF_EVEN)
								.multiply(BigDecimal.valueOf(100d)));
				break;
			case 1:
				faturamentoItem.setPercentual(faturamentoItem.getParent() == null ? BigDecimal.valueOf(100d)
						: faturamentoItem.getValorSocial().divide(faturamentoItem.getParent().getValorSocial(), 4, RoundingMode.HALF_EVEN)
								.multiply(BigDecimal.valueOf(100d)));
				break;

			default:
				break;
			}
		}

		return this;
	}

	public void view() throws JRException {

		JRDataSource ds = new JRBeanCollectionDataSource(data);

		JasperPrint jasperPrint = DynamicJasperHelper.generateJasperPrint(dynamicReport, new ClassicLayoutManager(), ds);

		new ReportViewer(jasperPrint).setVisible(Boolean.TRUE);
	}

}
