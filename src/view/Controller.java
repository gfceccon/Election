package view;

import database.Database;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

import java.sql.SQLException;

public class Controller
{
    private Database database;

    public void setDatabase(Database database)
    {
        this.database = database;
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
}
