package ars.file.office;

import java.io.File;
import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.DocumentException;
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
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.ITextFontResolver;

import ars.util.Files;
import ars.util.Strings;

/**
 * 文件转换工具类
 *
 * @author wuyongqiang
 */
public final class Converts {
    private static String openOfficeHost;
    private static int openOfficePort;

    private Converts() {

    }

    public static String getOpenOfficeHost() {
        if (openOfficeHost == null) {
            synchronized (Converts.class) {
                if (openOfficeHost == null) {
                    openOfficeHost = Strings.DEFAULT_LOCALHOST_ADDRESS;
                }
            }
        }
        return openOfficeHost;
    }

    public static void setOpenOfficeHost(String openOfficeHost) {
        if (openOfficeHost == null) {
            throw new IllegalArgumentException("Host must not be null");
        }
        if (Converts.openOfficeHost != null) {
            throw new IllegalStateException("Host already initialized");
        }
        synchronized (Converts.class) {
            if (Converts.openOfficeHost == null) {
                Converts.openOfficeHost = openOfficeHost;
            }
        }
    }

    public static int getOpenOfficePort() {
        if (openOfficePort < 1) {
            synchronized (Converts.class) {
                if (openOfficePort < 1) {
                    openOfficePort = SocketOpenOfficeConnection.DEFAULT_PORT;
                }
            }
        }
        return openOfficePort;
    }

    public static void setOpenOfficePort(int openOfficePort) {
        if (openOfficePort < 1) {
            throw new IllegalArgumentException("Port must not be less than 1, got " + openOfficePort);
        }
        if (Converts.openOfficePort > 0) {
            throw new IllegalStateException("Port already initialized");
        }
        synchronized (Converts.class) {
            if (Converts.openOfficePort < 1) {
                Converts.openOfficePort = openOfficePort;
            }
        }
    }

