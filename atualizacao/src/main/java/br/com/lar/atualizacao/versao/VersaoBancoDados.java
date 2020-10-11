package br.com.lar.atualizacao.versao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.UnsupportedLookAndFeelException;

import br.com.lar.atualizacao.changelog.core.Changelog;
import br.com.lar.atualizacao.changelog.core.Conexao;
import br.com.lar.atualizacao.ui.FrmConexao;
import br.com.sysdesc.util.classes.LookAndFeelUtil;
import br.com.sysdesc.util.exception.SysDescException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersaoBancoDados {

	public String buscarVersaoBanco() {

		String sqlQuery = "select nr_versao from tb_versao order by id_versao desc limit 1";

		try (Connection connection = criarConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
				ResultSet rs = preparedStatement.executeQuery()) {

			if (rs.next()) {
				return rs.getString(1);
			}

		} catch (Exception e) {

			log.error("Erro ao buscar versão no banco de dados", e);
		}

		return "0.0.0";
	}

	public void atualizarVersao(String versao) {

		String sqlQuery = "insert into tb_versao values(nextval('gen_versao'), ?, current_timestamp)";

		try (Connection connection = criarConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

			connection.setAutoCommit(false);

			preparedStatement.setString(1, versao);

			preparedStatement.executeUpdate();

			connection.commit();

		} catch (Exception e) {

			log.error("Erro ao atualizar versão no banco de dados", e);
		}
	}

	public void upgradeDatabase(String versao) {

		try (Connection connection = criarConnection()) {

			Changelog.runChangelog(connection);

			this.atualizarVersao(versao);

		} catch (Exception e) {
			log.error("Erro ao atualizar versão no banco de dados", e);
		}
	}

	private Connection criarConnection() {

		try {

			return Conexao.buscarConexao();

		} catch (SysDescException e) {

			log.warn("Foi encontrado um erro ao configurar o banco de dados", e);

			configure();

			return criarConnection();
		}

	}

	private void configure() {

		try {
			LookAndFeelUtil.configureLayout();

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			log.error("Falha ao configurar Layout", e);
		}

		new FrmConexao().setVisible(Boolean.TRUE);
	}

}
