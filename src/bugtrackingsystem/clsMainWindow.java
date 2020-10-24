/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bugtrackingsystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import bugtrackingsystem.textpad.*;
import com.sun.java.swing.plaf.motif.MotifBorders;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import javax.swing.border.*;
/**
 *
 * @author Sanele
 */
public class clsMainWindow extends JFrame {
    public clsMainWindow() {
        this.setTitle("Bug Tracking System / Bug-tionary");
        this.setExtendedState(MAXIMIZED_BOTH);
        this.setMinimumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        mMainWindow();
        txtDescription.requestFocusInWindow();
        mLoadComboBox();
        cboExceptionNames.addItem("Other...");
        mLoadSeverityComboBox();
    }
    
    private JTextField txtDescription = new JTextField();
    private JComboBox cboProjectName = new JComboBox();
    private JTextField txtStartDate = new JTextField();
    private JTextField txtEndDate = new JTextField();
    private JComboBox cboExceptionNames = new JComboBox();
    private JComboBox cboSeverity = new JComboBox();
    private JComboBox cboStatus = new JComboBox();
    private JList lstReportedBugs = new JList();
    private JList lstOpenBugReports = new JList();
    private DefaultListModel modelOpenBugReports = new DefaultListModel();
    private DefaultListModel modelReportedBugs = new DefaultListModel();
    private String[] arrBugDetails = new String[8];
            
    public void mSetOpenBugReports(DefaultListModel model) {
        this.modelOpenBugReports = model;
    }
        
    private void mMainWindow() {
        this.setLayout(new BorderLayout());
        JPanel jpMainPanel = new JPanel(new GridBagLayout());
        jpMainPanel.setOpaque(true);
        jpMainPanel.setBackground(new Color(255, 255, 255));
        this.add(mCreateHeader(), BorderLayout.NORTH);
        jpMainPanel.add(mCreateCenter());
        this.add(mCreateFooter(), BorderLayout.SOUTH);
        mSetLstReportedBugs();
        this.add(jpMainPanel);
    }
    
    private String[] mGetBugReportDetails() {
        return arrBugDetails = new String[] {
            cboExceptionNames.getSelectedItem().toString(),
            txtDescription.getText(), cboSeverity.getSelectedItem().toString(),
            cboProjectName.getSelectedItem().toString(), txtStartDate.getText(),
            txtEndDate.getText(), "Open"
        };
    }
    
