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

    response.setContentType(CONTENT_TYPE); //必须设置ContentType为image/jpeg

    int length = 6; //设置密码长度

    Date d = new Date();

    long lseed = d.getTime();

    java.util.Random r = new Random(lseed); //设置随机种子

    StringBuffer str = new StringBuffer();

    for (int i = 0; i < length; i++) {

      str.append(r.nextInt(9)); //生成随机数字

    }
//保存验证码的代码
    HttpSession session = request.getSession();
    session.removeAttribute("verifyCode");
    session.setAttribute("verifyCode", str.toString());
//创建内存图像

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

//使用JPEG编码，输出到response的输出流

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
