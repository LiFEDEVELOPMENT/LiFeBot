package de.life;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class UsefulSubclasses {
	
	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	public static String shortString(String pString) {
		if (pString.length() > 6)
			return pString.substring(0, pString.length() - 6) + "."
					+ pString.substring(pString.length() - 6, pString.length() - 5) + "M";
		if (pString.length() > 3)
			return pString.substring(0, pString.length() - 3) + "K";
		return pString;
	}
	
	public static byte[] extractBytes(File ImageName) throws IOException {
		// open image
		BufferedImage bufferedImage = ImageIO.read(ImageName);

		// get DataBufferBytes from Raster
		WritableRaster raster = bufferedImage.getRaster();
		DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

		return (data.getData());
	}

}
