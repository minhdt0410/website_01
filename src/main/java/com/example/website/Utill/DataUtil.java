package com.example.website.Utill;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.stream.Collectors;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Query;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.text.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DataUtil {
    public static boolean isNullOrEmpty(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    public static boolean isNullOrEmpty(final Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNullOrEmpty(final Object[] collection) {
        return collection == null || collection.length == 0;
    }

    public static boolean isNullOrEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
    public static <T> List<T> convertObjectsToClassDynamic(List<String> attConvert, List<Object[]> objects, Class<T> clazz) {
        if (DataUtil.isNullOrEmpty(objects)) {
            return new ArrayList<>();
        }
        return objects.stream().map(item -> {
            try {
                return convertObjectsToClassDynamic(attConvert, item, clazz);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
    public static String safeToString(Object obj1, String defaultValue) {
        if (obj1 == null) {
            return defaultValue;
        }

        return obj1.toString().trim();
    }

    public static String safeToLower(String obj1) {
        if (obj1 == null) {
            return null;
        }

        return obj1.toLowerCase();
    }

    /**
     * @param obj1 Object
     * @return String
     */
    public static String safeToString(Object obj1) {
        return safeToString(obj1, "");
    }

    public static Integer safeToInteger(Object obj) {
        if (obj == null) {
            return 0;
        } else {
            try {
                return new Integer(obj.toString());
            } catch (Exception e) {
                return 0;
            }
        }
    }
    public static <T> T convertObjectsToClassDynamic(List<String> attConvert,
                                                     Object[] objects, Class<T> clazz) throws Exception {
        Object object = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < attConvert.size(); i++) {
            Field f;
            int finalIndex = i;
            f = Arrays.stream(fields).filter(item -> attConvert.get(finalIndex).equals(item.getName())).findFirst().orElse(null);
            if (f != null) {
                f.setAccessible(true);
                Class<?> t = f.getType();
                if (objects[i] == null)
                    continue;
                    switch (t.getName()) {
                        case "java.lang.String":
                            if (objects[i] instanceof String || objects[i] instanceof Long || objects[i] instanceof BigInteger ||
                                    objects[i] instanceof Integer || objects[i] instanceof BigDecimal) {
                                f.set(object, DataUtil.safeToString(objects[i]));
                            }
                            break;
                        case "java.lang.Long":
                        case "java.lang.Double":
                        case "java.lang.Boolean":
                        case "boolean":
                            f.set(object, objects[i]);
                            break;
                        case "java.lang.Integer":
                        case "int":
                            f.set(object, DataUtil.safeToInteger(objects[i]));
                            break;
                        case "java.math.BigInteger":
                            f.set(object, DataUtil.safeToBigInteger(objects[i]));
                            break;
                        case "java.math.BigDecimal":
                            f.set(object, DataUtil.safeToBigDecimal(objects[i]));
                            break;
                        default:
                            break;
                    }
            }
        }
        return (T) object;
    }
    public static BigDecimal safeToBigDecimal(Object obj1) {
        if (obj1 == null) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(obj1.toString());
        } catch (final NumberFormatException nfe) {
            return BigDecimal.ZERO;
        }
    }
    public static BigInteger safeToBigInteger(Object obj1, BigInteger defaultValue) {
        if (obj1 == null) {
            return defaultValue;
        }
        try {
            return new BigInteger(obj1.toString());
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static BigInteger safeToBigInteger(Object obj1) {
        return safeToBigInteger(obj1, BigInteger.ZERO);
    }

    public static List<String> changeParamTypeSqlToJava(String sqlType) {
        String[] tmp = sqlType.trim().split(",");
        List<String> stringList = new ArrayList<>();
        for (String s : tmp) {
            s = s.trim().replaceAll("\\s+,", "");
            StringBuilder builder = new StringBuilder();
            String[] words = s.split("[\\W_]+");
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                if (words.length > 1) {
                    if (i == 0) {
                        word = word.isEmpty() ? word : word.toLowerCase();
                    } else {
                        word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
                    }
                } else {
                    word = word.isEmpty() ? word : word.toLowerCase();
                }
                builder.append(word);
            }
            stringList.add(builder.toString());
        }
        return stringList;
    }
}
