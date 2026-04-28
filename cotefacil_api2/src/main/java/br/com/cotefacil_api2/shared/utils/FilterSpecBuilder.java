package br.com.cotefacil_api2.shared.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class FilterSpecBuilder {

    private FilterSpecBuilder() {
    }

    public static <T> Specification<T> build(Map<String, String> filters, Map<String, String> fieldMapping) {
        if (filters == null || filters.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value == null || value.isBlank()) continue;
                String mapped = fieldMapping != null ? fieldMapping.getOrDefault(key, key) : key;
                Path<?> path = resolvePath(root, mapped);
                if (path == null) continue;
                Predicate predicate = buildPredicate(cb, path, value.trim());
                if (predicate != null) predicates.add(predicate);
            }
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Path<?> resolvePath(Root<?> root, String path) {
        if (path == null || path.isBlank()) return null;
        if (path.contains(".")) {
            String[] parts = path.split("\\.");
            Path<?> current = root.get(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                current = current.get(parts[i]);
            }
            return current;
        }
        if (hasField(root, path)) return root.get(path);
        if (path.endsWith("Id")) {
            String candidate = path.substring(0, path.length() - 2);
            if (hasField(root, candidate)) {
                return root.get(candidate).get("id");
            }
        }
        return null;
    }

    private static boolean hasField(Root<?> root, String field) {
        try {
            root.get(field);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static Predicate buildPredicate(CriteriaBuilder cb, Path<?> path, String rawValue) {
        Class<?> type = path.getJavaType();
        if (String.class.equals(type)) {
            return cb.like(cb.lower(path.as(String.class)), "%" + rawValue.toLowerCase() + "%");
        }
        if (Boolean.class.equals(type) || boolean.class.equals(type)) {
            Boolean value = parseBoolean(rawValue);
            if (value == null) return null;
            return cb.equal(path, value);
        }
        if (Number.class.isAssignableFrom(type) || type.isPrimitive()) {
            Number number = parseNumber(rawValue, type);
            if (number == null) return null;
            return cb.equal(path, number);
        }
        if (type.isEnum()) {
            Object enumValue = parseEnum(rawValue, type);
            if (enumValue == null) return null;
            return cb.equal(path, enumValue);
        }
        if (LocalDate.class.equals(type)) {
            LocalDate date = parseLocalDate(rawValue);
            if (date == null) return null;
            return cb.equal(path, date);
        }
        if (LocalDateTime.class.equals(type)) {
            LocalDateTime dateTime = parseLocalDateTime(rawValue);
            if (dateTime == null) return null;
            return cb.equal(path, dateTime);
        }
        if (Instant.class.equals(type)) {
            Instant instant = parseInstant(rawValue);
            if (instant == null) return null;
            return cb.equal(path, instant);
        }
        if (OffsetDateTime.class.equals(type)) {
            OffsetDateTime offset = parseOffsetDateTime(rawValue);
            if (offset == null) return null;
            return cb.equal(path, offset);
        }
        return cb.like(cb.lower(path.as(String.class)), "%" + rawValue.toLowerCase() + "%");
    }

    private static Boolean parseBoolean(String raw) {
        if ("true".equalsIgnoreCase(raw) || "yes".equalsIgnoreCase(raw) || "1".equals(raw)) return true;
        if ("false".equalsIgnoreCase(raw) || "no".equalsIgnoreCase(raw) || "0".equals(raw)) return false;
        return null;
    }

    private static Number parseNumber(String raw, Class<?> type) {
        try {
            if (Integer.class.equals(type) || int.class.equals(type)) return Integer.valueOf(raw);
            if (Long.class.equals(type) || long.class.equals(type)) return Long.valueOf(raw);
            if (Double.class.equals(type) || double.class.equals(type)) return Double.valueOf(raw);
            if (Float.class.equals(type) || float.class.equals(type)) return Float.valueOf(raw);
            if (Short.class.equals(type) || short.class.equals(type)) return Short.valueOf(raw);
        } catch (Exception ignored) {
            return null;
        }
        try {
            return Double.valueOf(raw);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Object parseEnum(String raw, Class<?> type) {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Enum> enumType = (Class<? extends Enum>) type;
            return Enum.valueOf(enumType, raw.trim().toUpperCase());
        } catch (Exception ignored) {
            return null;
        }
    }

    private static LocalDate parseLocalDate(String raw) {
        try {
            return LocalDate.parse(raw);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static LocalDateTime parseLocalDateTime(String raw) {
        try {
            return LocalDateTime.parse(raw, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static OffsetDateTime parseOffsetDateTime(String raw) {
        try {
            return OffsetDateTime.parse(raw, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Instant parseInstant(String raw) {
        try {
            return Instant.parse(raw);
        } catch (Exception ignored) {
            return null;
        }
    }
}
