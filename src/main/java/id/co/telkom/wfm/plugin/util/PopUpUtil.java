/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.util;


import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
//import javafx.application.Platform;

/**
 *
 * @author User
 */
public class PopUpUtil {
    public void infoBox(String headerMessage, String titleBar, String infoMessage)
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }
    public void alertBox(String headerMessage, String titleBar, String infoMessage)
    {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }
    public void errorBox(String headerMessage, String titleBar, String infoMessage)
    {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }
}
