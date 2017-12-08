package ars.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.Random;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.net.URL;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.codec.binary.Base64;

import ars.util.Dates;

/**
 * 字符串工具类
 * 
 * @author wuyq
 * 
 */
public final class Strings {
	/**
	 * 空字符串
	 */
	public static final String EMPTY_STRING = "";

	/**
	 * 空字符串数组
	 */
	public static final String[] EMPTY_ARRAY = new String[0];

	/**
	 * GBK字符集
	 */
	public static final String GBK = "gbk";

	/**
	 * utf-8字符集
	 */
	public static final String UTF8 = "utf-8";

	/**
	 * 16进制字符串序列
	 */
	public static final String HEX_SEQUENCE = "0123456789ABCDEF";

	/**
	 * 当前主机
	 */
	public static final String LOCALHOST = "localhost";

	/**
	 * 当前主机名称
	 */
	public static final String LOCALHOST_NAME;

	/**
	 * 当前主机地址
	 */
	public static final String LOCALHOST_ADDRESS;

	/**
	 * 默认当前主机地址
	 */
	public static final String DEFAULT_LOCALHOST_ADDRESS = "127.0.0.1";

	/**
	 * 临时文件目录
	 */
	public static final String TEMP_PATH;

	/**
	 * 当前文件目录
	 */
	public static final String CURRENT_PATH;

	/**
	 * IP正则表达式匹配模式
	 */
	public static final Pattern PATTERN_IP = Pattern.compile(
			"^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$");

	/**
	 * URL正则表达式匹配模式
	 */
	public static final Pattern PATTERN_URL = Pattern.compile("http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?");

	/**
	 * 邮箱正则表达式匹配模式
	 */
	public static final Pattern PATTERN_EMAIL = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

	/**
	 * 数字正则表达式匹配模式
	 */
	public static final Pattern PATTERN_NUMBER = Pattern.compile("-?[0-9]+\\.?[0-9]*");

	/**
	 * 字母正则表达式匹配模式
	 */
	public static final Pattern PATTERN_LETTER = Pattern.compile("[a-zA-Z]+");

