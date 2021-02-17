package br.com.lar.ui.relatorios;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.toedter.calendar.JDateChooser;

import br.com.lar.reports.DiarioReportBuilder;
import br.com.lar.repository.projection.DiarioReportProjection;
import br.com.lar.service.diario.DiarioService;
import br.com.sysdesc.components.AbstractInternalFrame;
import br.com.sysdesc.util.classes.DateUtil;
import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JRException;

public class FrmRelatorioDiario extends AbstractInternalFrame {

	private static final long serialVersionUID = 1L;

	private JPanel painelContent;
	private JDateChooser txDataInicial;
	private JDateChooser txDataFinal;
	private DiarioService diarioService = new DiarioService();

	public FrmRelatorioDiario(Long permissaoPrograma, Long codigoUsuario) {
		super(permissaoPrograma, codigoUsuario);

		initComponents();
	}

	private void initComponents() {

		setSize(300, 86);
		setClosable(Boolean.TRUE);
		setTitle("Relatório Diário");

		painelContent = new JPanel();

		painelContent.setLayout(new MigLayout("", "[grow][grow][]", "[][]"));

		getContentPane().add(painelContent);

		JLabel lblDataInicial = new JLabel("Data Inicial:");
		JLabel lblDataFinal = new JLabel("Data Final:");
		txDataInicial = new JDateChooser("dd/MM/yyyy", "##/##/####", '_');
		txDataFinal = new JDateChooser("dd/MM/yyyy", "##/##/####", '_');
		JButton btnVisualizar = new JButton("Visualizar");

		Calendar calendar = Calendar.getInstance();
		txDataFinal.setDate(calendar.getTime());
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		txDataInicial.setDate(calendar.getTime());

		btnVisualizar.addActionListener(e -> gerarRelatorio());
		painelContent.add(lblDataInicial, "cell 0 0");
		painelContent.add(lblDataFinal, "cell 1 0");
		painelContent.add(txDataInicial, "cell 0 1,growx");
		painelContent.add(txDataFinal, "cell 1 1,growx");
		painelContent.add(btnVisualizar, "cell 2 1");

	}

	private void gerarRelatorio() {

		try {
			List<DiarioReportProjection> diarioReports = diarioService.buscarDiarioPeriodo(txDataInicial.getDate(), txDataFinal.getDate());

			new DiarioReportBuilder().build("HISTÓRICO DIÁRIO", montarSubTitulo()).setData(diarioReports).view();

		} catch (JRException e) {
			JOptionPane.showMessageDialog(this, "Ocorreu um erro ao Gerar relatório diário");
		}
	}

	private List<String> montarSubTitulo() {

		List<String> subtitulo = new ArrayList<>();

		if (txDataInicial.getDate() != null || txDataFinal.getDate() != null) {

			StringBuilder stringBuilder = new StringBuilder("Data de movimento: ");

			if (txDataFinal.getDate() != null && txDataInicial.getDate() != null) {

				stringBuilder.append("De: ").append(DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, txDataInicial.getDate()))
						.append(" Até: ").append(DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, txDataFinal.getDate()));
			} else if (txDataInicial.getDate() != null) {

				stringBuilder.append("A partir De: ")
						.append(DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, txDataInicial.getDate()));

			} else {
				stringBuilder.append("Até: ")
						.append(DateUtil.format(DateUtil.FORMATO_DD_MM_YYY, txDataFinal.getDate()));

			}

			subtitulo.add(stringBuilder.toString());
		}

		return subtitulo;
	}
}
