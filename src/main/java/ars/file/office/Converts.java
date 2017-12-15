package ars.file.office;

import java.io.File;
import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;

import ars.util.Files;
import ars.util.Strings;

/**
 * 文件转换工具类
 * 
 * @author yongqiangwu
 * 
 */
public final class Converts {
	private static String openOfficeHost = Strings.DEFAULT_LOCALHOST_ADDRESS;
	private static int openOfficePort = SocketOpenOfficeConnection.DEFAULT_PORT;

	public static String getOpenOfficeHost() {
		return openOfficeHost;
	}

	public static void setOpenOfficeHost(String openOfficeHost) {
		Converts.openOfficeHost = openOfficeHost;
	}

	public static int getOpenOfficePort() {
		return openOfficePort;
	}

	public static void setOpenOfficePort(int openOfficePort) {
		Converts.openOfficePort = openOfficePort;
	}

	/**
	 * 将文件转换成swf文件
	 * 
	 * @param input
	 *            输入文件
	 * @param output
	 *            输出文件
	 * @param handle
	 *            转换操作
	 * @throws IOException
	 *             IO操作异常
	 */
	private static void file2swf(File input, File output, String handle) throws IOException {
		if (input == null) {
			throw new IllegalArgumentException("Illegal input:" + input);
		}
		if (output == null) {
			throw new IllegalArgumentException("Illegal output:" + output);
		}
		if (handle == null) {
			throw new IllegalArgumentException("Illegal handle:" + handle);
		}
		String command = new StringBuilder(handle).append(" ").append(input.getPath()).append(" -o ")
				.append(output.getPath()).append(" -T 9").toString();
		try {
			Runtime.getRuntime().exec(command).waitFor();
		} catch (InterruptedException e) {
		}
	}

