package com.xx.platform.util.tools.blob;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XLSXParserTool implements ParserBlobTool{

	private ZipEntry entry = null;
	private ByteArrayInputStream bais = null;
	private String sharedStrings[] = null;
	private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	private StringBuffer connect = new StringBuffer();

	public String extract(InputStream in) {
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(in));
		try {
			while ((entry = zis.getNextEntry()) != null) {

				if (entry.getName().equalsIgnoreCase("xl/sharedStrings.xml")) {

					byte[] bytes = IoUtil.readBytes(zis);
					if (bytes != null) {
						bais = new ByteArrayInputStream(bytes);
					}

					Document sharedString = dbf.newDocumentBuilder()
							.parse(bais);
					NodeList str = sharedString.getElementsByTagName("t");
					sharedStrings = new String[str.getLength()];
					for (int n = 0; n < str.getLength(); n++) {
						Element element = (Element) str.item(n);
						sharedStrings[n] = element.getTextContent();
						connect.append(sharedStrings[n]);
					}
				}

			}
		} catch (Exception ex) {
		} finally {
			try {
				zis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return connect.toString();
	}

	public static void main(String[] args) throws Exception {
		InputStream in = new FileInputStream("D:/book1.xlsx");
		XLSXParserTool uz = new XLSXParserTool();
		System.out.print("获得返回值为：\n" + uz.extract(in));
	}

}
