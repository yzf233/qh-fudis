package com.xx.platform.util.tools.blob;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.xx.platform.util.tools.ms.MSExtractor;
import com.xx.platform.util.tools.ms.WordExtractor;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author quhuan
 * @version 1.0
 */
public class MSParserTool implements ParserBlobTool{
    private static final String WORD_NAME = "WordDocument";
    private static final String WORKBOOK_NAME = "Workbook" ;
    private static final String WORKBOOK1_NAME = "WORKBOOK" ;
    public String extract(InputStream in) {
        try {
            POIFSFileSystem fs = new POIFSFileSystem(in);
            MSExtractor e = null;
            try {
                //word
                fs.getRoot().getEntry(WORD_NAME);
                e = new WordExtractor();
            } catch (FileNotFoundException w) {
//                // excel
//                try {
//                    fs.getRoot().getEntry(WORKBOOK_NAME);
//                    e = new ExcelExtractor();
//                } catch (FileNotFoundException wb) {
//                    try {
//                        fs.getRoot().getEntry(WORKBOOK1_NAME);
//                        e = new ExcelExtractor();
//                    } catch (FileNotFoundException wb1) {
//                        return "";
//                        // Doesn't contain it in either form
//                        //throw new IllegalArgumentException("The supplied POIFSFileSystem contained neither a 'Workbook' entry, nor a 'WORKBOOK' entry. Is it really an excel file?");
//                    }
//                }
            	return "";
            }
            return e.extractText(fs);
        } catch (Exception ex) {
        	
            return "";
        }
    }
}
