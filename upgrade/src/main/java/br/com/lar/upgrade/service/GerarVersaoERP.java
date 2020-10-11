package br.com.lar.upgrade.service;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import br.com.sysdesc.util.vo.VersaoERPVO;

public class GerarVersaoERP {

	private static final String URL_VERSOES = "https://github.com/leandroZanatta/Lar/raw/develop/versoes/";

	private String versaoERP;

	private File pathDir = new File(System.getProperty("user.dir")).getParentFile();
	private VersaoService versaoService = new VersaoService();
	private ZipService zipService = new ZipService();

	public GerarVersaoERP(String versaoERP) {
		this.versaoERP = versaoERP;
	}

	public String build() {

		try {

			File arquivoJson = new File(pathDir, "versoes\\versao.json");
			File target = new File(pathDir, "interface\\target");
			File upgrade = new File(pathDir, "upgrade\\upgrade");
			File resources = new File(target, "resources");
			File interfaceJar = new File(target, "interface.jar");
			File interfaceJarDependencies = new File(target, "interface-jar-with-dependencies.jar");
			File atualizacaoJarDepencencies = new File(pathDir,
					"atualizacao\\target\\atualizacao-jar-with-dependencies.jar");

			String versao = FileUtils.readFileToString(arquivoJson, Charset.defaultCharset());
			VersaoERPVO versaoVO = new Gson().fromJson(versao, VersaoERPVO.class);
			String novaVersao = versaoService.adicionarVersao(this.versaoERP);

			File atualizacao = new File(pathDir, "atualizacao\\target\\lar.jar");

			versaoService.changeMavenVersion(pathDir, novaVersao);

			Files.deleteIfExists(interfaceJar.toPath());

			interfaceJarDependencies.renameTo(interfaceJar);

			atualizacaoJarDepencencies.renameTo(atualizacao);

			zipService.createZip(pathDir, "versoes\\" + novaVersao + "-erp.zip", resources, interfaceJar, upgrade);
			zipService.createZip(pathDir, "versoes\\" + novaVersao + "-atualizacao.zip", atualizacao);

			versaoVO.setArquivoERP(URL_VERSOES + novaVersao + "-erp.zip");
			versaoVO.setArquivoAtualizacao(URL_VERSOES + novaVersao + "-atualizacao.zip");
			versaoVO.setVersaoERP(novaVersao);

			FileUtils.writeStringToFile(arquivoJson, new Gson().toJson(versaoVO), Charset.defaultCharset());

			return novaVersao;

		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, "Erro ao gerar versao:" + e.getMessage());
		}

		return this.versaoERP;
	}
}
