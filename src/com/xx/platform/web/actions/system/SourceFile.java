package com.xx.platform.web.actions.system;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class SourceFile{
	private String imgPath;//图片文件夹的位置
	private String imgName;//图片的名字
	private String name;
	private String path;
	private String xpath;//双斜线的path
	private String relativePath;//双斜线，相对路径
	private String UTF8Name;
	private String suffix;
	private File file;
	private String[] imgType={"bmp","dib","jpg","jpeg","jpe","jfif","gif","png"};
	SourceFile(File file){
		this.file=file;
		this.name=file.getName();
		this.path=file.getPath();
		this.xpath=path.replace("\\","\\\\");
		this.relativePath=path.replace(ProjectFileManager.rootPath,"").replace("\\","\\\\");
		try {
			this.UTF8Name=URLEncoder.encode(name,"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(name.indexOf(".")>0){
			suffix=name.substring(name.lastIndexOf(".")+1,name.length());
		}
	}
	SourceFile(File file,String imgPath){
		this.file=file;
		this.name=file.getName();
		this.path=file.getPath();
		this.imgPath=imgPath;
		this.xpath=path.replace("\\","\\\\");
		this.relativePath=path.replace(ProjectFileManager.rootPath,"").replace("\\","\\\\");
		try {
			this.UTF8Name=URLEncoder.encode(name,"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(name.indexOf(".")>0){
			suffix=name.substring(name.lastIndexOf(".")+1,name.length());
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getUTF8Name() {
		return UTF8Name;
	}
	public void setUTF8Name(String name) {
		UTF8Name = name;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public String getImgPath() {
		imgPath=imgPath.concat("/").concat(getImgName());
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	public String getImgName() {
		if(file.isDirectory()){
			imgName="ico_fldr_32.gif";
		}else{
			boolean isImg=false;
			for(String type:imgType){
				if(type.equalsIgnoreCase(suffix)){
					isImg=true;
					break;
				}
			}
			if(isImg){
				imgName="ico_jpg_48.gif";
			}
		}
		if(imgName==null){
			imgName="ico_txt_48.gif";
		}
		return imgName;
	}
	public void setImgName(String imgName) {
		this.imgName = imgName;
	}
	public String getXpath() {
		return xpath;
	}
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	public String getRelativePath() {
		return relativePath;
	}
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
}
