package utility.persistence.query;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import utility.Encodable;

public class MultipleInsertQueryBuilder implements QueryBuilder {

    private final EntityManager em;
    private final String tableName;

    protected final List<String> valuesList = new ArrayList<>();

    protected final List<Object> valueList = new ArrayList<>();

    public MultipleInsertQueryBuilder(EntityManager em, String tableName) {
        this.em = em;
        this.tableName = tableName;
    }

    public MultipleInsertQueryBuilder values(InsertQueryBuilder insert) {
        this.valuesList.add(StringUtils.join(insert.positionList, ','));
        this.valueList.addAll(insert.valueList);
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

        for (int i = 0; i < valueList.size(); i++) {
            Object value = valueList.get(i);
            if (value instanceof Encodable) {
                value = ((Encodable) value).getCode();
            }
            query.setParameter((i + 1), value);
        }
        return query;
    }

    @Override
    public String toString() {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(tableName);
        sql.append(" VALUES(");
        sql.append(StringUtils.join(valuesList, "),("));
        sql.append(");");
        return sql.toString();
    }
}
