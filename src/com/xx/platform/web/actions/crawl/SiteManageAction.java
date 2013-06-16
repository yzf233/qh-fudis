package com.xx.platform.web.actions.crawl;

import java.io.File;
import java.util.List;

import com.opensymphony.xwork2.Action;
import com.xx.platform.core.nutch.DirectoryFethListTool;
import com.xx.platform.core.nutch.WebDB;
import com.xx.platform.domain.model.crawl.SiteManage;
import com.xx.platform.util.constants.IbeaProperty;
import com.xx.platform.web.actions.BaseAction;

import org.apache.commons.io.FileUtils;
import org.apache.nutch.db.Page;
import org.hibernate.criterion.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: 北京线点科技有限公司</p>
 *
 * @author
 * @version 1.0
 */
public class SiteManageAction
    extends BaseAction {
  private List<SiteManage> siteManageList;
  private SiteManage siteManage;
  private Integer sum = 0;
  private int page = 1;
  private String sname;

  private String downFilePath;

  private File upload;
  private String uploadFileName;
  private String uploadContentType;

  public String list() throws Exception {
    DetachedCriteria dc = DetachedCriteria.forClass(SiteManage.class);
    if (sname != null && !sname.trim().equals("")) {
      dc.add(Restrictions.ilike("name", sname, MatchMode.ANYWHERE));
    }
    siteManageList = service.findPageByCriteria(dc, 50, page);
    return Action.SUCCESS;
  }

  public String addDo() throws Exception {
    if (siteManage != null) {
      if (siteManage.getUrl() != null &&
          !siteManage.getUrl().trim().startsWith("http://")) {
        siteManage.setUrl("http://" + siteManage.getUrl());
      }
      service.saveIObject(siteManage);
      //更新频率处理() qh 2007-07-27
      if (siteManage.getFre() != 0) {
        WebDB webdb = new WebDB(new Page(siteManage.getUrl(),
                                         IbeaProperty.SOURCE_DEFUALT +
                                         3.6f / siteManage.getLev(),
                                         IbeaProperty.SOURCE_DEFUALT +
                                         3.6f / siteManage.getLev(),
                                         30));
        webdb.setVersion(siteManage.getId());
        service.saveIObject(webdb);
      }
//            if(SearchContext.getXdtechsite().getUrlfilterreg())
//            {
//                URL url = new URL(siteManage.getUrl());
//                Urlfilterreg filter = new Urlfilterreg();
//                filter.setXdname(siteManage.getName());
//                filter.setXdcode((url.getHost() != null &&
//                                  url.getHost().length() < 30) ? url.getHost() :
//                                 url.getHost().substring(0, 20));
//                filter.setXsource(0);
//                filter.setFiltertype(1);
//                filter.setXdprocess(1);
//                filter.setFilterreg(url.getHost().replaceFirst("www.","") + "[\\S\\s]*");
//                service.saveIObject(filter);
//            }
    }
    return Action.SUCCESS;
  }

  public String edit() throws Exception {

    if (siteManage != null && siteManage.getId() != null) {
      siteManage = (SiteManage) service.getIObjectByPK(SiteManage.class,
          siteManage.getId());
    }
    return Action.SUCCESS;
  }

  public String editDo() throws Exception {
    if (request.getParameter("type") != null &&
        request.getParameter("type").equals("1")) {
      if (siteManage != null && siteManage.getId() != null) {
        service.deleteIObject(siteManage);
        service.execByHQL("delete from WebDB where url ='" + siteManage.getUrl() +
                          "/'");
      }
    }
    else {
      if (siteManage != null && siteManage.getId() != null) {
        if (siteManage.getUrl() != null &&
            !siteManage.getUrl().trim().startsWith("http://")) {
          siteManage.setUrl("http://" + siteManage.getUrl());
        }
        service.updateIObject(siteManage);
        //更新频率处理 qh
        if (request.getParameter("oldurl") != null) {
          if (siteManage.getFre() == 0) {
            service.execByHQL("delete from WebDB where url ='" +
                              request.getParameter("oldurl") + "/'");
          }
          else if (siteManage.getFre() != 0) {
            service.execByHQL("delete from WebDB where url ='" +
                              request.getParameter("oldurl") + "/'");
            service.saveIObject(new WebDB(new Page(siteManage.
                getUrl(),
                IbeaProperty.SOURCE_DEFUALT +
                3.6f / siteManage.getLev(),
                IbeaProperty.SOURCE_DEFUALT +
                3.6f / siteManage.getLev(),
                30)));
          }
        }
//                if(SearchContext.getXdtechsite().getUrlfilterreg())
//                {
//                    URL url = new URL(siteManage.getUrl());
//                    Urlfilterreg filter = new Urlfilterreg();
//                    filter.setXdname(siteManage.getName());
//                    filter.setXdcode((url.getHost() != null &&
//                                      url.getHost().length() < 30) ?
//                                     url.getHost() :
//                                     url.getHost().substring(0, 20));
//                    filter.setXsource(0);
//                    filter.setFiltertype(1);
//                    filter.setXdprocess(1);
//
//                    filter.setFilterreg(url.getHost().replaceFirst("www.","") + "[\\S\\s]*");
//                    service.saveIObject(filter);
//                }
      }
    }
    return Action.SUCCESS;
  }

  ///上传文件
    public String addFile() {
        message = null;
//        String type = ".txt";
        try {
//            if (upload != null && uploadFileName!=null) {
//                type = uploadFileName.substring(uploadFileName.lastIndexOf("."));
//            }
            File target = new File(DirectoryFethListTool.path,uploadFileName==null?String.valueOf(System.currentTimeMillis()):uploadFileName);

            FileUtils.copyFile(upload, target);

        } catch (Exception ex) {
            message = "上传文件失败：文件最大不能超过2M或请稍后再试！["+ex.getMessage()+"]";
             return Action.SUCCESS;
        }
        message = "上传文件成功！";
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(message);
//        return null;
        return Action.SUCCESS;
    }

    public String downFile() throws Exception{
//        if(checkDownFile(downFilePath)){
//            //读到流中
//            java.io.InputStream inStream = new java.io.FileInputStream(downFilePath); //文件的存放路径
//            //设置输出的格式
//            response.reset();
//            response.setContentType("bin");
//            response.addHeader("Content-Disposition",
//                               "attachment; filename=\"" + fileName + "\"");
//            //循环取出流中的数据
//            byte[] b = new byte[1024];
//            int len;
//            while ((len = inStream.read(b)) > 0)
//                response.getOutputStream().write(b, 0, len);
//            inStream.close();
//
//        }
        return null;
    }

    //文件下载检查
    private boolean checkDownFile(String downFilePath) {
        return false;
    }


    public SiteManage getSiteManage() {
    return siteManage;
  }

  public List getSiteManageList() {
    return siteManageList;
  }

  public Integer getSum() {
    if (siteManageList != null) {
      sum = siteManageList.size();
    }
    return sum;
  }

  public int getPage() {
    return page;
  }

  public String getSname() {
    return sname;
  }

    public File getUpload() {
        return upload;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public String getUploadContentType() {
        return uploadContentType;
    }

    public String getDownFilePath() {
        return downFilePath;
    }

    public void setSiteManage(SiteManage siteManage) {
    this.siteManage = siteManage;
  }

  public void setSiteManageList(List siteManageList) {
    this.siteManageList = siteManageList;
  }

  public void setSum(Integer sum) {
    this.sum = sum;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public void setSname(String sname) {
    this.sname = sname;
  }

    public void setUpload(File upload) {
        this.upload = upload;
    }

    public void setUploadFileName(String fileName) {
        this.uploadFileName = fileName;
    }

    public void setUploadContentType(String uploadContentType) {
        this.uploadContentType = uploadContentType;
    }

    public void setDownFilePath(String downFilePath) {
        this.downFilePath = downFilePath;
    }

}
