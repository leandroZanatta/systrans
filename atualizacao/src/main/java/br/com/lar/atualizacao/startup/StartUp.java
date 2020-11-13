package br.com.lar.atualizacao.startup;

import static br.com.sysdesc.util.resources.Resources.APPLICATION_JAR;
import static br.com.sysdesc.util.resources.Resources.translate;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import br.com.lar.atualizacao.versao.ExtratorZip;
import br.com.lar.atualizacao.versao.VersaoBancoDados;
import br.com.lar.atualizacao.versao.VersaoInternet;
import br.com.sysdesc.util.resources.Configuracoes;
import br.com.sysdesc.util.vo.VersaoERPVO;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

@Slf4j
public class StartUp extends JDialog {

	private static final long serialVersionUID = 1L;

	private final VersaoBancoDados versaoBancoDados = new VersaoBancoDados();

	private final VersaoInternet versaoInternet = new VersaoInternet();

	private final ExtratorZip extratorZip = new ExtratorZip();

	private final JProgressBar progressBar;

	public StartUp() {

		setUndecorated(true);
		setSize(400, 38);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[24.00]"));

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		getContentPane().add(progressBar, "cell 0 0,growx");

		new Thread(this::starApplication).start();
	}

	public static void main(String[] args) {

		StartUp startUp = new StartUp();

		startUp.setVisible(true);
	}

	private void starApplication() {

		File arquivoVersao = new File(Configuracoes.VERSAO);

		escolherAtualizacao(arquivoVersao);

		iniciarAplicacao();

		System.exit(0);
	}

	private void escolherAtualizacao(File arquivoVersao) {

		try {

			nextStep("Verificando versões remotas", 10);

			VersaoERPVO versaoVO = versaoInternet.obterVersaoVO(arquivoVersao);

			nextStep("Verificando versões do banco de dados", 20);

			String versaoBanco = versaoBancoDados.buscarVersaoBanco();

			if (!versaoBanco.equals(versaoVO.getVersaoERP())) {

				nextStep("Obtendo versão da internet", 50);

				File arquivoZip = versaoInternet.obterArquivoVersaoZip(versaoVO);

				nextStep("Extraindo versão", 70);

				extratorZip.extrairVersao(arquivoZip);

				nextStep("Atualizando Base de dados", 90);

				versaoBancoDados.upgradeDatabase(versaoVO.getVersaoERP());

				Files.deleteIfExists(arquivoZip.toPath());
			}
		} catch (IOException e) {

			JOptionPane.showMessageDialog(this, "Erro ao buscar vesões do sitema, causa:" + e.getCause());
		}
	}

	private void nextStep(String mensagem, int percentual) {
		progressBar.setString(mensagem);
		progressBar.setValue(percentual);
	}

	private void iniciarAplicacao() {

		try {

			nextStep("Iniciando a aplicação", 100);

			Desktop.getDesktop().open(new File(translate(APPLICATION_JAR, "interface.jar")));

		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, "ERRO AO INICIALIZAR APLICAÇÃO");

			log.error("ERRO AO INICIALIZAR APLICAÇÃO", e);
		}
	}

}
