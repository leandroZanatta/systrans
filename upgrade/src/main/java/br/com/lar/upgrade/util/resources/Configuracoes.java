package br.com.lar.upgrade.util.resources;

import java.io.File;

public class Configuracoes {

	private Configuracoes() {
	}

	public static final String USER_DIR = System.getProperty("user.dir");
	public static final String SEPARATOR = File.separator;

	public static final String UPGRADE = USER_DIR + SEPARATOR + "upgrade";
	public static final String CARGA = USER_DIR + SEPARATOR + "carga";
	public static final String CHANGELOG = "db.changelog-master.xml";
	public static final String CHANGELOG_CARGA = "db.changelog-carga.xml";
	public static final String CONEXAO = USER_DIR + SEPARATOR + "config" + SEPARATOR + "config.01";
	public static final String RESOURCES = USER_DIR + SEPARATOR + "config" + SEPARATOR + "resources.01";
	public static final String PATH_RESOURCES = USER_DIR + SEPARATOR + "resources";
	public static final String FOLDER_IMAGE = PATH_RESOURCES + SEPARATOR + "image";

}
