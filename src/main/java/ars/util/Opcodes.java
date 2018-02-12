package ars.util;

import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.util.Random;

import ars.util.Randoms;

/**
 * 操作证码工具类
 * 
 * @author yongqiangwu
 * 
 */
public final class Opcodes {
	private Opcodes() {

	}

	private static Color getRandomColor(int fc, int bc) {
		if (fc > 255) {
			fc = 255;
		}
		if (bc > 255) {
			bc = 255;
		}
		Random random = Randoms.getCurrentRandom();
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

	private static void drawLine(Graphics graphics, int number, int width, int height) {
		Random random = Randoms.getCurrentRandom();
		for (int i = 0; i < number; i++) {
			int x = random.nextInt(width - 1);
			int y = random.nextInt(height - 1);
			int xl = random.nextInt(6) + 1;
			int yl = random.nextInt(12) + 1;
			graphics.drawLine(x, y, x + xl + 40, y + yl + 20);
		}
	}

	private static void drawYawp(BufferedImage image, float rate, int width, int height) {
		Random random = Randoms.getCurrentRandom();
		int area = (int) (rate * width * height);
		for (int i = 0; i < area; i++) {
			image.setRGB(random.nextInt(width), random.nextInt(height), getRandomIntColor());
		}
	}

	private static int getRandomIntColor() {
		int color = 0;
		Random random = Randoms.getCurrentRandom();
		for (int i = 0; i < 3; i++) {
			color = color << 8;
			color = color | random.nextInt(255);
		}
		return color;
	}

	private static void shearX(Graphics graphics, Color color, int width, int height) {
		Random random = Randoms.getCurrentRandom();
		int period = random.nextInt(2);
		boolean borderGap = true;
		int frames = 1;
		int phase = random.nextInt(2);
		for (int i = 0; i < height; i++) {
			double d = (double) (period >> 1)
					* Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
			graphics.copyArea(0, i, width, 1, (int) d, 0);
			if (borderGap) {
				graphics.setColor(color);
				graphics.drawLine((int) d, i, 0, i);
				graphics.drawLine((int) d + width, i, width, i);
			}
		}
	}

	private static void shearY(Graphics graphics, Color color, int width, int height) {
		Random random = Randoms.getCurrentRandom();
		int period = random.nextInt(40) + 10; // 50;
		boolean borderGap = true;
		int frames = 20;
		int phase = 7;
		for (int i = 0; i < width; i++) {
			double d = (double) (period >> 1)
					* Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
			graphics.copyArea(i, 0, 1, height, 0, (int) d);
			if (borderGap) {
				graphics.setColor(color);
				graphics.drawLine(i, (int) d, i, 0);
				graphics.drawLine(i, (int) d + height, i, height);
			}
		}
	}

	/**
	 * 验证码编码
	 * 
	 * @param content
	 *            验证码内容
	 * @return 图片对象
	 */
	public static BufferedImage encode(String content) {
		return encode(content, 120, 50);
	}

	/**
	 * 验证码编码
	 * 
	 * @param content
	 *            验证码内容
	 * @param width
	 *            图片宽度
	 * @param height
	 *            图片高度
	 * @return 图片对象
	 */
	public static BufferedImage encode(String content, int width, int height) {
		if (content == null) {
			throw new IllegalArgumentException("Illegal content:" + content);
		}
		if (width < 1) {
			throw new IllegalArgumentException("Illegal width:" + width);
		}
		if (height < 1) {
			throw new IllegalArgumentException("Illegal height:" + height);
		}
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(Color.GRAY);// 设置边框色
		graphics.fillRect(0, 0, width, height);
		graphics.setColor(getRandomColor(200, 250));// 设置背景色
		graphics.fillRect(0, 2, width, height - 4);
		Color color = getRandomColor(160, 200);
		graphics.setColor(color);// 设置线条的颜色
		drawLine(graphics, 20, width, height);// 绘制干扰线
		drawYawp(image, 0.05f, width, height);// 添加噪点
		shearX(graphics, color, width, height); // 扭曲横柱
		shearY(graphics, color, width, height); // 扭曲纵柱
		Random random = Randoms.getCurrentRandom();
		graphics.setColor(getRandomColor(100, 160));
		int fontSize = height - 4;
		graphics.setFont(new Font("Algerian", Font.ITALIC, fontSize));
		char[] chars = content.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			AffineTransform affine = new AffineTransform();
			affine.setToRotation(Math.PI / 4 * random.nextDouble() * (random.nextBoolean() ? 1 : -1),
					(width / chars.length) * i + fontSize / 2, height / 2);
			graphics.setTransform(affine);
			graphics.drawChars(chars, i, 1, ((width - 10) / chars.length) * i + 5, height / 2 + fontSize / 2 - 10);
		}
		graphics.dispose();
		return image;
	}

}
