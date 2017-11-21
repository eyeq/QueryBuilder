package utility;

import java.io.Serializable;

public interface Encodable<T extends Serializable> {

    T getCode();
}