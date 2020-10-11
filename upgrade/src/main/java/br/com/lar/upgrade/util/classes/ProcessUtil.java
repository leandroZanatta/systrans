package br.com.lar.upgrade.util.classes;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public final class ProcessUtil {

	private ProcessUtil() {

	}

	public static Process createProcess(String comand) throws IOException {

		return createProcess(comand, null, null);

	}

	public static Process createProcess(String comand, File logFile, String directory) throws IOException {

		ProcessBuilder builder = new ProcessBuilder(Arrays.asList(comand.split(" ")));

		if (!StringUtil.isNullOrEmpty(directory)) {
			builder.directory(new File(directory));
		}

		if (logFile != null) {
			builder.inheritIO();

			builder.redirectOutput(logFile);

		}
		return builder.start();

	}
}
