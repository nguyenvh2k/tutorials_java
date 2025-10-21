package me.meocoder.anotation.process;

import me.meocoder.anotation.Init;
import me.meocoder.anotation.JsonElement;
import me.meocoder.anotation.JsonSerializable;
import me.meocoder.anotation.exception.JsonSerializationException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ObjectToJsonConverter {

    public String convertToJson(Object object) throws JsonSerializationException {
        try {
            checkIfSerializable(object);
            initializeObject(object);
            return getJsonString(object);
        } catch (Exception e) {
            throw new JsonSerializationException(e.getMessage());
        }
    }

    private void checkIfSerializable(Object obj) {
        if (obj == null) {
            throw new JsonSerializationException("Object is null");
        }

        Class<?> clazz = obj.getClass();
        if (!clazz.isAnnotationPresent(JsonSerializable.class)){
            throw new JsonSerializationException(
                    "Class " + clazz.getSimpleName() + " is not annotated with JsonSerializable"
            );
        }
    }

    private void initializeObject(Object obj) throws InvocationTargetException, IllegalAccessException {
        Class<?> clazz = obj.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Init.class)) {
                method.setAccessible(true);
                method.invoke(obj);
            }
        }
    }

    private String getJsonString(Object obj) throws JsonSerializationException, IllegalAccessException {
        Class<?> clazz = obj.getClass();
        Map<String, String> jsonElements = new HashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(JsonElement.class)) {
                String key = getKey(field);
                String value = (String) field.get(obj);
                jsonElements.put(key, value);
            }
        }
        String json = jsonElements.entrySet().stream()
                .map(entry -> "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"")
                .collect(Collectors.joining(","));
        return "{" + json + "}";
    }

    private String getKey(Field field) throws IllegalAccessException {
        JsonElement annotation = field.getAnnotation(JsonElement.class);
        return annotation.key().isEmpty() ? field.getName() : annotation.key();
    }
}
