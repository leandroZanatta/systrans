package br.com.lar.atualizacao.changelog.core;

import static br.com.sysdesc.util.constants.MensagemConstants.MENSAGEM_CONFIGURACOES_INVALIDAS;
import static br.com.sysdesc.util.resources.Resources.translate;
import static java.sql.DriverManager.getConnection;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.ConfigurationException;

import org.apache.commons.io.FileUtils;

import br.com.lar.atualizacao.enumeradores.TipoConexaoEnum;
import br.com.sysdesc.util.classes.CryptoUtil;
import br.com.sysdesc.util.exception.SysDescException;
import br.com.sysdesc.util.resources.Configuracoes;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Conexao {

	private Conexao() {
	}

	private static boolean isconfigured() {

		return new File(Configuracoes.CONEXAO).exists();
	}

	public static File getConfiguracaoBanco() throws ConfigurationException {

		if (!isconfigured()) {

			throw new ConfigurationException("Configuração de banco de dados não encontrada");
		}

		return new File(Configuracoes.CONEXAO);
	}

	public static Connection buscarConexao() {

		try {

			Properties propertiesConexao = buscarPropertiesConexao();

			String clazz = propertiesConexao.getProperty(TipoConexaoEnum.JDBCDRIVER);
			String url = propertiesConexao.getProperty(TipoConexaoEnum.JDBCURL);
			String usuario = propertiesConexao.getProperty(TipoConexaoEnum.JDBCUSER);
			String senha = propertiesConexao.getProperty(TipoConexaoEnum.JDBCPASSWORD);

			Class.forName(clazz);

			return getConnection(url, usuario, senha);

		} catch (SQLException | ConfigurationException e) {
			throw new SysDescException("Não foi possive conectar ao banco de dados selecionado", e);
		} catch (ClassNotFoundException e) {
			throw new SysDescException("Driver do banco de dados não foi encontrado", e);
		} catch (IOException e) {
			throw new SysDescException("Não foi possivel encontrar o arquivo de configuração do banco de dados", e);
		}
	}

	private static Properties buscarPropertiesConexao() throws ConfigurationException, IOException {

		Properties properties = new Properties();

		String arquivoConfiguracao = CryptoUtil
				.fromBlowfish(FileUtils.readFileToString(getConfiguracaoBanco(), StandardCharsets.UTF_8));

		if (arquivoConfiguracao == null) {
			throw new ConfigurationException(translate(MENSAGEM_CONFIGURACOES_INVALIDAS));
		}

		properties.load(new StringReader(arquivoConfiguracao));

		return properties;

	}

}
