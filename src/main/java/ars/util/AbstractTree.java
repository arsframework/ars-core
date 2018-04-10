package ars.util;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * 树抽象实现
 *
 * @param <T> 树对象类型
 * @author wuyongqiang
 */
public abstract class AbstractTree<T extends AbstractTree<T>> implements Tree<T>, Formable {
    private static final long serialVersionUID = 1L;

    private Boolean leaf; // 是否为叶节点
    private Integer level; // 当前节点层级
    private T parent; // 父节点
    private List<T> children = new ArrayList<T>(0); // 子节点列表

    @Override
    public Boolean getLeaf() {
        if (this.leaf == null) {
            this.leaf = this.children.isEmpty();
        }
        return this.leaf;
    }

    @Override
    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    @Override
    public Integer getLevel() {
        if (this.level == null) {
            this.level = Trees.getLevel(this);
        }
        return this.level;
    }

    @Override
    public void setLevel(Integer level) {
        this.level = level;
    }

    @Override
    public T getParent() {
        return this.parent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> getParents() {
        return (List<T>) Trees.getParents(this);
    }

    @Override
    public void setParent(T parent) {
        this.parent = parent;
    }

    @Override
    public List<T> getChildren() {
        return this.children;
    }

    @Override
    public void setChildren(List<T> children) {
        this.children = children;
    }

    @Override
    public Map<String, Object> format() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("leaf", this.getLeaf());
        values.put("level", this.getLevel());
        values.put("children", this.children);
        return values;
    }

}
