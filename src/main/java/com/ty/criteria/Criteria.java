package com.ty.criteria;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据检索条件
 */
public class Criteria {

    /**
     * 检索条件列表
     */
    private List<Criterion> criteria;

    public Criteria() {
        criteria = new ArrayList<Criterion>();
    }

    /**
     * get criteria
     */
    public List<Criterion> getCriteria() {
        return this.criteria;
    }

    /**
     * 检索列表是否有效
     */
    public boolean isValid() {
        return criteria.size() != 0;
    }

    /**
     * is null
     *
     * @param fieldName 字段名
     */
    public Criteria andPropertyIsNull(String fieldName) {
        addCriterion(fieldName + " is null");
        return this;
    }

    /**
     * is not null
     *
     * @param fieldName 字段名
     */
    public Criteria andPropertyIsNotNull(String fieldName) {
        addCriterion(fieldName + " is not null");
        return this;
    }

    /**
     * equal to
     *
     * @param fieldName 字段名
     * @param value     检索条件值
     */
    public Criteria andPropertyEqualTo(String fieldName, Object value) {
        addCriterion(fieldName + " = ", value, fieldName);
        return this;
    }

    /**
     * not equal to
     *
     * @param fieldName 字段名
     * @param value     检索条件值
     */
    public Criteria andPropertyNotEqualTo(String fieldName, Object value) {
        addCriterion(fieldName + " <> ", value, fieldName);
        return this;
    }

    /**
     * greater than
     *
     * @param fieldName 字段名
     * @param value     检索条件值
     */
    public Criteria andPropertyGreaterThan(String fieldName, Object value) {
        addCriterion(fieldName + " > ", value, fieldName);
        return this;
    }

    /**
     * greater than or equal to
     *
     * @param fieldName 字段名
     * @param value     检索条件值
     */
    public Criteria andPropertyGreaterThanOrEqualTo(String fieldName, Object value) {
        addCriterion(fieldName + " >= ", value, fieldName);
        return this;
    }

    /**
     * less than
     *
     * @param fieldName 字段名
     * @param value     检索条件值
     */
    public Criteria andPropertyLessThan(String fieldName, Object value) {
        addCriterion(fieldName + " < ", value, fieldName);
        return this;
    }

    /**
     * less than or equal to
     *
     * @param fieldName 字段名
     * @param value     检索条件值
     */
    public Criteria andPropertyLessThanOrEqualTo(String fieldName, Object value) {
        addCriterion(fieldName + " <= ", value, fieldName);
        return this;
    }

    /**
     * in
     *
     * @param fieldName 字段名
     * @param values    检索条件值
     */
    public Criteria andPropertyIn(String fieldName, List<?> values) {
        addCriterion(fieldName + " in ", values, fieldName);
        return this;
    }

    /**
     * not in
     *
     * @param fieldName 字段名
     * @param values    检索条件值
     */
    public Criteria andPropertyNotIn(String fieldName, List<?> values) {
        addCriterion(fieldName + " not in ", values, fieldName);
        return this;
    }

    /**
     * Between
     *
     * @param fieldName 字段名
     * @param value1    检索条件值下限
     * @param value2    检索条件值上限
     */
    public Criteria andPropertyBetween(String fieldName, Object value1, Object value2) {
        addCriterion(fieldName + " between ", value1, value2, fieldName);
        return this;
    }

    /**
     * NotBetween
     *
     * @param fieldName 字段名
     * @param value1    检索条件值下限
     * @param value2    检索条件值上限
     */
    public Criteria andPropertyNotBetween(String fieldName, Object value1, Object value2) {
        addCriterion(fieldName + " not between ", value1, value2, fieldName);
        return this;
    }

    private void addCriterion(String condition) {
        if (condition == null) {
            throw new RuntimeException("Value for condition cannot be null");
        }
        criteria.add(new Criterion(condition));
    }

    private void addCriterion(String condition, Object value, String property) {
        if (value == null) {
            throw new RuntimeException("Value for " + property + " cannot be null");
        }
        criteria.add(new Criterion(condition, value));
    }

    private void addCriterion(String condition, Object value1, Object value2, String property) {
        if (value1 == null || value2 == null) {
            throw new RuntimeException("Between values for " + property + " cannot be null");
        }
        criteria.add(new Criterion(condition, value1, value2));
    }

    public Criteria andLikeTo(String fieldName, Object value) {
        if (value == null || String.valueOf(value).contains("null")) {
            return this;
        }
        addCriterion(fieldName + " like ", value, fieldName);
        return this;
    }

    public Criteria andCustomizeCondition(String customizeCondition) {
        addCriterion(customizeCondition);
        return this;
    }

    /**
     * 单个检索条件
     *
     */
    public static class Criterion {

        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}
