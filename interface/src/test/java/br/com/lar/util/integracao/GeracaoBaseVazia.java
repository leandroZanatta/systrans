package br.com.lar.util.integracao;

import static java.sql.DriverManager.getConnection;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Connection;
import java.util.Properties;

import br.com.lar.atualizacao.enumeradores.TipoConexaoEnum;
import br.com.sysdesc.util.dao.Conexao;
import br.com.sysdesc.util.resources.Configuracoes;

public class GeracaoBaseVazia {

	public void gerarBaseVazia() throws Exception {

		try {

			String configuracao = Configuracoes.USER_DIR + Configuracoes.SEPARATOR + "config" + Configuracoes.SEPARATOR
					+ "config.02";

			// Changelog.runChangelog(buscarConexao(configuracao),
			// pastaChangesets.getAbsolutePath(),
			// Configuracoes.CHANGELOG);

			Conexao.createConnection(new File(configuracao));
		} catch (Exception e) {

			fail(e.getMessage());
		}

	}

	public static Connection buscarConexao(String configuracao) throws Exception {

		Properties propertiesConexao = Conexao.buscarPropertiesConexao(new File(configuracao));

		String clazz = propertiesConexao.getProperty(TipoConexaoEnum.JDBCDRIVER);
		String url = propertiesConexao.getProperty(TipoConexaoEnum.JDBCURL);
		String usuario = propertiesConexao.getProperty(TipoConexaoEnum.JDBCUSER);
		String senha = propertiesConexao.getProperty(TipoConexaoEnum.JDBCPASSWORD);

		Class.forName(clazz);

		return getConnection(url, usuario, senha);

	}

}
