package ars.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import ars.util.Nfile;

/**
 * IO流操作工具类
 * 
 * @author yongqiangwu
 * 
 */
public final class Streams {
	/**
	 * 空字节数组
	 */
	public static final byte[] EMPTY_ARRAY = new byte[0];

	/**
	 * 默认数据缓冲区大小
	 */
	public static final int DEFAULT_BUFFER_SIZE = 2048;

	/**
	 * 判断对象是否为流数据
	 * 
	 * @param object
	 *            对象实例
	 * @return true/false
	 */
	public static boolean isStream(Object object) {
		return object instanceof byte[] || object instanceof File || object instanceof Nfile
				|| object instanceof InputStream || object instanceof ReadableByteChannel;
	}

	/**
	 * 对象序列化，将对象转换成字节数组
	 * 
	 * @param object
	 *            需要转换的对象
	 * @return 字节数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public static byte[] serialize(Serializable object) throws IOException {
		if (object == null) {
			return null;
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(bos);
		try {
			os.writeObject(object);
		} finally {
			os.close();
		}
		return bos.toByteArray();
	}

	/**
	 * 对象反序列化，将对象的字节数组转换成对象
	 * 
	 * @param bytes
	 *            字节数组
	 * @return 对象
	 * @throws IOException
	 *             IO操作异常
	 * @throws ClassNotFoundException
	 *             类不存在异常
	 */
	public static Serializable deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		return bytes == null || bytes.length == 0 ? null
				: (Serializable) new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
	}

	/**
	 * 对象反序列化，从数据输入流中获取对象的字节数据，并将字节数据转换成对象
	 * 
	 * @param input
	 *            输入流
	 * @return 对象
	 * @throws IOException
	 *             IO操作异常
	 * @throws ClassNotFoundException
	 *             类不存在异常
	 */
	public static Serializable deserialize(InputStream input) throws IOException, ClassNotFoundException {
		return (Serializable) new ObjectInputStream(input).readObject();
	}

	/**
	 * 对象反序列化，从套节字通道中获取对象的字节数据，并将字节数据转换成对象
	 * 
	 * @param channel
	 *            套节字连接通道
	 * @return 对象
	 * @throws IOException
	 *             IO操作异常
	 * @throws ClassNotFoundException
	 *             类不存在异常
	 */
	public static Serializable deserialize(ReadableByteChannel channel) throws IOException, ClassNotFoundException {
		int n = 0;
		PipedOutputStream pos = new PipedOutputStream();
		PipedInputStream pis = new PipedInputStream(pos);
		ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
		try {
			while ((n = channel.read(buffer)) > 0) {
				pos.write(buffer.array(), 0, n);
			}
			return (Serializable) new ObjectInputStream(pis).readObject();
		} finally {
			pis.close();
		}
	}

	/**
	 * 从文件中获取字节
	 * 
	 * @param file
	 *            文件对象
	 * @return 字节数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public static byte[] getBytes(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		try {
			return getBytes(is);
		} finally {
			is.close();
		}
	}

	/**
	 * 从输入流中获取字节
	 * 
	 * @param input
	 *            输入流
	 * @return 字节数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public static byte[] getBytes(InputStream input) throws IOException {
		int n = 0;
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((n = input.read(buffer)) > 0) {
			bos.write(buffer, 0, n);
		}
		return bos.toByteArray();
	}

	/**
	 * 从套节字通道中获取字节
	 * 
	 * @param channel
	 *            套接字读取通道
	 * @return 字节数组
	 * @throws IOException
	 *             IO操作异常
	 */
	public static byte[] getBytes(ReadableByteChannel channel) throws IOException {
		int n = 0;
		ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((n = channel.read(buffer)) > 0) {
			bos.write(buffer.array(), 0, n);
		}
		return bos.toByteArray();
	}

	/**
	 * 将字节数据追加到文件中
	 * 
	 * @param source
	 *            源字节数组
	 * @param target
	 *            目标文件对象
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void append(byte[] source, File target) throws IOException {
		File path = target.getParentFile();
		if (path != null && !path.exists()) {
			path.mkdirs();
		}
		FileOutputStream os = new FileOutputStream(target, true);
		try {
			os.write(source);
		} finally {
			os.close();
		}
	}

	/**
	 * 将源文件数据追加到目标文件中
	 * 
	 * @param source
	 *            源文件
	 * @param target
	 *            目标文件
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void append(File source, File target) throws IOException {
		if (!source.exists()) {
			return;
		}
		File path = target.getParentFile();
		if (path != null && !path.exists()) {
			path.mkdirs();
		}
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(target, true);
			in = fis.getChannel();
			out = fos.getChannel();
			in.transferTo(0, in.size(), out);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} finally {
				try {
					if (fos != null) {
						fos.close();
					}
				} finally {
					try {
						if (in != null) {
							in.close();
						}
					} finally {
						if (fis != null) {
							fis.close();
						}
					}
				}
			}
		}
	}

	/**
	 * 将文件数据追加到文件
	 * 
	 * @param source
	 *            源文件
	 * @param target
	 *            目标文件
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void append(Nfile source, File target) throws IOException {
		if (source.isFile()) {
			append(source.getFile(), target);
		} else {
			InputStream is = source.getInputStream();
			try {
				append(is, target);
			} finally {
				is.close();
			}
		}
	}

	/**
	 * 将输入流中的数据追加到文件
	 * 
	 * @param source
	 *            源输入流
	 * @param target
	 *            目标文件对象
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void append(InputStream source, File target) throws IOException {
		File path = target.getParentFile();
		if (path != null && !path.exists()) {
			path.mkdirs();
		}
		OutputStream os = new FileOutputStream(target, true);
		try {
			write(source, os);
		} finally {
			os.close();
		}
	}

	/**
	 * 将套接字读取通道中的数据追加到文件
	 * 
	 * @param source
	 *            套节字输入流通道
	 * @param target
	 *            目标文件对象
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void append(ReadableByteChannel source, File target) throws IOException {
		File path = target.getParentFile();
		if (path != null && !path.exists()) {
			path.mkdirs();
		}
		OutputStream os = new FileOutputStream(target, true);
		try {
			write(source, os);
		} finally {
			os.close();
		}
	}

	/**
	 * 将字节数据写入到文件
	 * 
	 * @param source
	 *            源字节数据
	 * @param target
	 *            目标文件对象
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void write(byte[] source, File target) throws IOException {
		File path = target.getParentFile();
		if (path != null && !path.exists()) {
			path.mkdirs();
		}
		FileOutputStream os = new FileOutputStream(target);
		try {
			os.write(source);
		} finally {
			os.close();
		}
	}

	/**
	 * 将文件数据写入到文件
	 * 
	 * @param source
	 *            源文件
	 * @param target
	 *            目标文件
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void write(File source, File target) throws IOException {
		if (!source.exists()) {
			return;
		}
		File path = target.getParentFile();
		if (path != null && !path.exists()) {
			path.mkdirs();
		}
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(target);
			in = fis.getChannel();
			out = fos.getChannel();
			in.transferTo(0, in.size(), out);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} finally {
				try {
					if (fos != null) {
						fos.close();
					}
				} finally {
					try {
						if (in != null) {
							in.close();
						}
					} finally {
						if (fis != null) {
							fis.close();
						}
					}
				}
			}
		}
	}

	/**
	 * 将文件数据写入到输出流
	 * 
	 * @param source
	 *            源文件对象
	 * @param target
	 *            目标输出流
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void write(File source, OutputStream target) throws IOException {
		InputStream is = new FileInputStream(source);
		try {
			write(is, target);
		} finally {
			is.close();
		}
	}

	/**
	 * 将文件数据写入到套接字写通道中
	 * 
	 * @param source
	 *            源文件对象
	 * @param target
	 *            目标输出流
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void write(File source, WritableByteChannel target) throws IOException {
		InputStream is = new FileInputStream(source);
		try {
			write(is, target);
		} finally {
			is.close();
		}
	}

	/**
	 * 将文件数据写入到文件
	 * 
	 * @param source
	 *            源文件
	 * @param target
	 *            目标文件
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void write(Nfile source, File target) throws IOException {
		if (source.isFile()) {
			write(source.getFile(), target);
		} else {
			InputStream is = source.getInputStream();
			try {
				write(is, target);
			} finally {
				is.close();
			}
		}
	}

	/**
	 * 将文件数据写入到输出流
	 * 
	 * @param source
	 *            源文件对象
	 * @param target
	 *            目标输出流
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void write(Nfile source, OutputStream target) throws IOException {
		InputStream is = source.getInputStream();
		try {
			write(is, target);
		} finally {
			is.close();
		}
	}

	/**
	 * 将文件数据写入到套接字写通道中
	 * 
	 * @param source
	 *            源文件对象
	 * @param target
	 *            目标输出流
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void write(Nfile source, WritableByteChannel target) throws IOException {
		InputStream is = source.getInputStream();
		try {
			write(is, target);
		} finally {
			is.close();
		}
	}

	/**
	 * 将输入流中的数据写入文件
	 * 
	 * @param source
	 *            源输入流
	 * @param target
	 *            目标文件对象
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void write(InputStream source, File target) throws IOException {
		File path = target.getParentFile();
		if (path != null && !path.exists()) {
			path.mkdirs();
		}
		OutputStream os = new FileOutputStream(target);
		try {
			write(source, os);
		} finally {
			os.close();
		}
	}

	/**
	 * 将输入流中的数据写入输出流
	 * 
	 * @param source
	 *            源输入流
	 * @param target
	 *            目标输出流
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void write(InputStream source, OutputStream target) throws IOException {
		int n = 0;
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		while ((n = source.read(buffer)) > 0) {
			target.write(buffer, 0, n);
		}
	}

	/**
	 * 将输入流中的数据写入套接字写通道
	 * 
	 * @param source
	 *            源输入流
	 * @param target
	 *            目标输出流
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void write(InputStream source, WritableByteChannel target) throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		while (source.read(buffer) > 0) {
			target.write(ByteBuffer.wrap(buffer));
		}
	}

	/**
	 * 将套接字读取通道中的数据写入文件
	 * 
	 * @param source
	 *            源套节字输入流通道
	 * @param target
	 *            目标文件对象
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void write(ReadableByteChannel source, File target) throws IOException {
		File path = target.getParentFile();
		if (path != null && !path.exists()) {
			path.mkdirs();
		}
		OutputStream os = new FileOutputStream(target);
		try {
			write(source, os);
		} finally {
			os.close();
		}
	}

	/**
	 * 将套接字读取通道中的数据写入输出流
	 * 
	 * @param source
	 *            源套节字输入流通道
	 * @param target
	 *            目标输出流
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void write(ReadableByteChannel source, OutputStream target) throws IOException {
		int n = 0;
		ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
		while ((n = source.read(buffer)) > 0) {
			target.write(buffer.array(), 0, n);
		}
	}

	/**
	 * 将套接字读通道中的数据写入套接字写通道中
	 * 
	 * @param source
	 *            源套接字读通道
	 * @param target
	 *            目标套接字写通道
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void write(ReadableByteChannel source, WritableByteChannel target) throws IOException {
		int n = 0;
		ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
		while ((n = source.read(buffer)) > 0) {
			target.write(ByteBuffer.wrap(buffer.array(), 0, n));
		}
	}

}
