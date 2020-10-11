package br.com.lar.upgrade.service;

import java.io.File;
import java.nio.charset.Charset;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import br.com.lar.upgrade.vo.VersaoServidorVO;

public class GerarVersaoServidor {

	private static final String URL_VERSOES = "https://github.com/leandroZanatta/Lar/raw/develop/versoes/";

	private String versaoServidor;

	private File pathDir = new File(System.getProperty("user.dir")).getParentFile();
	private VersaoService versaoService = new VersaoService();
	private ZipService zipService = new ZipService();

	public GerarVersaoServidor(String versaoERP) {
		this.versaoServidor = versaoERP;
	}

	public String build() {

		try {

			File arquivoJson = new File(pathDir, "versoes\\versaoservidor.json");

			File pastaProjeto = new File(pathDir, "lar-server");

			File target = new File(pastaProjeto, "target");

			String versao = FileUtils.readFileToString(arquivoJson, Charset.defaultCharset());
			VersaoServidorVO versaoVO = new Gson().fromJson(versao, VersaoServidorVO.class);
			String novaVersao = versaoService.adicionarVersao(this.versaoServidor);

			versaoService.changeMavenVersion(pastaProjeto, novaVersao);

			zipService.createZip(pathDir, "versoes\\" + novaVersao + "-server.zip", new File(target, "lar-server.jar"));

			versaoVO.setArquivo(URL_VERSOES + novaVersao + "-server.zip");
			versaoVO.setVersao(novaVersao);

			FileUtils.writeStringToFile(arquivoJson, new Gson().toJson(versaoVO), Charset.defaultCharset());

			return novaVersao;

		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, "Erro ao gerar versao:" + e.getMessage());
		}

		return this.versaoServidor;
	}

}