	/**
	 * 将文件转换成pdf文件
	 * 
	 * @param file
	 *            源文件
	 * @return 目标文件
	 * @throws IOException
	 *             IO操作异常
	 */
	public static File file2pdf(File file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("Illegal file:" + file);
		}
		if (file.getName().toLowerCase().endsWith(".pdf")) {
			return file;
		}
		File output = new File(file.getPath() + ".temp.pdf");
		file2pdf(file, output);
		return output;
	}

	/**
	 * 将文件转换成pdf文件
	 * 
	 * @param input
	 *            源文件
	 * @param output
	 *            目标文件
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void file2pdf(File input, File output) throws IOException {
		if (input == null) {
			throw new IllegalArgumentException("Illegal input:" + input);
		}
		if (output == null) {
			throw new IllegalArgumentException("Illegal output:" + output);
		}
		if (!output.exists()) {
			synchronized (output.getPath().intern()) {
				if (!output.exists()) {
					OpenOfficeConnection connection = new SocketOpenOfficeConnection(openOfficeHost, openOfficePort);
					connection.connect();
					try {
						DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
						converter.convert(input, output);
					} finally {
						connection.disconnect();
					}
				}
			}
		}
	}

	/**
	 * 将pdf文件转换成swf文件
	 * 
	 * @param input
	 *            输入文件
	 * @param output
	 *            输出文件
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void pdf2swf(File input, File output) throws IOException {
		file2swf(input, output, "pdf2swf");
	}

	/**
	 * 将gif文件转换成swf文件
	 * 
	 * @param input
	 *            输入文件
	 * @param output
	 *            输出文件
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void gif2swf(File input, File output) throws IOException {
		file2swf(input, output, "gif2swf");
	}

	/**
	 * 将png文件转换成swf文件
	 * 
	 * @param input
	 *            输入文件
	 * @param output
	 *            输出文件
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void png2swf(File input, File output) throws IOException {
		file2swf(input, output, "png2swf");
	}

	/**
	 * 将jpeg文件转换成swf文件
	 * 
	 * @param input
	 *            输入文件
	 * @param output
	 *            输出文件
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void jpeg2swf(File input, File output) throws IOException {
		file2swf(input, output, "jpeg2swf");
	}

	/**
	 * 将wav文件转换成swf文件
	 * 
	 * @param input
	 *            输入文件
	 * @param output
	 *            输出文件
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void wav2swf(File input, File output) throws IOException {
		file2swf(input, output, "wav2swf");
	}

	/**
	 * 将ttf文件转换成swf文件
	 * 
	 * @param input
	 *            输入文件
	 * @param output
	 *            输出文件
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void font2swf(File input, File output) throws IOException {
		file2swf(input, output, "font2swf");
	}

	/**
	 * 将txt文件转换成swf文件
	 * 
	 * @param input
	 *            输入文件
	 * @param output
	 *            输出文件
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void txt2swf(File input, File output) throws IOException {
		String encoding = Files.getEncoding(input);
		String charset = Charset.defaultCharset().name();
		if (charset.equalsIgnoreCase(encoding)) {
			File pdf = file2pdf(input);
			try {
				file2swf(pdf, output);
			} finally {
				pdf.delete();
			}
		} else {
			File temp = new File(input.getPath() + ".temp.txt");
			OutputStream os = new FileOutputStream(temp);
			try {
				IOUtils.write(FileUtils.readFileToString(input, encoding), os, charset);
			} finally {
				os.close();
			}
			File pdf = file2pdf(temp);
			try {
				file2swf(pdf, output);
			} finally {
				pdf.delete();
				temp.delete();
			}
		}
	}

	/**
	 * 将文件转换成SWF文件
	 * 
	 * @param input
	 *            输入文件
	 * @param output
	 *            输出文件
	 * @throws IOException
	 *             IO操作异常
	 */
	public static void file2swf(File input, File output) throws IOException {
		String name = input.getName().toLowerCase();
		if (name.endsWith(".jpg") || name.endsWith(".jpe") || name.endsWith(".jpeg")) {
			jpeg2swf(input, output);
		} else if (name.endsWith(".png")) {
			png2swf(input, output);
		} else if (name.endsWith(".pdf")) {
			pdf2swf(input, output);
		} else if (name.endsWith(".gif")) {
			gif2swf(input, output);
		} else if (name.endsWith(".wav")) {
			wav2swf(input, output);
		} else if (name.endsWith(".ttf")) {
			font2swf(input, output);
		} else if (name.endsWith(".txt")) {
			txt2swf(input, output);
		} else {
			File pdf = file2pdf(input);
			try {
				file2swf(pdf, output);
			} finally {
				pdf.delete();
			}
		}
	}

	/**
	 * 将SVG文件转换成PDF文件
	 * 
	 * @param source
	 *            SVG文件
	 * @param target
	 *            PDF文件
	 * @throws IOException
	 *             IO操作异常
	 * @throws TranscoderException
	 *             转换异常
	 */
	public static void svg2pdf(File source, File target) throws IOException, TranscoderException {
		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(target);
			svg2pdf(input, output);
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} finally {
				if (input != null) {
					input.close();
				}
			}
		}
	}

	/**
	 * 将SVG输入流转换并写入到PDF输出流
	 * 
	 * @param reader
	 *            SVG字符流
	 * @param output
	 *            PDF输出流
	 * @throws TranscoderException
	 *             转换异常
	 */
	public static void svg2pdf(Reader reader, OutputStream output) throws TranscoderException {
		Transcoder transcoder = new PDFTranscoder();
		transcoder.transcode(new TranscoderInput(reader), new TranscoderOutput(output));
	}

	/**
	 * 将SVG输入流转换并写入到PDF输出流
	 * 
	 * @param input
	 *            SVG输入流
	 * @param output
	 *            PDF输出流
	 * @throws TranscoderException
	 *             转换异常
	 */
	public static void svg2pdf(InputStream input, OutputStream output) throws TranscoderException {
		Transcoder transcoder = new PDFTranscoder();
		transcoder.transcode(new TranscoderInput(input), new TranscoderOutput(output));
	}

	/**
	 * 将SVG文件转换成PNG文件
	 * 
	 * @param source
	 *            SVG文件
	 * @param target
	 *            PNG文件
	 * @throws IOException
	 *             IO操作异常
	 * @throws TranscoderException
	 *             转换异常
	 */
	public static void svg2png(File source, File target) throws IOException, TranscoderException {
		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(target);
			svg2png(input, output);
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} finally {
				if (input != null) {
					input.close();
				}
			}
		}
	}

	/**
	 * 将SVG输入流转换并写入到PNG输出流
	 * 
	 * @param reader
	 *            SVG字符流
	 * @param output
	 *            PNG输出流
	 * @throws TranscoderException
	 *             转换异常
	 */
	public static void svg2png(Reader reader, OutputStream output) throws TranscoderException {
		Transcoder transcoder = new PNGTranscoder();
		transcoder.transcode(new TranscoderInput(reader), new TranscoderOutput(output));
	}

	/**
	 * 将SVG输入流转换并写入到PNG输出流
	 * 
	 * @param input
	 *            SVG输入流
	 * @param output
	 *            PNG输出流
	 * @throws TranscoderException
	 *             转换异常
	 */
	public static void svg2png(InputStream input, OutputStream output) throws TranscoderException {
		Transcoder transcoder = new PNGTranscoder();
		transcoder.transcode(new TranscoderInput(input), new TranscoderOutput(output));
	}

	/**
	 * 将SVG文件转换成JPEG文件
	 * 
	 * @param source
	 *            SVG文件
	 * @param target
	 *            JPEG文件
	 * @throws IOException
	 *             IO操作异常
	 * @throws TranscoderException
	 *             转换异常
	 */
	public static void svg2jpeg(File source, File target) throws IOException, TranscoderException {
		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(target);
			svg2jpeg(input, output);
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} finally {
				if (input != null) {
					input.close();
				}
			}
		}
	}

	/**
	 * 将SVG输入流转换并写入到JPEG输出流
	 * 
	 * @param reader
	 *            SVG字符流
	 * @param output
	 *            JPEG输出流
	 * @throws TranscoderException
	 *             转换异常
	 */
	public static void svg2jpeg(Reader reader, OutputStream output) throws TranscoderException {
		Transcoder transcoder = new JPEGTranscoder();
		transcoder.transcode(new TranscoderInput(reader), new TranscoderOutput(output));
	}

	/**
	 * 将SVG输入流转换并写入到JPEG输出流
	 * 
	 * @param input
	 *            SVG输入流
	 * @param output
	 *            JPEG输出流
	 * @throws TranscoderException
	 *             转换异常
	 */
	public static void svg2jpeg(InputStream input, OutputStream output) throws TranscoderException {
		Transcoder transcoder = new JPEGTranscoder();
		transcoder.transcode(new TranscoderInput(input), new TranscoderOutput(output));
	}

}