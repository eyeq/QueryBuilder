package utility.persistence.query;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import utility.Encodable;

public class DeleteQueryBuilder implements QueryBuilder {

    private final EntityManager em;
    private final String tableName;

    protected WhereClause where;

    public DeleteQueryBuilder(EntityManager em, String tableName) {
        this.em = em;
        this.tableName = tableName;
    }

    public DeleteQueryBuilder where(WhereClause where) {
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
        sql.append("DELETE FROM ");
        sql.append(tableName);
        if (where != null) {
            sql.append(" WHERE ");
            sql.append(where.getClause());
        }
        sql.append(';');
        return sql.toString();
    }
}
