package sample.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample.Main;
import sample.Model.*;

import java.io.*;
import java.util.*;

public class AdminController {
    @FXML
    private Button buttonLogout;
    @FXML
    private TextArea textareaBooksInfo;
    @FXML
    private TextField textfieldDisplayISBN;
    @FXML
    private Button buttonDisplay;
    @FXML
    private TextField textfieldReturnISBN;
    @FXML
    private Button buttonReturn;
    @FXML
    private Button buttonAbout;

    public void alertError(Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error!");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    public void alertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText("Error!");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void alertMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alert");
        alert.setHeaderText("Notification");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void logout(ActionEvent event) {
        try {
            alertMessage("Logging out admin");

            Stage thisStage = (Stage) buttonLogout.getScene().getWindow();
            thisStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/View/login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("AirBooks - Login");
            stage.show();
        } catch (Exception e) {
            alertError(e);
        }
    }

    @FXML
    public void returnBook(ActionEvent event) {
        String ISBN = textfieldReturnISBN.getText();
        Book book = null;
        try {
            book = Main.database.getBook(ISBN);
        } catch (Exception e) {
            alertError(e);
        }
        if (book == null) alertWarning("No such book exists. book == null");
        else {
            if (book.getIsRented()) {
                String msg = "";
                Account account = Main.security.getAccount(book.getStudentID());
                account.returnBook(book);
                msg += "Book \"" + book.getTitle() + "\" successfully returned.\n";
                account.addToWallet(book.getDeposit());
                msg += String.format("$%.2f was added to ", book.getDeposit());
                msg += account.getStudentID() + " wallet.";
                alertMessage(msg);
                Main.database.writeBook(System.getProperty("user.dir") + "/Books.csv");
                Main.security.writeAccount(System.getProperty("user.dir") + "/Secure.csv");
            } else alertWarning("Cannot return a book which is not rented.\n");
        } clearAllTheLists();
    }

    @FXML
    public void displayInfo(ActionEvent event) {
        String ISBN = textfieldDisplayISBN.getText();
        Book book = null;
        try {
            book = Main.database.getBook(ISBN);
        } catch (Exception e) {
            alertError(e);
        }
        if (book == null) alertWarning("No such book exists. book == null");
        else {
            alertMessage(book.toString());
        } clearAllTheLists();
    }

    public void clearAllTheLists() {
        textareaBooksInfo.setText(Main.database.returnListOfBooks());
        textfieldDisplayISBN.setText("");
        textfieldReturnISBN.setText("");
    }

    @FXML
    public void aboutProgrammer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/View/about.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("AirBooks - About the Programmer");
            stage.showAndWait();
        } catch (Exception e) {
            alertError(e);
        }
    }
}
