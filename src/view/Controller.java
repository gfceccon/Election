package view;

import database.SQLTable;
import database.SQLTableColumn;
import database.SQLValue;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

    @FXML
    public Button insertionButton;

    @FXML
    public TextArea ddlTextArea;

    public Vector<Object> insertionData;


    private Vector<SQLTable> tables;
    private Database database;

    public void setDatabase(Database database)
    {
        this.database = database;

        database.setErrorObserver((observable, error) -> errorArea.setText((String)error));

        tables = database.getTables();

        tableSelector.getItems().addAll(tables);
        tableSelector.valueProperty().addListener(observable -> setTable());
        setDDL();
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

    private void addOnGrid()
    {
        Vector<SQLTableColumn> columns = tableSelector.getValue().columns;

        for (int i = 0; i < columns.size(); i++)
        {
            if(columns.elementAt(i).values.isEmpty())
            {
                TextArea ta = new TextArea();
                insertionPane.add(new Label(columns.elementAt(i).name), i, 0);
                insertionPane.add(ta, i, 1);
                insertionData.add(ta);
            }
            else
            {
                ComboBox cb = populateComboBox(columns.elementAt(i).values);
                insertionPane.add(new Label(columns.elementAt(i).name), i, 0);
                insertionPane.add(cb, i, 1);
                insertionData.add(cb);
            }
        }
    }


    private ComboBox<String> populateComboBox(Vector<String> data)
    {
        ComboBox<String> cb = new ComboBox<>();

        cb.getItems().addAll(data);

        return cb;
    }

    private void setDDL(){
        String data = new String();

        for (int i = 0; i < tables.size(); i++) {
            data += getDDL(tables.elementAt(i).name) + "\n";
        }

        ddlTextArea.setText(data);
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

    @FXML
    protected void insertData(ActionEvent event)
    {
        Vector<String> data = new Vector<>();

        for (int i = 0; i < tableSelector.getValue().columns.size(); i++)
        {
            if(insertionData.elementAt(i).getClass() == TextArea.class)
            {
                data.add(((TextArea) insertionData.elementAt(i)).getText());
            }
            else
            {
                data.add(((ComboBox) insertionData.elementAt(i)).getValue().toString());
            }

        }
        Database.insertDataSQL(tableSelector.getValue(), data);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        errorArea.setEditable(false);
    }
}
