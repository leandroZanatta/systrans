package br.com.lar.atualizacao.startup;

import static br.com.sysdesc.util.resources.Resources.APPLICATION_JAR;
import static br.com.sysdesc.util.resources.Resources.translate;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JOptionPane;

import br.com.lar.atualizacao.versao.ExtratorZip;
import br.com.lar.atualizacao.versao.VersaoBancoDados;
import br.com.lar.atualizacao.versao.VersaoInternet;
import br.com.sysdesc.util.resources.Configuracoes;
import br.com.sysdesc.util.vo.VersaoERPVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StartUp {

	private final VersaoBancoDados versaoBancoDados = new VersaoBancoDados();

	private final VersaoInternet versaoInternet = new VersaoInternet();

	private final ExtratorZip extratorZip = new ExtratorZip();

	public static void main(String[] args) throws Exception {

		StartUp startUp = new StartUp();

		File arquivoVersao = new File(Configuracoes.VERSAO);

		startUp.escolherAtualizacao(arquivoVersao);

		startUp.iniciarAplicacao();

		System.exit(0);

	}

	private void escolherAtualizacao(File arquivoVersao) throws IOException {

		VersaoERPVO versaoVO = versaoInternet.obterVersaoVO(arquivoVersao);

		String versaoBanco = versaoBancoDados.buscarVersaoBanco();

		if (!versaoBanco.equals(versaoVO.getVersaoERP())) {

			File arquivoZip = versaoInternet.obterArquivoVersaoZip(versaoVO);

			extratorZip.extrairVersao(arquivoZip);

			versaoBancoDados.upgradeDatabase(versaoVO.getVersaoERP());

			Files.deleteIfExists(arquivoZip.toPath());
		}

	}

	private void iniciarAplicacao() {

		try {

			Desktop.getDesktop().open(new File(translate(APPLICATION_JAR, "interface.jar")));

		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, "ERRO AO INICIALIZAR APLICAÇÃO");

			log.error("ERRO AO INICIALIZAR APLICAÇÃO", e);
		}
	}

}
