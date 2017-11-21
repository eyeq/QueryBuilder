package utility.persistence.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import utility.Encodable;
import org.apache.commons.lang3.StringUtils;

public class InsertQueryBuilder implements QueryBuilder {

    private final EntityManager em;
    private final String tableName;

    protected final List<String> positionList = new ArrayList<>();
    protected final List<Object> valueList = new ArrayList<>();

    public InsertQueryBuilder(EntityManager em, String tableName) {
        this.em = em;
        this.tableName = tableName;
    }

    public InsertQueryBuilder value(Object value) {
        if (value == null) {
            positionList.add("null");
        } else {
            positionList.add("?");
            valueList.add(value);
        }
        return this;
    }

    public InsertQueryBuilder values(Object... values) {
        for (Object value : values) {
            this.value(value);
        }
        return this;
    }

    public InsertQueryBuilder values(Iterable<? extends Object> values) {
        Iterator iterator = values.iterator();
        while (iterator.hasNext()) {
            this.value(iterator.next());
        }
        return this;
    }

    public InsertQueryBuilder valueWindow(String function) {
        positionList.add(function);
        return this;
    }

    @Override
    public Query build() {
        Query query = em.createNativeQuery(this.toString());
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
        sql.append(StringUtils.join(positionList, ','));
        sql.append(");");
        return sql.toString();
    }
}