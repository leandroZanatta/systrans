package br.com.lar.upgrade.util.classes;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.ImageIcon;

import br.com.lar.upgrade.util.resources.Configuracoes;

public class ImageUtil {

	private ImageUtil() {
	}

	public static ImageIcon resize(String source, Integer width, Integer height) {

		try {
			return resize(new File(Configuracoes.FOLDER_IMAGE + File.separator + source), width, height);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public static ImageIcon resize(File source, Integer width, Integer height) throws MalformedURLException {

		BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(new ImageIcon(source.toURI().toURL()).getImage(), 0, 0, width, height, null);
		g2.dispose();

		return new ImageIcon(resizedImg);
	}

	public static ImageIcon resize(Class<?> resource, String string) {

		return new ImageIcon(resource.getClassLoader().getResource(string));
	}

}
