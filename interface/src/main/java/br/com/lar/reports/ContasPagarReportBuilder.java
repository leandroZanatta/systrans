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
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import br.com.lar.reports.models.ContasPagarReport;
import br.com.lar.repository.model.ContasPagar;
import br.com.sysdesc.pesquisa.ui.components.ReportViewer;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ContasPagarReportBuilder {

	private Integer margin = 20;
	private List<ContasPagarReport> data = new ArrayList<>();
	private DynamicReport dynamicReport;

	public ContasPagarReportBuilder build(String title) {

		DynamicReportBuilder drb = new DynamicReportBuilder();

		Style titleStyle = new Style("titleStyle");
		titleStyle.setFont(new Font(18, Font._FONT_VERDANA, true));
		titleStyle.setHorizontalAlign(HorizontalAlign.CENTER);

		Style valueStyle = new Style("valueStyle");
		valueStyle.setHorizontalAlign(HorizontalAlign.RIGHT);

		Style totalStyle = new Style("totalStyle");
		totalStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
		totalStyle.setFont(Font.ARIAL_MEDIUM_BOLD);

		AbstractColumn valorLiquido = ColumnBuilder.getNew().setColumnProperty("valorLiquido", BigDecimal.class.getName()).setTitle("Valor Líquido")
				.setPattern("#,##0.00").setWidth(95).setHeaderStyle(valueStyle).setStyle(valueStyle).build();

		AbstractColumn valorBruto = ColumnBuilder.getNew().setColumnProperty("valorTotal", BigDecimal.class.getName()).setTitle("Valor Bruto")
				.setPattern("#,##0.00")
				.setWidth(85).setHeaderStyle(valueStyle).setStyle(valueStyle).build();

		AbstractColumn valorDesconto = ColumnBuilder.getNew().setColumnProperty("valorDesconto", BigDecimal.class.getName()).setTitle("Desconto")
				.setPattern("#,##0.00")
				.setWidth(85).setHeaderStyle(valueStyle).setStyle(valueStyle).build();

		AbstractColumn valorAcrescimo = ColumnBuilder.getNew().setColumnProperty("valorAcrescimo", BigDecimal.class.getName()).setTitle("Acréscimo")
				.setPattern("#,##0.00")
				.setWidth(85).setHeaderStyle(valueStyle).setStyle(valueStyle).build();

		AbstractColumn valorPago = ColumnBuilder.getNew().setColumnProperty("valorPago", BigDecimal.class.getName()).setTitle("Pago")
				.setPattern("#,##0.00")
				.setWidth(85).setHeaderStyle(valueStyle).setStyle(valueStyle).build();

		drb.setTitle(title).setTitleStyle(titleStyle).setDetailHeight(15).setLeftMargin(margin).setRightMargin(margin).setTopMargin(margin)
				.setBottomMargin(margin)
				.setPrintBackgroundOnOddRows(false).setGrandTotalLegendStyle(totalStyle).setGrandTotalLegend("Totais");
		drb.addColumn(ColumnBuilder.getNew().setColumnProperty("codigoConta", Long.class.getName()).setTitle("Código:").setWidth(90).build());
		drb.addColumn(ColumnBuilder.getNew().setColumnProperty("cliente", String.class.getName()).setTitle("Cliente").setWidth(185).build());
		drb.addColumn(ColumnBuilder.getNew().setColumnProperty("formaPagamento", String.class.getName()).setTitle("Forma de Pagamento").setWidth(185)
				.build());
		drb.addColumn(ColumnBuilder.getNew().setColumnProperty("baixado", String.class.getName()).setTitle("Baixado").setWidth(55).build());

		drb.addColumn(ColumnBuilder.getNew().setColumnProperty("dataVencimento", Date.class.getName()).setTitle("Vencimento").setPattern("dd/MM/yyyy")
				.setWidth(85).build());

		drb.addColumn(valorBruto);
		drb.addColumn(valorDesconto);
		drb.addColumn(valorAcrescimo);
		drb.addColumn(valorPago);
		drb.addColumn(valorLiquido);

		drb.setUseFullPageWidth(true);
		drb.addAutoText(AutoText.AUTOTEXT_PAGE_X_SLASH_Y, AutoText.POSITION_FOOTER, AutoText.ALIGNMENT_RIGHT);

		drb.setPageSizeAndOrientation(Page.Page_A4_Landscape());
		drb.addGlobalFooterVariable(valorBruto, DJCalculation.SUM, totalStyle);
		drb.addGlobalFooterVariable(valorDesconto, DJCalculation.SUM, totalStyle);
		drb.addGlobalFooterVariable(valorAcrescimo, DJCalculation.SUM, totalStyle);
		drb.addGlobalFooterVariable(valorPago, DJCalculation.SUM, totalStyle);
		drb.addGlobalFooterVariable(valorLiquido, DJCalculation.SUM, totalStyle);

		dynamicReport = drb.build();

		return this;
	}

	public ContasPagarReportBuilder setData(List<ContasPagar> data) {

		this.data = data.stream().map(this::mapearData).collect(Collectors.toList());

		this.data.sort(Comparator.comparing(ContasPagarReport::getDataVencimento));

		return this;
	}

	public void view() throws JRException {

		JRDataSource ds = new JRBeanCollectionDataSource(data);

		JasperPrint jasperPrint = DynamicJasperHelper.generateJasperPrint(dynamicReport, new ClassicLayoutManager(), ds);

		new ReportViewer(jasperPrint).setVisible(Boolean.TRUE);
	}

	private ContasPagarReport mapearData(ContasPagar contasPagar) {

		ContasPagarReport contasPagarReport = new ContasPagarReport();

		BigDecimal valorLiquido = contasPagar.getValorParcela().add(contasPagar.getValorAcrescimo()).subtract(contasPagar.getValorDesconto())
				.subtract(contasPagar.getValorPago());

		contasPagarReport.setCodigoConta(contasPagar.getIdContasPagar());
		contasPagarReport.setCliente(contasPagar.getCliente().getNome());
		contasPagarReport.setFormaPagamento(contasPagar.getFormasPagamento().getDescricao());
		contasPagarReport.setDataVencimento(contasPagar.getDataVencimento());
		contasPagarReport.setBaixado(contasPagar.isBaixado() ? "Sim" : "Não");
		contasPagarReport.setValorTotal(contasPagar.getValorParcela());
		contasPagarReport.setValorAcrescimo(contasPagar.getValorAcrescimo());
		contasPagarReport.setValorDesconto(contasPagar.getValorDesconto().negate());
		contasPagarReport.setValorPago(contasPagar.getValorPago().negate());
		contasPagarReport.setValorLiquido(valorLiquido);

		return contasPagarReport;
	}

}
