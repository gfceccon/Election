package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.Controller;
import view.Login;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(view.Login.class.getResource("loginScene.fxml"));
        Parent login = loginLoader.load();

        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(view.Login.class.getResource("mainScene.fxml"));
        Parent main = mainLoader.load();

        Login loginController = loginLoader.getController();
        Controller mainController = mainLoader.getController();
        loginController.setLoginFlow(stage, new Scene(main), mainController);

        stage.setTitle("Login");
        stage.setScene(new Scene(login));
        stage.show();
        stage.setResizable(false);
        stage.setOnCloseRequest(mainController.getCloseRequestEvent());
    }


    public static void main(String[] args) {
        launch(args);
    }
}
