package ars.file.xhtml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

/**
 * SVG元素工厂对象
 * 
 * @author yongqiangwu
 * 
 */
public class SVGReplacedElementFactory implements ReplacedElementFactory {

	@Override
	public ReplacedElement createReplacedElement(LayoutContext c, BlockBox box, UserAgentCallback uac, int cssWidth,
			int cssHeight) {
		Element element = box.getElement();
		if ("svg".equals(element.getNodeName())) {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder;

			try {
				documentBuilder = documentBuilderFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new RuntimeException(e);
			}
			Document svgDocument = documentBuilder.newDocument();
			Element svgElement = (Element) svgDocument.importNode(element, true);
			svgDocument.appendChild(svgElement);
			return new SVGReplacedElement(svgDocument, cssWidth, cssHeight);
		}
		return null;
	}

	@Override
	public void reset() {

	}

	@Override
	public void remove(Element e) {

	}

	@Override
	public void setFormSubmissionListener(FormSubmissionListener listener) {

	}

}
