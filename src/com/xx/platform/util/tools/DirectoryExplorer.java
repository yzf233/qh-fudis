package com.xx.platform.util.tools;

import java.io.File;
import java.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author quhuan copy from ibm
 * @version 1.0
 */
public class DirectoryExplorer {
       private String currentDirectory;
       private static final String parentDirectory = "[..]";
       private static final String windowsRoot = "\\\\";
       private static final String unixRoot = "/";

       public DirectoryExplorer(String s)
       {
           if(s == null || s.equals(""))
           {
               currentDirectory = getVirtualRoot();
           } else
           {
               currentDirectory = s;
           }
       }

       public Vector getDirectories()
       {
           if(currentDirectory.equals("\\\\"))
           {
               return getRoots();
           }
           File file = new File(currentDirectory);
           Vector vector = new Vector();
           String as[] = file.list();
           if(as == null)
           {
               return null;
           }
           for(int i = 0; i < as.length; i++)
           {
               if(as[i].equals(".") || as[i].equals(".."))
               {
                   continue;
               }
               File file1 = new File((new StringBuilder()).append(currentDirectory).append(File.separator).append(as[i]).toString());
               if(file1.isDirectory() && !file1.isHidden())
               {
                   vector.add(as[i]);
               }
           }

           Collections.sort(vector);
           if(!currentDirectory.equals("/"))
           {
               vector.insertElementAt("[..]", 0);
           }
           return vector;
       }

       public boolean isValidDir()
       {
           if(currentDirectory == null)
           {
               return false;
           }
           if(currentDirectory.equals("\\\\"))
           {
               return true;
           } else
           {
               File file = new File(currentDirectory);
               return file.exists() && file.isDirectory();
           }
       }

       public void changeDirectory(String s)
       {
           if(s.equals("[..]"))
           {
               File file = new File(currentDirectory);
               String s1 = file.getParent();
               if(s1 != null)
               {
                   currentDirectory = s1;
               } else
               {
                   currentDirectory = "\\\\";
               }
           } else
           if(currentDirectory.equals("/"))
           {
               currentDirectory = (new StringBuilder()).append(currentDirectory).append(s).toString();
           } else
           if(currentDirectory.equals("\\\\"))
           {
               currentDirectory = s;
           } else
           if(getRoots().contains(currentDirectory))
           {
               currentDirectory = (new StringBuilder()).append(currentDirectory).append(s).toString();
           } else
           {
               currentDirectory = (new StringBuilder()).append(currentDirectory).append(File.separator).append(s).toString();
           }
       }

       public String getCurrentDirectory()
       {
           if(currentDirectory.equals("\\\\"))
           {
               return "";
           } else
           {
               return currentDirectory;
           }
       }

       public LinkedList getHierarchy()
       {
           LinkedList linkedlist = new LinkedList();
           for(File file = (new File(currentDirectory)).getParentFile(); file != null; file = file.getParentFile())
           {
               linkedlist.addFirst(file.getPath());
           }

           return linkedlist;
       }

       public static Vector getRoots()
       {
           File afile[] = File.listRoots();
           Vector vector = new Vector(afile.length);
           for(int i = 0; i < afile.length; i++)
           {
               vector.add(afile[i].getPath());
           }

           return vector;
       }

       public static String getVirtualRoot()
       {
           if(((String)getRoots().firstElement()).equals("/"))
           {
               return "/";
           } else
           {
               return "\\\\";
           }
       }
   }
