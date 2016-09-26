package database;

import java.sql.*;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.regex.Pattern;

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
            query = "SELECT COLUMN_NAME AS NAME, DATA_TYPE AS TYPE, DATA_LENGTH AS LENGTH FROM USER_TAB_COLUMNS WHERE TABLE_NAME = '" + table.name + "'";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);

            Vector<SQLTableColumn> columns = new Vector<>();
            while(rs.next())
            {
                SQLTableColumn column = new SQLTableColumn();
                column.name = rs.getString("NAME");
                columns.add(column);
            }

            stmt.close();

            query = "SELECT COLS.COLUMN_NAME AS NAME, CONS.CONSTRAINT_TYPE AS TYPE, CONS.SEARCH_CONDITION AS CONDITION, " +
                    "COLS2.TABLE_NAME AS R_TABLE_NAME, COLS2.COLUMN_NAME AS R_COLUMN_NAME FROM USER_CONSTRAINTS CONS " +
                    "LEFT JOIN USER_CONS_COLUMNS COLS " +
                    "ON CONS.CONSTRAINT_NAME = COLS.CONSTRAINT_NAME " +
                    "LEFT JOIN USER_CONS_COLUMNS COLS2 " +
                    "ON COLS2.CONSTRAINT_NAME = CONS.R_CONSTRAINT_NAME WHERE CONS.TABLE_NAME = '" + table.name + "' AND CONS.CONSTRAINT_TYPE IN ('P', 'R', 'C')";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);

            while(rs.next())
            {
                String columnName = rs.getString("NAME");
                String type = rs.getString("TYPE");
                SQLTableColumn column = columns.stream().filter(sqlTableColumn -> sqlTableColumn.name.equals(columnName)).findFirst().get();


                if(type.equals("P"))
                    column.isPrimary = true;

                if(type.equals("R"))
                {
                    column.isForeign = true;
                    column.refTable = rs.getString("R_TABLE_NAME");
                    column.refColumn = rs.getString("R_COLUMN_NAME");
                }

                if(type.equals("C"))
                {
                    String check = rs.getString("CONDITION");
                    if(check.contains("IN"))
                    {
                        column.values = new Vector<>(Arrays.asList(check.split("IN")[1].trim().replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\'", "").split(",")));
                        column.values.forEach(String::trim);
                    }
                }
            }

            stmt.close();


            query = "SELECT * FROM " + table.name;
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);

            Vector<SQLValue> data = new Vector<>();
            while(rs.next()) {
                Vector<String> row = new Vector<>();
                for (int i = 1; i < columns.size() + 1; i++) {
                    row.add(rs.getString(i));
                }
                data.add(new SQLValue(columns, row));
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
