/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bugtrackingsystem.textpad;

import bugtrackingsystem.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Sanele
 */
public class clsTextProcessingView extends JFrame {
    public clsTextProcessingView() {
        this.setSize(700, 600);
        this.setTitle("PAEDIT");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        mCreateWindow();
        this.addWindowListener(new clsWindowCloser());
    }
    private JTextArea txtDoc = new JTextArea();
    private JLabel lblStatus;
    private clsModelDocument doc = new clsModelDocument();
    private boolean boolChanged = false;
    private String strTransaction = null;
    private static String strText = null; 
    
    private void mCreateWindow() {
        mCreateMenu();
        this.setLayout(new BorderLayout());
        JPanel jpPanel = new JPanel(new BorderLayout());
        jpPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
        jpPanel.add(mCreateToolBar(), BorderLayout.NORTH);
        jpPanel.add(lblStatus = mCreateStatus(), BorderLayout.SOUTH);
        jpPanel.add(mCreateField());
        this.add(jpPanel);
        txtDoc.requestFocusInWindow();
    }
        
    private void mCreateMenu() {
        JMenuBar mbMenuBar = new JMenuBar();
        mbMenuBar.add(mCreateFileMenu());
        mbMenuBar.add(mCreateEditMenu());
        setJMenuBar(mbMenuBar);
    }
    
    private JMenu mCreateFileMenu() {
        JMenu mnuMenuFile = new JMenu("File");
        mnuMenuFile.add(mCreateMenuItem("Open Document", this::mOpenDocument));
        mnuMenuFile.add(mCreateMenuItem("Save Document", this::mSave));
        mnuMenuFile.add(mCreateMenuItem("Save Locally", this::mSaveAs));
        mnuMenuFile.addSeparator();
        mnuMenuFile.add(mCreateMenuItem("Exit", this::mExit));
        return mnuMenuFile;
    }
    
    private JMenu mCreateEditMenu() {
        JMenu mnuMenuEdit = new JMenu("Edit");
        mnuMenuEdit.add(mCreateMenuItem("Copy", this::mCopy));
        mnuMenuEdit.add(mCreateMenuItem("Paste", this::mPaste));
        mnuMenuEdit.add(mCreateMenuItem("Cut", this::mCut));
        mnuMenuEdit.addSeparator();
        mnuMenuEdit.add(mCreateMenuItem("Search", this::mSearch));
        return mnuMenuEdit;
    }
    
    private JMenuItem mCreateMenuItem(String strText, ActionListener listener) {
      JMenuItem mnuItem = new JMenuItem(strText);
      mnuItem.addActionListener(listener);
      return mnuItem;
    }
    
