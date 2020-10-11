package br.com.lar.atualizacao.enumeradores;

public enum TipoConexaoEnum {

	POSTGRES("org.postgresql.Driver", "jdbc:postgresql://localhost",
			"SELECT datname FROM pg_database WHERE datistemplate = false", "postgres", 5432),

	H2("org.h2.Driver", "jdbc:h2:file:./sysdesc", "", "", 0);

	private String driver;

	private String url;

	private String database;

	private String defaultDatabase;

	private Integer porta;

	public static final String JDBCPASSWORD = "javax.persistence.jdbc.password";
	public static final String JDBCUSER = "javax.persistence.jdbc.user";
	public static final String JDBCURL = "javax.persistence.jdbc.url";
	public static final String JDBCDRIVER = "javax.persistence.jdbc.driver";

	private TipoConexaoEnum(String driver, String url, String database, String defaultDatabase, Integer porta) {
		this.driver = driver;
		this.url = url;
		this.porta = porta;
		this.database = database;
		this.defaultDatabase = defaultDatabase;

	}

	public String getDriver() {
		return driver;
	}

	public String getUrl() {
		return url;
	}

	public Integer getPorta() {
		return porta;
	}

	public String getDatabase() {
		return database;
	}

	public String getDefaultDatabase() {
		return defaultDatabase;
	}

	@Override
	public String toString() {
		return this.driver;
	}
}
