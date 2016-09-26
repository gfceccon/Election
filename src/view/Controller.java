package view;

import database.SQLTable;
import database.SQLTableColumn;
import database.SQLValue;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import database.Database;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Vector;

public class Controller implements Initializable
{
    @FXML
    public ComboBox<SQLTable> tableSelector;

    @FXML
    public TableView<SQLValue> dataTable;

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

        database.setErrorObserver((observable, error) -> errorArea.setText((String)error));

        tables = database.getTables();

        tableSelector.getItems().addAll(tables);
        tableSelector.valueProperty().addListener(observable -> setTable());
    }

    private void setTable()
    {
        SQLTable table = tableSelector.getValue();
        dataTable.getColumns().clear();
        dataTable.getItems().clear();
        for (int i = 0; i < table.columns.size(); i++)
        {
            final int index = i;
            TableColumn<SQLValue, String> dataCol = new TableColumn<>(table.columns.get(index).name);
            dataCol.setCellValueFactory(value -> new ReadOnlyStringWrapper(value.getValue().values.get(index)));
            dataTable.getColumns().add(dataCol);
        }
        ObservableList<SQLValue> data = FXCollections.observableArrayList(table.data);
        dataTable.setItems(data);
    }


    public EventHandler<WindowEvent> getCloseRequestEvent()
    {
        return windowEvent ->
        {
            try
            {
                if(database != null)
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
