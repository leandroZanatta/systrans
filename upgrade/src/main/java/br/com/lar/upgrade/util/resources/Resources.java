package br.com.lar.upgrade.util.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

public class Resources {

	private Resources() {
	}

	private static Properties mapaValores;
	private static Charset charset = Charset.forName("UTF-8");

	public static final String FRMCONEXAO_TITULO = "FRMCONEXAO_TITULO";
	public static final String FRMCONEXAO_MSG_SALVAR = "FRMCONEXAO_MSG_SALVAR";
	public static final String FRMCONEXAO_PRP_CONEXAO = "FRMCONEXAO_PRP_CONEXAO";
	public static final String FRMCONEXAO_BT_SALVAR = "FRMCONEXAO_BT_SALVAR";
	public static final String FRMCONEXAO_BT_CANCELAR = "FRMCONEXAO_BT_CANCELAR";
	public static final String FRMCONEXAO_LB_TIPOBANCO = "FRMCONEXAO_LB_TIPOBANCO";
	public static final String FRMCONEXAO_LB_URL = "FRMCONEXAO_LB_URL";
	public static final String FRMCONEXAO_LB_PORTA = "FRMCONEXAO_LB_PORTA";
	public static final String FRMCONEXAO_LB_USUARIO = "FRMCONEXAO_LB_USUARIO";
	public static final String FRMCONEXAO_LB_SENHA = "FRMCONEXAO_LB_SENHA";
	public static final String FRMCONEXAO_LB_BANCO = "FRMCONEXAO_LB_BANCO";

	public static final String MENSAGEM_DRIVER_NAO_ENCONTRADO = "MENSAGEM_DRIVER_NAO_ENCONTRADO";
	public static final String APPLICATION_JAR = "APPLICATION_JAR";

	public static final String OPTION_ERRO = "OPTION_ERRO";
	public static final String MENSAGEM_CONEXAO_INVALIDA = "MENSAGEM_CONEXAO_INVALIDA";
	public static final String MENSAGEM_ERRO_BUSCAR_PROPRIEDADES_CONEXAO = "MENSAGEM_ERRO_BUSCAR_PROPRIEDADES_CONEXAO";
	public static final String MENSAGEM_CONFIGURACOES_INVALIDAS = "MENSAGEM_CONFIGURACOES_INVALIDAS";
	public static final String APPLICATION_VERSOES = "APPLICATION_VERSOES";

	static {
		try {
			File arquivoConfiguracao = new File(Configuracoes.RESOURCES);

			if (!arquivoConfiguracao.exists()) {
				FileUtils.writeStringToFile(arquivoConfiguracao, "pt_br.properties", charset);
			}

			String arquivoConfig = FileUtils.readFileToString(arquivoConfiguracao, charset);

			mapaValores = new Properties();

			File file = new File(Configuracoes.PATH_RESOURCES + Configuracoes.SEPARATOR + arquivoConfig);

			mapaValores.load(new InputStreamReader(new FileInputStream(file), charset));

		} catch (IOException e) {

		}
	}

	public static String translate(String menu) {

		return translate(menu, "");

	}

	public static String translate(String menu, String defaultValue) {

		return mapaValores.getProperty(menu, defaultValue);

	}

}