    /**
     * 将文件转换成swf文件
     *
     * @param input  输入文件
     * @param output 输出文件
     * @param handle 转换操作
     * @throws IOException IO操作异常
     */
    private static void file2swf(File input, File output, String handle) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("Input file must not be null");
        }
        if (output == null) {
            throw new IllegalArgumentException("Output file must not be null");
        }
        if (handle == null) {
            throw new IllegalArgumentException("Handle must not be null");
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
     * @param file 源文件
     * @return 目标文件
     * @throws IOException IO操作异常
     */
    public static File file2pdf(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File must not be null");
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
     * @param input  源文件
     * @param output 目标文件
     * @throws IOException IO操作异常
     */
    public static void file2pdf(File input, File output) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("Input file must not be null");
        }
        if (output == null) {
            throw new IllegalArgumentException("Output file must not be null");
        }
        File path = output.getParentFile();
        if (path != null && !path.exists()) {
            path.mkdirs();
        }
        OpenOfficeConnection connection = new SocketOpenOfficeConnection(getOpenOfficeHost(), getOpenOfficePort());
        connection.connect();
        try {
            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
            converter.convert(input, output);
        } finally {
            connection.disconnect();
        }
    }

    /**
     * 将pdf文件转换成swf文件
     *
     * @param input  输入文件
     * @param output 输出文件
     * @throws IOException IO操作异常
     */
    public static void pdf2swf(File input, File output) throws IOException {
        file2swf(input, output, "pdf2swf");
    }

    /**
     * 将gif文件转换成swf文件
     *
     * @param input  输入文件
     * @param output 输出文件
     * @throws IOException IO操作异常
     */
    public static void gif2swf(File input, File output) throws IOException {
        file2swf(input, output, "gif2swf");
    }

    /**
     * 将png文件转换成swf文件
     *
     * @param input  输入文件
     * @param output 输出文件
     * @throws IOException IO操作异常
     */
    public static void png2swf(File input, File output) throws IOException {
        file2swf(input, output, "png2swf");
    }

    /**
     * 将jpeg文件转换成swf文件
     *
     * @param input  输入文件
     * @param output 输出文件
     * @throws IOException IO操作异常
     */
    public static void jpeg2swf(File input, File output) throws IOException {
        file2swf(input, output, "jpeg2swf");
    }

    /**
     * 将wav文件转换成swf文件
     *
     * @param input  输入文件
     * @param output 输出文件
     * @throws IOException IO操作异常
     */
    public static void wav2swf(File input, File output) throws IOException {
        file2swf(input, output, "wav2swf");
    }

    /**
     * 将ttf文件转换成swf文件
     *
     * @param input  输入文件
     * @param output 输出文件
     * @throws IOException IO操作异常
     */
    public static void font2swf(File input, File output) throws IOException {
        file2swf(input, output, "font2swf");
    }

    /**
     * 将txt文件转换成swf文件
     *
     * @param input  输入文件
     * @param output 输出文件
     * @throws IOException IO操作异常
     */
    public static void txt2swf(File input, File output) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("Input file must not be null");
        }
        if (output == null) {
            throw new IllegalArgumentException("Output file must not be null");
        }
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
     * @param input  输入文件
     * @param output 输出文件
     * @throws IOException IO操作异常
     */
    public static void file2swf(File input, File output) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("Input file must not be null");
        }
        if (output == null) {
            throw new IllegalArgumentException("Output file must not be null");
        }
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
     * 将SVG数据转换成PDF文件
     *
     * @param svg    SVG字符串
     * @param target PDF文件
     * @throws IOException         IO操作异常
     * @throws TranscoderException 转换异常
     */
    public static void svg2pdf(String svg, File target) throws IOException, TranscoderException {
        if (svg == null) {
            throw new IllegalArgumentException("Svg must not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target file must not be null");
        }
        File path = target.getParentFile();
        if (path != null && !path.exists()) {
            path.mkdirs();
        }
        OutputStream output = new FileOutputStream(target);
        try {
            svg2pdf(svg, output);
        } finally {
            output.close();
        }
    }

    /**
     * 将SVG数据转换成PDF文件
     *
     * @param svg    SVG字符串
     * @param output PDF文件输出流
     * @throws IOException         IO操作异常
     * @throws TranscoderException 转换异常
     */
    public static void svg2pdf(String svg, OutputStream output) throws IOException, TranscoderException {
        if (svg == null) {
            throw new IllegalArgumentException("Svg must not be null");
        }
        if (output == null) {
            throw new IllegalArgumentException("OutputStream must not be null");
        }
        svg2pdf(new ByteArrayInputStream(svg.getBytes()), output);
    }

    /**
     * 将SVG输入流转换并写入到PDF输出流
     *
     * @param reader SVG字符流
     * @param output PDF输出流
     * @throws TranscoderException 转换异常
     */
    public static void svg2pdf(Reader reader, OutputStream output) throws TranscoderException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader must not be null");
        }
        if (output == null) {
            throw new IllegalArgumentException("OutputStream must not be null");
        }
        Transcoder transcoder = new PDFTranscoder();
        transcoder.transcode(new TranscoderInput(reader), new TranscoderOutput(output));
    }

    /**
     * 将SVG输入流转换并写入到PDF输出流
     *
     * @param input  SVG输入流
     * @param output PDF输出流
     * @throws TranscoderException 转换异常
     */
    public static void svg2pdf(InputStream input, OutputStream output) throws TranscoderException {
        if (input == null) {
            throw new IllegalArgumentException("InputStream must not be null");
        }
        if (output == null) {
            throw new IllegalArgumentException("OutputStream must not be null");
        }
        Transcoder transcoder = new PDFTranscoder();
        transcoder.transcode(new TranscoderInput(input), new TranscoderOutput(output));
    }

    /**
     * 将SVG数据转换成PNG文件
     *
     * @param svg    SVG字符串
     * @param target PNG文件
     * @throws IOException         IO操作异常
     * @throws TranscoderException 转换异常
     */
    public static void svg2png(String svg, File target) throws IOException, TranscoderException {
        if (svg == null) {
            throw new IllegalArgumentException("Svg must not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target file must not be null");
        }
        File path = target.getParentFile();
        if (path != null && !path.exists()) {
            path.mkdirs();
        }
        OutputStream output = new FileOutputStream(target);
        try {
            svg2png(svg, output);
        } finally {
            output.close();
        }
    }

    /**
     * 将SVG数据转换成PNG文件
     *
     * @param svg    SVG字符串
     * @param output PNG文件输出流
     * @throws IOException         IO操作异常
     * @throws TranscoderException 转换异常
     */
    public static void svg2png(String svg, OutputStream output) throws IOException, TranscoderException {
        if (svg == null) {
            throw new IllegalArgumentException("Svg must not be null");
        }
        if (output == null) {
            throw new IllegalArgumentException("OutputStream must not be null");
        }
        svg2png(new ByteArrayInputStream(svg.getBytes()), output);
    }

    /**
     * 将SVG输入流转换并写入到PNG输出流
     *
     * @param reader SVG字符流
     * @param output PNG输出流
     * @throws TranscoderException 转换异常
     */
    public static void svg2png(Reader reader, OutputStream output) throws TranscoderException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader must not be null");
        }
        if (output == null) {
            throw new IllegalArgumentException("OutputStream must not be null");
        }
        Transcoder transcoder = new PNGTranscoder();
        transcoder.transcode(new TranscoderInput(reader), new TranscoderOutput(output));
    }

    /**
     * 将SVG输入流转换并写入到PNG输出流
     *
     * @param input  SVG输入流
     * @param output PNG输出流
     * @throws TranscoderException 转换异常
     */
    public static void svg2png(InputStream input, OutputStream output) throws TranscoderException {
        if (input == null) {
            throw new IllegalArgumentException("InputStream must not be null");
        }
        if (output == null) {
            throw new IllegalArgumentException("OutputStream must not be null");
        }
        Transcoder transcoder = new PNGTranscoder();
        transcoder.transcode(new TranscoderInput(input), new TranscoderOutput(output));
    }

    /**
     * 将SVG数据转换成JPEG文件
     *
     * @param svg    SVG字符串
     * @param target JPEG文件
     * @throws IOException         IO操作异常
     * @throws TranscoderException 转换异常
     */
    public static void svg2jpeg(String svg, File target) throws IOException, TranscoderException {
        if (svg == null) {
            throw new IllegalArgumentException("Svg must not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target file must not be null");
        }
        File path = target.getParentFile();
        if (path != null && !path.exists()) {
            path.mkdirs();
        }
        OutputStream output = new FileOutputStream(target);
        try {
            svg2jpeg(svg, output);
        } finally {
            output.close();
        }
    }

    /**
     * 将SVG数据转换成JPEG文件
     *
     * @param svg    SVG字符串
     * @param output JPEG文件输出流
     * @throws IOException         IO操作异常
     * @throws TranscoderException 转换异常
     */
    public static void svg2jpeg(String svg, OutputStream output) throws IOException, TranscoderException {
        if (svg == null) {
            throw new IllegalArgumentException("Svg must not be null");
        }
        if (output == null) {
            throw new IllegalArgumentException("OutputStream must not be null");
        }
        svg2jpeg(new ByteArrayInputStream(svg.getBytes()), output);
    }

    /**
     * 将SVG输入流转换并写入到JPEG输出流
     *
     * @param reader SVG字符流
     * @param output JPEG输出流
     * @throws TranscoderException 转换异常
     */
    public static void svg2jpeg(Reader reader, OutputStream output) throws TranscoderException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader must not be null");
        }
        if (output == null) {
            throw new IllegalArgumentException("OutputStream must not be null");
        }
        Transcoder transcoder = new JPEGTranscoder();
        transcoder.transcode(new TranscoderInput(reader), new TranscoderOutput(output));
    }

    /**
     * 将SVG输入流转换并写入到JPEG输出流
     *
     * @param input  SVG输入流
     * @param output JPEG输出流
     * @throws TranscoderException 转换异常
     */
    public static void svg2jpeg(InputStream input, OutputStream output) throws TranscoderException {
        if (input == null) {
            throw new IllegalArgumentException("InputStream must not be null");
        }
        if (output == null) {
            throw new IllegalArgumentException("OutputStream must not be null");
        }
        Transcoder transcoder = new JPEGTranscoder();
        transcoder.transcode(new TranscoderInput(input), new TranscoderOutput(output));
    }

    /**
     * 将html转换成PDF文件
     *
     * @param html   Html数据
     * @param target PDF文件
     * @param fonts  样式文件路径数据
     * @throws DocumentException 文档操作异常
     * @throws IOException       IO操作异常
     */
    public static void html2pdf(String html, File target, String... fonts) throws DocumentException, IOException {
        if (html == null) {
            throw new IllegalArgumentException("Html must not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target file must not be null");
        }
        File path = target.getParentFile();
        if (path != null && !path.exists()) {
            path.mkdirs();
        }
        OutputStream output = new FileOutputStream(target);
        try {
            html2pdf(html, output, fonts);
        } finally {
            output.close();
        }
    }

    /**
     * 将html转换成PDF文件
     *
     * @param html   Html数据
     * @param output PDF文件输出流
     * @param fonts  样式文件路径数据
     * @throws DocumentException 文档操作异常
     * @throws IOException       IO操作异常
     */
    public static void html2pdf(String html, OutputStream output, String... fonts)
        throws DocumentException, IOException {
        if (html == null) {
            throw new IllegalArgumentException("Html must not be null");
        }
        if (output == null) {
            throw new IllegalArgumentException("OutputStream must not be null");
        }
        ITextRenderer renderer = new ITextRenderer();
        ChainingReplacedElementFactory chainingReplacedElementFactory = new ChainingReplacedElementFactory();
        chainingReplacedElementFactory.addReplacedElementFactory(new SVGReplacedElementFactory());
        renderer.getSharedContext().setReplacedElementFactory(chainingReplacedElementFactory);
        renderer.setDocumentFromString(html);
        if (fonts != null && fonts.length > 0) {
            ITextFontResolver fontResolver = renderer.getFontResolver();
            for (String font : fonts) {
                fontResolver.addFont(Strings.getRealPath(font), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            }
        }
        renderer.layout();
        try {
            renderer.createPDF(output);
        } finally {
            output.close();
        }
    }

    /**
     * 将html转换成PDF文件
     *
     * @param source Html源文件
     * @param target PDF文件
     * @param fonts  样式文件路径数据
     * @throws DocumentException 文档操作异常
     * @throws IOException       IO操作异常
     */
    public static void html2pdf(File source, File target, String... fonts) throws DocumentException, IOException {
        if (source == null) {
            throw new IllegalArgumentException("Source file must not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target file must not be null");
        }
        File path = target.getParentFile();
        if (path != null && !path.exists()) {
            path.mkdirs();
        }
        OutputStream output = new FileOutputStream(target);
        try {
            html2pdf(source, output, fonts);
        } finally {
            output.close();
        }
    }

    /**
     * 将html转换成PDF文件
     *
     * @param source Html源文件
     * @param output PDF文件输出流
     * @param fonts  样式文件路径数据
     * @throws DocumentException 文档操作异常
     * @throws IOException       IO操作异常
     */
    public static void html2pdf(File source, OutputStream output, String... fonts)
        throws DocumentException, IOException {
        if (source == null) {
            throw new IllegalArgumentException("Source file must not be null");
        }
        if (output == null) {
            throw new IllegalArgumentException("OutputStream must not be null");
        }
        ITextRenderer renderer = new ITextRenderer();
        ChainingReplacedElementFactory chainingReplacedElementFactory = new ChainingReplacedElementFactory();
        chainingReplacedElementFactory.addReplacedElementFactory(new SVGReplacedElementFactory());
        renderer.getSharedContext().setReplacedElementFactory(chainingReplacedElementFactory);
        renderer.setDocument(source);
        if (fonts != null && fonts.length > 0) {
            ITextFontResolver fontResolver = renderer.getFontResolver();
            for (String font : fonts) {
                fontResolver.addFont(Strings.getRealPath(font), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            }
        }
        renderer.layout();
        try {
            renderer.createPDF(output);
        } finally {
            output.close();
        }
    }

}
