package sample;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.Controller.LoginController;
import sample.Controller.SampleController;
import sample.Model.Database;
import sample.Model.Security;

public class Main extends Application {

    public static Database database = new Database(System.getProperty("user.dir") + "/Student.csv", System.getProperty("user.dir") + "/Books.csv", System.getProperty("user.dir") + "/SelfCollectStn.csv", System.getProperty("user.dir") + "/DistrictAreas.csv");
    public static Security security = new Security(System.getProperty("user.dir") + "/Secure.csv");

    @Override
    public void start(Stage primaryStage) throws Exception {
        database = new Database(System.getProperty("user.dir") + "/Student.csv", System.getProperty("user.dir") + "/Books.csv", System.getProperty("user.dir") + "/SelfCollectStn.csv", System.getProperty("user.dir") + "/DistrictAreas.csv");
        security = new Security(System.getProperty("user.dir") + "/Secure.csv");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("View/login.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("AirBooks - Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

