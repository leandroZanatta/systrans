package br.com.lar.upgrade.ui;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import br.com.lar.upgrade.changelog.core.Changelog;
import br.com.lar.upgrade.changelog.core.Conexao;
import br.com.lar.upgrade.service.GerarVersaoERP;
import br.com.lar.upgrade.util.classes.LookAndFeelUtil;
import br.com.lar.upgrade.util.classes.StringUtil;
import br.com.lar.upgrade.util.resources.Configuracoes;
import br.com.lar.upgrade.vo.VersaoERPVO;

public class FrmPrincipal extends JFrame {

	private static final long serialVersionUID = 1L;

	private JLabel lbVersaoERP;
	private JLabel lbConfiguracao;

	private JTextField txConfiguracao;
	private JTextField txVersaoErp;

	private JButton btAtualizarBt;
	private JButton btGerarVersao;
	private JButton btUpgade;

	public FrmPrincipal() {

		setTitle("Ações Projeto");
		getContentPane().setLayout(null);
		setSize(444, 274);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		lbConfiguracao = new JLabel("Arquivo de Configuração:");
		lbVersaoERP = new JLabel("Versão ERP:");

		txConfiguracao = new JTextField();
		txVersaoErp = new JTextField();

		btGerarVersao = new JButton("Gerar ERP");
		btAtualizarBt = new JButton("Atualizar DB");
		btUpgade = new JButton("Upgade");

		btAtualizarBt.addActionListener(e -> new FrmConexao(txConfiguracao.getText()).setVisible(true));
		btUpgade.addActionListener(e -> executarUpgade());
		btGerarVersao.addActionListener(e -> atualizarVersaoERP());

		VersaoERPVO versaoVO = buscarVersoes();

		txConfiguracao.setText(new File(System.getProperty("user.dir")).getParent() + "\\interface\\config\\config.01");
		txVersaoErp.setText(versaoVO.getVersaoERP());

		btAtualizarBt.setBounds(103, 80, 104, 34);
		txConfiguracao.setBounds(10, 29, 408, 20);
		txVersaoErp.setBounds(10, 159, 185, 20);
		lbVersaoERP.setBounds(10, 142, 130, 14);
		btGerarVersao.setBounds(36, 190, 104, 34);
		lbConfiguracao.setBounds(10, 11, 145, 14);
		btUpgade.setBounds(223, 80, 104, 34);

		getContentPane().add(btAtualizarBt);
		getContentPane().add(txConfiguracao);
		getContentPane().add(txVersaoErp);
		getContentPane().add(lbVersaoERP);
		getContentPane().add(btGerarVersao);
		getContentPane().add(lbConfiguracao);
		getContentPane().add(btUpgade);

	}

	private void atualizarVersaoERP() {

		txVersaoErp.setText(new GerarVersaoERP(txVersaoErp.getText()).build());
	}

	private void executarUpgade() {

		try {

			Changelog.runChangelog(Conexao.buscarConexao(txConfiguracao.getText()), Configuracoes.UPGRADE,
					Configuracoes.CHANGELOG);

		} catch (Exception e) {

			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	private VersaoERPVO buscarVersoes() {

		try {

			File pathDir = new File(System.getProperty("user.dir")).getParentFile();

			String arquivoJson = FileUtils.readFileToString(new File(pathDir, "versoes\\versao.json"),
					Charset.defaultCharset());

			if (StringUtil.isNullOrEmpty(arquivoJson)) {

				return new VersaoERPVO();
			}

			return new Gson().fromJson(arquivoJson, VersaoERPVO.class);

		} catch (IOException e) {

			return new VersaoERPVO();
		}
	}

	public static void main(String[] args) throws Exception {

		LookAndFeelUtil.configureLayout("Times New Roman plain 11");

		new FrmPrincipal().setVisible(true);
	}
}
