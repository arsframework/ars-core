package ars.util;

import java.util.List;
import java.io.Serializable;

/**
 * 树接口
 *
 * @param <T> 对象类型
 * @author wuyongqiang
 */
public interface Tree<T extends Tree<T>> extends Serializable {
    /**
     * 获取当前节点是否是叶节点
     *
     * @return true/false
     */
    public Boolean getLeaf();

    /**
     * 设置当前节点是否是叶节点
     *
     * @param leaf true/false
     */
    public void setLeaf(Boolean leaf);

    /**
     * 获取树节点所在树中的层级（从1开始）
     *
     * @return 树节点所在树中的层级
     */
    public Integer getLevel();

    /**
     * 设置树节点所在树中的层级
     *
     * @param level 树节点所在树中的层级
     */
    public void setLevel(Integer level);

    /**
     * 获取当前节点父节点
     *
     * @return 父节点
     */
    public T getParent();

    /**
     * 获取所有父节点
     *
     * @return 父节点列表
     */
    public List<T> getParents();

    /**
     * 设置父节点
     *
     * @param parent 父节点
     */
    public void setParent(T parent);

    /**
     * 获取当前节点子节点
     *
     * @return 子节点集合
     */
    public List<T> getChildren();

    /**
     * 设置当前节点子节点
     *
     * @param children 子节点集合
     */
    public void setChildren(List<T> children);

}
