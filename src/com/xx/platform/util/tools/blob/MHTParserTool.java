package com.xx.platform.util.tools.blob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.nutch.util.StringUtil;

public class MHTParserTool implements ParserBlobTool {

	public String extract(InputStream in) {
		Session mailSession;
		if (in == null)
			return null;
		mailSession = Session.getDefaultInstance(System.getProperties(), null);
		MimeMessage msg;
		try {
			msg = new MimeMessage(mailSession, in);
			Object content = msg.getContent();
			if (content instanceof Multipart)
				return handleMultipart((Multipart) content);
			
			return handlePart(msg);
		} catch (Exception e1) {
			e1.getMessage();
		}

		return null;
	}

	public String handleMultipart(Multipart multipart)
			throws MessagingException, IOException {
		StringBuffer strb = new StringBuffer();
		int i = 0;
		for (int n = multipart.getCount(); i < n; i++)
			strb.append(handlePart(multipart.getBodyPart(i)));

		return strb.toString();
	}

	public String handlePart(Part part) throws MessagingException, IOException {
		String contentType = part.getContentType();
		String encode = StringUtil.parseCharacterEncoding(contentType);
		InputStreamReader sbis = null;
		StringBuffer strb = new StringBuffer();
		if (encode != null && !"".equals(encode))
			sbis = new InputStreamReader(part.getInputStream(), encode);
		else
			sbis = new InputStreamReader(part.getInputStream());
		BufferedReader reader = new BufferedReader(sbis);
		if (contentType.length() >= 8
				&& contentType.toLowerCase().substring(0, 8).equals("text/htm")) {
			String line;
			while ((line = reader.readLine()) != null)
				strb.append(line);
			return html2Text(strb.toString());
		} else {
			return null;
		}
	}

	public String html2Text(String inputString) {
		String htmlStr = inputString;
		String textStr = "";
		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
			String regEx_html = "<[^>]+>";
			Pattern p_script = Pattern.compile(regEx_script, 2);
			Matcher m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll("");
			Pattern p_style = Pattern.compile(regEx_style, 2);
			Matcher m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll("");
			Pattern p_html = Pattern.compile(regEx_html, 2);
			Matcher m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll("");
			textStr = htmlStr;
		} catch (Exception e) {
			System.err.println((new StringBuilder("Html2Text: ")).append(
					e.getMessage()).toString());
		}
		return textStr;
	}
	
	
	public static void main(String[] args){
		MHTParserTool mt = new MHTParserTool();
		try {
			InputStream in = new FileInputStream(new File("d:/tes.mht"));
			System.out.println(mt.extract(in));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
