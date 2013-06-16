package com.xx.platform.core.nutch;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

public class EMLAnalyzer {

	public MessageBean read(String filepath) {
		InputStream is = null;
		MessageBean bean = null;
		try {
			is = new FileInputStream(filepath);
			bean = analyze(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bean;
	}

	/**
	 * 解析eml文件
	 * 
	 * @param is
	 * @throws Exception
	 */
	public MessageBean analyze(InputStream is) throws Exception {
		MessageBean bean = new MessageBean();
		Session mailSession = Session.getDefaultInstance(System.getProperties(), null);
		MimeMessage message;
		message = new MimeMessage(mailSession, is);
		/*
		 * 获取标题
		 */
		String subject = message.getSubject();
		String[] subjects = message.getHeader("Subject");
		if (subjects != null && subjects.length > 0) {
			if (subjects[0] != null && subjects[0].indexOf("=?") < 0) {
				subject = new String(subjects[0].getBytes("iso-8859-1"), "gbk");
			}
		}
		bean.setSubject(subject);
		bean.setSendDate(message.getSentDate());
		bean.setMessageSize(message.getSize());
		bean.setToUsers(getMailTo(Message.RecipientType.TO, message));
		bean.setCcUsers(getMailTo(Message.RecipientType.CC, message));
		bean.setSendUser(getMailFrom(message));
		Object obj = message.getContent();
		String content = getMailContent(obj);
		// System.out.println("编码：" + message.getEncoding());

		// System.out.println("--------------"+MimeUtility.getEncoding(message.getDataHandler()));
		// BASE64Decoder decoder = new BASE64Decoder();
		// System.out.println(new String(decoder.decodeBuffer(content)));
		// System.out.println(new String(content.getBytes("iso-8859-1"),"gbk"));
		bean.setContent(content);
		bean.setAttaList(getAttachment(message));
		return bean;
	}

	/**
	 * 获取邮件内容
	 * 
	 * @param obj
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	private String getMailContent(Object obj) throws MessagingException, IOException {
		String result = "";
		if (obj == null) {
			return result;
		}
		if (obj instanceof String) {
			result = (String) obj;
		} else if (obj instanceof Multipart) {
			Multipart parts = (Multipart) obj;
			if (parts.getCount() > 0) {
				BodyPart body = parts.getBodyPart(0);
				Object bodyObj = body.getContent();
				result = getMailContent(bodyObj);
			}
		}
		return result;
	}

	/**
	 * 获取附件
	 * 
	 * @param mimeMessage
	 * @throws MessagingException
	 * @throws IOException
	 */
	private List<Attachment> getAttachment(MimeMessage mimeMessage) throws IOException, MessagingException {
		List<Attachment> attaList = new ArrayList<Attachment>();
		Object obj = mimeMessage.getContent();
		if (obj instanceof String) {
			return attaList;
		} else {
			Multipart part = (Multipart) obj;
			int count = part.getCount();
			for (int i = 0; i < count; i++) {
				BodyPart bp = part.getBodyPart(i);
				String type = bp.getDisposition();
				if ("attachment".equals(type)) {
					Attachment atta = new Attachment();
					String fileName = bp.getFileName();
					if (fileName != null) {
						fileName = MimeUtility.decodeText(fileName);
						atta.setFileName(fileName);
					}
					InputStream is = bp.getInputStream();
					atta.setSize(is.available());
					atta.setIs(is);

					attaList.add(atta);
				}
			}
			return attaList;
		}
	}

	/**
	 * 获取邮件收件人信息
	 * 
	 * @param type
	 * @param mimeMessage
	 * @return
	 * @throws Exception
	 */
	private List<MailUser> getMailTo(Message.RecipientType type, MimeMessage mimeMessage) throws Exception {
		InternetAddress[] address = (InternetAddress[]) mimeMessage.getRecipients(type);
		List<MailUser> userList = new ArrayList<MailUser>();
		if (address != null) {
			MailUser user = null;
			for (int i = 0; i < address.length; i++) {
				String email = address[i].getAddress();
				if (email == null)
					email = "";
				else {
					email = MimeUtility.decodeText(email);
				}
				String personal = address[i].getPersonal();
				if (personal == null)
					personal = "";
				else {
					personal = MimeUtility.decodeText(personal);
				}
				user = new MailUser();
				user.setMailAddr(email);
				user.setUserName(personal);
				userList.add(user);
			}
		}
		return userList;
	}

	/**
	 * 获取发件人信息
	 * 
	 * @param mimeMessage
	 * @return
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	private MailUser getMailFrom(MimeMessage mimeMessage) throws MessagingException, UnsupportedEncodingException {
		MailUser user = new MailUser();
		Address[] add = mimeMessage.getFrom();
		String[] from = mimeMessage.getHeader("From");
		if (add != null && add.length > 0 && from != null && from.length > 0) {
			if (add[0] instanceof InternetAddress) {
				InternetAddress addr = (InternetAddress) add[0];
				String name = addr.getPersonal();
				if (from[0].indexOf("=?") == -1) {
					name = new String(name.getBytes("iso-8859-1"), "gbk");
				}
				if (name != null) {
					user.setUserName(MimeUtility.decodeText(name));
				}
				String address = addr.getAddress();
				if (address != null) {
					user.setMailAddr(MimeUtility.decodeText(address));
				}
			}
		}
		return user;
	}

	public class MessageBean {
		/**
		 * 标题
		 */
		private String subject = "";
		/**
		 * 发送时间
		 */
		private Date sendDate;
		/**
		 * 文件大小
		 */
		private long messageSize;
		/**
		 * 收件人
		 */
		private List<MailUser> toUsers;
		/**
		 * 抄送人
		 */
		private List<MailUser> ccUsers;
		/**
		 * 发件人
		 */
		private MailUser sendUser;
		/**
		 * 邮件内容
		 */
		private String content = "";
		/**
		 * 邮件附件
		 */
		private List<Attachment> attaList;

		public String getSubject() {
			if (subject == null) {
				subject = "";
			}
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public Date getSendDate() {
			return sendDate;
		}

		public void setSendDate(Date sendDate) {
			this.sendDate = sendDate;
		}

		public long getMessageSize() {
			return messageSize;
		}

		public void setMessageSize(long messageSize) {
			this.messageSize = messageSize;
		}

		public List<MailUser> getToUsers() {
			return toUsers;
		}

		public void setToUsers(List<MailUser> toUsers) {
			this.toUsers = toUsers;
		}

		public List<MailUser> getCcUsers() {
			return ccUsers;
		}

		public void setCcUsers(List<MailUser> ccUsers) {
			this.ccUsers = ccUsers;
		}

		public MailUser getSendUser() {
			return sendUser;
		}

		public void setSendUser(MailUser sendUser) {
			this.sendUser = sendUser;
		}

		public String getContent() {
			if (content == null) {
				content = "";
			}
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public List<Attachment> getAttaList() {
			return attaList;
		}

		public void setAttaList(List<Attachment> attaList) {
			this.attaList = attaList;
		}

		public String toString() {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			StringBuilder sbInfo = new StringBuilder();
			sbInfo.append("标          题：").append(subject).append("\r\n");
			sbInfo.append("收   件  人：").append(toUsers).append("\r\n");
			sbInfo.append("抄   送  人：").append(ccUsers).append("\r\n");
			sbInfo.append("发   件  人：").append(sendUser).append("\r\n");
			sbInfo.append("附          件：").append(attaList).append("\r\n");
			if (sendDate != null) {
				sbInfo.append("发送时间：").append(sdf.format(sendDate)).append("\r\n");
			}
			sbInfo.append("邮件大小：").append(messageSize).append("\r\n");
			sbInfo.append("邮件内容：").append(content).append("\r\n");
			return sbInfo.toString();
		}
	}

	/**
	 * 邮箱使用人
	 * 
	 * @author 线点科技
	 * 
	 */
	public class MailUser {
		private String userName = "";
		private String mailAddr = "";

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getMailAddr() {
			return mailAddr;
		}

		public void setMailAddr(String mailAddr) {
			this.mailAddr = mailAddr;
		}

		public String toString() {
			StringBuffer sbInfo = new StringBuffer();
			sbInfo.append(userName).append(":").append(mailAddr);
			return sbInfo.toString();
		}
	}

	/**
	 * 附件
	 * 
	 * @author 线点科技
	 * 
	 */
	public static class Attachment {
		private String fileName;
		private long size;
		private InputStream is;

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}

		public InputStream getIs() {
			return is;
		}

		public void setIs(InputStream is) {
			this.is = is;
		}

		public String toString() {
			StringBuffer sbInfo = new StringBuffer();
			sbInfo.append(fileName).append("<大小：").append(size / 1024).append("K>");
			return sbInfo.toString();
		}
	}

	/**
	 * 测试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		File file = new File("G:\\邮件文件\\test");
		File[] files = file.listFiles(new FileFilter() {

			public boolean accept(File file) {
				if (file.exists() && file.getName().toLowerCase().endsWith(".eml")) {
					return true;
				} else {
					return false;
				}
			}

		});
		long l1 = System.currentTimeMillis();
		if (file != null) {
			for (File f : files) {
				System.out.println(f);
				EMLAnalyzer test = new EMLAnalyzer();
				MessageBean bean = test.read(f.getPath());
				System.out.println(bean);
			}
		}
		System.out.println((System.currentTimeMillis() - l1) / 1000);
	}
}
