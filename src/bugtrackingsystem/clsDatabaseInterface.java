/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bugtrackingsystem;

import java.sql.*;
import javax.swing.JOptionPane;
/**
 *
 * @author Sanele
 */
public class clsDatabaseInterface {
    public Connection mConnectToDatabase() {
        String strDBConnectionString = "jdbc:mysql://localhost:3306/bugTrackingsystem";
        String strUser = "root";
        String strPassword = "password";
        Connection conMySQLConnectionString = null;
        try {
            return conMySQLConnectionString = DriverManager.getConnection(strDBConnectionString, 
                    strUser, strPassword);
        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage() ,
                    "Error Message", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    public boolean mCheckDetailsExist(String strQuery) {
        boolean boolStatus = false;
        Statement stStatement = null;
        ResultSet rs = null;
        try{
            stStatement = mConnectToDatabase().prepareStatement(strQuery);
            rs = stStatement.executeQuery(strQuery);
            boolStatus = rs.next();
            stStatement.close();
            rs.close();
        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "Error while checking if bug details exist", JOptionPane.ERROR_MESSAGE);
        } finally {
            try{
                stStatement.close();
                rs.close();
            } catch(SQLException | NullPointerException ex){
            }
        }
        return boolStatus;
    }
    
    public boolean mCreateBugRecord(String strQuery) {
        Statement stStatement = null;
        try{
            stStatement = mConnectToDatabase().prepareStatement(strQuery);
            stStatement.execute(strQuery);
            stStatement.close();
            return true;
        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "Error while creating new bug details", JOptionPane.ERROR_MESSAGE);
        } finally {
            try{
                stStatement.close();
            } catch(SQLException | NullPointerException ex){
            }
        }
        return false;
    }
    
    public int mGetNumericField(String strQuery) {
        Statement stStatement = null;
        ResultSet rs = null;
        try{
            stStatement = mConnectToDatabase().prepareStatement(strQuery);
            rs = stStatement.executeQuery(strQuery);
            while(rs.next()){
                return rs.getInt(1);
            }
            stStatement.close();
            rs.close();
        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "Error while getting numeric field", JOptionPane.ERROR_MESSAGE);
        }   finally {
             try{
                    stStatement.close();
                    rs.close();
                } catch(SQLException | NullPointerException ex) {
                }
            }
        return 0;
    }
    
    public String mGetTextField(String strQuery) {
        Statement stStatement = null;
        ResultSet rs = null;
        try {
            stStatement = mConnectToDatabase().prepareStatement(strQuery);
            rs = stStatement.executeQuery(strQuery);
            while(rs.next()){
                return rs.getString(1);
            }
            stStatement.close();
            rs.close();
        } catch(SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "Error while gettting text field", JOptionPane.ERROR_MESSAGE);
        } finally {
            try{
                stStatement.close();
                rs.close();
            } catch(SQLException | NullPointerException ex) {
            }
        }
        return null;
    }
        
    public boolean mUpdateBugDetails(String strQuery) {
        Statement stStatement = null;
         try {
             stStatement = mConnectToDatabase().prepareStatement(strQuery);
             stStatement.executeUpdate(strQuery);
             stStatement.close();
             return true;
         } catch(SQLException ex) {
             JOptionPane.showMessageDialog(null, ex.getMessage(), 
                     "Error while updating bug details", JOptionPane.ERROR_MESSAGE);
         } finally {
             try{
                 stStatement.close();
             }catch(SQLException | NullPointerException ex){
             }
         }
        return false;
    }
}
