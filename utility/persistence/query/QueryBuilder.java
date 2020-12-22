package utility.persistence.query;

import javax.persistence.Query;

public interface QueryBuilder {

    public Query build();

    public Query build(Class resultClass);
}
