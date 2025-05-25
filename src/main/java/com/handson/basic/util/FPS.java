package com.handson.basic.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handson.basic.model.Pagination;
import com.handson.basic.model.PaginationAndList;
import com.handson.basic.model.SortDirection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.math.BigInteger;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FPS<T> {
    private List<FPSField> select = new ArrayList<>();
    private List<String> from = new ArrayList<>();
    private List<String> joins = new ArrayList<>();
    private List<FPSCondition> conditions = new ArrayList<>();
    private String sortField;
    private SortDirection sortDirection;
    private Integer page;
    private Integer count;
    private Class<T> itemClass;

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'").withZone(ZoneOffset.UTC);

    public FPS() {}

    public PaginationAndList exec(EntityManager em, ObjectMapper om) throws JsonProcessingException {
        Query qrySelect = em.createNativeQuery(getSelectSql(), itemClass);
        Query qryCount = em.createNativeQuery(getCountSql());

        conditions.stream()
                .filter(c -> c.getValue() != null)
                .forEach(c -> {
                    qrySelect.setParameter(c.getParameterName(), c.getValue());
                    qryCount.setParameter(c.getParameterName(), c.getValue());
                });

        List<T> rows = qrySelect.getResultList();

        // FIX: handle possible Long, BigInteger, Integer return types
        Object raw = qryCount.getSingleResult();
        long totalCount;

        if (raw instanceof BigInteger bigInt) {
            totalCount = bigInt.longValue();
        } else if (raw instanceof Long longVal) {
            totalCount = longVal;
        } else if (raw instanceof Integer intVal) {
            totalCount = intVal;
        } else {
            throw new IllegalStateException("Unexpected result type for count: " + raw.getClass());
        }

        int totalPages = (int) Math.ceil((double) totalCount / count);
        return PaginationAndList.of(Pagination.of(page, totalPages, (int) totalCount), rows);
    }

    private String getSelectSql() {
        String fieldsSql = "select " + select.stream()
                .map(f -> f.getField() + " " + f.getAlias())
                .collect(Collectors.joining(", "));
        return fieldsSql + getFromSql() + getWhereSql() + getOrderSql() + getLimitSql();
    }

    private String getCountSql() {
        return "select count(*)" + getFromSql() + getWhereSql();
    }

    private String getFromSql() {
        return " from " + String.join(",", from);
    }

    private String getWhereSql() {
        List<String> parts = new ArrayList<>();
        if (!joins.isEmpty()) parts.addAll(joins);
        conditions.stream()
                .filter(c -> c.getValue() != null)
                .map(FPSCondition::getCondition)
                .forEach(parts::add);
        return parts.isEmpty() ? "" : " where " + String.join(" and ", parts);
    }

    private String getOrderSql() {
        if (sortField == null) return "";
        return " order by " + sortField + (SortDirection.desc.equals(sortDirection) ? " desc" : "");
    }

    private String getLimitSql() {
        if (page == null && count == null) return "";
        StringBuilder sb = new StringBuilder();
        if (count != null) sb.append(" limit ").append(count);
        if (page != null) sb.append(" offset ").append((page - 1) * count);
        return sb.toString();
    }

    public static <T> FPSBuilder<T> aFPS() {
        return new FPSBuilder<>();
    }

    public static final class FPSBuilder<T> {
        private final FPS<T> fps = new FPS<>();

        public FPSBuilder<T> select(List<FPSField> select) {
            fps.select = select;
            return this;
        }

        public FPSBuilder<T> from(List<String> from) {
            fps.from = from;
            return this;
        }

        public FPSBuilder<T> joins(List<String> joins) {
            fps.joins = joins;
            return this;
        }

        public FPSBuilder<T> conditions(List<FPSCondition> conditions) {
            fps.conditions = conditions;
            return this;
        }

        public FPSBuilder<T> sortField(String sortField) {
            fps.sortField = sortField;
            return this;
        }

        public FPSBuilder<T> sortDirection(SortDirection sortDirection) {
            fps.sortDirection = sortDirection;
            return this;
        }

        public FPSBuilder<T> page(Integer page) {
            fps.page = page;
            return this;
        }

        public FPSBuilder<T> count(Integer count) {
            fps.count = count;
            return this;
        }

        public FPSBuilder<T> itemClass(Class<T> itemClass) {
            fps.itemClass = itemClass;
            return this;
        }

        public FPS<T> build() {
            return fps;
        }
    }
}

//