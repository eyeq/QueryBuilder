package utility.persistence.query;

import java.util.List;
import javax.persistence.NoResultException;

public class QueryUtils {

    @SuppressWarnings("unchecked")
    public static <T> T getSingleResult(SelectQueryBuilder builder, Class<? extends T> clazz) {
        try {
            return (T) builder.build(clazz).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getResultList(SelectQueryBuilder builder, Class<? extends T> clazz) {
        return (List<T>) builder.build(clazz).getResultList();
    }
}
