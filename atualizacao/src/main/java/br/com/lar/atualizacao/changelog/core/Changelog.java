package br.com.lar.atualizacao.changelog.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.logging.Logger;

import br.com.sysdesc.util.resources.Configuracoes;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Changelog {

	private Changelog() {
	}

	public static void runChangelog(Connection connection) {

		Logger log = Logger.getLogger(Changelog.class.getName());

		Database database;

		try {

			database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

			Liquibase liquibase = new liquibase.Liquibase(Configuracoes.CHANGELOG,
					new FileSystemResourceAccessor(Configuracoes.UPGRADE), database);

			liquibase.update(new Contexts(), new LabelExpression());

			File pastaChangelogs = new File(Configuracoes.UPGRADE);

			deletarChangelogs(pastaChangelogs);

		} catch (LiquibaseException e) {

			log.severe("OCORREU UM ERRO AO ATUALIZAR A BASE DE DADOS");

			e.printStackTrace();

			System.exit(0);
		}

	}

	private static void deletarChangelogs(File pastaChangelog) {

		for (File arquivo : pastaChangelog.listFiles()) {

			if (arquivo.isDirectory()) {
				deletarChangelogs(arquivo);
			}

			deleteFile(arquivo);
		}

		deleteFile(pastaChangelog);
	}

	private static void deleteFile(File arquivo) {

		try {

			Files.deleteIfExists(arquivo.toPath());

		} catch (IOException e) {

			log.error("Não foi possivel deletar o arquivo:{}", arquivo, e);
		}

	}
}
