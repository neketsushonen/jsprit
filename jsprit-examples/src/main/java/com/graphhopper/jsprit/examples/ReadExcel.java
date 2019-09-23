
package com.graphhopper.jsprit.examples;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ReadExcel {

    public static void main(String[] args) throws Exception {
        Map<String,List<String> > frases = new TreeMap<String,List<String>>();
 			
        Workbook workbook = null;
        workbook = new XSSFWorkbook(new FileInputStream(new File("/Volumes/MAC2/道/翻譯/選字-2.xlsx")));
        
        for(Sheet sheet : workbook ){
            int fila = 0;
            for (Row row : sheet) {
                if (fila != 0 && row.getCell(0)!=null && !StringUtils.isBlank(row.getCell(0).getStringCellValue())) {
                    String word = StringUtils.trim(row.getCell(0).getStringCellValue()).replace("。", "").replace("*", "");
                    if(!frases.containsKey(word))
                        frases.put(word, new ArrayList<String>());
                    int column = 1;
                    while(row.getCell(column)!=null ){
                        try{
                            if(!StringUtils.isBlank(row.getCell(column).getStringCellValue())){
                                String w = null;
                                try{
                                   w = StringUtils.trim(row.getCell(column++).getStringCellValue()).replaceAll("\n", "");
        
                                }catch(IllegalStateException e){
                                    w = String.valueOf(row.getCell(column++).getNumericCellValue()).replaceAll("\n", "");
        
                                }
                                
                                if(!frases.get(word).contains(w))
                                    frases.get(word).add(w);
                            }
                        }catch(Exception e){

                        }
                        
                    }
                }
                fila++;
            }
        }
        
        workbook.close();

        PrintWriter writer = new PrintWriter(new File("/tmp/word.csv"),"UTF-8");
        writer.println("單字;");
        for(Map.Entry<String,List<String>> entry:frases.entrySet()){
            if(entry.getValue().size()>=3)
                writer.print(entry.getKey()+";");
            else writer.print("*"+entry.getKey()+";");
           
            for(String word: entry.getValue()){
                writer.print(word+";");
            }
            writer.println();
            writer.flush();
        }
        writer.close();
        /*
        workbook = new XSSFWorkbook(new FileInputStream(new File("/Volumes/MAC2/道/翻譯/選字-總合.xlsx")));
        Sheet sheet = workbook.createSheet("總合");
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("單字");
        for(Map.Entry<String,List<String>> entry:frases.entrySet()){
            int column = 0;
            row.createCell(column++).setCellValue(entry.getKey());
            for(String word: entry.getValue()){
                row.createCell(column++).setCellValue(word);
            }
        }
        workbook.close();
        */

    }


}

