package com.xx.platform.util.page;


import javax.servlet.*;

import javax.servlet.http.*;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.io.*;

import java.util.*;

import com.sun.image.codec.jpeg.*;

import java.awt.*;

import com.sun.image.codec.jpeg.*;

import java.awt.image.BufferedImage;


public class VerifyCode {

  private static String CONTENT_TYPE = "image/jpeg";
  public String number;

  /**
   * generator img and out put
   * @param response HttpServletResponse
   * @throws ServletException
   * @throws IOException
   */
  public void outImage(HttpServletResponse response, HttpServletRequest request) throws

      ServletException, IOException {

    response.setContentType(CONTENT_TYPE); //��������ContentTypeΪimage/jpeg

    int length = 6; //�������볤��

    Date d = new Date();

    long lseed = d.getTime();

    java.util.Random r = new Random(lseed); //�����������

    StringBuffer str = new StringBuffer();

    for (int i = 0; i < length; i++) {

      str.append(r.nextInt(9)); //�����������

    }
//������֤��Ĵ���
    HttpSession session = request.getSession();
    session.removeAttribute("verifyCode");
    session.setAttribute("verifyCode", str.toString());
//�����ڴ�ͼ��

    BufferedImage bi = new BufferedImage(70, 25, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = bi.createGraphics();
    g.setColor(new Color(255, 255, 255));
    //  g.setColor(new Color(250,246,237));
    g.fillRect(0, 0, 70, 25);

    g.setColor(Color.black);
    Font strfont = new Font("Arial", Font.BOLD + Font.ITALIC, 16);
    g.setFont(strfont);
    g.drawString(str.toString(), 3, 20);
    try {

//ʹ��JPEG���룬�����response�������

      JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(response.

          getOutputStream());

      JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);

      param.setQuality(1.0f, false);

      encoder.setJPEGEncodeParam(param);

      encoder.encode(bi);

    }

    catch (Exception ex) {

    }

  }
}
