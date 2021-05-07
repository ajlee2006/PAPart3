package sample.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sample.Model.Database;
import sample.Model.Security;

public class LoginController {
    @FXML
    private TextField textfieldUsername;
    @FXML
    private PasswordField textfieldPassword;
    @FXML
    private Button buttonLogin;
    @FXML
    private Button buttonExit;

    @FXML
    public void login(ActionEvent event) {
        try {
            String username = textfieldUsername.getText();
            String password = textfieldPassword.getText();

            if (username.equals("admin") && password.equals("#AB#admin123")) {
                Stage thisStage = (Stage) buttonLogin.getScene().getWindow();
                thisStage.close();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/View/admin.fxml"));
                Parent root = loader.load();
                AdminController adminController = loader.getController();
                adminController.clearAllTheLists();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("AirBooks - Admin");
                stage.show();
            } else {
                Security tempSecurity = new Security("Secure.csv");
                boolean loggedin = tempSecurity.login(username, password);
                if (loggedin) {
                    Stage thisStage = (Stage) buttonLogin.getScene().getWindow();
                    thisStage.close();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/View/sample.fxml"));
                    Parent root = loader.load();
                    SampleController sampleController = loader.getController();
                    sampleController.getLoginInfo(username, password);

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("AirBooks - Main Page");
                    stage.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error!");
                    alert.setContentText("Invalid login credentials.");
                    alert.showAndWait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void closeApp(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Close");
        alert.setHeaderText("Exiting app");
        alert.setContentText("Thank you for using AirBooks!");
        alert.showAndWait();
        Stage thisStage = (Stage) buttonExit.getScene().getWindow();
        thisStage.close();
    }
}
