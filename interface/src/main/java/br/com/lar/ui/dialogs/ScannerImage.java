package br.com.lar.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import br.com.sysdesc.util.classes.ImageUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScannerImage extends JDialog {

	private static final long serialVersionUID = 1L;

	private File scannedFile;
	private ImagePanel imagePanel;
	private Boolean created = Boolean.FALSE;
	private byte[] pdfBytes;
	private JButton salvar;
	private int width;
	private int height;

	public ScannerImage() {
		initComponents();
	}

	private void initComponents() {
		setModal(true);
		imagePanel = new ImagePanel();
		JButton capturar = new JButton("Selecionar Arquivo");
		salvar = new JButton("Salvar");
		salvar.setEnabled(false);

		capturar.addActionListener(e -> abrirSeletorArquivos());
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

	private void abrirSeletorArquivos() {

		try {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.addChoosableFileFilter(new ImageFilter());
			fileChooser.setAcceptAllFileFilterUsed(false);

			int option = fileChooser.showOpenDialog(this);
			if (option == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();

				BufferedImage image = ImageIO.read(file);

				this.width = image.getWidth();
				this.height = image.getHeight();

				imagePanel.setScannedImage(image);

				this.scannedFile = file;

				salvar.setEnabled(true);
			}
		} catch (IOException e) {

			JOptionPane.showMessageDialog(this, "Não foi possível criar a imagem");
		}
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

			Dimension dimension = ImageUtil.scaleImage(this.width, this.height, 595, 842);

			image.scaleToFit((float) dimension.getWidth(), (float) dimension.getHeight());

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

class ImageFilter extends FileFilter {
	public static final String JPEG = "jpeg";
	public static final String JPG = "jpg";
	public static final String GIF = "gif";
	public static final String TIFF = "tiff";
	public static final String TIF = "tif";
	public static final String PNG = "png";

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = getExtension(f);
		if (extension != null) {
			if (extension.equals(TIFF) ||
					extension.equals(TIF) ||
					extension.equals(GIF) ||
					extension.equals(JPEG) ||
					extension.equals(JPG) ||
					extension.equals(PNG)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Image Only";
	}

	String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}
}

class ImagePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Image scannedImage;

	public void setScannedImage(BufferedImage scannedImage) {

		this.scannedImage = scannedImage;

		if (scannedImage != null) {

			Dimension dimension = ImageUtil.scaleImage(scannedImage.getWidth(), scannedImage.getHeight(), 400, 500);

			this.scannedImage = scannedImage.getScaledInstance((int) dimension.getWidth(), (int) dimension.getHeight(), 0);
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