package com.mq.back.exceptions.custom;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class clazz, Object... searchParamsMap) {
        super(EntityNotFoundException.generateMessage(clazz.getSimpleName(), toMap(String.class, String.class, searchParamsMap)));
    }

    private static String generateMessage(String entity, Map<String, String> searchParams) {
        return StringUtils.capitalize(entity) +
                " was not found for parameters " +
                searchParams;
    }

    private static <K, V> Map<K, V> toMap(Class<K> keyType, Class<V> valueType, Object... entries) {
        if(entries.length % 2 != 0)
            throw new IllegalArgumentException("Invalid entries");
        return IntStream.range(0, entries.length / 2)
                .map(index -> index * 2)
                .collect(HashMap::new,
                        (container, index) -> container.put(keyType.cast(entries[index]), valueType.cast(entries[index + 1])),
                        (startHashMap, finalHashMap) -> finalHashMap.putAll(startHashMap)
                );
    }
}

