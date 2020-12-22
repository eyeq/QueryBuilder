package utility.persistence;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.UUID;

@Converter
public class UUIDConverter implements AttributeConverter<UUID, Object> {

    @Override
    public Object convertToDatabaseColumn(UUID attribute) {
        return attribute;
    }

    @Override
    public UUID convertToEntityAttribute(Object dbData) {
        if(dbData == null) {
            return null;
        }
        if(dbData instanceof UUID) {
            return (UUID) dbData;
        }
        return UUID.fromString(dbData.toString());
    }
}
