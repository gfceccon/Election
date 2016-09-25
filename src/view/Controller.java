package view;

import database.SQLTable;
import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import database.Database;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Vector;

public class Controller implements Initializable
{
    @FXML
    public ComboBox<SQLTable> tableSelector;

    @FXML
    public TableView dataTable;

    @FXML
    public GridPane insertionPane;

    @FXML
    public TextArea errorArea;

    @FXML
    public TextArea usersAndPrivileges;


    private Vector<SQLTable> tables;
    private Database database;

    public void setDatabase(Database database)
    {
        this.database = database;
        this.tables = database.getTables();
        database.setErrorObserver((observable, error) -> errorArea.setText((String)error));
        tableSelector.getItems().addAll(tables);
        tableSelector.valueProperty().addListener(observable -> setTable());
    }

    private void setTable()
    {
        dataTable.getColumns().setAll(tableSelector.getValue().columns);
    }


    public EventHandler<WindowEvent> getCloseRequestEvent()
    {
        return windowEvent ->
        {
            try
            {
                database.close();
            } catch (SQLException e)
            {
                e.printStackTrace();
            }
        };
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        errorArea.setEditable(false);
    }
}
