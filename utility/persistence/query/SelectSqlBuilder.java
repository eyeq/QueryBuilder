package utility.persistence.query;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class SelectSqlBuilder<T extends SelectSqlBuilder<T>> {

    private boolean distinct = false;

    protected final List<String> columnList = new ArrayList<>();
    protected final List<String> fromList = new ArrayList<>();
    protected final List<String> joinList = new ArrayList<>();

    public SelectSqlBuilder() {
    }

    public SelectSqlBuilder(String tableName) {
        this.from(tableName);
    }

    @SuppressWarnings("unchecked")
    public T distinect() {
        this.distinct = true;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T select(String column) {
        this.columnList.add(column);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T from(String table) {
        this.fromList.add(table);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T joinUsing(String table, String column) {
        this.joinList.add(" JOIN " + table + " USING(" + column + ")");
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T joinOn(String table, String condition) {
        this.joinList.add(" JOIN " + table + " ON " + condition);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T leftJoinUsing(String table, String column) {
        this.joinList.add(" LEFT JOIN " + table + " USING(" + column + ")");
        return (T) this;
    }
	
    @SuppressWarnings("unchecked")
    public T leftJoinOn(String table, String condition) {
        this.joinList.add(" LEFT JOIN " + table + " ON " + condition);
        return (T) this;
    }

    @Override
    public String toString() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        if (distinct) {
            sql.append("DISTINCT ");
        }
        if (columnList.isEmpty()) {
            sql.append("*");
        } else {
            sql.append(StringUtils.join(columnList, ','));
        }
        if (!fromList.isEmpty()) {
            sql.append(" FROM ");
            sql.append(StringUtils.join(fromList, ','));
        }
        sql.append(StringUtils.join(joinList, null));
        return sql.toString();
    }
}