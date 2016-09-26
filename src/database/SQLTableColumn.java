package database;

import java.util.Vector;

public class SQLTableColumn
{
    public String name;
    public Vector<String> values;
    public boolean isPrimary;
    public boolean isForeign;

    public String refTable;
    public String refColumn;

    public SQLTableColumn()
    {
        isPrimary = false;
        isForeign = false;
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    public String getName()
    {
        return name;
    }
}
