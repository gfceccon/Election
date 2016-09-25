package view;

import database.Database;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.xml.crypto.Data;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Login implements Initializable
{
    @FXML
    public Button loginButton;

    @FXML
    public TextField user;

    @FXML
    public PasswordField password;

    private Stage stage;
    private Scene mainScene;
    private Controller mainController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        loginButton.setDefaultButton(true);
        loginButton.onActionProperty().set(actionEvent ->
        {
            try
            {
                Database db = Database.connect(user.getText(), password.getText());
                stage.setScene(mainScene);
                stage.show();
                mainController.setDatabase(db);
            } catch (ClassNotFoundException | SQLException e)
            {
                e.printStackTrace();
            }
        });
    }

    public void setLoginFlow(Stage stage, Scene scene, Controller controller)
    {
        this.stage = stage;
        this.mainScene = scene;
        this.mainController = controller;
    }
}
