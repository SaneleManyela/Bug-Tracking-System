/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bugtrackingsystem.textpad;

import java.io.*;
/**
 *
 * @author Sanele
 */
public class clsModelDocument {
    public clsModelDocument() {
        this.strText = "";
        this.flFile = null;
    }
    
    public clsModelDocument(File fl) throws Exception {
        BufferedReader reader = null;
        try {
            StringBuilder builder = new StringBuilder();
            reader = new BufferedReader(new FileReader(fl));
            for(String strLine = reader.readLine(); strLine != null; strLine = reader.readLine()) {
                builder.append(strLine);
                builder.append("\n");
            }
            strText = builder.toString();
            this.flFile = fl;
        } catch(IOException e) {
            strText = "";
            this.flFile = null;
            throw new Exception("The content of the file could not be read");
        } finally {
            if(reader != null) {
                reader.close();
            }
        }
    }
    
    private String strText;
    private File flFile;
    
    
    public String mGetText() {
        return strText;
    }
    
    public void mSetText(String strText) {
        this.strText = strText;
    }
    
    public boolean mSaveFile() {
        if(flFile == null) {
            return false;
        } else {
            return mSave(flFile);
        }
    }
    
    public boolean mSave(File flFile) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(flFile));
            writer.write(strText);
            this.flFile = flFile;
            return true;
        } catch(IOException e) {
            return false;
        } finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch(IOException e) {  
                }
            }
        }
    }
}