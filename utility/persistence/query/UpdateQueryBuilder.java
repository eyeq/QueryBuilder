package utility.persistence.query;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import utility.Encodable;

public class UpdateQueryBuilder implements QueryBuilder {

    private final EntityManager em;
    private final String tableName;

    protected final List<String> columnList = new ArrayList<>();

    protected final List<Object> valueList = new ArrayList<>();

    protected WhereClause where;

    public UpdateQueryBuilder(EntityManager em, String tableName) {
        this.em = em;
        this.tableName = tableName;
    }

    public UpdateQueryBuilder set(String column, Object value) {
        if (value == null) {
            columnList.add(column + "=null");
        } else {
            columnList.add(column + "=?");
            valueList.add(value);
        }
        return this;
    }

    public UpdateQueryBuilder setWindow(String column, String function) {
        columnList.add(column + '=' + function);
        return this;
    }

    public UpdateQueryBuilder where(WhereClause where) {
        if (this.where == null) {
            this.where = where;
        } else {
            this.where.and(where);
        }
        return this;
    }

    @Override
    public Query build() {
        return this.build(null);
    }

    @Override
    public Query build(Class resultClass) {
        Query query;
        if (resultClass == null) {
            query = em.createNativeQuery(this.toString());
        } else {
            query = em.createNativeQuery(this.toString(), resultClass);
        }

        int i = 1;
        for (Object value : valueList) {
            if (value instanceof Encodable) {
                value = ((Encodable) value).getCode();
            }
            query.setParameter(i++, value);
        }
        if (where != null) {
            for (Object value : where.getValueList()) {
                if (value instanceof Encodable) {
                    value = ((Encodable) value).getCode();
                }
                query.setParameter(i++, value);
            }
        }
        return query;
    }

    @Override
    public String toString() {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(tableName);
        sql.append(" SET ");
        sql.append(StringUtils.join(columnList, ','));
        if (where != null) {
            sql.append(" WHERE ");
            sql.append(where.getClause());
        }
        sql.append(';');
        return sql.toString();
    }
}
