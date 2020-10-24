/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bugtrackingsystem.textpad;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 *
 * @author Sanele
 */
public class clsTools {
    public static Font defFont = new Font("Liberation Sans", Font.PLAIN, 14);
    public static Font txtFont = new Font("FreeMono", Font.PLAIN, 16);
    public static Color statusLineColour = new Color(240, 240, 240);
    
    public static ImageIcon mCreateImageIcon(String strPath, int intWidth) {
        java.net.URL imgURL = clsTools.class.getResource(strPath);
        if(imgURL != null) {
            return new ImageIcon(new ImageIcon(imgURL, "").getImage()
                    .getScaledInstance(intWidth, intWidth, Image.SCALE_SMOOTH), "");
        }
        return null;
    }
    
    public static JButton mCreateButton(Icon icon, String strToolTip,
            ActionListener listener) {
        JButton btnButton = new JButton();
        btnButton.setFont(defFont);
        btnButton.setIcon(icon);
        btnButton.setMargin(new Insets(0, 0, 0, 0));
        btnButton.setToolTipText(strToolTip);
        btnButton.addActionListener(listener);
        return btnButton;
    }
}
