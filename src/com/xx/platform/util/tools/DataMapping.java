package com.xx.platform.util.tools;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import org.apache.commons.configuration.*;

import com.xx.platform.util.constants.IbeaProperty;

public class DataMapping {
    public static Configuration dataType;

    static {
            try {
                dataType = new PropertiesConfiguration(DataMapping.class.
                        getResource(
                                "/datatype.properties"));
//                            System.out.println("dataType"+dataType);

            } catch (Exception ex) {
                IbeaProperty.log.info("Load data type convert config error !");
            }
        }
        /**
         * db2数据库取数据表字段的数据类型：从数字转换到数据类型实际值，
         * 对应dataType.properties文件中的键值对，例如：db2.-5=bigint
         * @param typeId int
         * @return String
         */
        public static String convertDataType(int typeId) {
//            System.out.println("db2数值是： "+typeId);
        String type = dataType.getString("db2."+String.valueOf(typeId));
        if (type == null) {
            type = "String";
        }
        return type;
       }

       /**
        * mysql sqlserver oracle数据库取数据表字段的数据类型：从数字转换到数据类型实际值，
        * 对应dataType.properties文件中的键值对，例如：oracle.3=number
        * @param typeId int 数据类型对应的数字值
        * @param dbType String 数据库类型
        * @return String
        */
       public static String convertDataType(int typeId,String dbType) {

            String type = null;
            if(dbType.equals(null) || "".equals(dbType)){
                 type = dataType.getString(String.valueOf(typeId));
            }else{
//                System.out.println("键："+dbType+"."+String.valueOf(typeId));
                 type = dataType.getString(dbType+"."+String.valueOf(typeId));
            }
            if (type == null) {
                type = "String";
            }
            return type;
    }
}