    private JToolBar mCreateToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setBackground(clsTools.statusLineColour);
        toolBar.add(clsTools.mCreateButton(clsTools.mCreateImageIcon(
                "/bugtrackingsystem/textpad/images/save.png", 26),
                "Copy text from the clipboard", this::mSave));
        toolBar.addSeparator(new Dimension(10, 10));
        toolBar.add(clsTools.mCreateButton(clsTools.mCreateImageIcon(
                "/bugtrackingsystem/textpad/images/copy.png", 26),
                "Copy text from the clipboard", this::mCopy));
        toolBar.addSeparator(new Dimension(10, 10));
        toolBar.add(clsTools.mCreateButton(clsTools.mCreateImageIcon(
                "/bugtrackingsystem/textpad/images/paste.jpg", 26),
                "Insert text from the clipboard", this::mPaste));
        toolBar.addSeparator(new Dimension(10, 10));
        toolBar.setPreferredSize(new Dimension(0, 36));
        return toolBar;
    }
    
    private JScrollPane mCreateField() {
        txtDoc.setFont(clsTools.txtFont);
        txtDoc.setWrapStyleWord(true);
        txtDoc.addKeyListener(new clsTextChanged());
        return new JScrollPane(txtDoc);
    }
    
    private JLabel mCreateStatus() {
        JLabel lblLabel = new JLabel();
        lblLabel.setFont(clsTools.defFont);
        lblLabel.setOpaque(true);
        lblLabel.setBackground(clsTools.statusLineColour);
        lblLabel.setHorizontalAlignment(JLabel.LEFT);
        lblLabel.setPreferredSize(new Dimension(0, 25));
        return lblLabel;
    }
        
    private void mSetStatus()
    {
        String strInput = txtDoc.getText();
        char[] arrChar = new char[] {'(', ')', '"', '"',' ', ',', '.', '!', '?', '/', ';', '-', ':'};
        
        int intCharacterCount = 0, intWordCount = 1, intSentenceCount = 0, intParagraphCount = 0, intSentenceTempCount = 0;
        String strOutput;
        
        //Get Number of words
        for(int i = 0; i < strInput.length() - 1; ++i)
        {
            if(strInput.charAt(i) == ' ')
            {
                intWordCount++;
            }          
        }
        
        //Get number of characters
        for(int i = 0; i < strInput.length(); i++)
        {
            for(int index = 0; index < arrChar.length; index++)
            {
              if(strInput.charAt(i) == arrChar[index])
              {
                  intCharacterCount++;
              }
            }
        }
        
        //Get number of sentences
        for(int i = 0; i < strInput.length(); i++)
        {
            if(strInput.charAt(i) == '.' || strInput.charAt(i) == '!' || strInput.charAt(i) == '?')
            {
                intSentenceTempCount++;
                intSentenceCount++;
                if(intSentenceTempCount > 4 && (strInput.charAt(i) == '.' || strInput.charAt(i) == '!' || strInput.charAt(i) == '?')){
                    intParagraphCount++;
                    intSentenceTempCount = 0;
                }
            }
        }
        
        System.out.println((txtDoc.getText().length() + 1)+" characters");
        strOutput = intWordCount+" words, "+ intCharacterCount+" characters,"+
                intSentenceCount+" sentences,"+intParagraphCount+" paragraphs";
        lblStatus.setText(txtDoc.getLineCount()+" line(s), " + strOutput);
    }
    
    public void mSetTransactionString(String strTransaction) {
        this.strTransaction = strTransaction;
    }
    
    private void mSave(ActionEvent e) {
        switch(strTransaction) {
            case "Create":
                mSetFileText();
                new clsMainWindow().mRecordBugDetails();
                break;
            case "Update":
                mSetFileText();
                clsDialogs dialog = new clsDialogs();
                dialog.new clsUpdateDialog(null, "").mUpdateTransactionsCommit();
                break;
        }
    }
    
    private void mSaveAs(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser(
                new File(System.getProperty("user.home")));
        fileChooser.setDialogTitle("Save file locally");
        if(fileChooser.showSaveDialog(clsTextProcessingView.this) 
                == JFileChooser.APPROVE_OPTION) {
            if(doc.mSave(new File(fileChooser.getCurrentDirectory()+"/BugFile.txt"))) {
                lblStatus.setText("Saved!!");
                txtDoc.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(this, "The document could not be saved",
                "Saving Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void mOpenDocument(String str) {
        doc.mSetText(str);
        txtDoc.setText(doc.mGetText());
        mSetStatus();
    }
    
    private void mSetFileText() {
        strText = txtDoc.getText();
    }
    
    public String mGetFileText() {
        return strText;
    }
    
    private void mOpenDocument(ActionEvent e) {
        if(boolChanged && JOptionPane.showConfirmDialog(this,
                "The document is changed. Should document be saved?",
                "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
            == JOptionPane.YES_OPTION) {
            mSave(e);
        }
        JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.home")));
        fileChooser.setDialogTitle("Open text file");
        if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File flFile = fileChooser.getSelectedFile();
            try {
                doc = new clsModelDocument(flFile);
                txtDoc.setText(doc.mGetText());
                boolChanged = false;
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "The document could not be opened",
                        "Error Message", JOptionPane.OK_OPTION);
                txtDoc.setText("");
                boolChanged = false;
            }
            mSetStatus();
        }
    }
    
    private void mCopy(ActionEvent e) {
         try {
             String strText = txtDoc.getSelectedText();
             Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
             cb.setContents(new StringSelection(strText), null);
         } catch(Exception ex) {
             JOptionPane.showMessageDialog(this, "Text could not be copied to the clipboard",
                     "Error message", JOptionPane.OK_OPTION);
         }
    }
    
    private void mPaste(ActionEvent e) {
        try {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable data = cb.getContents(this);
            if(data == null) {
                return;
            }
            String str = (String)data.getTransferData(DataFlavor.stringFlavor);
            String text = txtDoc.getText();
            int intDocSelectionStart = txtDoc.getSelectionStart();
            int intDocSelectionEnd = txtDoc.getSelectionEnd();
            if(intDocSelectionEnd > intDocSelectionStart){
                txtDoc.setText(intDocSelectionEnd < text.length() ? 
                        text.substring(0, intDocSelectionStart) + str + text.substring(intDocSelectionEnd + 1) 
                        : text.substring(0, intDocSelectionStart) + str);
                
            } else {
                intDocSelectionStart = txtDoc.getCaretPosition();
                txtDoc.setText(text.substring(0, intDocSelectionStart) +
                        str + text.substring(intDocSelectionEnd));
            }
            boolChanged = true;
            mSetStatus();
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "The text could not be pasted from the clipboard",
                    "Error message", JOptionPane.OK_OPTION);
        }
    }
    
    private void mCut(ActionEvent e) {
        mCopy(e);
        int intDocSelectionStart = txtDoc.getSelectionStart();
        int intDocSelectionEnd = txtDoc.getSelectionEnd();
        txtDoc.replaceRange("", intDocSelectionStart, intDocSelectionEnd);
        txtDoc.requestFocusInWindow();
    }
    
    private void mSearch(ActionEvent e) {
        String str = JOptionPane.showInputDialog(this, "Enter a search text", "Search",
                JOptionPane.INFORMATION_MESSAGE);
        if(str != null && str.length() > 0){
            String text = txtDoc.getText();
            int intIndexOfSearchString = text.indexOf(str);
            if(intIndexOfSearchString >= 0) {
                txtDoc.select(intIndexOfSearchString, intIndexOfSearchString + str.length());
                txtDoc.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(this, "The text does not exist",
                        "Information", JOptionPane.OK_OPTION);
            }
        }
    }
    
    private void mExit(ActionEvent e){
        this.dispose();
    }
    
    class clsTextChanged extends KeyAdapter {
        public void mKeyTyped(KeyEvent e) {
            boolChanged = true;
            mSetStatus();
        }
    }
    
    class clsWindowCloser extends WindowAdapter {
        public void mWindowClosing(WindowEvent e) {
            if(boolChanged && JOptionPane.showConfirmDialog(clsTextProcessingView.this,
                    "The document is changed. Do you want to save it?",
                    "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                doc.mSetText(txtDoc.getText());
                if(doc.mSaveFile()) {
                    boolChanged = false;
                }
            }
        }
    }
}
