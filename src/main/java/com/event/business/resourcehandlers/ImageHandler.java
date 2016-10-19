package com.event.business.resourcehandlers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageHandler {
	public static BufferedImage byteToBufferedImage(byte[] bytes) {
		BufferedImage buf = null;
		InputStream in = new ByteArrayInputStream(bytes);
		try {
			buf = ImageIO.read(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buf;
	}

	public static byte[] BufferedImageToByte(BufferedImage img) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ImageIO.write(img, "png", out);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	public static BufferedImage getBufferedImageFromFile(File file) {
		BufferedImage buf = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			buf = ImageIO.read(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buf;
	}

	public static BufferedImage getBufferedImageFromPath(String path) {
		return getBufferedImageFromFile(new File(path));
	}

	public static File createFileFromBufferedImage(BufferedImage img, String filename, String type) {
		File downloads = new File(filename, "downloads");
		if (!downloads.exists()) {
			if (!downloads.mkdir()) {
				System.err.println("Unable to create temporary downloads folder");
			}
		}
		File outputfile = new File(downloads, "downloaded.png");

		if (!outputfile.exists()) {
			try {
				outputfile.createNewFile();
			} catch (IOException e) {
				System.err.println("Cant create new file in: " + outputfile.getParentFile());
				e.printStackTrace();
			}
		}

		try {
			ImageIO.write(img, type, outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outputfile;
	}

}