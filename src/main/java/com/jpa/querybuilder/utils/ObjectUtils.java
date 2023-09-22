package com.jpa.querybuilder.utils;

import lombok.SneakyThrows;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ObjectUtils {

    public static Map<String, Object> getFlatObject(Map<String, Object> objFields) {
        Map<String, Object> flats = new HashMap<>();
        flatObject(flats, objFields, "");
        return flats;
    }

    public static Map<String, Object> buildFlatObject(Map<String, Object> objFields) {
        Map<String, Object> flats = new HashMap<>();
        buildFlatObject(flats, objFields, "");
        return flats;
    }

    @SneakyThrows
    public static void resetObjectFieldValues(Class type, Object obj, Map<String, Object> resetFields) {
        if (Objects.isNull(resetFields) || resetFields.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry: resetFields.entrySet()) {
            try {
                if (!entry.getKey().contains(".")) {
                    PropertyUtils.setSimpleProperty(type.cast(obj), entry.getKey(),null);
                } else {
                    PropertyUtils.setNestedProperty(obj, entry.getKey(),null);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception ex) {

            }
        }
    }

    @SneakyThrows
    public static void overrideObjectFieldValues(Class type, Object obj, Map<String, Object> resetFields) {
        if (Objects.isNull(resetFields) || resetFields.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry: resetFields.entrySet()) {
            try {
                if (!entry.getKey().contains(".")) {
                    PropertyUtils.setSimpleProperty(type.cast(obj), entry.getKey(),entry.getValue());
                } else {
                    PropertyUtils.setNestedProperty(obj, entry.getKey(),entry.getValue());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    private static void flatObject(Map<String, Object> flats, Map<String, Object> fields, String fieldName) {
        for (Map.Entry<String, Object> entry: fields.entrySet()) {
            String name;
            if (StringUtils.isBlank(fieldName)) {
                name = entry.getKey();
            } else {
                name = String.format("%s.%s", fieldName, entry.getKey());
            }

            putObject(flats, entry, name);
        }
    }

    private static void putObject(Map<String, Object> flats, Map.Entry<String, Object> entry, String name) {
        if (Objects.isNull(entry.getValue())) {
            flats.put(name, entry.getValue());
        } else if (isArrays(entry.getValue())) {
            ArrayList arrays = (ArrayList)  entry.getValue();
            for (int i = 0; i < arrays.size(); i++) {
                name = name.replace(String.format("[%s]", (i - 1)), "");
                name = String.format("%s[%s]", name, i);
                if (!Objects.isNull(arrays.get(i)) && !isValueType(arrays.get(i))) {
                    flatObject(flats, (Map<String, Object>) arrays.get(i), name);
                }
            }
        } else if (!isValueType(entry.getValue())) {
            flatObject(flats, (Map<String, Object>)entry.getValue(), name);
        }
    }

    @SneakyThrows
    private static void buildFlatObject(Map<String, Object> flats, Map<String, Object> fields, String fieldName) {
        if (fields == null || fields.isEmpty()) return;

        for (Map.Entry<String, Object> entry: fields.entrySet()) {
            String name;
            if (StringUtils.isBlank(fieldName)) {
                name = entry.getKey();
                if (!isValueType(entry.getValue())) {
                    flatObject(flats, (Map<String, Object>)entry.getValue(), name);
                }
                else {
                    flats.put(name, entry.getValue());
                }
            } else {
                name = String.format("%s.%s", fieldName, entry.getKey());
                if (Objects.isNull(entry.getValue())) {
                    flats.put(name, entry.getValue());
                    continue;
                }
                if (!isValueType(entry.getValue())) {
                    flatObject(flats, (Map<String, Object>)entry.getValue(), name);
                }
            }
        }
    }

    private static boolean isValueType(Object obj) {
        return obj instanceof LocalDate
                || obj instanceof Date
                || obj instanceof LocalDateTime
                || obj instanceof Boolean
                || obj instanceof Integer
                || obj instanceof Long
                || obj instanceof String
                || obj instanceof Float
                || obj instanceof Enum
                || obj instanceof BigDecimal;
    }

    private static boolean isArrays(Object obj) {
        return obj instanceof ArrayList
                || obj instanceof List
                || obj instanceof Array;
    }
}
