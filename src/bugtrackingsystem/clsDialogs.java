/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bugtrackingsystem;

import bugtrackingsystem.textpad.clsTextProcessingView;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import javax.swing.*;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Sanele
 */
public class clsDialogs extends JDialog {

    public clsDialogs() {
        super(null, Dialog.ModalityType.APPLICATION_MODAL); //Set the dialog to rquire all the focus
    }

    JPanel jpContainer = mPrepareContainer();
    JComboBox cboCombo = new JComboBox();
    JTextField txtText = new JTextField();

    private void mSetModel() {
        new clsMainWindow().mSetOpenBugReports(null);
    }
    
    private JPanel mPrepareContainer() {
        JPanel jpPanel = new JPanel(new BorderLayout(0, 20));  //Creates a JPanel container object and sets its layout
        jpPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); //Sets a border 
        jpPanel.setOpaque(true); //Prepares the JPanel to be applied colour
        jpPanel.setBackground(new Color(255, 255, 255)); //Sets background colour to the JPanel   
        return jpPanel;
    }

    private void mDialogProperties() {
        this.setTitle("");
        this.setSize(400, 200); //sets size of the dialog
        this.setLocationRelativeTo(null); //displays the dialog at the very center
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE); //Causes the dialog to be exited but not the entire app
    }

    private JPanel mCreateDialogCenter(String strQuery) {
        JPanel jpCenterPart = new JPanel(new BorderLayout()); //A JPanel to contain the center part of the dialog GUI
        jpCenterPart.add(cboCombo, BorderLayout.CENTER);
        mLoadToComboBox(strQuery, cboCombo);
        return jpCenterPart;
    }

    private JPanel mCreateDialogBottom(ActionListener listener) {
        JPanel jpLowerPart = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); //A JPanel to contain the lower part of the GUI
        jpLowerPart.setOpaque(true);
        jpLowerPart.setBackground(new Color(255, 255, 255));
        JButton btn = new clsMainWindow().mCreateButton(90, 25, "Ok", listener);
        jpLowerPart.add(btn);
        return jpLowerPart;
    }

    public void mCreateUpdateDialog() {
        mDialogProperties();
        jpContainer.add(new clsMainWindow().mCreateLabel("Select the bug to update", new Font("Tahoma", Font.BOLD, 14)), BorderLayout.NORTH);
        jpContainer.add(mCreateDialogCenter("SELECT ID, Description FROM tblBugs"));
        jpContainer.add(mCreateDialogBottom(this::mFetchForUpdate), BorderLayout.SOUTH);
        this.add(jpContainer); 
        this.setVisible(true);
    }
    private void mCreateAddProjectDialog() {
        mDialogProperties();
        jpContainer.add(new clsMainWindow().mCreateLabel("Provide The name of the project you're currently working on",
                new Font("Tahoma", Font.BOLD, 14)), BorderLayout.NORTH);
        jpContainer.add(txtText, BorderLayout.CENTER);
        jpContainer.add(mCreateDialogBottom(this::mAddProjectName), BorderLayout.SOUTH);
        this.add(jpContainer);
        this.setVisible(true);
    }
    public void mCreateExceptionSpecifyingDialog() {
        mDialogProperties();
        jpContainer.add(new clsMainWindow().mCreateLabel("Specify Exception", new Font("Tahoma", Font.BOLD, 14)), BorderLayout.NORTH);
        jpContainer.add(txtText, BorderLayout.CENTER);
        jpContainer.add(mCreateDialogBottom(this::mSpecifyException), BorderLayout.SOUTH);
        this.add(jpContainer);
        this.setVisible(true);
    }

    public void mCreateFilterByProjectDialog() {
        mDialogProperties();
        JLabel lblLabel = new clsMainWindow().mCreateLabel("Select Project to Filter by", new Font("Tahoma", Font.BOLD, 14));
        jpContainer.add(lblLabel, BorderLayout.NORTH);
        jpContainer.add(mCreateDialogCenter("SELECT ProjectName FROM tblProjects"));
        jpContainer.add(mCreateDialogBottom(this::mSetFilterByProjectModel), BorderLayout.SOUTH);
        this.add(jpContainer);
        this.setVisible(true);
    }

    private String mCleaner(StringBuilder sb) {
        if ((sb.toString().contains(".") && sb.toString().contains("util"))
                    || (sb.toString().contains(".") && sb.toString().contains("lang"))
                    || (sb.toString().contains("io") && sb.toString().contains("."))) {
            for (int i = 0; i < 2; i++) {
                sb.delete(0, sb.indexOf(".")).deleteCharAt(sb.indexOf("."));
            }
        }
        return sb.substring(0, sb.indexOf("|")).trim();
    }
    
    
    public String[] mCleanExceptionName() {
        String[] arr = new String[new clsDatabaseInterface().mGetNumericField("SELECT COUNT(ExceptionName) FROM tblBugTypes")];
        StringBuilder builder = mArrayData("SELECT ExceptionName FROM tblBugTypes");
        for(int i = 0; i < arr.length; i++) {
            arr[i] = mCleaner(builder);
        }
        return arr;
    }

    public void mCreateFilterByBugDialog() {
        mDialogProperties();
        JLabel lblLabel = new clsMainWindow().mCreateLabel("Select bug type to Filter by", new Font("Tahoma", Font.BOLD, 14));
        jpContainer.add(lblLabel, BorderLayout.NORTH);
        JPanel jpCenterPart = new JPanel(new BorderLayout()); //A JPanel to contain the center part of the dialog GUI
        for (int i = 0; i < mCleanExceptionName().length; i++) {
            cboCombo.addItem(mCleanExceptionName()[i]);
        }
        jpCenterPart.add(cboCombo, BorderLayout.CENTER);
        jpContainer.add(jpCenterPart, BorderLayout.CENTER);
        jpContainer.add(mCreateDialogBottom(this::mSetFilterByBugType), BorderLayout.SOUTH);
        this.add(jpContainer);
        this.setVisible(true);
    }

    public void mCreateFilterByStatusDialog() {
        mDialogProperties();
        JLabel lblLabel = new clsMainWindow().mCreateLabel("Select Status to Filter by", new Font("Tahoma", Font.BOLD, 14));
        jpContainer.add(lblLabel, BorderLayout.NORTH);
        cboCombo.addItem("Open - Not Yet Solved");
        cboCombo.addItem("Close - Solved");
        jpContainer.add(cboCombo, BorderLayout.CENTER);
        jpContainer.add(mCreateDialogBottom(this::mSetFilterByBugStatus), BorderLayout.SOUTH);
        this.add(jpContainer);
        this.setVisible(true);
    }

    public void mCreateFilterAphabeticallyDialog() {
        clsFilterByAlphabetDialog filterByAlphaDialog = new clsFilterByAlphabetDialog();
    }

    public void mCreateSearchDialog() {
        mDialogProperties();
        JLabel lblLabel = new clsMainWindow().mCreateLabel("Search for a solution to your bug", new Font("Tahoma", Font.BOLD, 14));
        jpContainer.add(lblLabel, BorderLayout.NORTH);
        jpContainer.add(txtText, BorderLayout.CENTER);
        JPanel jpLowerPart = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); //A JPanel to contain the lower part of the GUI
        jpLowerPart.setOpaque(true);
        jpLowerPart.setBackground(new Color(255, 255, 255));
        JButton btn = new clsMainWindow().mCreateButton(90, 25, "Search", cboCombo);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = "";
                url = txtText.getText().equals("") ? "https://google.com/" : url+txtText.getText();
                if (Desktop.isDesktopSupported()
                        && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.browse(new URI(url));
                    } catch (IOException | URISyntaxException eX) {
                        JOptionPane.showMessageDialog(null, "A technical error has been encountered\n" + eX);
                    }
                } else {
                    Runtime runtime = Runtime.getRuntime();
                    try {
                        runtime.exec("xdg-open " + url);
                    } catch (IOException eX) {
                        JOptionPane.showMessageDialog(null, "A technical error has been encountered\n" + eX);
                    }
                }
                mCloseDialog();
            }
        });
        jpLowerPart.add(btn);
        jpContainer.add(jpLowerPart, BorderLayout.SOUTH);
        this.add(jpContainer);
        this.setVisible(true);
    }

    //A method to destroy a current object of this class
    private void mCloseDialog() {
        this.hide();
    }

    private StringBuilder mArrayData(String strQuery) {
        StringBuilder builder = new StringBuilder();
        Statement stStament = null;
        ResultSet rs = null;
        try {
            stStament = new clsDatabaseInterface().mConnectToDatabase().prepareStatement(strQuery);
            rs = stStament.executeQuery(strQuery);
            while (rs.next()) {
                builder.append(rs.getString(1)).append("|");
            }
            stStament.close();
            rs.close();
            return builder;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                stStament.close();
                rs.close();
            } catch (SQLException ex) {
            }
        } 
        return null;
    }

    //A methos to fetch details from the database and populate thie dialog's conbo box
    private void mLoadToComboBox(String strQuery, JComboBox cbo) {
        Statement stStatement = null;
        ResultSet rs = null;
        try {
            stStatement = new clsDatabaseInterface().mConnectToDatabase().prepareStatement(strQuery);
            rs = stStatement.executeQuery(strQuery);
            ResultSetMetaData rsmt = rs.getMetaData();
            while (rs.next()) {
                if (rsmt.getColumnCount() == 2) {
                    cbo.addItem(rs.getInt(1) + " " + rs.getString(2));
                } else {
                    cbo.addItem(rs.getString(1));
                }
            }
            stStatement.close();
            rs.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error while loading to combo box", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                stStatement.close();
                rs.close();
            } catch (SQLException ex) {
            }
        }
    }

    private void mFetchForUpdate(ActionEvent e) {
        String[] arrBugDetails = new String[8];
        String strBugId = cboCombo.getSelectedItem().toString().substring(
                0, cboCombo.getSelectedItem().toString().indexOf(" ")).trim();
        arrBugDetails[0] = String.valueOf(new clsDatabaseInterface().mGetNumericField("SELECT ProjectWithBug FROM tblBugs WHERE ID ='"
                + strBugId + "'"));
        arrBugDetails[1] = new clsDatabaseInterface().mGetTextField("SELECT Description FROM tblBugs WHERE ID ='" + strBugId + "'");
        arrBugDetails[2] = new clsDatabaseInterface().mGetTextField("SELECT Severity FROM tblBugs WHERE ID ='"+ strBugId + "'");
        arrBugDetails[3] = new clsDatabaseInterface().mGetTextField("SELECT BugFile FROM tblBugs WHERE ID ='"+ strBugId +"'");
        arrBugDetails[4] = new clsDatabaseInterface().mGetTextField("SELECT BugStatus FROM tblBugs WHERE ID ='"+ strBugId+"'");
        arrBugDetails[5] = new clsDatabaseInterface().mGetTextField("SELECT ProjectName FROM tblProjects WHERE ID ='"
                + new clsDatabaseInterface().mGetNumericField("SELECT ProjectWithBug FROM tblBugs WHERE ID ='" + strBugId
                        + "'")+"'");
        arrBugDetails[6] = new clsDatabaseInterface().mGetTextField("SELECT StartDate FROM tblProjects WHERE ID ='"
                + new clsDatabaseInterface().mGetNumericField("SELECT ProjectWithBug FROM tblBugs WHERE ID ='" + strBugId + "'")+"'");
        arrBugDetails[7] = new clsDatabaseInterface().mGetTextField("SELECT EndDate FROM tblProjects WHERE ID ='"
                + new clsDatabaseInterface().mGetNumericField("SELECT ProjectWithBug FROM tblBugs WHERE ID ='" + strBugId + "'")+"'");
        clsUpdateDialog dialogUpdate = new clsUpdateDialog(arrBugDetails, strBugId);
        dialogUpdate.setVisible(true);
        mCloseDialog();
    }

    private void mAddProjectName(ActionEvent e) {
        //Get text and send to arrBugDetails
    }
    
    private void mSpecifyException(ActionEvent e) {
        new clsUpdateDialog(null, "").mOpenBugReportFile(new clsMainWindow().mBugTextModel(), "Create");
        if(!txtText.getText().equals("")) {
            if(new clsDatabaseInterface().mCheckDetailsExist("SELECT * FROM tblBugTypes WHERE ExceptionName LIKE '%"+
                    txtText.getText()+"%'"))
                JOptionPane.showMessageDialog(this, "This Exception name does exist in the exceptions combo box",
                        "WARNING", JOptionPane.WARNING_MESSAGE);
            else{
                new clsMainWindow().mSpecificExceptionName(txtText.getText());
            }
        }
        mCloseDialog();
    }
    
    private void mSetFilterByProjectModel(ActionEvent e) {
        mSetModel();
        new clsMainWindow().mSetLstOpenBugReportModel("SELECT Desription FROM tblBugs WHERE ProjectWithBug ='"
                + new clsDatabaseInterface().mGetNumericField("SELECT ID FROM tblProjects WHERE ProjectName ='"
                        + cboCombo.getSelectedItem().toString() + "'"));
        mCloseDialog();
    }

    private void mSetFilterByBugType(ActionEvent e) {
        mSetModel();
        new clsMainWindow().mSetLstOpenBugReportModel("SELECT Description FROM tblBugs WHERE ProjectWithBug ='"
                + new clsDatabaseInterface().mGetNumericField("SELECT ID FROM tblProjects WHERE ProjectName ='"
                        + cboCombo.getSelectedItem().toString() + "'"));
        mCloseDialog();
    }

    private void mSetFilterByBugStatus(ActionEvent e) {
        mSetModel();
        new clsMainWindow().mSetLstOpenBugReportModel("SELECT Description FROM tblBugs WHERE BugStatus ='" +
                cboCombo.getSelectedItem().toString() + "'");
        mCloseDialog();
    }

    //A method that create the GUI of the dialog by specifying how each component
    //should be positioned
    public class clsUpdateDialog extends JDialog {
        public clsUpdateDialog(String[] arr, String id) {
            super(null, "Update Reported Bug Details", Dialog.ModalityType.APPLICATION_MODAL);
            this.setSize(500, 500);
            this.setLocationRelativeTo(null);
            this.setLayout(new BorderLayout(10, 20));
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            mCreateDialogWindow();
            mSetBugReportDetailsToGUI(arr, id);
        }
        
        private JComboBox cboExceptionNames = new JComboBox();
        private JTextField txtDescription = new JTextField();
        private JTextField txtProject = new JTextField();
        private JTextField txtStartDate = new JTextField();
        private JTextField txtProjectEndDate = new JTextField();
        private JTextField txtBugEndDate = new JTextField();
        private JComboBox cboSeverity = new JComboBox();
        private JComboBox cboStatus = new JComboBox();
        private String[] arrBugDetails = new String[7];
        private String strId;
        
        private void mCreateDialogWindow() {
            this.setLayout(new BorderLayout());
            JPanel jpPanel = new JPanel(new BorderLayout());
            jpPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            jpPanel.setOpaque(true);
            jpPanel.setBackground(new Color(255, 255, 255));
            jpPanel.add(mCreateWindowTop(), BorderLayout.NORTH);
            jpPanel.add(mCreateWindowCenter(), BorderLayout.CENTER);
            jpPanel.add(mCreateWindowBottom(), BorderLayout.SOUTH);
            this.add(jpPanel);  
        }
        
        private JPanel mCreateWindowTop() {
            JPanel jpTop = new JPanel(new BorderLayout());
            jpTop.setBorder(new EmptyBorder(10, 10, 10, 10));
            jpTop.setOpaque(true);
            jpTop.setBackground(new Color(255, 255, 255));
            jpTop.add(new clsMainWindow().mCreateLabel(
                    "Update Bug Details", new Font("Tahoma", Font.BOLD, 28)),
                    BorderLayout.NORTH);
            return jpTop;
        }
        
        private JPanel mCreateWindowCenter() {
            JPanel jpCenter = new JPanel(new GridLayout(8, 1, 0, 20));
            jpCenter.setOpaque(true);
            jpCenter.setBackground(new Color(255, 255, 255));
            jpCenter.add(mAddComponent("Exception name", cboExceptionNames = new JComboBox()));
            jpCenter.add(mAddComponent("Bug Description", new clsMainWindow().mTextFieldDimensions(txtDescription, 80, 30, 
                    "Update the description of what caused the bug")));
            jpCenter.add(mAddComponent("Bug Severity", cboSeverity = new clsMainWindow().mComboBoxDimensions(80, 30)));
            jpCenter.add(mAddComponent("Bug Status", cboStatus = new clsMainWindow().mComboBoxDimensions(80, 30)));
            jpCenter.add(mAddComponent("Bug Close Date", new clsMainWindow().mTextFieldDimensions(txtBugEndDate, 80, 30,
                    "Update the date of when the bug was solved")));
            jpCenter.add(mAddComponent("Project name", new clsMainWindow().mTextFieldDimensions(txtProject, 80, 30,
                    "Update the name of the project")));
            jpCenter.add(mAddComponent("Project Start Date", new clsMainWindow().mTextFieldDimensions(txtStartDate, 80, 30, 
                    "Update Start Date of the project")));
            jpCenter.add(mAddComponent("Project End Date", new clsMainWindow().mTextFieldDimensions(txtProjectEndDate, 80, 30,
                    "Update End Date of the project")));
            return jpCenter;
        }
        
        private JPanel mCreateWindowBottom() {
            JPanel jpBottom = new JPanel(new GridLayout(1, 2, 40, 0));
            jpBottom.setOpaque(true);
            jpBottom.setBackground(new Color(255, 255, 255));
            JButton btnSave = new clsMainWindow().mCreateButton(80, 30, "Save", this::mSave);
            JButton btnCancel = new clsMainWindow().mCreateButton(80, 30, "Cancel", this::mCancelUpdate);
            jpBottom.add(btnSave);
            jpBottom.add(btnCancel);
            return jpBottom;
        }
        
        private JPanel mAddComponent(String str, Component component) {
            JPanel jpComponent = new JPanel(new GridLayout(1, 2, 40, 0));
            jpComponent.setOpaque(true);
            jpComponent.setBackground(new Color(255, 255, 255));
            JLabel lblLabel = new JLabel(str);
            lblLabel.setSize(new Dimension(80, 30));
            jpComponent.add(lblLabel);
            component.setSize(new Dimension(200, 30));
            jpComponent.add(component);
            return jpComponent;
        }
                
        private void mSetBugReportDetailsToGUI(String[] arr, String strID) {
            this.arrBugDetails = arr;
            this.strId = strID;
            cboExceptionNames.setSelectedItem(new clsDatabaseInterface().mGetTextField("SELECT ExceptionName FROM tblBugTypes WHERE ID ='"+
                    new clsDatabaseInterface().mGetNumericField("SELECT BugType FROM tblBugs WHERE ID ='"+strId+"'")+"'"));
            txtDescription.setText(arrBugDetails[1]);
            cboSeverity.setSelectedItem(arrBugDetails[2]);
            cboStatus.setSelectedItem(arrBugDetails[4]);
            txtBugEndDate.setText(arrBugDetails[4]);
            txtProject.setText(arrBugDetails[5]);
            txtStartDate.setText(arrBugDetails[6]);
            txtProjectEndDate.setText(arrBugDetails[7]);
        }
        
        private String[] mGetBugDetailsFromGUI() {
            return new String[] {
                cboExceptionNames.getSelectedItem().toString(),
                txtDescription.getText(), cboSeverity.getSelectedItem().toString(),
                cboStatus.getSelectedItem().toString(), txtBugEndDate.getText(),
                txtProject.getText(), txtStartDate.getText(), txtProjectEndDate.getText()
            };
        }
        
        private String mUpdateReportedBug() {
            return "UPDATE tblBugs SET Description ='"+arrBugDetails[1]+"' Severity='"+
                    arrBugDetails[2] + "' BugFile ='"+ new clsTextProcessingView().mGetFileText() +"' BugStatus ='"+
                    arrBugDetails[3] +"' Opened ='" +arrBugDetails[4] +"' Closed ='"+
                    txtBugEndDate.getText() == null ? new clsDatabaseInterface().mGetTextField("SELECT Closed FROM tblBugs WHERE ID ='"+strId+"'") : txtBugEndDate.getText()
                    +"' BugType='"+ new clsDatabaseInterface().mGetNumericField("SELECT ID FROM tblBugTypes WHERE ExceptionName LIKE '%"+arrBugDetails[0]+"%'")
                    +"' WHERE ID ='"+strId+"'";
        }
        
        private String mUpdateProject() {
            return "UPDATE tblProjects SET StartDate ='"+txtStartDate.getText() == null ? 
                    new clsDatabaseInterface().mGetTextField("SELECT StartDate FROM tblProjects WHERE ID ='"+
                    new clsDatabaseInterface().mGetNumericField("SELECT ProjectWithBug FROM tblBugs WHERE ID='"+strId+"'")+"'") : txtStartDate.getText()+
                    "' EndDate ='"+ txtProjectEndDate.getText() == null ? 
                    new clsDatabaseInterface().mGetTextField("SELECT EndDate FROM tblProjects WHERE ID ='"+
                    new clsDatabaseInterface().mGetNumericField("SELECT ProjectWithBug FROM tblBugs WHERE ID='"+strId+"'")+"'") : txtProjectEndDate.getText()+
                    "' WHERE ID ='"+ new clsDatabaseInterface().mGetNumericField("SELECT ProjectWithBug FROM tblBugs WHERE ID='"+strId+"'");
        }
                
        public void mUpdateTransactionsCommit() {
            arrBugDetails = mGetBugDetailsFromGUI();
            if(new clsDatabaseInterface().mUpdateBugDetails(mUpdateReportedBug()) &&
                    new clsDatabaseInterface().mUpdateBugDetails(mUpdateProject())) {
                JOptionPane.showMessageDialog(this, "Update Sucessful!", "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
            }
        }
           
        public void mOpenBugReportFile(String strText, String strTransaction) {
            clsTextProcessingView clsTextView = new clsTextProcessingView();
            clsTextView.mSetTransactionString(strTransaction);
            clsTextView.mOpenDocument(strText);
            clsTextView.setVisible(true);
        }
        
        private void mSave(ActionEvent e) {
            if(txtDescription.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Provide a brief description of the bug",
                    "WARNING", JOptionPane.WARNING_MESSAGE);
                txtDescription.requestFocusInWindow();
            } else if(txtBugEndDate.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Provide Bug Close Date",
                        "WARNING", JOptionPane.WARNING_MESSAGE);
                txtBugEndDate.requestFocusInWindow();
            } else if(LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(
                    new java.util.Date())).isAfter(LocalDate.parse(
                            txtBugEndDate.getText().replace("/", "-")))) {
                JOptionPane.showMessageDialog(this, "Provide the correct date the bug was closed",
                    "WARNING", JOptionPane.WARNING_MESSAGE);
                txtBugEndDate.requestFocusInWindow();
            }else if(txtProject.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Provide the name of the project you are currently working on",
                    "WARNING", JOptionPane.WARNING_MESSAGE);
                txtProject.requestFocusInWindow();
            } else if(txtStartDate.equals("")) {
                JOptionPane.showMessageDialog(this, "Provide the project start date",
                    "WARNING", JOptionPane.WARNING_MESSAGE);
                txtStartDate.requestFocusInWindow();
            }else if (txtProjectEndDate.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Provide the project end date",
                    "WARNING", JOptionPane.WARNING_MESSAGE);
                txtProjectEndDate.requestFocusInWindow();
            } else if(LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(
                    new java.util.Date())).isAfter(LocalDate.parse(
                            txtProjectEndDate.getText().replace("/", "-")))) {
                JOptionPane.showMessageDialog(this, "Provide the correct project end date. \nThe project cannot have possibly ended while still getting errors on",
                        "WARNING", JOptionPane.WARNING_MESSAGE);
                txtProjectEndDate.requestFocusInWindow();
            } else {
                if(cboExceptionNames.getSelectedItem().toString().equals("Other...")) {
                    new clsDialogs().mCreateExceptionSpecifyingDialog();
                } else {
                    mOpenBugReportFile(arrBugDetails[3], "Update");
                }
            }
        }
        
        private void mCancelUpdate(ActionEvent e) {
            this.dispose();
        }
    }

    
    public class clsFilterByAlphabetDialog extends JDialog {

        public clsFilterByAlphabetDialog() {
            super(null, "Select Alphabet to Filter by", Dialog.ModalityType.APPLICATION_MODAL);
            this.setSize(500, 200);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.setLayout(new BorderLayout());
            JLabel lblLabel = new clsMainWindow().mCreateLabel("Select A Letter to Filter by that Represents The First Character of an Exception Name",
                    new Font("Tahoma", Font.BOLD, 14));
            this.add(lblLabel, BorderLayout.NORTH);
            this.add(new JLabel());
            mCreateDialogWindow();
            this.setVisible(true);
        }
        String[] arrCleanExceptionNames = new clsDialogs().mCleanExceptionName();
        String strButtonLetter;
        private void mCreateDialogWindow() {
            JPanel jpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            jpPanel.setOpaque(true);
            jpPanel.setBackground(new Color(255, 255, 255));
            for (char c = 'A'; c <= 'Z'; c++) {
                jpPanel.add(mButton("" + c));
            }
            this.add(jpPanel);
        }

        private DefaultListModel mModel(String[] arrModel) {
            DefaultListModel model = new DefaultListModel();
            for (int i = 0; i < arrModel.length; i++) {
                model.addElement(arrModel[i]);
            }
            return model;
        }

        private int mGetFilteredArrayLength(String strLetter) {
            int intCount = 0;
            for (int i = 0; i < arrCleanExceptionNames.length; i++) {
                if (arrCleanExceptionNames[i].startsWith(strLetter)) {
                    intCount++;
                }
            }
            return intCount;
        }
        
        private JButton mButton(String strText) {
            JButton btnButton = new clsMainWindow().mCreateButton(50, 30, strText, null);
            btnButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {           
                    String[] arrFilteredArray = new String[mGetFilteredArrayLength(strButtonLetter)];
                    for (int i = 0; i < arrCleanExceptionNames.length; i++) {
                        if (arrCleanExceptionNames[i].startsWith(strButtonLetter)) {
                            for(int index = 0; i < arrFilteredArray.length; index++) {
                                arrFilteredArray[index] = arrCleanExceptionNames[i];
                            }
                        }
                    }
                    String[] arrBugTypeID = new String[arrFilteredArray.length];
                    for(int i = 0; i < arrBugTypeID.length; i++) {
                        arrBugTypeID[i] = String.valueOf(new clsDatabaseInterface().mGetNumericField(
                                "SELECT ID FROM tblBugTypes WHERE ExceptionName ='"+arrFilteredArray[i]+"'"));
                    }
                    StringBuilder s = new StringBuilder();
                    for(int i = 0; i < arrBugTypeID.length; i++) {
                        s.append(new clsDatabaseInterface().mGetTextField("SELECT Description FROM tblBugs WHERE BugType ='"+arrBugTypeID[i]+"'")).append("|");
                    }
                    for(int i = 0; i < arrFilteredArray.length; i++) {
                        arrFilteredArray[i] = new clsDialogs().mCleaner(s);
                    }
                    mSetModel();
                    new clsMainWindow().mSetOpenBugReports(mModel(arrFilteredArray));
                    new clsMainWindow().mSetLstOpenBugReportModel("");
                    new clsDialogs().mCloseDialog();
                }
            });
            return btnButton;
        }
    }
}