    public void mRecordBugDetails() {
        if(new clsDatabaseInterface().mCreateBugRecord(mInsertIntoTableProjects())) {
            if(new clsDatabaseInterface().mCreateBugRecord(mInsertIntoTableBugs())) {
                JOptionPane.showMessageDialog(clsMainWindow.this, "Bug Reported Sucessfully",
                    "Bug Report", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(clsMainWindow.this, "The window is about to be refreshed",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                new clsMainWindow().setVisible(true);
            }
        }
    }
    
    private String mInsertIntoTableBugs() {
        Calendar c = Calendar.getInstance(); //Get a calendar instance
        c.setTime(new Date()); // Now use current dtDate.
        c.add(Calendar.DATE, 7); // Adds 7 days to the current dtDate
        java.util.Date dt = c.getTime();
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
        
        return "INSERT INTO tblBugs(Description, Severity, BugStatus, Opened, Closed, ProjectWithBug, BugFile, BugType)"
                + "VALUES('"+arrBugDetails[1]+"','"+arrBugDetails[2]+"','"+arrBugDetails[6]
                +"','"+arrBugDetails[4].replace('/', '-')+"','"+(txtEndDate.getText().equals(
                        new SimpleDateFormat("yyyy/MM/dd").format(new Date())) ? sm.format(dt) : arrBugDetails[5].replace('/', '-'))
                +"','"+new clsDatabaseInterface().mGetNumericField("SELECT ID FROM tblProjects WHERE ProjectName ='"+arrBugDetails[3]+"'")
                +"','"+ new clsTextProcessingView().mGetFileText()+"','"+
                new clsDatabaseInterface().mGetNumericField(
                        "SELECT ID FROM tblBugTypes WHERE ExceptionName LIKE '%"+arrBugDetails[0]+"%'")+"')";                
    }
    
    private String mInsertIntoTableProjects() {
        return "INSERT INTO tblProjects(ProjectName, StartDate, EndDate)"
                + "VALUES('"+arrBugDetails[3]+"','"+arrBugDetails[4].replace('/', '-')+"','"+arrBugDetails[5].replace('/', '-')+"')";
    }
    
    public void mSpecificExceptionName(String strException) {
        arrBugDetails[0] = strException == null ? arrBugDetails[0] : strException;
        mAddNewException();
    }
    
    private void mAddNewException() {
        if(new clsDatabaseInterface().mCreateBugRecord("INSERT INTO TABLE tblBugTypes(ExceptionName)"
                + "VALUES('"+arrBugDetails[0]+"')")) {
        }
    }
    
    public String mBugTextModel() {
        return "Summary - The error that casued the exception:\n "
                + arrBugDetails[1]+"                        \n"
                + "Detailed description of the error:       \n"
                + "                                         \n"
                +"Reproduction Steps:                       \n"
                +"                                          \n"
                +"Expected Behaviour:                       \n"
                + "                                         \n"
                + "Observed Behaviour:                      \n"
                + "                                         \n"
                + "Impact on the system:                    \n"
                + "                                         \n"
                + "Bug Severity:                            \n"
                + arrBugDetails[2]+"                        \n"
                + "Project Name:                            \n"
                + arrBugDetails[3]+"                        \n"
                + "Project Description:                     \n"
                + "                                         \n"
                + "Project Start Date:                      \n"
                + arrBugDetails[4]+"                        \n"
                + "Project End Date:                        \n"
                + arrBugDetails[5]+"                        \n"
                + "Bug Status:                              \n"
                + arrBugDetails[6]+"                        \n"
                + "Bug workaround:                          \n"
                + "                                         \n"
                + "Notes:                                   \n"
                + "                                         \n";
    }
    
    public void mLoadComboBox() {
        String[] arrExceptionNames = new clsDialogs().mCleanExceptionName();
        for(int i = 0; i < arrExceptionNames.length; i++) {
            cboExceptionNames.addItem(arrExceptionNames[i]);
        }
    }
    
    private void mLoadSeverityComboBox() {
        cboSeverity.addItem("Catastrophic - System Failure");
        cboSeverity.addItem("Critical - Severely Impacts functionality");
        cboSeverity.addItem("Major - Relatively Severe");
        cboSeverity.addItem("Normal - Inconvenience");
        cboSeverity.addItem("Minor");
        cboSeverity.addItem("Trivial");
    }
        
    public JLabel mCreateLabel(String strText, Font f) {
        JLabel lblLabel = new JLabel(strText);
        lblLabel.setHorizontalAlignment(JLabel.CENTER);
        lblLabel.setOpaque(true);
        lblLabel.setBackground(new Color(255, 255, 255));
        return lblLabel;
    }
    
    public JButton mCreateButton(int intWidth, int intHeight,
            String strText, ActionListener listener) {
        JButton btnButton = new JButton(strText);
        btnButton.addActionListener(listener);
        btnButton.setPreferredSize(new Dimension(intWidth, intHeight));
        btnButton.setOpaque(true);
        btnButton.setBackground(new Color(255, 255, 255));
        btnButton.setBorder(new BevelBorder(BevelBorder.RAISED));
        btnButton.setText(strText);
        return btnButton;
    }
    
    private JScrollPane mCreateList(JList list, DefaultListModel model,
            MouseListener listener, int intWidth, int intHeight) {
        list = new JList(model);
        list.setEnabled(true);
        list.addMouseListener(listener);
        list.setOpaque(true);
        list.setBackground(new Color(255, 255, 255));
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(intWidth, intHeight));
        scroll.setBorder(new MotifBorders.FrameBorder(scroll));
        return scroll;
    }
    
    private DefaultListModel mGetModel(String strQuery) {
        DefaultListModel model = new DefaultListModel();
        Statement stStatement = null;
        ResultSet rs = null;
        try{
            stStatement = new clsDatabaseInterface().mConnectToDatabase().prepareStatement(strQuery);
            rs = stStatement.executeQuery(strQuery);
            while(rs.next()) {
                model.addElement(rs.getString(1));
            }
        } catch(SQLException | NullPointerException ex) {
            
            JOptionPane.showMessageDialog(clsMainWindow.this, ex.getMessage(),
                    "Error while getting reported bugs for model", JOptionPane.ERROR_MESSAGE);
        } finally {
            try{
                stStatement.close();
                rs.close();
            } catch(SQLException | NullPointerException ex) {
            }
        }
        return model;
    }
    
    private void mSetLstReportedBugs() {
        modelReportedBugs = mGetModel("SELECT Description FROM tblBugs");
        if(modelReportedBugs.getSize() != 0) {
            lstReportedBugs.setModel(modelReportedBugs);
        }
        lstReportedBugs.addMouseListener(new clsOpenBugMouseHandler());
    }
    
    public void mSetLstOpenBugReportModel(String strQuery) {
        if(modelOpenBugReports.getSize() == 0) {
            modelOpenBugReports = mGetModel("SELECT Description FROM tblBugs WHERE BugStatus = 'open'");
            lstOpenBugReports.setModel(modelOpenBugReports); //Check the use of mLoadConboBox() withouth the use of count
        } else if(strQuery.equals("")) {
            lstOpenBugReports.setModel(modelReportedBugs);
        } else {
            modelOpenBugReports = mGetModel(strQuery);
            lstOpenBugReports.setModel(modelOpenBugReports);
        } 
        lstOpenBugReports.addMouseListener(new clsOpenBugMouseHandler());
    }
    
    public JTextField mTextFieldDimensions(JTextField txt, int intWidth, int intHeight, String strToolTip){
        txt = new JTextField();
        txt.setEnabled(true);
        txt.setPreferredSize(new Dimension(intWidth, intHeight));
        txt.setToolTipText(strToolTip);
        return txt;
    }
    
    public JComboBox mComboBoxDimensions(int intWidth, int intHeight) {
        JComboBox cbo = new JComboBox();
        cbo.setSize(new Dimension(intWidth, intHeight));
        return cbo;
    }
    
    private JPanel mCreateHeader() {
        JPanel jpHeadingPanel = new JPanel(new GridBagLayout());
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        mAddComponent(jpHeadingPanel, new JLabel(new ImageIcon(new ImageIcon(clsTools.class.getResource("/bugtrackingsystem/images/header.PNG"), "").getImage()
                    .getScaledInstance(d.width, 100, Image.SCALE_SMOOTH), "")), 0, 0, 10, 6, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.LINE_START,
                new Insets(0, 0, 0, 0));
        return jpHeadingPanel;
    }
    
    private JPanel mCreateFooter() {
        JPanel jpFooterPanel = new JPanel(new BorderLayout());
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        jpFooterPanel.add(new JLabel(new ImageIcon(new ImageIcon(clsTools.class.getResource("/bugtrackingsystem/images/footer.PNG"), "").getImage()
                    .getScaledInstance(d.width, 80, Image.SCALE_SMOOTH), "")));
        return jpFooterPanel;
    }
        
    private JPanel mCreateCenter() {
        JPanel jpPanel = new JPanel(new GridBagLayout());
        jpPanel.setBorder(new EmptyBorder(10, 40, 10, 20));
        jpPanel.setBorder(new LineBorder(Color.black, 6));
        jpPanel.setOpaque(true);
        jpPanel.setBackground(new Color(255, 255, 255));    
        mAddComponent(jpPanel, mCreateLeftPane(), 
                0, 0, 10, 15, 0, 0, GridBagConstraints.BOTH, 
                GridBagConstraints.LINE_START, new Insets(0, 0, 0, 0));
        mAddComponent(jpPanel, mCreateCenterPane(), 10, 0, 10, 15, 0, 0, 
                GridBagConstraints.BOTH, GridBagConstraints.LINE_START, new Insets(0, 0, 0, 0));
        mAddComponent(jpPanel, mCreateRightPane(), 20, 0, 10, 15, 0, 0, GridBagConstraints.NONE,
                GridBagConstraints.LINE_START, new Insets(0, 0, 0, 0));
        return jpPanel;
    }
    
    private JPanel mCreateLeftPane() {
        JPanel jpLeftPane = new JPanel(new GridBagLayout());
        jpLeftPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        jpLeftPane.setOpaque(true);
        jpLeftPane.setBackground(new Color(255, 255, 255));
        mAddComponent(jpLeftPane, mCreateLabel("Enter Bug Information", new Font("Tahoma", Font.BOLD, 16)),
                0, 0, 6, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.LINE_START, 
                new Insets(0, 20, 0, 0));
        mAddComponent(jpLeftPane, mCreateLabel("Exception name", new Font("Tahoma", Font.BOLD, 14)),
                0, 4, 4, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.LINE_START, 
                new Insets(20, 20, 20, 20));
        mAddComponent(jpLeftPane, cboExceptionNames = mComboBoxDimensions(150, 20),
                4, 4, 8, 2, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.LINE_START,
                new Insets(20, 20, 20, 20));
        mAddComponent(jpLeftPane, mCreateLabel("Bug Description", new Font("Tahoma", Font.BOLD, 14)),
                0, 8, 6, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.LINE_START, 
                new Insets(20, 20, 20, 20));
        mAddComponent(jpLeftPane, mTextFieldDimensions(txtDescription, 200, 20, "Describe the bug"),
                4, 8, 8, 2, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.LINE_START, 
                new Insets(20, 20, 20, 20));
        mAddComponent(jpLeftPane, mCreateLabel("Bug Severity", new Font("Tahoma", Font.BOLD, 14)),
                0, 12, 6, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.LINE_START, 
                new Insets(20, 20, 20, 20));
        mAddComponent(jpLeftPane, cboSeverity = mComboBoxDimensions(150, 20),
                4, 12, 8, 2, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.LINE_START, 
                new Insets(20, 20, 20, 20));
        mAddComponent(jpLeftPane, mCreateLabel("Project Name", new Font("Tahoma", Font.BOLD, 14)),
                0, 16, 4, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.LINE_START, 
                new Insets(20, 20, 20, 20));
        mAddComponent(jpLeftPane, cboProjectName = mComboBoxDimensions(150, 20),
                4, 16, 8, 2, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.LINE_START, new Insets(20, 20, 20, 20));
        mAddComponent(jpLeftPane, mCreateLabel("Project Start Date", new Font("Tahoma", Font.BOLD, 14)),
                0, 20, 6, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.LINE_START,
                new Insets(20, 20, 20, 20));
        txtStartDate.setText(new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
        mTextFieldDimensions(txtStartDate, 150, 20, "Enter date of when you started working on this project "
                + "\n use the format yyyy/MM/dd");
        mAddComponent(jpLeftPane,txtStartDate , 4, 20, 8, 2, 0, 0, GridBagConstraints.BOTH, 
                GridBagConstraints.LINE_START, new Insets(20, 20, 20, 20));
       mAddComponent(jpLeftPane, mCreateLabel("Project End Date", new Font("Tahoma", Font.BOLD, 14)), 
                0, 24, 6, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.LINE_START, 
                new Insets(20, 20, 20, 20));
        mTextFieldDimensions(txtEndDate, 150, 20, "Enter date of when you finished this project");
        txtEndDate.addMouseListener(new clsEndDateMouseHandler());
        txtEndDate.setText(new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
        mAddComponent(jpLeftPane, txtEndDate, 4, 24, 8, 2, 0, 0, GridBagConstraints.BOTH,
                GridBagConstraints.LINE_START, new Insets(20, 20, 20, 20));
        
        mAddComponent(jpLeftPane, mCreateButton(100, 30, "Report Bug", this::mReportBug),
                0, 26, 4, 2, 0, 0, GridBagConstraints.NONE, GridBagConstraints.LINE_START, new Insets(10, 10, 10, 10));
        mAddComponent(jpLeftPane, mCreateButton(100, 30, "Add Project", null),
                4, 26 , 4, 2, 0, 0, GridBagConstraints.NONE, GridBagConstraints.LINE_START, new Insets(20, 20, 20, 20));
        mAddComponent(jpLeftPane, mCreateButton(100, 30, "Update Bug", this::mUpdateBugDetails), 
                8, 26, 4, 2, 0, 0, GridBagConstraints.NONE, GridBagConstraints.LINE_START, new Insets(20, 20, 20, 20));
        return jpLeftPane;
    }
        
    private JPanel mCreateCenterPane() {
        JPanel jpCenterPane = new JPanel(new GridBagLayout());
        jpCenterPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        jpCenterPane.setOpaque(true);
        jpCenterPane.setSize(new Dimension(500, 300));
        jpCenterPane.setBackground(new Color(255, 255, 255));
                        
        mAddComponent(jpCenterPane, mCreateLabel("Reported Bugs", new Font("Tahoma", Font.BOLD, 16)),
                0, 0, 6, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.LINE_START, 
                new Insets(0, 0, 20, 0));
        new clsOpenBugMouseHandler().mSetModel(modelReportedBugs);
        mAddComponent(jpCenterPane, mCreateList(lstReportedBugs, 
                mGetModel("SELECT Description FROM tblBugs"), new clsReportedBugMouseHandler(),300, 400),
                0, 4, 12, 8, 0, 0, GridBagConstraints.NONE, GridBagConstraints.LINE_START,
                new Insets(0, 0, 20, 0));
        return jpCenterPane;
    }
    
    private JPanel mFilteringAndSearchButtonPane() {
        JPanel jpPanel = new JPanel(new GridBagLayout()); //A JPanel to contain the lower part of the GUI
        jpPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        jpPanel.setOpaque(true);
        jpPanel.setBackground(new Color(255, 255, 255));
        mAddComponent(jpPanel, mCreateButton(120, 30, "Filter By Project", this::mFilterByProject),
                4, 0, 2, 2, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.LINE_START, 
                new Insets(20, 20, 20, 20));
        mAddComponent(jpPanel, mCreateButton(120, 30,"Filter By Bug Type" , this::mFilterByBugType),
                6, 0, 2, 2, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.LINE_START,
                new Insets(20, 20, 20, 20));
        mAddComponent(jpPanel, mCreateButton(120, 30, "Filter By Bug Status", this::mFilterByStatus), 
                4, 4, 2, 2, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.LINE_START,
                new Insets(20, 20, 20, 20));
        mAddComponent(jpPanel, mCreateButton(120, 30, "Filter Alphabetically", this::mFilterAlphabetically),
                6, 4, 2, 2, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.LINE_START,
                new Insets(20, 20, 20, 20));
        mAddComponent(jpPanel, mCreateButton(120, 30, "Search Web", this::mSearchTheWeb),
                4, 6, 2, 2, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.LINE_START,
                new Insets(20, 20, 20, 20));
        mAddComponent(jpPanel, mCreateButton(120, 30, "Refresh Window", this::mRefreshWindow) ,
                6, 6, 2, 2, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.LINE_START,
                new Insets(20, 20, 20, 20));
        return jpPanel;
    }
       
    private JPanel mCreateRightPane() {
        JPanel jpRightPane = new JPanel(new GridBagLayout());
        jpRightPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        jpRightPane.setOpaque(true);
        jpRightPane.setBackground(new Color(255, 255, 255));
        mAddComponent(jpRightPane, mCreateLabel("Filter and Search bugs", new Font("Tahoma", Font.BOLD, 14)),
                0, 0, 4, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.LINE_START, 
                new Insets(0, 30, 20, 0));
        new clsOpenBugMouseHandler().mSetModel(modelOpenBugReports);
        mAddComponent(jpRightPane, mCreateList(lstOpenBugReports, 
                mGetModel("SELECT Description FROM tblBugs WHERE BugStatus ='open'"),
                new clsOpenBugMouseHandler(), 280, 200),
                0, 2, 2, 2, 0, 0, GridBagConstraints.NONE, GridBagConstraints.LINE_START,
                new Insets(0, 30, 10, 0));
        mAddComponent(jpRightPane, mFilteringAndSearchButtonPane(), 
                0, 12, 8, 5, 0, 0, GridBagConstraints.NONE, GridBagConstraints.LINE_START, 
                new Insets(0, 0, 0, 0));
         
        return jpRightPane;
    }
    
    private static void mAddComponent(Container container, Component component,
        int intGridX, int intGridY, int intGridWidth, int intGridHeight, 
        double dblWeightX, double dblWeightY, int intFill, int intAnchor, 
        Insets insets) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = intGridX;
        constraints.gridy = intGridY;
        constraints .gridwidth = intGridWidth;
        constraints.gridheight = intGridHeight;
        constraints.weightx = dblWeightX;
        constraints.weighty = dblWeightY;
        constraints.fill = intFill;
        constraints.anchor = intAnchor;
        constraints.insets = insets;
        container.add(component, constraints);
    }
    
    private void mReportBug(ActionEvent e) {
        if(txtDescription.getText().equals("")) {
            JOptionPane.showMessageDialog(clsMainWindow.this, "Provide a brief description of the bug",
                    "WARNING", JOptionPane.WARNING_MESSAGE);
            txtDescription.requestFocusInWindow();
        } else if(txtStartDate.getText().equals("")) {
            JOptionPane.showMessageDialog(clsMainWindow.this, "Provide the project start date",
                    "WARNING", JOptionPane.WARNING_MESSAGE);
            txtStartDate.requestFocusInWindow();
        }else if (txtEndDate.getText().equals("")) {
            JOptionPane.showMessageDialog(clsMainWindow.this, "Provide the project end date",
                    "WARNING", JOptionPane.WARNING_MESSAGE);
            txtEndDate.requestFocusInWindow();
        } else if(LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).isAfter(LocalDate.parse(txtEndDate.getText().replace("/", "-")))) {
            JOptionPane.showMessageDialog(clsMainWindow.this, "Provide the correct project end date. \nThe project cannot possibly ended while still getting errors on",
                    "WARNING", JOptionPane.WARNING_MESSAGE);
            txtEndDate.requestFocusInWindow();
        } else {
            mGetBugReportDetails();
            if(cboExceptionNames.getSelectedItem().toString().equals("Other...")) {
                new clsDialogs().mCreateExceptionSpecifyingDialog();
            } else {
                clsDialogs dialog = new clsDialogs();
                dialog.new clsUpdateDialog(null, "").mOpenBugReportFile(mBugTextModel(), "Create");
            }
        }
    }
    
    private void mUpdateBugDetails(ActionEvent e) {
        new clsDialogs().mCreateUpdateDialog();
    }
    
    private void mClear(ActionEvent e) {
        txtDescription.setText("");
        txtStartDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        txtEndDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }
    
    private void mFilterByProject(ActionEvent e) {
        new clsDialogs().mCreateFilterByProjectDialog();
    }
    
    private void mFilterByBugType(ActionEvent e) {
        new clsDialogs().mCreateFilterByBugDialog();
    }
    
    private void mFilterByStatus(ActionEvent e) {
        new clsDialogs().mCreateFilterByStatusDialog();
    }
    
    private void mFilterAlphabetically(ActionEvent e) {
        new clsDialogs().mCreateFilterAphabeticallyDialog();
    }
    
    private void mSearchTheWeb(ActionEvent e) {
        new clsDialogs().mCreateSearchDialog();
    }
    
    private void mRefreshWindow(ActionEvent e) {
        this.dispose();
        new clsMainWindow().setVisible(true);
    }
    
    
    class clsOpenBugMouseHandler extends MouseAdapter{
        private DefaultListModel model = new DefaultListModel();
        
        public void mSetModel(DefaultListModel model) {
            this.model = model;
        }
        
        public void mMouseClicked(MouseEvent e)
        {
            JList lst = (JList) e.getSource();
            if(e.getClickCount() == 2){
                int n = lst.locationToIndex(e.getPoint());
                new clsTextProcessingView().mOpenDocument(
                        new clsDatabaseInterface().mGetTextField("SELECT BugFile FROM tblBugs WHERE description ='"+model.getElementAt(n)+"'"));
            }
        }
    }
    
    class clsReportedBugMouseHandler extends MouseAdapter{
        private DefaultListModel model = new DefaultListModel();
        
        public void mSetModel(DefaultListModel model) {
            this.model = model;
        }
        
        public void mMouseClicked(MouseEvent e) {
            JList lst = (JList) e.getComponent();
            if(e.getClickCount() == 2){
                int n = lst.locationToIndex(e.getPoint());
                new clsTextProcessingView().mOpenDocument(
                        new clsDatabaseInterface().mGetTextField("SELECT BugFile FROM tblBugs WHERE description ='"+model.getElementAt(n)+"'"));
            }
        }
    }
    
    class clsEndDateMouseHandler extends MouseAdapter {
        public void mMouseClicked(MouseEvent e) {
            if(JOptionPane.showConfirmDialog(clsMainWindow.this, "Are you still working on the project?",
                    "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(clsMainWindow.this, "Please leave the default end date value");
            } else {
                txtEndDate.requestFocusInWindow();
            }
        }
    }
}
