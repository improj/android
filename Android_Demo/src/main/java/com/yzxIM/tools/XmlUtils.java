package com.yzxIM.tools;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.yzxtcp.tools.CustomLog;



public class XmlUtils {
	public static Document getXml(String xml) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			CustomLog.e(e.toString());
		}
		Document doc = null;
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(xml.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			CustomLog.e(e.toString());
		}
		try {
			if (builder != null)
				doc = builder.parse(is);
		} catch (SAXException e) {
			CustomLog.e(e.toString());
		} catch (IOException e) {
			CustomLog.e(e.toString());
		}
		return doc;
	}
}
