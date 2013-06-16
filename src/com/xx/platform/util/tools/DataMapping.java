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
         * db2���ݿ�ȡ���ݱ��ֶε��������ͣ�������ת������������ʵ��ֵ��
         * ��ӦdataType.properties�ļ��еļ�ֵ�ԣ����磺db2.-5=bigint
         * @param typeId int
         * @return String
         */
        public static String convertDataType(int typeId) {
//            System.out.println("db2��ֵ�ǣ� "+typeId);
        String type = dataType.getString("db2."+String.valueOf(typeId));
        if (type == null) {
            type = "String";
        }
        return type;
       }

       /**
        * mysql sqlserver oracle���ݿ�ȡ���ݱ��ֶε��������ͣ�������ת������������ʵ��ֵ��
        * ��ӦdataType.properties�ļ��еļ�ֵ�ԣ����磺oracle.3=number
        * @param typeId int �������Ͷ�Ӧ������ֵ
        * @param dbType String ���ݿ�����
        * @return String
        */
       public static String convertDataType(int typeId,String dbType) {

            String type = null;
            if(dbType.equals(null) || "".equals(dbType)){
                 type = dataType.getString(String.valueOf(typeId));
            }else{
//                System.out.println("����"+dbType+"."+String.valueOf(typeId));
                 type = dataType.getString(dbType+"."+String.valueOf(typeId));
            }
            if (type == null) {
                type = "String";
            }
            return type;
    }
}
