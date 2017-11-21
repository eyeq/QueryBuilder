package utility.persistence.query;

import java.util.List;

public interface WhereClause {

    WhereClause and(WhereClause where);

    WhereClause or(WhereClause where);

    String getClause();

    List<Object> getValueList();
}