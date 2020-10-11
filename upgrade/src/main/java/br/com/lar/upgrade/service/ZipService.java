package br.com.lar.upgrade.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipService {

	private byte[] buffer = new byte[1024];

	public void createZip(File pathDir, String zipFile, File... files) throws IOException {

		File arquivoZip = new File(pathDir, zipFile);

		Files.deleteIfExists(arquivoZip.toPath());

		arquivoZip.createNewFile();

		try (ZipOutputStream zipOuputStream = new ZipOutputStream(new FileOutputStream(arquivoZip))) {

			for (File arquivo : files) {

				zipFile("", arquivo, zipOuputStream);
			}

			zipOuputStream.closeEntry();
		}
	}

	private void zipFile(String path, File file, ZipOutputStream zipFile) throws IOException {

		String separator = path.length() == 0 ? "" : (path + "/");

		if (file.isDirectory()) {

			for (File entry : file.listFiles()) {

				zipFile(separator + file.getName(), entry, zipFile);
			}

			return;
		}

		zipFile.putNextEntry(new ZipEntry(separator + file.getName()));

		try (FileInputStream fis = new FileInputStream(file)) {

			int length;

			while ((length = fis.read(buffer)) > 0) {
				zipFile.write(buffer, 0, length);
			}

		}

		zipFile.closeEntry();
	}

}
