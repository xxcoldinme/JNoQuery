package com.itzstonlex.jnq.orm.data;

import com.itzstonlex.jnq.content.type.SchemaContent;
import com.itzstonlex.jnq.content.type.TableContent;
import com.itzstonlex.jnq.orm.ObjectMapper;
import com.itzstonlex.jnq.orm.ObjectMapperProperties;
import com.itzstonlex.jnq.orm.annotation.MappingEntity;
import com.itzstonlex.jnq.orm.annotation.MappingColumn;
import com.itzstonlex.jnq.orm.base.properties.ObjectMapperPropertiesImpl;
import com.itzstonlex.jnq.orm.base.request.MappingRequestFactoryImpl;
import com.itzstonlex.jnq.orm.data.repository.EntityRepositoryContext;
import com.itzstonlex.jnq.orm.exception.JnqObjectMappingException;
import com.itzstonlex.jnq.orm.mapper.AnnotationMapper;
import com.itzstonlex.jnq.orm.mapper.JsonMapper;
import com.itzstonlex.jnq.orm.mapper.SerializationMapper;
import com.itzstonlex.jnq.orm.request.MappingRequestFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ObjectMappingService {
    
    public static ObjectMappingService instanceOf(@NonNull SchemaContent schemaContent, @NonNull String table) {
        return new ObjectMappingService(schemaContent, table);
    }
    
    public static ObjectMappingService instanceOf(@NonNull TableContent tableContent) {
        return instanceOf(tableContent.getSchema(), tableContent.getName());
    }

    @Getter
    SchemaContent schemaContent;
    @Getter
    String table;

    @Getter
    EntityRepositoryContext entityRepositoryContext = new EntityRepositoryContext(this);

    Map<Class<?>, ObjectMapper<?>>
            mappersByTypesMap = new HashMap<>(),
            existsMappersMap = new HashMap<>();

    {
        existsMappersMap.put(AnnotationMapper.class, new AnnotationMapper<>());
        existsMappersMap.put(JsonMapper.class, new JsonMapper<>());
        existsMappersMap.put(SerializationMapper.class, new SerializationMapper<>());
    }

    private Map<String, Field> _toMapFields(Class<?> cls) {
        Map<String, Field> map = new HashMap<>();

        Field[] declaredFields = cls.getDeclaredFields();

        for (Field field : declaredFields) {
            String name = field.getName();

            MappingColumn mappingColumn = field.getDeclaredAnnotation(MappingColumn.class);

            if (mappingColumn != null && !mappingColumn.value().isEmpty()) {
                name = mappingColumn.value();
            }

            field.setAccessible(true);
            map.put(name, field);
        }

        return map;
    }

    public MappingRequestFactory getRequestFactory() {
        return new MappingRequestFactoryImpl(schemaContent, table, this);
    }

    public ObjectMapperProperties createProperties() {
        return new ObjectMapperPropertiesImpl();
    }

    public Map<String, Object> toMap(@NonNull Object object)
            throws JnqObjectMappingException {

        Map<String, Object> map = new LinkedHashMap<>();

        Map<String, Field> mapFields = _toMapFields(object.getClass());

        for (String name : mapFields.keySet()) {
            Field field = mapFields.get(name);

            try {
                map.put(name, field.get(object));
            }
            catch (Exception exception) {
                throw new JnqObjectMappingException("parse to map", exception);
            }
        }

        return map;
    }

    public Map<String, Class<?>> toMap(@NonNull Class<?> cls) {
        Map<String, Class<?>> map = new LinkedHashMap<>();

        _toMapFields(cls).forEach((name, field) -> map.put(name, field.getType()));
        return map;
    }

    @SuppressWarnings("unchecked")
    public <T> ObjectMapper<T> findRequiredMapperByType(@NonNull Class<? extends T> cls) throws JnqObjectMappingException {
        if (!mappersByTypesMap.containsKey(cls)) {
            throw new JnqObjectMappingException("No mapper found");
        }

        return (ObjectMapper<T>) mappersByTypesMap.get(cls);
    }

    @SuppressWarnings("unchecked")
    public <T> ObjectMapper<T> findMapperByType(@NonNull Class<? extends T> cls) throws JnqObjectMappingException {
        for (Class<?> registeredType : mappersByTypesMap.keySet()) {

            if (registeredType.isAssignableFrom(cls)) {
                return (ObjectMapper<T>) mappersByTypesMap.get(cls);
            }
        }

        if (cls.getDeclaredAnnotation(MappingEntity.class) != null) {
            return findMapper(AnnotationMapper.class);
        }

        if (cls.isAssignableFrom(Serializable.class)) {
            return findMapper(SerializationMapper.class);
        }

        return findMapper(JsonMapper.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends ObjectMapper<?>> T findMapper(@NonNull Class<T> mapperCls) throws JnqObjectMappingException {
        for (Class<?> registeredType : existsMappersMap.keySet()) {

            ObjectMapper<?> registeredMapper = existsMappersMap.get(registeredType);

            if (registeredMapper.getClass().equals(mapperCls)) {
                return (T) registeredMapper;
            }
        }

        throw new JnqObjectMappingException("Unknown target mapper class");
    }

    public <T> void register(@NonNull Class<T> cls, @NonNull ObjectMapper<T> mapper) throws JnqObjectMappingException {
        if (mappersByTypesMap.containsKey(cls)) {
            throw new JnqObjectMappingException("Type `%s` is already registered!", cls.getName());
        }

        mappersByTypesMap.put(cls, mapper);

        if (!existsMappersMap.containsKey(mapper.getClass())) {
            existsMappersMap.put(mapper.getClass(), mapper);
        }
    }

    public void unregister(@NonNull Class<?> cls) {
        mappersByTypesMap.remove(cls);
    }

    public void unregisterAll(@NonNull Class<? extends ObjectMapper<?>> mapperCls) {
        for (Class<?> registeredType : existsMappersMap.keySet()) {

            ObjectMapper<?> registeredMapper = existsMappersMap.get(registeredType);

            if (registeredMapper.getClass().equals(mapperCls)) {
                mappersByTypesMap.remove(registeredType);
            }
        }
    }
}
