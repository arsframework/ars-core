package ars.file.office;

import java.io.InputStream;
import java.io.IOException;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;

import ars.util.Strings;

/**
 * 自定义文档操作接口实现
 * 
 * @author wuyq
 * 
 */
public class CustomXWPFDocument extends XWPFDocument {
	private static final int EMU = 9525;

	public CustomXWPFDocument() {
		super();
	}

	public CustomXWPFDocument(OPCPackage pack) throws IOException {
		super(pack);
	}

	public CustomXWPFDocument(InputStream input) throws IOException {
		super(input);
	}

	@SuppressWarnings("deprecation")
	public void createPicture(XWPFParagraph paragraph, int id, int width,
			int height, String path) {
		width *= EMU;
		height *= EMU;
		String blipId = getAllPictures().get(id).getPackageRelationship()
				.getId();
		CTInline inline = paragraph.createRun().getCTR().addNewDrawing()
				.addNewInline();
		paragraph.createRun().setText(path);
		String picXml = new StringBuilder()
				.append("<a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"><a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\"><pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\"><pic:nvPicPr><pic:cNvPr id=\"")
				.append(id)
				.append("\" name=\"Generated\"/><pic:cNvPicPr/></pic:nvPicPr><pic:blipFill><a:blip r:embed=\"")
				.append(blipId)
				.append("\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"/><a:stretch><a:fillRect/></a:stretch></pic:blipFill><pic:spPr><a:xfrm><a:off x=\"0\" y=\"0\"/><a:ext cx=\"")
				.append(width)
				.append("\" cy=\"")
				.append(height)
				.append("\"/></a:xfrm><a:prstGeom prst=\"rect\"><a:avLst/></a:prstGeom></pic:spPr></pic:pic></a:graphicData></a:graphic>")
				.toString();
		inline.addNewGraphic().addNewGraphicData();
		try {
			XmlToken xmlToken = XmlToken.Factory.parse(picXml);
			inline.set(xmlToken);
		} catch (XmlException xe) {
			xe.printStackTrace();
		}
		inline.setDistT(0);
		inline.setDistB(0);
		inline.setDistL(0);
		inline.setDistR(0);

		CTPositiveSize2D extent = inline.addNewExtent();
		extent.setCx(width);
		extent.setCy(height);

		CTNonVisualDrawingProps props = inline.addNewDocPr();
		props.setId(id);
		props.setName(String.valueOf(id));
		props.setDescr(Strings.EMPTY_STRING);
	}

}
