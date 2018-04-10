package ars.util;

import java.util.Map;
import java.util.List;
import java.util.Map.Entry;
import java.util.LinkedList;
import java.util.Collection;

/**
 * 条件操作工具类
 *
 * @author wuyongqiang
 */
public final class Conditions {
    private Conditions() {

    }

    /**
     * 条件接口
     *
     * @author wuyongqiang
     */
    public static interface Condition {

    }

    /**
     * 条件包装器抽象实现
     *
     * @author wuyq
     */
    public static abstract class AbstractConditionWrapper implements Condition {
        private final List<Condition> conditions = new LinkedList<Condition>(); // 条件集合

        public AbstractConditionWrapper(Condition... conditions) {
            if (conditions == null || conditions.length == 0) {
                throw new IllegalArgumentException("Conditions must not be empty");
            }
            for (Condition condition : conditions) {
                this.conditions.add(condition);
            }
        }

        public AbstractConditionWrapper(Collection<Condition> conditions) {
            if (conditions == null || conditions.isEmpty()) {
                throw new IllegalArgumentException("Conditions must not be empty");
            }
            this.conditions.addAll(conditions);
        }

        public AbstractConditionWrapper(Map<String, Object> conditions) {
            if (conditions == null || conditions.isEmpty()) {
                throw new IllegalArgumentException("Conditions must not be empty");
            }
            for (Entry<String, Object> entry : conditions.entrySet()) {
                this.conditions.add(new Match(entry.getKey(), entry.getValue()));
            }
        }

        public List<Condition> getConditions() {
            return conditions;
        }

    }

    /**
     * 或逻辑实现
     *
     * @author wuyongqiang
     */
    public static class Or extends AbstractConditionWrapper {

        public Or(Condition... conditions) {
            super(conditions);
        }

        public Or(Collection<Condition> conditions) {
            super(conditions);
        }

        public Or(Map<String, Object> conditions) {
            super(conditions);
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            for (Condition condition : this.getConditions()) {
                if (buffer.length() > 0) {
                    buffer.append(" or ");
                }
                if (!(condition instanceof Match)) {
                    buffer.append('(');
                }
                buffer.append(condition);
                if (!(condition instanceof Match)) {
                    buffer.append(')');
                }
            }
            return buffer.toString();
        }

    }

    /**
     * 与逻辑实现
     *
     * @author wuyongqiang
     */
    public static class And extends AbstractConditionWrapper {

        public And(Condition... conditions) {
            super(conditions);
        }

        public And(Collection<Condition> conditions) {
            super(conditions);
        }

        public And(Map<String, Object> conditions) {
            super(conditions);
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            for (Condition condition : this.getConditions()) {
                if (buffer.length() > 0) {
                    buffer.append(" and ");
                }
                if (!(condition instanceof Match)) {
                    buffer.append('(');
                }
                buffer.append(condition);
                if (!(condition instanceof Match)) {
                    buffer.append(')');
                }
            }
            return buffer.toString();
        }

    }

    /**
     * 条件匹配逻辑实现
     *
     * @author wuyongqiang
     */
    public static class Match implements Condition {
        private String key;
        private Object value;

        public Match(String key, Object value) {
            if (key == null) {
                throw new IllegalArgumentException("Key must not be null");
            }
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder(this.key).append('=');
            return this.value == null ? buffer.toString() : buffer.append(this.value).toString();
        }

    }

    /**
     * 条件表达式逻辑对象转换
     *
     * @param expression 条件表达式
     * @return 条件逻辑对象
     */
    public static Condition parse(String expression) {
        if (Strings.isBlank(expression)) {
            return null;
        }
        boolean continued = false;
        int offset = 0, start = 0, end = 0;
        List<String> setions = new LinkedList<String>();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') {
                if (start == end) {
                    offset = i;
                }
                start++;
            } else if (c == ')') {
                if (start == ++end) {
                    setions.add(expression.substring(offset, i + 1));
                    start = 0;
                    end = 0;
                    offset = i + 1;
                    continued = true;
                }
            } else if (start == end) {
                int index = 0;
                String handle = null;
                if ((i > 3 && (handle = expression.substring(index = i - 3, i + 1)).equalsIgnoreCase(" or "))
                    || (i > 4 && (handle = expression.substring(index = i - 4, i + 1)).equalsIgnoreCase(" and "))) {
                    if (!continued) {
                        setions.add(expression.substring(offset, index));
                    }
                    offset = i + 1;
                    continued = false;
                    setions.add(handle.trim().toLowerCase());
                }
            }
        }
        if (start != end) {
            throw new IllegalArgumentException("Illegal expression:" + expression);
        }
        if (offset < expression.length()) {
            setions.add(expression.substring(offset));
        }
        Condition condition = null;
        for (int i = 0; i < setions.size(); i += 2) {
            Condition _condition = null;
            String setion = setions.get(i).trim();
            if (setion.isEmpty()) {
                continue;
            }
            if (setion.charAt(0) == '(' && setion.charAt(setion.length() - 1) == ')') {
                setion = setion.substring(1, setion.length() - 1).trim();
                if (setion.isEmpty()) {
                    continue;
                }
                _condition = parse(setion);
            } else {
                int split = setion.indexOf("=");
                String key = split < 0 ? setion.trim() : setion.substring(0, split).trim();
                if (Strings.isBlank(key)) {
                    continue;
                }
                String value = split < 0 ? null : setion.substring(split + 1).trim();
                _condition = new Match(key, Strings.isList(value) ? Strings.toList(value)
                    : Strings.isBlank(value) ? null : value);
            }
            if (condition == null) {
                condition = _condition;
            } else if (setions.get(i - 1).equals("or")) {
                if (condition instanceof Or) {
                    ((Or) condition).getConditions().add(_condition);
                } else {
                    condition = new Or(condition, _condition);
                }
            } else {
                if (condition instanceof And) {
                    ((And) condition).getConditions().add(_condition);
                } else {
                    condition = new And(condition, _condition);
                }
            }
        }
        return condition;
    }

}
