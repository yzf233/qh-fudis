package com.xx.platform.web.actions;

import com.xx.platform.util.tools.DirectoryExplorer;
import com.opensymphony.xwork2.Action;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DirectoryAjaxAction extends BaseAction {
    public String exploreDirectory()throws Exception{
        String output =null;
        try
        {
            DirectoryExplorer directoryexplorer = (DirectoryExplorer)request.getSession().getAttribute("directoryExplorer");
            if(directoryexplorer == null)
            {
                directoryexplorer = new DirectoryExplorer(DirectoryExplorer.getVirtualRoot());
                request.getSession().setAttribute("directoryExplorer", directoryexplorer);
            }
            String s = directoryexplorer.getCurrentDirectory();
            String s1 = request.getParameter("cd");
            if(s1 != null && s1.length() > 0)
            {
                directoryexplorer.changeDirectory(s1);
            } else
            {
                String s2 = request.getParameter("setDir");
                if(s2 == null || s2.equals(""))
                {
                    s2 = DirectoryExplorer.getVirtualRoot();
                }
                directoryexplorer = new DirectoryExplorer(s2);
                request.getSession().setAttribute("directoryExplorer", directoryexplorer);
            }
            java.util.Vector vector = directoryexplorer.getDirectories();

            if(!directoryexplorer.isValidDir() || vector == null){
//                request.setAttribute("errorForward", "A1515I.unable.to.open.dir");
                output = "{ error: \"错误：无法打开此目录。\" }";
                directoryexplorer = new DirectoryExplorer(s);
                request.getSession().setAttribute("directoryExplorer", directoryexplorer);
            } else{
                output = createJSONOutput(vector,directoryexplorer);
//                request.setAttribute("directories", vector);
//                request.setAttribute("currentDir", directoryexplorer.getCurrentDirectory());
//                request.setAttribute("hierarchy", directoryexplorer.getHierarchy());
            }
        }
        catch(Exception exception){
            output = "{ error: \"错误：无法打开此目录。\" }";
        }
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(output);
        return null;
    }

    private String createJSONOutput(java.util.Vector vector,DirectoryExplorer directoryexplorer){
        StringBuffer output = new StringBuffer();
        String s2 = directoryexplorer.getCurrentDirectory();
        java.util.LinkedList linkedlist = directoryexplorer.getHierarchy();
        if(vector == null || s2 == null || linkedlist == null){
            return "{ error: \"错误：无法打开此目录。\" }";
        }
        output.append("{ currentDir : \"").append(s2.replace("\\", "\\\\")).append("\" ");
        if(linkedlist.size() == 0){
            output.append(" , hierarchy : []");
        } else{
            output.append(" , hierarchy : [");
            while(linkedlist.size()>0){
                output.append("\"").append(((String)linkedlist.poll()).replace("\\", "\\\\")).append("\" ,");
            }
            output.append("]");
        }
        output.append(" , subdirectories : ");
        if(vector.size() == 0){
            output.append("[]");
        } else{
            output.append("[");
            int i;
            for(i = 0; i < vector.size() - 1; i++)
            {
                output.append("\"").append(((String)vector.elementAt(i)).replace("\\", "\\\\")).append("\" ,");
            }
            output.append("\"").append(((String)vector.elementAt(i)).replace("\\", "\\\\")).append("\"");
            output.append("]");
        }
        output.append("}");
//        System.out.println(output.toString());
        return output.toString();
    }

}
