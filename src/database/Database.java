package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database
{
    private Connection connection;

    private Database() { }

    public static Database connect(String user, String password) throws ClassNotFoundException, SQLException
    {
        Database database = new Database();
        Class.forName("oracle.jdbc.driver.OracleDriver");
        database.connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@grad.icmc.usp.br:15215:orcl",
                user,
                password);
        return database;
    }

    public void close() throws SQLException
    {
        connection.close();
    }
}
