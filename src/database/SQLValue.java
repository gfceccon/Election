package database;

import java.util.Vector;

public class SQLValue
{
    public Vector<SQLTableColumn> columns;
    public Vector<String> values;

    public SQLValue(Vector<SQLTableColumn> columns, Vector<String> values)
    {
        this.columns = columns;
        this.values = values;
    }
}
