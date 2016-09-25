package database;

import java.sql.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

public class Database
{
    private Connection connection;
    private Statement stmt;
    private ResultSet rs;
    private Observable error;

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

    public Vector<SQLTable> getTables(){
        String query = "";
        Vector<SQLTable> tables = new Vector<>();
        try {
            for(SQLTable.TableType type : SQLTable.TableType.values()) {
                query = type.getQuery();
                stmt = connection.createStatement();
                rs = stmt.executeQuery(query);
                while (rs.next()) {
                    SQLTable table = new SQLTable(rs.getString("NAME"), type);
                    tables.addElement(table);
                }
                stmt.close();
            }
        } catch (SQLException ex) {
            error.notifyObservers("Erro na consulta: \"" + query + "\"");
        }

        tables.forEach(this::fillTable);

        return tables;
    }

    private void fillTable(SQLTable table) {
        String query = "";
        try {

            query = "SELECT * FROM " + table.name;
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);

            ResultSetMetaData metadata = rs.getMetaData();

            Vector<SQLTableColumn> columns = new Vector<>();
            for(int i = 1; i < metadata.getColumnCount() + 1; i++)
                columns.add(new SQLTableColumn(metadata.getColumnName(i)));

            Vector<Vector<SQLValue>> data = new Vector<>();
            while(rs.next()) {
                Vector<SQLValue> row = new Vector<>();
                for (int i = 1; i < columns.size() + 1; i++) {
                    row.add(new SQLValue(rs.getString(i)));
                }
                data.add(row);
            }

            stmt.close();


            table.fillData(columns, data);
        } catch (SQLException ex) {
            error.notifyObservers("Erro na consulta: \"" + query + "\"\n" + ex.getMessage());
        }
    }

    public void setErrorObserver(Observer observer)
    {
        this.error = new Observable();
        error.addObserver(observer);
    }
}
