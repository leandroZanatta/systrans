package br.com.lar.thread;

import static br.com.sysdesc.util.constants.MensagemConstants.MENSAGEM_ATUALIZAR_VERSAO;
import static br.com.sysdesc.util.constants.MensagemConstants.MENSAGEM_LOG_ARQUIVO_DA_VERSAO;
import static br.com.sysdesc.util.constants.MensagemConstants.MENSAGEM_LOG_GERANDO_ARQUIVO;
import static br.com.sysdesc.util.constants.MensagemConstants.MENSAGEM_LOG_VERSAO_NAO_PODE_SER_GERADO;
import static br.com.sysdesc.util.constants.MensagemConstants.MENSAGEM_THREAD_VERSAO_INTEROMPIDA;
import static br.com.sysdesc.util.resources.Configuracoes.FOLDER_VERSOES;
import static br.com.sysdesc.util.resources.Configuracoes.VERSAO;
import static br.com.sysdesc.util.resources.Resources.OPTION_VALIDACAO;
import static br.com.sysdesc.util.resources.Resources.translate;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.gson.Gson;

import br.com.lar.repository.dao.VersaoDAO;
import br.com.lar.repository.model.Versao;
import br.com.sysdesc.util.classes.ExtratorZip;
import br.com.sysdesc.util.resources.Resources;
import br.com.sysdesc.util.vo.VersaoERPVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AtualizacaoThread extends Thread {

	private JPanel contentVersao;

	private JLabel lbVersao;

	private String versaoBase;

	private static final String URLVERSAO = "https://raw.githubusercontent.com/leandroZanatta/Lar/develop/versoes/versao.json";

	private VersaoDAO versaoDAO = new VersaoDAO();

	public AtualizacaoThread(JPanel contentVersao) {
		this.contentVersao = contentVersao;

		lbVersao = new JLabel();
	}

	@Override
	public void run() {

		this.verificarVersaoBase();

		lbVersao.setText(this.versaoBase);

		this.verificarVersaoRemota(this.recuperarVersaoInternet());
	}

	private VersaoERPVO recuperarVersaoInternet() {

		URL arquivoUrl;

		try {

			arquivoUrl = new URL(URLVERSAO);

			log.info("buscando versão da internet url {}", URLVERSAO);

			URLConnection urlConnection = arquivoUrl.openConnection();
			urlConnection.setUseCaches(Boolean.FALSE);

			try (BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {

				String inputLine;

				StringBuilder stringBuilder = new StringBuilder();

				while ((inputLine = in.readLine()) != null) {

					stringBuilder.append(inputLine);
				}

				return new Gson().fromJson(stringBuilder.toString(), VersaoERPVO.class);

			}

		} catch (IOException e) {

			try {
				Thread.sleep(18000);

				return recuperarVersaoInternet();

			} catch (InterruptedException e1) {

				Thread.currentThread().interrupt();

				log.error(Resources.translate(MENSAGEM_THREAD_VERSAO_INTEROMPIDA), e1);

				return null;
			}
		}

	}

	private void verificarVersaoRemota(VersaoERPVO versaoVO) {
		log.info("Verificando versões");

		if (versaoVO != null) {

			try {

				VersaoERPVO versaoLocal = getArquivoVersaoLocal();

				log.info(String.format("Versão base: %s ~ Versão Remota: %s", versaoBase, versaoVO.getVersaoERP()));

				if (!versaoBase.equals(versaoVO.getVersaoERP())) {

					String mensagem = String.format(translate(MENSAGEM_ATUALIZAR_VERSAO), versaoVO.getVersaoERP());

					Integer retornoOpcao = JOptionPane.showConfirmDialog(null, mensagem, OPTION_VALIDACAO,
							JOptionPane.YES_NO_OPTION);

					if (retornoOpcao == JOptionPane.YES_OPTION) {

						efetuarDownloadVersao(versaoVO.getArquivoERP());
						efetuarDownloadVersao(versaoVO.getArquivoAtualizacao());

						File arquivoAtualizacao = criarArquivoVersao(versaoVO.getArquivoAtualizacao());

						new ExtratorZip().extrairVersao(arquivoAtualizacao);

						log.info("Excluindo arquivo .rar de Atualização");

						Files.delete(Paths.get(arquivoAtualizacao.toURI()));

						versaoLocal.setVersaoERP(versaoVO.getVersaoERP());
						versaoLocal.setArquivoERP(versaoVO.getArquivoERP());
					}
				}

				FileUtils.writeStringToFile(new File(VERSAO), new Gson().toJson(versaoLocal), Charset.defaultCharset());

			} catch (Exception e) {

				try {
					log.warn("Não foi possivel conectar ao servidor esperando 180 segundos para tentar novamente");

					Thread.sleep(18000);

					verificarVersaoRemota(versaoVO);

				} catch (InterruptedException e1) {

					log.error(Resources.translate(MENSAGEM_THREAD_VERSAO_INTEROMPIDA), e);

					Thread.currentThread().interrupt();
				}
			}
		}
	}

	private VersaoERPVO getArquivoVersaoLocal() {
		try {

			return new Gson().fromJson(FileUtils.readFileToString(new File(VERSAO), Charset.defaultCharset()),
					VersaoERPVO.class);
		} catch (Exception e) {

			return new VersaoERPVO();
		}

	}

	private void efetuarDownloadVersao(String urlVersao) throws IOException {
		log.info("Efetuando download da versão {}", urlVersao);

		JProgressBar progres = new JProgressBar();

		progres.setStringPainted(true);

		contentVersao.removeAll();

		contentVersao.add(progres);

		URL arquivoUrl = new URL(urlVersao);

		URLConnection urlConnection = arquivoUrl.openConnection();

		urlConnection.setUseCaches(Boolean.FALSE);

		Long tamanhoArquivo = urlConnection.getContentLengthLong();

		log.info("Tamanho do Arquivo {} bytes", tamanhoArquivo);

		File arquivoVersao = criarArquivoVersao(urlVersao);

		try (BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
				FileOutputStream fileOutputStream = new FileOutputStream(arquivoVersao)) {

			byte[] dataBuffer = new byte[1024];

			int bytesRead;

			Long bufferTotal = 0L;

			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {

				bufferTotal += bytesRead;

				fileOutputStream.write(dataBuffer, 0, bytesRead);

				Double percentual = bufferTotal.doubleValue() / tamanhoArquivo.doubleValue() * 100;

				progres.setValue(percentual.intValue());

			}
		}

		contentVersao.removeAll();

		contentVersao.add(lbVersao);
	}

	private File criarArquivoVersao(String arquivo) throws IOException {

		File folderVersao = new File(FOLDER_VERSOES);

		if (!folderVersao.exists()) {
			log.info(Resources.translate(MENSAGEM_LOG_GERANDO_ARQUIVO) + folderVersao.getName());
			folderVersao.mkdir();
		}

		File arquivoVersao = new File(folderVersao, FilenameUtils.getName(new URL(arquivo).getPath()));

		if (!arquivoVersao.exists()) {
			log.info(Resources.translate(MENSAGEM_LOG_ARQUIVO_DA_VERSAO) + arquivoVersao.getName());

			if (!arquivoVersao.createNewFile()) {
				log.error(Resources.translate(MENSAGEM_LOG_VERSAO_NAO_PODE_SER_GERADO) + arquivoVersao.getName());
			}
		}

		return arquivoVersao;
	}

	private void verificarVersaoBase() {

		log.info("Verificando versão da Base");

		Versao versao = versaoDAO.last();

		contentVersao.add(lbVersao);

		if (versao != null) {

			this.versaoBase = versao.getNumeroVersao();

			return;
		}

		this.versaoBase = "0.0.0";
	}

}
