package br.com.lar.atualizacao.ui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

@Slf4j
public class FrmDownloader extends JDialog {

	private static final long serialVersionUID = 1L;

	private JProgressBar progressBar;
	private String url;
	private File fileOut;
	private boolean sucesso;

	public FrmDownloader(String url, File fileOut) {
		this.url = url;
		this.fileOut = fileOut;

		initComponents();

		iniciarDownload();
	}

	private void iniciarDownload() {

		new Thread(() -> {

			URL arquivoUrl;

			try {

				log.info("fazendo download do arquivo: " + this.url);

				arquivoUrl = new URL(this.url);

				HttpURLConnection urlConnection = (HttpURLConnection) arquivoUrl.openConnection();
				urlConnection.setUseCaches(Boolean.FALSE);
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setReadTimeout(10000);
				urlConnection.setConnectTimeout(15000);
				urlConnection.setRequestMethod("GET");

				Long tamanhoArquivo = urlConnection.getContentLengthLong();

				log.info("Tamanho do arquivo: " + tamanhoArquivo);

				if (tamanhoArquivo > 0L) {

					boolean arquivoCriado = false;

					if (!fileOut.exists() && fileOut.createNewFile()) {

						arquivoCriado = true;

						log.info("Arquivo {} criado com sucesso!", fileOut.getAbsolutePath());
					}

					this.downoadArquivo(urlConnection, tamanhoArquivo, arquivoCriado);

					dispose();
				}

				dispose();
			} catch (Exception e) {

				log.error("OCORREU UM ERRO AO EFETUAR O DOWNLOAD", e);

				JOptionPane.showMessageDialog(this, "OCORREU UM ERRO AO EFETUAR O DOWNLOAD:\n" + e.getMessage());

				dispose();
			}

		}).start();
	}

	private void downoadArquivo(HttpURLConnection urlConnection, Long tamanhoArquivo, boolean arquivoCriado) throws IOException {

		try (BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
				FileOutputStream fileOutputStream = new FileOutputStream(this.fileOut)) {

			byte[] dataBuffer = new byte[1024];

			int bytesRead;

			Long bufferTotal = 0L;

			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {

				bufferTotal += bytesRead;

				fileOutputStream.write(dataBuffer, 0, bytesRead);

				Double valorProgresso = bufferTotal.doubleValue() / tamanhoArquivo.doubleValue() * 100;

				log.info("progresso: " + valorProgresso);

				progressBar.setValue(valorProgresso.intValue());

			}

			sucesso = Boolean.TRUE;
		} catch (IOException e) {

			if (arquivoCriado && Files.deleteIfExists(fileOut.toPath())) {

				log.info("O arquivo {} foi excluido devido a erros ocorridos no processo", fileOut, e);
			}

			log.error("OCORREU UM ERRO AO EFETUAR O DOWNLOAD", e);

			JOptionPane.showMessageDialog(this,
					"OCORREU UM ERRO AO EFETUAR O DOWNLOAD:\n" + e.getMessage());
		}
	}

	private void initComponents() {
		setModal(Boolean.TRUE);
		setUndecorated(Boolean.TRUE);
		setSize(500, 50);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[grow][27.00][grow]"));

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setString(this.url);
		getContentPane().add(progressBar, "cell 0 1,grow");
	}

	public boolean isSucesso() {
		return sucesso;
	}

}
