package utility.persistence.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class Where implements WhereClause {

    private final StringBuilder clause;

    protected final List<Object> valueList;

    @SuppressWarnings("unchecked")
    public Where(String clause) {
        this(clause, Collections.EMPTY_LIST);
    }

    public Where(String clause, SelectQueryBuilder select) {
        this.clause = new StringBuilder(clause);
        this.valueList = new ArrayList<>();

        this.valueList.addAll(select.valueList);
        if (select.where != null) {
            this.valueList.addAll(select.where.getValueList());
        }
    }

    public Where(String clause, Object... values) {
        this(clause, Arrays.asList(values));
    }

    public Where(String clause, List<? extends Object> valueList) {
        this.clause = new StringBuilder(clause);
        this.valueList = new ArrayList<>(valueList.size());

        this.valueList.addAll(valueList);
    }

    @Override
    public Where and(WhereClause where) {
        if (where == null) {
            return this.and(NULL());
        }
        this.clause.append(" AND ").append(where.getClause());
        this.valueList.addAll(where.getValueList());
        return this;
    }

    public Where and(String clause) {
        return this.and(new Where(clause));
    }

    @Override
    public Where or(WhereClause where) {
        if (where == null) {
            return this.or(NULL());
        }
        this.clause.append(" OR ").append(where.getClause());
        this.valueList.addAll(where.getValueList());
        return this;
    }

    public Where or(String clause) {
        return this.or(new Where(clause));
    }

    @Override
    public String getClause() {
        return '(' + clause.toString() + ')';
    }

    @Override
    public List<Object> getValueList() {
        return valueList;
    }

    @Override
    public String toString() {
        return this.getClause();
    }

    public Where where(String clause) {
        return this.and(new Where(clause));
    }

    public Where whereOr(String clause) {
        return this.or(new Where(clause));
    }

    public Where whereLike(String column, String pattern) {
        return this.and(like(column, pattern));
    }

    public Where whereIlike(String column, String pattern) {
        return this.and(ilike(column, pattern));
    }

    public Where whereEq(String column, Object value) {
        return this.and(eq(column, value));
    }

    public Where whereNe(String column, Object value) {
        return this.and(ne(column, value));
    }

    public Where whereGt(String column, Object value) {
        return this.and(gt(column, value));
    }

    public Where whereLt(String column, Object value) {
        return this.and(lt(column, value));
    }

    public Where whereGe(String column, Object value) {
        return this.and(ge(column, value));
    }

    public Where whereLe(String column, Object value) {
        return this.and(le(column, value));
    }

    public Where whereIn(String column, Object... values) {
        return this.and(in(column, values));
    }

    public Where whereIn(String column, List<? extends Object> values) {
        return this.and(in(column, values));
    }

    public Where whereIn(String column, SelectQueryBuilder select) {
        return this.and(in(column, select));
    }

    public Where whereBetween(String startColumn, Object value, String endColumn) {
        return this.and(between(startColumn, value, endColumn));
    }

    public Where whereBetween(Object start, String column, Object end) {
        return this.and(between(start, column, end));
    }

    public Where whereBetween(String startColumn, Object start, Object end, String endColumn) {
        return this.and(between(startColumn, start, end, endColumn));
    }

    public Where whereBetween(Object start, String startColumn, String endColumn, Object end) {
        return this.and(between(start, startColumn, endColumn, end));
    }

    public static Where NULL() {
        return new Where("NULL");
    }

    public static Where FALSE() {
        return new Where("FALSE");
    }

    public static Where TRUE() {
        return new Where("TRUE");
    }

    public static Where compare(String column, char operator, Object value) {
        String clause = column + operator + '?';
        return new Where(clause, value);
    }

    public static Where compare(String column, String operator, Object value) {
        String clause = column + operator + '?';
        return new Where(clause, value);
    }

    public static Where like(String column, String pattern) {
        return compare(column, " LIKE ", pattern);
    }

    public static Where ilike(String column, String pattern) {
        return compare(column, " ILIKE ", pattern);
    }

    public static Where eq(String column, Object value) {
        if (value == null) {
            return new Where(column + " IS NULL");
        }
        return compare(column, '=', value);
    }

    public static Where ne(String column, Object value) {
        if (value == null) {
            return new Where(column + " IS NOT NULL");
        }
        return compare(column, "<>", value);
    }

    protected static Where compareAndNullCheck(String column, char operator, Object value) {
        if (value == null) {
            return NULL();
        }
        return compare(column, operator, value);
    }

    protected static Where compareAndNullCheck(String column, String operator, Object value) {
        if (value == null) {
            return NULL();
        }
        return compare(column, operator, value);
    }

    public static Where gt(String column, Object value) {
        return compareAndNullCheck(column, '>', value);
    }

    public static Where lt(String column, Object value) {
        return compareAndNullCheck(column, '<', value);
    }

    public static Where ge(String column, Object value) {
        return compareAndNullCheck(column, ">=", value);
    }

    public static Where le(String column, Object value) {
        return compareAndNullCheck(column, "<=", value);
    }

    public static Where in(String column, Object... values) {
        if (values.length == 0) {
            return FALSE();
        }
        String clause = column + " IN(?" + StringUtils.repeat(",?", values.length - 1) + ')';
        return new Where(clause, values);
    }

    public static Where in(String column, List<? extends Object> values) {
        if (values.isEmpty()) {
            return FALSE();
        }
        String clause = column + " IN(?" + StringUtils.repeat(",?", values.size() - 1) + ')';
        return new Where(clause, values);
    }

    public static Where in(String column, SelectQueryBuilder select) {
        String clause = column + " IN (" + select.toString() + ')';
        return new Where(clause, select);
    }

    public static Where between(String startColumn, Object value, String endColumn) {
        if (value == null) {
            return NULL();
        }
        String clause = "? BETWEEN " + startColumn + " AND " + endColumn;
        return new Where(clause, value);
    }

    public static Where between(Object start, String column, Object end) {
        if (start == null || end == null) {
            return NULL();
        }
        String clause = column + " BETWEEN ? AND ?";
        return new Where(clause, start, end);
    }

    public static Where between(String startColumn, Object start, Object end, String endColumn) {
        if (start == null || end == null) {
            return NULL();
        }
        // startColumn <= start AND end <= endColumn
        return le(startColumn, start).whereGe(endColumn, end);
    }

    public static Where between(Object start, String startColumn, String endColumn, Object end) {
        if (start == null || end == null) {
            return NULL();
        }
        // start <= startColumn  AND endColumn <= end
        return ge(startColumn, start).whereLe(endColumn, end);
    }
}
