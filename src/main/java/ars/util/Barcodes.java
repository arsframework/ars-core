package ars.util;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.awt.image.BufferedImage;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.ReaderException;
import com.google.zxing.WriterException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;

import ars.util.Strings;

/**
 * 条码工具类
 * 
 * @author wuyq
 * 
 */
public final class Barcodes {
	/**
	 * 将内容编码
	 * 
	 * @param content
	 *            图片内容
	 * @return 图片对象
	 * @throws IOException
	 *             IO操作异常
	 */
	public static BufferedImage encode(String content) throws IOException {
		return encode(content, BarcodeFormat.QR_CODE, 200, 200);
	}

	/**
	 * 将内容编码
	 * 
	 * @param content
	 *            图片内容
	 * @param format
	 *            图片格式
	 * @param width
	 *            图片宽度
	 * @param height
	 *            图片高度
	 * @return 图片对象
	 */
	public static BufferedImage encode(String content, BarcodeFormat format, int width, int height) {
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>(1, 1);
		hints.put(EncodeHintType.CHARACTER_SET, Strings.UTF8);
		try {
			BitMatrix matrix = new MultiFormatWriter().encode(content, format, width, height, hints);
			return MatrixToImageWriter.toBufferedImage(matrix);
		} catch (WriterException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 内容解码
	 * 
	 * @param image
	 *            图片对象
	 * @return 图形内容
	 */
	public static String decode(BufferedImage image) {
		Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>(1, 1);
		hints.put(DecodeHintType.CHARACTER_SET, Strings.UTF8);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
		try {
			return new MultiFormatReader().decode(bitmap, hints).getText();
		} catch (ReaderException e) {
			throw new RuntimeException(e);
		}
	}

}
