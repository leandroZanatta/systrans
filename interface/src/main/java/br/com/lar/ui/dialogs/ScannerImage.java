package br.com.lar.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;
import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerListener;

@Slf4j
public class ScannerImage extends JDialog implements ScannerListener {

	private static final long serialVersionUID = 1L;

	private Scanner scanner;
	private File scannedFile;
	private ImagePanel imagePanel;
	private Boolean created = Boolean.FALSE;
	private byte[] pdfBytes;
	private JButton salvar;

	public ScannerImage() {
		initComponents();
		scanner = Scanner.getDevice();
		scanner.addListener(this);
		setVisible(true);
	}

	private void initComponents() {
		setModal(true);
		imagePanel = new ImagePanel();
		JButton capturar = new JButton("Capturar Imagem");
		salvar = new JButton("Salvar");
		salvar.setEnabled(false);

		capturar.addActionListener(e -> {
			try {
				scanner.acquire();
			} catch (ScannerIOException ex) {
				log.error("Erro ao abrir scanner:", ex);
			}
		});

		salvar.addActionListener(e -> salvarPDF());
		setSize(410, 510);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(capturar, BorderLayout.NORTH);
		getContentPane().add(salvar, BorderLayout.SOUTH);
		getContentPane().add(imagePanel, BorderLayout.CENTER);
		setTitle("Hello Scanner!");
	}

	private void salvarPDF() {
		try {

			log.info("Criando PDF do arquivo:" + scannedFile.getAbsolutePath());

			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4, 20, 20, 20, 20);

			PdfWriter.getInstance(document, bo);

			document.open();

			log.info("Buscando Imagem da URL:" + scannedFile.toURI().toURL());

			com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(scannedFile.toURI().toURL());

			log.info("Imagem Criada:");

			document.add(image);
			document.close();

			log.info("Lendo Bytes do pdf");

			pdfBytes = bo.toByteArray();

			created = Boolean.TRUE;

			log.info("Conversão gerada com sucesso");

			dispose();
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(ScannerIOMetadata.Type type, ScannerIOMetadata siom) {

		if (type.equals(ScannerIOMetadata.EXCEPTION)) {

			log.error("Tipo de scanneamento Exception:", siom.getException());

			salvar.setEnabled(false);
		}

		if (type.equals(ScannerIOMetadata.ACQUIRED)) {
			log.info("Criado arquivo:" + siom.getFile().getAbsolutePath());

			salvar.setEnabled(siom.getFile().exists());

			scannedFile = siom.getFile();
			BufferedImage bufferedImage = siom.getImage();

			imagePanel.setScannedImage(bufferedImage);
		}
	}

	public static void main(String[] args) {
		new ScannerImage();
	}

	public boolean created() {

		return this.created;
	}

	public byte[] getPdfBytes() {

		return this.pdfBytes;
	}
}

class ImagePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Image scannedImage;

	public void setScannedImage(Image scannedImage) {
		this.scannedImage = scannedImage;
		if (scannedImage != null) {
			this.scannedImage = scannedImage.getScaledInstance(400, 500, 0);
		}
		super.repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (scannedImage != null) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.drawImage(scannedImage, 0, 0, null);
			g2d.dispose();
		}
	}

	public Image getScannedImage() {
		return scannedImage;
	}
}