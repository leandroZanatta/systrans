package br.com.lar.upgrade.changelog.core;

import java.sql.Connection;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;

public class Changelog {

	private Changelog() {
	}

	public static void runChangelog(Connection connection, String path, String file) {

		Database database;

		try {

			database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

			Liquibase liquibase = new Liquibase(file, new FileSystemResourceAccessor(path), database);

			liquibase.update(new Contexts(), new LabelExpression());

		} catch (LiquibaseException e) {

			e.printStackTrace();

			System.exit(0);
		}

	}

}
