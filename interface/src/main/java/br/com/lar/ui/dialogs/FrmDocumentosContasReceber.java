package br.com.lar.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import br.com.lar.repository.model.DocumentoEntrada;
import br.com.lar.repository.model.FaturamentoEntradasCabecalho;
import br.com.lar.service.faturamento.DocumentoEntradaService;
import br.com.lar.tablemodels.DocumentosTableModel;
import br.com.sysdesc.components.ButtonColumn;
import br.com.sysdesc.util.resources.Configuracoes;
import net.miginfocom.swing.MigLayout;

public class FrmDocumentosContasReceber extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private final DocumentoEntradaService documentoEntradaService = new DocumentoEntradaService();
	private DocumentosTableModel documentosTableModel = new DocumentosTableModel();
	private final FaturamentoEntradasCabecalho faturamentoEntrada;
	private JPanel panelAcoes;
	private JButton btCancelar;
	private JScrollPane scrollPane;
	private JTable table;
	private JButton btAdicionar;
	private JButton btRemover;

	public FrmDocumentosContasReceber(FaturamentoEntradasCabecalho faturamentoEntrada) {
		this.faturamentoEntrada = faturamentoEntrada;

		documentosTableModel.setRows(faturamentoEntrada.getDocumentoEntradas());

		setTitle("FATURAMENTO - ENTRADA - DOCUMENTOS");
		setSize(789, 250);
		setModal(true);
		setLocationRelativeTo(null);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		contentPanel.setLayout(new MigLayout("", "[grow]", "[grow][grow][]"));
		panelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btCancelar = new JButton("Fechar");

		btCancelar.addActionListener(e -> dispose());
		table = new JTable(documentosTableModel);
		scrollPane = new JScrollPane(table);

		contentPanel.add(scrollPane, "cell 0 0,grow");
		contentPanel.add(panelAcoes, "cell 0 1,grow");

		btAdicionar = new JButton("+");
		btAdicionar.setBackground(Color.GREEN);
		btAdicionar.addActionListener(e -> abrirScanner());
		panelAcoes.add(btAdicionar);

		btRemover = new JButton("-");
		btRemover.addActionListener(e -> excluirArquivo());
		btRemover.setBackground(Color.RED);
		panelAcoes.add(btRemover);
		panelAcoes.add(btCancelar);

		ButtonColumn buttonColumn = new ButtonColumn(table, 2, "...");
		buttonColumn.addButtonListener(column -> {
			try {
				Desktop.getDesktop().open(new File(documentosTableModel.getRow(column).getLocal()));
			} catch (IOException e1) {

				JOptionPane.showMessageDialog(this, "Ocorreu um erro ao tentar abrir o arquivo");
			}
		});

		table.getColumnModel().getColumn(0).setPreferredWidth(80);
		table.getColumnModel().getColumn(1).setPreferredWidth(600);
		table.getColumnModel().getColumn(2).setPreferredWidth(60);
	}

	private void excluirArquivo() {

		if (table.getSelectedRowCount() == 1) {

			if (JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o arquivo selecionado", "Veriricação",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

				DocumentoEntrada documentoEntrada = documentosTableModel.getRow(table.getSelectedRow());

				documentoEntradaService.excluir(documentoEntrada);
				documentosTableModel.remove(table.getSelectedRow());
				faturamentoEntrada.getDocumentoEntradas().remove(documentoEntrada);

				try {
					Files.delete(new File(documentoEntrada.getLocal()).toPath());
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, "Erro ao excluir documento. Causa:" + e.getCause());
				}

			}
		} else {
			JOptionPane.showMessageDialog(null, "Pecisa Selecionar um registro para excluir");
		}

	}

	private void abrirScanner() {

		ScannerImage scannerImageTest = new ScannerImage();
		scannerImageTest.setVisible(true);

		if (scannerImageTest.created()) {
			salvarScannerPDF(scannerImageTest.getPdfBytes());
		}

	}

	private void salvarScannerPDF(byte[] pdfbytes) {

		try {
			Calendar calendar = Calendar.getInstance();
			Long codigoHistorico = faturamentoEntrada.getHistorico().getIdHistorico();

			File arquivo = createDirs("documentos", codigoHistorico.toString(), String.valueOf(calendar.get(Calendar.YEAR)),
					String.valueOf(calendar.get(Calendar.MONTH)));

			File arquivoEscanear = new File(arquivo, new Date().getTime() + ".pdf");

			try (FileOutputStream fileOutputStream = new FileOutputStream(arquivoEscanear)) {

				fileOutputStream.write(pdfbytes);
			}

			DocumentoEntrada documentoEntrada = new DocumentoEntrada();
			documentoEntrada.setFaturamentoEntrada(faturamentoEntrada);
			documentoEntrada.setLocal(arquivoEscanear.getAbsolutePath());

			documentoEntradaService.salvar(documentoEntrada);
			documentosTableModel.addRow(documentoEntrada);
			faturamentoEntrada.getDocumentoEntradas().add(documentoEntrada);

		} catch (IOException e) {

			JOptionPane.showMessageDialog(this, "Erro ao criar pdf do arquivo");
		}
	}

	private File createDirs(String... diretorios) {

		File actualDir = new File(Configuracoes.USER_DIR);

		for (String nomeDiretorio : diretorios) {

			actualDir = createDir(actualDir, nomeDiretorio);
		}

		return actualDir;
	}

	private File createDir(File actualDir, String dir) {

		File newDir = new File(actualDir, dir);

		if (!newDir.exists()) {
			newDir.mkdirs();
		}

		return newDir;
	}
}
