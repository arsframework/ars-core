package ars.util;

import java.util.Map;

/**
 * 对象节点树实现
 *
 * @param <N> 树节点对象类型
 * @author wuyongqiang
 */
public class ObjectTree<N> extends AbstractTree<ObjectTree<N>> {
    private static final long serialVersionUID = 1L;

    private N node; // 当前节点对象

    public ObjectTree(N node) {
        if (node == null) {
            throw new IllegalArgumentException("Node must not be null");
        }
        this.node = node;
    }

    public N getNode() {
        return node;
    }

    @Override
    public Map<String, Object> format() {
        Map<String, Object> values = super.format();
        values.put("node", this.node);
        return values;
    }

    @Override
    public int hashCode() {
        return 31 + this.node.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj
            || (obj != null && obj instanceof ObjectTree && this.node.equals(((ObjectTree<?>) obj).getNode()));
    }

    @Override
    public String toString() {
        return this.node.toString();
    }

}
