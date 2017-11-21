package utility.persistence;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ArrayConverter implements AttributeConverter<Object[], Object[]> {

    @Override
    public Object[] convertToDatabaseColumn(Object[] attribute) {
        return attribute;
    }

    @Override
    public Object[] convertToEntityAttribute(Object[] dbData) {
        return dbData;
    }
}