	static {
		TEMP_PATH = System.getProperty("java.io.tmpdir");
		CURRENT_PATH = Strings.class.getResource("/").getPath();

		InetAddress localhost = null;
		try {
			localhost = InetAddress.getLocalHost();
		} catch (IOException e) {
			e.printStackTrace();
		}
		LOCALHOST_NAME = localhost == null ? null : localhost.getHostName();

		boolean windows = System.getProperty("os.name").startsWith("Windows");
		if (windows) {
			LOCALHOST_ADDRESS = localhost == null ? DEFAULT_LOCALHOST_ADDRESS : localhost.getHostAddress();
		} else {
			String ip = null;
			try {
				Enumeration<NetworkInterface> enumeration = (Enumeration<NetworkInterface>) NetworkInterface
						.getNetworkInterfaces();
				outer: while (enumeration.hasMoreElements()) {
					NetworkInterface networks = enumeration.nextElement();
					Enumeration<InetAddress> inetAddresses = networks.getInetAddresses();
					while (inetAddresses.hasMoreElements()) {
						InetAddress inetAddress = inetAddresses.nextElement();
						if (inetAddress.isSiteLocalAddress() && !inetAddress.isLoopbackAddress()
								&& (ip = inetAddress.getHostAddress()).indexOf(":") == -1) {
							break outer;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			LOCALHOST_ADDRESS = ip == null ? DEFAULT_LOCALHOST_ADDRESS : ip;
		}
	}

	/**
	 * MD5加密
	 * 
	 * @param source
	 *            明文
	 * @return 密文
	 */
	public static String md5(String source) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(source.getBytes());
			return Base64.encodeBase64String(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * SHA-1加密
	 * 
	 * @param source
	 *            明文
	 * @return 密文
	 */
	public static String sha1(String source) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(source.getBytes());
			return Base64.encodeBase64String(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * DES加密
	 * 
	 * @param source
	 *            明文
	 * @param key
	 *            密钥
	 * @return 密文
	 */
	public static String des(String source, String key) {
		try {
			DESKeySpec spec = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(spec);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, securekey, new SecureRandom());
			return Base64.encodeBase64String(cipher.doFinal(source.getBytes()));
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeySpecException e) {
			throw new RuntimeException(e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException(e);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * DES解密
	 * 
	 * @param source
	 *            密文
	 * @param key
	 *            秘钥
	 * @return 明文
	 */
	public static String undes(String source, String key) {
		try {
			DESKeySpec spec = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(spec);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, securekey, new SecureRandom());
			return new String(cipher.doFinal(Base64.decodeBase64(source)));
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeySpecException e) {
			throw new RuntimeException(e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException(e);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * AES加密
	 * 
	 * @param source
	 *            数据源
	 * @param key
	 *            秘钥
	 * @return 密文（base64）
	 */
	public static String aes(String source, String key) {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128, new SecureRandom(key.getBytes()));
			SecretKeySpec secretKeySpec = new SecretKeySpec(keyGenerator.generateKey().getEncoded(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			return Base64.encodeBase64String(cipher.doFinal(source.getBytes()));
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException(e);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * AES解密
	 * 
	 * @param source
	 *            数据源（base64）
	 * @param key
	 *            秘钥
	 * @return 明文
	 */
	public static String unaes(String source, String key) {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128, new SecureRandom(key.getBytes()));
			SecretKeySpec secretKeySpec = new SecretKeySpec(keyGenerator.generateKey().getEncoded(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			return new String(cipher.doFinal(Base64.decodeBase64(source)));
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException(e);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将字符转换字节
	 * 
	 * @param c
	 *            字符
	 * @return 字节
	 */
	public static byte charToByte(char c) {
		return (byte) HEX_SEQUENCE.indexOf(c);
	}

	/**
	 * 将16进制字符串转换成字节数组
	 * 
	 * @param hex
	 *            16进制字符串
	 * @return 字节数组
	 */
	public static byte[] hexToByte(String hex) {
		int length = hex.length() / 2;
		char[] chars = hex.toUpperCase().toCharArray();
		byte[] bytes = new byte[length];
		for (int i = 0, pos = 0; i < length; i++, pos = i * 2) {
			bytes[i] = (byte) (charToByte(chars[pos]) << 4 | charToByte(chars[pos + 1]));
		}
		return bytes;
	}

	/**
	 * 将字节数组转换16进制
	 * 
	 * @param bytes
	 *            字节数组
	 * @return 16进制字符串
	 */
	public static String byteToHex(byte[] bytes) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				buffer.append('0');
			}
			buffer.append(hex);
		}
		return buffer.toString();
	}

	/**
	 * 将字符按照2字符一单元转换成16进制
	 * 
	 * @param chars
	 *            字符数组
	 * @param radix
	 *            单元长度
	 * @return 16进制字符串
	 */
	public static String charToHex(char[] chars, int radix) {
		StringBuilder buffer = new StringBuilder();
		for (char c : chars) {
			String s = Integer.toHexString(c);
			int offset = radix - s.length();
			for (int i = 0; i < offset; i++) {
				buffer.append('0');
			}
			buffer.append(s);
		}
		return buffer.toString();
	}

	/**
	 * 将16进制字符串按照2字符一单元转换成10进制字符
	 * 
	 * @param hex
	 *            16进制字符串
	 * @param radix
	 *            单元长度
	 * @return 10进制字符数组
	 */
	public static char[] hexToChar(String hex, int radix) {
		int len = hex.length() / radix;
		char[] chars = new char[len];
		for (int i = 0; i < len; i++) {
			chars[i] = (char) Integer.parseInt(hex.substring(i * radix, (i + 1) * radix), 16);
		}
		return chars;
	}

	/**
	 * 将unicode字符串转换成字符数组
	 * 
	 * @param unicode
	 *            unicode字符串
	 * @return 字符数组
	 */
	public static char[] unicodeToChar(String unicode) {
		int index = -1, _index = -1;
		StringBuilder buffer = new StringBuilder();
		while ((index = unicode.indexOf("\\u", index + 1)) > -1) {
			int offset = 0;
			if (_index > -1 && (offset = index - _index - 6) > 0) {
				if (offset > 0) {
					buffer.append(unicode.substring(_index + 6, _index + 6 + offset));
				}
			} else if (index > 0 && _index < 0) {
				buffer.append(unicode.substring(0, index));
			}
			buffer.append(hexToChar(unicode.substring(index + 2, index + 6), 4));
			_index = index;
		}
		if (_index < 0) {
			return unicode.toCharArray();
		}
		if (_index + 6 < unicode.length()) {
			buffer.append(unicode.substring(_index + 6));
		}
		char[] chars = new char[buffer.length()];
		buffer.getChars(0, buffer.length(), chars, 0);
		return chars;
	}

	/**
	 * 将对象转换成字符串形式
	 * 
	 * @param object
	 *            对象
	 * @return 字符串形式
	 */
	public static String toString(Object object) {
		if (object instanceof String) {
			return (String) object;
		} else if (object instanceof Date) {
			return Dates.format((Date) object);
		} else if (object instanceof Class) {
			return ((Class<?>) object).getName();
		} else if (object instanceof byte[]) {
			return Arrays.toString((byte[]) object);
		} else if (object instanceof char[]) {
			return Arrays.toString((char[]) object);
		} else if (object instanceof int[]) {
			return Arrays.toString((int[]) object);
		} else if (object instanceof short[]) {
			return Arrays.toString((short[]) object);
		} else if (object instanceof long[]) {
			return Arrays.toString((long[]) object);
		} else if (object instanceof float[]) {
			return Arrays.toString((float[]) object);
		} else if (object instanceof double[]) {
			return Arrays.toString((double[]) object);
		} else if (object instanceof boolean[]) {
			return Arrays.toString((boolean[]) object);
		} else if (object instanceof Object[]) {
			return Arrays.toString((Object[]) object);
		}
		return object == null ? null : object.toString();
	}

	/**
	 * 获取0~10之前的随机数（不包含10）
	 * 
	 * @param length
	 *            随机数长度
	 * @return 随机数字符串
	 */
	public static String random(int length) {
		return random(length, true);
	}

	/**
	 * 获取0~10之前的随机数（不包含10）
	 * 
	 * @param length
	 *            随机数长度
	 * @param repeat
	 *            是否允许重复
	 * @return 随机数字符串
	 */
	public static String random(int length, boolean repeat) {
		return random(length, 10, repeat);
	}

	/**
	 * 获取随机数
	 * 
	 * @param length
	 *            随机数长度
	 * @param max
	 *            随机数最大值（不包含）
	 * @return 随机数字符串
	 */
	public static String random(int length, int max) {
		return random(length, max, true);
	}

	/**
	 * 获取随机数
	 * 
	 * @param length
	 *            随机数长度
	 * @param max
	 *            随机数最大值（不包含）
	 * @param repeat
	 *            是否允许重复
	 * @return 随机数字符串
	 */
	public static String random(int length, int max, boolean repeat) {
		Random random = new Random();
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (repeat) {
				buffer.append(random.nextInt(max));
			} else {
				int number = -1;
				next: while (true) {
					number = random.nextInt(max);
					for (int j = 0; j < buffer.length(); j++) {
						if (buffer.charAt(j) == number) {
							continue next;
						}
					}
					break;
				}
				buffer.append(number);
			}
		}
		return buffer.toString();
	}

	/**
	 * 获取随机数
	 * 
	 * @param length
	 *            随机数长度
	 * @param source
	 *            随机数源
	 * @return 随机数组
	 */
	public static String random(int length, Object[] source) {
		return random(length, source, true);
	}

	/**
	 * 获取随机数
	 * 
	 * @param length
	 *            随机数长度
	 * @param source
	 *            随机数源
	 * @param repeat
	 *            是否允许重复
	 * @return 随机数组
	 */
	public static String random(int length, Object[] source, boolean repeat) {
		Random random = new Random();
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (repeat) {
				buffer.append(source[random.nextInt(source.length)]);
			} else {
				Object object;
				next: while (true) {
					object = source[random.nextInt(source.length)];
					for (int j = 0; j < buffer.length(); j++) {
						if (object.equals(buffer.charAt(j))) {
							continue next;
						}
					}
					break;
				}
				buffer.append(object);
			}
		}
		return buffer.toString();
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param source
	 *            字符串
	 * @return true/false
	 */
	public static boolean isEmpty(String source) {
		return source == null || source.isEmpty();
	}

	/**
	 * 判断字符串数组是否为空
	 * 
	 * @param sources
	 *            字符串数组
	 * @return true/false
	 */
	public static boolean isEmpty(String[] sources) {
		return sources == null || sources.length == 0;
	}

	/**
	 * 判断字符串集合是否为空
	 * 
	 * @param sources
	 *            字符串集合
	 * @return true/false
	 */
	public static boolean isEmpty(Collection<String> sources) {
		return sources == null || sources.isEmpty();
	}

	/**
	 * 判断字符串是否为空格
	 * 
	 * @param source
	 *            字符串
	 * @return true/false
	 */
	public static boolean isSpace(String source) {
		if (isEmpty(source)) {
			return false;
		}
		for (int i = 0; i < source.length(); i++) {
			if (source.charAt(i) != ' ') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符串是否是IP地址
	 * 
	 * @param source
	 *            字符串
	 * @return true/false
	 */
	public static boolean isIP(String source) {
		return !isEmpty(source) && PATTERN_IP.matcher(source).matches();
	}

	/**
	 * 判断字符串是否是URL地址
	 * 
	 * @param source
	 *            字符串
	 * @return true/false
	 */
	public static boolean isUrl(String source) {
		return !isEmpty(source) && PATTERN_URL.matcher(source).matches();
	}

	/**
	 * 判断字符串是否为邮件地址
	 * 
	 * @param source
	 *            字符串
	 * @return true/false
	 */
	public static boolean isEmail(String source) {
		return !isEmpty(source) && PATTERN_EMAIL.matcher(source).matches();
	}

	/**
	 * 判断字符串是否为数字
	 * 
	 * @param source
	 *            字符串
	 * @return true/false
	 */
	public static boolean isNumber(String source) {
		return !isEmpty(source) && PATTERN_NUMBER.matcher(source).matches();
	}

	/**
	 * 判断字符串是否为字母
	 * 
	 * @param source
	 *            字符串
	 * @return true/false
	 */
	public static boolean isLetter(String source) {
		return !isEmpty(source) && PATTERN_LETTER.matcher(source).matches();
	}

	/**
	 * 获取目标字符串在指定字符串中出现的次数，非正则表达式匹配
	 * 
	 * @param source
	 *            源字符串
	 * @param sign
	 *            目标字符串
	 * @return 次数
	 */
	public static int count(CharSequence source, char sign) {
		if (source == null || source.length() == 0) {
			return 0;
		}
		int count = 0;
		for (int i = 0, slen = source.length(); i < slen; i++) {
			if (source.charAt(i) == sign) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 获取目标字符串在指定字符串中出现的次数，非正则表达式匹配
	 * 
	 * @param source
	 *            源字符串
	 * @param sign
	 *            目标字符串
	 * @return 次数
	 */
	public static int count(CharSequence source, CharSequence sign) {
		if (source == null || source.length() == 0 || sign == null || sign.length() == 0) {
			return 0;
		}
		int count = 0;
		source: for (int i = 0, slen = source.length(), tlen = sign.length(); i < slen; i++) {
			for (int j = 0, k = i + 0; j < tlen; j++, k = i + j) {
				if (k >= slen || source.charAt(k) != sign.charAt(j)) {
					continue source;
				}
			}
			count++;
			i += tlen - 1;
		}
		return count;
	}

	/**
	 * 清理字符串中所有空格及换行符
	 * 
	 * @param source
	 *            源字符串
	 * @return 清理后字符串
	 */
	public static String clean(CharSequence source) {
		if (source == null) {
			return null;
		} else if (source.length() == 0) {
			return source.toString();
		}
		StringBuilder buffer = new StringBuilder();
		for (int i = 0, slen = source.length(); i < slen; i++) {
			char c = source.charAt(i);
			if (c != ' ' && c != '\n') {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

	/**
	 * 字符串替换，非正则表达式匹配
	 * 
	 * @param source
	 *            源字符串
	 * @param target
	 *            目标字符串
	 * @param replace
	 *            替换字符串
	 * @return 替换后字符串
	 */
	public static String replace(CharSequence source, CharSequence target, CharSequence replace) {
		return replace(source, target, replace, 0);
	}

	/**
	 * 字符串替换，非正则表达式匹配
	 * 
	 * @param source
	 *            源字符串
	 * @param target
	 *            目标字符串
	 * @param replace
	 *            替换字符串
	 * @param index
	 *            开始位置
	 * @return 替换后字符串
	 */
	public static String replace(CharSequence source, CharSequence target, CharSequence replace, int index) {
		if (source == null) {
			return null;
		} else if (source.length() == 0 || target == null || target.length() == 0 || replace == null) {
			return source.toString();
		}
		StringBuilder buffer = new StringBuilder();
		source: for (int i = index, slen = source.length(), tlen = target.length(); i < slen; i++) {
			for (int j = 0, k = i + 0; j < tlen; j++, k = i + j) {
				if (k >= slen || source.charAt(k) != target.charAt(j)) {
					buffer.append(source.charAt(i));
					continue source;
				}
			}
			buffer.append(replace);
			i += tlen - 1;
		}
		return buffer.toString();
	}

	/**
	 * 字符串替换，非正则表达式匹配
	 * 
	 * @param source
	 *            源字符串
	 * @param target
	 *            目标字符
	 * @param replace
	 *            替换字符串
	 * @return 替换后字符串
	 */
	public static String replace(CharSequence source, char target, CharSequence replace) {
		return replace(source, target, replace, 0);
	}

	/**
	 * 字符串替换，非正则表达式匹配
	 * 
	 * @param source
	 *            源字符串
	 * @param target
	 *            目标字符
	 * @param replace
	 *            替换字符串
	 * @param index
	 *            开始位置
	 * @return 替换后字符串
	 */
	public static String replace(CharSequence source, char target, CharSequence replace, int index) {
		if (source == null) {
			return null;
		} else if (source.length() == 0 || target == 0 || replace == null) {
			return source.toString();
		}
		StringBuilder buffer = new StringBuilder();
		for (int i = index, slen = source.length(); i < slen; i++) {
			char c = source.charAt(i);
			buffer.append(c == target ? replace : c);
		}
		return buffer.toString();
	}

	/**
	 * 字符串替换，非正则表达式匹配
	 * 
	 * @param source
	 *            源字符串
	 * @param target
	 *            目标字符
	 * @param replace
	 *            替换字符
	 * @return 替换后字符串
	 */
	public static String replace(CharSequence source, char target, char replace) {
		return replace(source, target, replace, 0);
	}

	/**
	 * 字符串替换，非正则表达式匹配
	 * 
	 * @param source
	 *            源字符串
	 * @param target
	 *            目标字符
	 * @param replace
	 *            替换字符
	 * @param index
	 *            开始位置
	 * @return 替换后字符串
	 */
	public static String replace(CharSequence source, char target, char replace, int index) {
		if (source == null) {
			return null;
		} else if (source.length() == 0 || target == 0) {
			return source.toString();
		}
		StringBuilder buffer = new StringBuilder();
		for (int i = index, slen = source.length(); i < slen; i++) {
			char c = source.charAt(i);
			buffer.append(c == target ? replace : c);
		}
		return buffer.toString();
	}

	/**
	 * 字符串拆分，非正则表达式匹配
	 * 
	 * @param source
	 *            源字符串
	 * @param sign
	 *            分隔符
	 * @return 字符串数组
	 */
	public static String[] split(CharSequence source, CharSequence sign) {
		return split(source, sign, 0);
	}

	/**
	 * 字符串拆分，非正则表达式匹配
	 * 
	 * @param source
	 *            源字符串
	 * @param sign
	 *            分隔符
	 * @param 开始位置
	 * @return 字符串数组
	 */
	public static String[] split(CharSequence source, CharSequence sign, int index) {
		if (source == null || sign == null) {
			return null;
		} else if (source.length() == 0 || source.equals(sign)) {
			return EMPTY_ARRAY;
		} else if (sign.length() == 0) {
			return new String[] { source.toString() };
		}
		int offset = 0;
		List<String> splits = new LinkedList<String>();
		source: for (int i = index, slen = source.length(), tlen = sign.length(); i < slen; i++) {
			for (int j = 0, k = i + 0; j < tlen; j++, k = i + j) {
				if (k >= slen || source.charAt(k) != sign.charAt(j)) {
					continue source;
				}
			}
			splits.add(source.subSequence(offset, i).toString());
			i += tlen - 1;
			offset = i + 1;
		}
		if (offset < source.length()) {
			splits.add(source.subSequence(offset, source.length()).toString());
		}
		return splits.toArray(EMPTY_ARRAY);
	}

	/**
	 * 字符串拆分，非正则表达式匹配
	 * 
	 * @param source
	 *            源字符串
	 * @param sign
	 *            分隔符
	 * @return 字符串数组
	 */
	public static String[] split(CharSequence source, char sign) {
		return split(source, sign, 0);
	}

	/**
	 * 字符串拆分，非正则表达式匹配
	 * 
	 * @param source
	 *            源字符串
	 * @param sign
	 *            分隔符
	 * @param index
	 *            开始位置
	 * @return 字符串数组
	 */
	public static String[] split(CharSequence source, char sign, int index) {
		if (source == null) {
			return null;
		} else if (source.length() == 0) {
			return EMPTY_ARRAY;
		} else if (sign == 0) {
			return new String[] { source.toString() };
		}
		StringBuilder buffer = new StringBuilder();
		List<StringBuilder> buffers = new LinkedList<StringBuilder>();
		for (int i = index, slen = source.length(); i < slen; i++) {
			char c = source.charAt(i);
			if (c == sign) {
				buffers.add(buffer);
				buffer = new StringBuilder();
				continue;
			}
			buffer.append(c);
		}
		buffers.add(buffer);
		int i = 0;
		String[] splits = new String[buffers.size()];
		for (StringBuilder b : buffers) {
			splits[i++] = b.toString();
		}
		return splits;
	}

	/**
	 * 将字节数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @return 连接后的字符串
	 */
	public static String join(byte[] array) {
		return join(array, null);
	}

	/**
	 * 将字节数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(byte[] array, char sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (byte b : array) {
			if (buffer.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(b);
		}
		return buffer.toString();
	}

	/**
	 * 将字节数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(byte[] array, CharSequence sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (byte b : array) {
			if (buffer.length() > 0 && sign != null && sign.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(b);
		}
		return buffer.toString();
	}

	/**
	 * 将字符数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @return 连接后的字符串
	 */
	public static String join(char[] array) {
		return join(array, null);
	}

	/**
	 * 将字符数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(char[] array, char sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (char c : array) {
			if (buffer.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(c);
		}
		return buffer.toString();
	}

	/**
	 * 将字符数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(char[] array, CharSequence sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (char c : array) {
			if (buffer.length() > 0 && sign != null && sign.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(c);
		}
		return buffer.toString();
	}

	/**
	 * 将短整形数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @return 连接后的字符串
	 */
	public static String join(short[] array) {
		return join(array, null);
	}

	/**
	 * 将短整形数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(short[] array, char sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (short s : array) {
			if (buffer.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(s);
		}
		return buffer.toString();
	}

	/**
	 * 将短整形数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(short[] array, CharSequence sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (short s : array) {
			if (buffer.length() > 0 && sign != null && sign.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(s);
		}
		return buffer.toString();
	}

	/**
	 * 将整形数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @return 连接后的字符串
	 */
	public static String join(int[] array) {
		return join(array, null);
	}

	/**
	 * 将整形数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(int[] array, char sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (int i : array) {
			if (buffer.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(array[i]);
		}
		return buffer.toString();
	}

	/**
	 * 将整形数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(int[] array, CharSequence sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (int i : array) {
			if (buffer.length() > 0 && sign != null && sign.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(array[i]);
		}
		return buffer.toString();
	}

	/**
	 * 将单精度浮点数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @return 连接后的字符串
	 */
	public static String join(float[] array) {
		return join(array, null);
	}

	/**
	 * 将单精度浮点数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(float[] array, char sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (float f : array) {
			if (buffer.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(f);
		}
		return buffer.toString();
	}

	/**
	 * 将单精度浮点数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(float[] array, CharSequence sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (float f : array) {
			if (buffer.length() > 0 && sign != null && sign.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(f);
		}
		return buffer.toString();
	}

	/**
	 * 将双精度浮点数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @return 连接后的字符串
	 */
	public static String join(double[] array) {
		return join(array, null);
	}

	/**
	 * 将双精度浮点数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(double[] array, char sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (double d : array) {
			if (buffer.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(d);
		}
		return buffer.toString();
	}

	/**
	 * 将双精度浮点数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(double[] array, CharSequence sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (double d : array) {
			if (buffer.length() > 0 && sign != null && sign.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(d);
		}
		return buffer.toString();
	}

	/**
	 * 将长整型数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @return 连接后的字符串
	 */
	public static String join(long[] array) {
		return join(array, null);
	}

	/**
	 * 将长整型数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(long[] array, char sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (long l : array) {
			if (buffer.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(l);
		}
		return buffer.toString();
	}

	/**
	 * 将长整型数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(long[] array, CharSequence sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (long l : array) {
			if (buffer.length() > 0 && sign != null && sign.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(l);
		}
		return buffer.toString();
	}

	/**
	 * 将真假数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @return 连接后的字符串
	 */
	public static String join(boolean[] array) {
		return join(array, null);
	}

	/**
	 * 将真假数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(boolean[] array, char sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (boolean b : array) {
			if (buffer.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(b);
		}
		return buffer.toString();
	}

	/**
	 * 将真假数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(boolean[] array, CharSequence sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (boolean b : array) {
			if (buffer.length() > 0 && sign != null && sign.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(b);
		}
		return buffer.toString();
	}

	/**
	 * 将对象数组链接成字符串
	 * 
	 * @param objects
	 *            对象数组
	 * @return 字符串
	 */
	public static String join(Object[] array) {
		return join(array, null);
	}

	/**
	 * 将对象数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(Object[] array, char sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (Object o : array) {
			if (buffer.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(o);
		}
		return buffer.toString();
	}

	/**
	 * 将对象数组链接成字符串
	 * 
	 * @param array
	 *            对象数组
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(Object[] array, CharSequence sign) {
		if (array == null || array.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (Object o : array) {
			if (buffer.length() > 0 && sign != null && sign.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(o);
		}
		return buffer.toString();
	}

	/**
	 * 将对象集合链接成字符串
	 * 
	 * @param collection
	 *            对象集合
	 * @return 字符串
	 */
	public static String join(Collection<?> collection) {
		return join(collection, null);
	}

	/**
	 * 将对象集合链接成字符串
	 * 
	 * @param collection
	 *            对象集合
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(Collection<?> collection, char sign) {
		if (collection == null || collection.isEmpty()) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (Object o : collection) {
			if (buffer.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(o);
		}
		return buffer.toString();
	}

	/**
	 * 将对象集合链接成字符串
	 * 
	 * @param collection
	 *            对象集合
	 * @param sign
	 *            链接标记
	 * @return 连接后的字符串
	 */
	public static String join(Collection<?> collection, CharSequence sign) {
		if (collection == null || collection.isEmpty()) {
			return EMPTY_STRING;
		}
		StringBuilder buffer = new StringBuilder();
		for (Object o : collection) {
			if (buffer.length() > 0 && sign != null && sign.length() > 0) {
				buffer.append(sign);
			}
			buffer.append(o);
		}
		return buffer.toString();
	}

	/**
	 * 判断字符串是否为列表格式；如[1,3]
	 * 
	 * @param source
	 *            源字符串
	 * @return true/false
	 */
	public static boolean isList(CharSequence source) {
		if (source == null || source.length() == 0) {
			return false;
		}
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			if (c == ' ') {
				continue;
			} else if (c != '[') {
				return false;
			}
			break;
		}
		for (int i = source.length() - 1; i > -1; i--) {
			char c = source.charAt(i);
			if (c == ' ') {
				continue;
			} else if (c != ']') {
				return false;
			}
			break;
		}
		return true;
	}

	/**
	 * 将字符串转换成列表对象
	 * 
	 * @param source
	 *            源字符串
	 * @return 对象列表
	 */
	public static List<?> toList(CharSequence source) {
		int skip = 0;
		StringBuilder buffer = new StringBuilder();
		List<StringBuilder> buffers = new LinkedList<StringBuilder>();
		for (int i = 1; i < source.length() - 1; i++) {
			char c = source.charAt(i);
			if (c == ' ') {
				continue;
			} else if (c == ',' && skip == 0) {
				buffers.add(buffer);
				buffer = new StringBuilder();
			} else {
				buffer.append(c);
				if (c == '[') {
					skip++;
				} else if (c == ']') {
					skip--;
				}
			}
		}
		buffers.add(buffer);
		List<Object> list = new ArrayList<Object>(buffers.size());
		for (StringBuilder b : buffers) {
			if (b.length() == 0) {
				list.add(null);
			} else {
				list.add(isList(b) ? toList(b) : b.toString());
			}
		}
		return list;
	}

	/**
	 * 将字符串表达式转换成键/值对
	 * 
	 * 表达式格式为：多个条件使用“,”号隔开；如果参数值为多个值，则使用“[]”包围，并且每个值使用“,”号隔开。
	 * 
	 * @param expression
	 *            字符串表达式
	 * @return 键/值对
	 */
	public static Map<String, Object> toMap(String... expressions) {
		if (isEmpty(expressions)) {
			return new HashMap<String, Object>(0);
		}
		List<StringBuilder> buffers = new LinkedList<StringBuilder>();
		for (String expression : expressions) {
			int skip = 0;
			StringBuilder buffer = new StringBuilder();
			for (int i = 0; i < expression.length(); i++) {
				char c = expression.charAt(i);
				if (c == ',' && skip == 0) {
					buffers.add(buffer);
					buffer = new StringBuilder();
				} else {
					buffer.append(c);
					if (c == '(' || c == '[') {
						skip++;
					} else if (c == ')' || c == ']') {
						skip--;
					}
				}
			}
			buffers.add(buffer);
		}
		Map<String, Object> map = new HashMap<String, Object>(buffers.size());
		for (StringBuilder buffer : buffers) {
			if (buffer.length() == 0) {
				continue;
			}
			int split = buffer.indexOf("=");
			String key = split < 0 ? buffer.toString().trim() : buffer.substring(0, split).trim();
			if (key.isEmpty()) {
				continue;
			}
			String value = split < 0 ? null : buffer.substring(split + 1).trim();
			map.put(key, isList(value) ? toList(value) : isEmpty(value) ? null : value);
		}
		return map;
	}

	/**
	 * 字符串匹配，不支持正则表达式匹配，多个表达式之间使用“,”号隔开（*：通配,-：排除），如果使用排除则优先生效
	 * 
	 * @param source
	 *            源字符串
	 * @param pattern
	 *            匹配模式
	 * @return true/false
	 */
	public static boolean matches(String source, String pattern) {
		if (source == null || source.isEmpty() || pattern == null || pattern.isEmpty()) {
			return false;
		} else if (pattern.length() == 1 && pattern.charAt(0) == '*') {
			return true;
		}
		int matches = 0;
		boolean notall = true;
		for (String setion : split(pattern, ',')) {
			if (setion.isEmpty()) {
				continue;
			}
			int index = -1;
			boolean matched = true;
			boolean not = setion.charAt(0) == '-';
			if (notall && !not) {
				notall = false;
			}
			String[] signs = split(setion, '*', not ? 1 : 0);
			for (String sign : signs) {
				if (sign.isEmpty()) {
					continue;
				}
				if ((index = source.indexOf(sign, index + 1)) < 0) {
					matched = false;
					break;
				}
			}
			if (matched && source.length() > index + signs[signs.length - 1].length()
					&& setion.charAt(setion.length() - 1) != '*') {
				matched = false;
			}
			if (matched) {
				if (not) {
					return false;
				}
				matches++;
			}
		}
		return (notall && matches == 0) || (!notall && matches > 0);
	}

	/**
	 * 获取实际路径 “./”表示当前路径、“../”表示当前路径上一级目录
	 * 
	 * @param path
	 *            路径
	 * @return 实际路径
	 */
	public static String getRealPath(String path) {
		if (isEmpty(path)) {
			return CURRENT_PATH;
		} else if (path.startsWith("./")) {
			return new File(CURRENT_PATH, path.substring(1)).getPath();
		} else if (path.startsWith("../")) {
			int count = count(path, "../");
			File _path = new File(CURRENT_PATH);
			for (int i = 0; i < count; i++) {
				File parent = _path.getParentFile();
				if (parent == null) {
					break;
				}
				_path = parent;
			}
			return new File(_path, path.substring(count * 3)).getPath();
		}
		int index = path.indexOf(':');
		if (index > 0 && path.substring(0, index).toLowerCase().equals("classpath")) {
			URL url = String.class.getClassLoader().getResource(path.substring(index + 1));
			if (url == null) {
				throw new RuntimeException("File not found:" + path);
			}
			return url.getFile();
		}
		return path;
	}

	/**
	 * 将驼峰式字符串以“_”号进行拆分
	 * 
	 * @param source
	 *            被拆分源字符串
	 * @return 拆分后字符串
	 */
	public static String splitHumpString(String source) {
		return splitHumpString(source, false);
	}

	/**
	 * 将驼峰式字符串以“_”号进行拆分
	 * 
	 * @param source
	 *            被拆分源字符串
	 * @param capital
	 *            是否大写
	 * @return 拆分后字符串
	 */
	public static String splitHumpString(String source, boolean capital) {
		if (isEmpty(source) || isEmpty(source)) {
			return source;
		}
		StringBuilder chars = new StringBuilder();
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i > 0) {
					chars.append('_');
				}
				chars.append(capital ? c : Character.toLowerCase(c));
			} else {
				chars.append(capital ? Character.toUpperCase(c) : c);
			}
		}
		return chars.toString();
	}

	/**
	 * 获取对象格式化字符串形式
	 * 
	 * @param object
	 *            原始对象
	 * @return 对象格式化字符串
	 */
	public static String format(Object object) {
		if (object instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) object;
			if (map.isEmpty()) {
				return map.toString();
			}
			Map<Object, Object> converted = new HashMap<Object, Object>(map.size());
			for (Entry<?, ?> entry : map.entrySet()) {
				converted.put(entry.getKey(), format(entry.getValue()));
			}
			return converted.toString();
		} else if (object instanceof Collection) {
			Collection<?> collection = (Collection<?>) object;
			if (collection.isEmpty()) {
				return collection.toString();
			}
			List<Object> converted = new ArrayList<Object>(collection.size());
			for (Object source : collection) {
				converted.add(format(source));
			}
			return converted.toString();
		} else if (object instanceof Object[]) {
			Object[] array = (Object[]) object;
			if (array.length == 0) {
				return Arrays.toString(array);
			}
			List<Object> converted = new ArrayList<Object>(array.length);
			for (Object source : array) {
				converted.add(format(source));
			}
			return converted.toString();
		} else if (object instanceof int[]) {
			return Arrays.toString((int[]) object);
		} else if (object instanceof short[]) {
			return Arrays.toString((short[]) object);
		} else if (object instanceof float[]) {
			return Arrays.toString((float[]) object);
		} else if (object instanceof double[]) {
			return Arrays.toString((double[]) object);
		} else if (object instanceof long[]) {
			return Arrays.toString((long[]) object);
		} else if (object instanceof char[]) {
			return Arrays.toString((char[]) object);
		} else if (object instanceof boolean[]) {
			return Arrays.toString((boolean[]) object);
		}
		return toString(object);
	}

	/**
	 * 特殊字符转义
	 * 
	 * @param source
	 *            源字符串
	 * @return 转义后字符串
	 */
	public static String escape(CharSequence source) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			switch (c) {
			case '\'':
				buffer.append("\\\"");
				break;
			case '\"':
				buffer.append("\\\"");
				break;
			case '\\':
				buffer.append("\\\\");
				break;
			case '\b':
				buffer.append("\\b");
				break;
			case '\f':
				buffer.append("\\f");
				break;
			case '\n':
				buffer.append("\\n");
				break;
			case '\r':
				buffer.append("\\r");
				break;
			case '\t':
				buffer.append("\\t");
				break;
			default:
				if (!((c >= 0 && c <= 31) || c == 127)) {
					buffer.append(c);
				}
				break;
			}
		}
		return buffer.toString();
	}

}
