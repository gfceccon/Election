package view;

import database.SQLTable;
import database.SQLTableColumn;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
        this.tables = database.getTables();
        database.setErrorObserver((observable, error) -> errorArea.setText((String)error));
        tableSelector.getItems().addAll(tables);
        tableSelector.valueProperty().addListener(observable -> setTable());
        setDDL();
    }

    private void setTable()
    {
        dataTable.getColumns().setAll(tableSelector.getValue().columns);
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
