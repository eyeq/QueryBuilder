package utility.persistence.query;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import utility.Encodable;
import org.apache.commons.lang3.StringUtils;

public class SelectQueryBuilder extends SelectSqlBuilder<SelectQueryBuilder> implements QueryBuilder {

    private final EntityManager em;

    private final String selectSql;
    protected final List<String> withList = new ArrayList<>();
    protected final List<String> groupList = new ArrayList<>();
    protected final List<String> orderList = new ArrayList<>();
    protected final List<Object> valueList = new ArrayList<>();
    protected WhereClause where;

    protected int fetch = 0;

    public SelectQueryBuilder(EntityManager em) {
        this(em, null);
    }

    public SelectQueryBuilder(EntityManager em, String selectSql) {
        this.em = em;
        this.selectSql = selectSql;
    }

    public SelectQueryBuilder addValue(List<Object> list) {
        list.addAll(this.valueList);
        if (this.where != null) {
            list.addAll(this.where.getValueList());
        }
        return this;
    }

    public SelectQueryBuilder with(String name, SelectQueryBuilder select) {
        this.withList.add(name + " AS(" + select.toString() + ')');
        select.addValue(this.valueList);
        return this;
    }

    public SelectQueryBuilder select(SelectQueryBuilder select) {
        this.select('(' + select.toString() + ')');
        select.addValue(this.valueList);
        return this;
    }

    public SelectQueryBuilder selectExists(SelectQueryBuilder select) {
        this.select("EXISTS(" + select.toString() + ')');
        select.addValue(this.valueList);
        return this;
    }

    public SelectQueryBuilder selectExistsWhereEq(String tableName, String column, Object value) {
        return this.selectExists(new SelectQueryBuilder(em).from(tableName).where(Where.eq(column, value)));
    }

    public SelectQueryBuilder from(String name, SelectQueryBuilder select) {
        this.from('(' + select.toString() + ") AS " + name);
        select.addValue(this.valueList);
        return this;
    }

    public SelectQueryBuilder joinOn(String table, Where where) {
        this.joinList.add(" JOIN " + table + " ON " + where.getClause());
        this.valueList.add(where.valueList);
        return this;
    }

    public SelectQueryBuilder leftJoinOn(String table, Where where) {
        this.joinList.add(" LEFT JOIN " + table + " ON " + where.getClause());
        this.valueList.add(where.valueList);
        return this;
    }

    public SelectQueryBuilder where(String condition) {
        return this.where(new Where(condition));
    }

    public SelectQueryBuilder where(WhereClause where) {
        if (this.where == null) {
            this.where = where;
        } else {
            this.where.and(where);
        }
        return this;
    }

    public SelectQueryBuilder groupBy(String column) {
        this.groupList.add(column);
        return this;
    }

    public SelectQueryBuilder orderBy(String column) {
        this.orderList.add(column);
        return this;
    }

    public SelectQueryBuilder fetchFirst(int n) {
        this.fetch = n;
        return this;
    }

    @Override
    public Query build() {
        return this.build(null);
    }

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
        if (!withList.isEmpty()) {
            sql.append("WITH ");
            sql.append(StringUtils.join(withList, ','));
        }
        if (StringUtils.isEmpty(this.selectSql)) {
            sql.append(super.toString());
        } else {
            sql.append(this.selectSql);
            if (!fromList.isEmpty()) {
                sql.append(" FROM ");
                sql.append(StringUtils.join(fromList, ','));
            }
            sql.append(StringUtils.join(joinList, null));
        }
        if (where != null) {
            sql.append(" WHERE ");
            sql.append(where.getClause());
        }
        if (!groupList.isEmpty()) {
            sql.append(" GROUP BY ");
            sql.append(StringUtils.join(groupList, ','));
        }
        if (!orderList.isEmpty()) {
            sql.append(" ORDER BY ");
            sql.append(StringUtils.join(orderList, ','));
        }
        if (fetch > 0) {
            sql.append(" FETCH FIRST ");
            sql.append(fetch);
            sql.append(" ROWS ONLY");
        }
        return sql.toString();
    }
}