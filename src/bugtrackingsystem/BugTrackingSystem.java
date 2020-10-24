/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bugtrackingsystem;

/**
 *
 * @author Sanele
 */
public class BugTrackingSystem {

    private String[] mGetExceptions() {
        return new String[]{
            ArithmeticException.class.getName(), ArrayIndexOutOfBoundsException.class.getName(),
            ArrayStoreException.class.getName(), java.util.EmptyStackException.class.getName(),
            java.util.NoSuchElementException.class.getName(), java.io.IOException.class.getName()
        };
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
               
        if(!(new clsDatabaseInterface().mCheckDetailsExist("SELECT * FROM tblBugTypes"))) {
            for(int i = 0; i < new BugTrackingSystem().mGetExceptions().length; i++) {
                new clsDatabaseInterface().mCreateBugRecord("INSERT INTO tblBugTypes(ExceptionName)"
                        + "VALUES('"+new BugTrackingSystem().mGetExceptions()[i]+"')");
            }
        }
        clsMainWindow clsMain = new clsMainWindow();
        clsMain.setVisible(true);
    }
    
}
