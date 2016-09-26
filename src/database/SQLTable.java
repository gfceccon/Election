package database;

import java.util.List;
import java.util.Vector;

/**
 *
 * @author mathe
 */
public class SQLTable {
    public enum TableType {
        USER,
        VIEW,
        MVIEW;

        public String getQuery()
        {
            switch(this) {
                case USER:
                    return "SELECT TABLE_NAME AS NAME FROM USER_TABLES MINUS SELECT MVIEW_NAME AS NAME FROM USER_MVIEWS";
                case VIEW:
                    return "SELECT VIEW_NAME AS NAME FROM USER_VIEWS";
                case MVIEW:
                    return "SELECT MVIEW_NAME AS NAME FROM USER_MVIEWS";
            }
            return "";
        }
    }
    public String name;
    public TableType type;
    public Vector<SQLTableColumn> columns;
    public Vector<SQLValue> data;

    public SQLTable(String name, TableType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        String name = "";
        switch(type)
        {
            case VIEW:
                name = "(VIEW)";
                break;
            case MVIEW:
                name = "(MATERIALIZED)";
        }
        name += this.name;
        return name;
    }

    public void fillData(Vector<SQLTableColumn> columns, Vector<SQLValue> data) {
        this.columns = columns;
        this.data = data;
    }
}
