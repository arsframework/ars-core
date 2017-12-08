package ars.util;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Collection;
import java.util.LinkedHashMap;

import ars.util.Tree;
import ars.util.Beans;

/**
 * 树工具类
 * 
 * @author wuyq
 * 
 */
public final class Trees {
	/**
	 * 获取树最大深度
	 * 
	 * @param tree
	 *            树对象实例
	 * @return 树深度
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <M extends Tree> int getDepth(M tree) {
		if (tree == null) {
			return 0;
		}
		List<M> children = tree.getChildren();
		if (children.isEmpty()) {
			return 1;
		}
		int depth = 0;
		for (int i = 0; i < children.size(); i++) {
			int _depth = getDepth(children.get(i));
			if (_depth > depth) {
				depth = _depth;
			}
		}
		return depth + 1;
	}

	/**
	 * 获取树最大宽度
	 * 
	 * @param tree
	 *            树对象实例
	 * @return 树宽度
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <M extends Tree> int getWidth(M tree) {
		if (tree == null) {
			return 0;
		}
		List<M> children = tree.getChildren();
		if (children.isEmpty()) {
			return 1;
		}
		int width = 0;
		for (int i = 0; i < children.size(); i++) {
			width += getWidth(children.get(i));
		}
		return width;
	}

	/**
	 * 获取树层级（从1开始）
	 * 
	 * @param tree
	 *            树对象实例
	 * @return 树层级
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <M extends Tree> int getLevel(M tree) {
		if (tree == null) {
			return 0;
		}
		int level = 1;
		M parent = (M) tree.getParent();
		while (parent != null) {
			level++;
			parent = (M) parent.getParent();
		}
		return level;
	}

	/**
	 * 判断树对象是否是环形对象
	 * 
	 * @param tree
	 *            树对象实例
	 * 
	 * @return true/false
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <M extends Tree> boolean isLoop(M tree) {
		if (tree == null) {
			return false;
		}
		M parent = (M) tree.getParent();
		while (parent != null) {
			if (tree.equals(parent)) {
				return true;
			}
			parent = (M) parent.getParent();
		}
		return false;
	}

	/**
	 * 获取树对象根节点
	 * 
	 * @param tree
	 *            树对象实例
	 * @return 树对象实例
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <M extends Tree> M getRoot(M tree) {
		if (tree == null) {
			return null;
		}
		M parent = null;
		while ((parent = (M) tree.getParent()) != null) {
			tree = parent;
		}
		return tree;
	}

	/**
	 * 获取树对象所有父节点
	 * 
	 * @param tree
	 *            树对象实例
	 * @return 父节点实例列表
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <M extends Tree> List<M> getParents(M tree) {
		M parent = null;
		if (tree == null || (parent = (M) tree.getParent()) == null) {
			return new ArrayList<M>(0);
		}
		LinkedList<M> parents = new LinkedList<M>();
		while (parent != null) {
			parents.addFirst(parent);
			parent = (M) parent.getParent();
		}
		return parents;
	}

	/**
	 * 获取树对象所有父节点
	 * 
	 * @param trees
	 *            树对象实例数组
	 * @return 父节点实例列表
	 */
	@SuppressWarnings("rawtypes")
	public static <M extends Tree> List<M> getParents(M[] trees) {
		return trees == null || trees.length == 0 ? new ArrayList<M>(0)
				: getParents(Arrays.asList(trees));
	}

	/**
	 * 获取树对象所有父节点
	 * 
	 * @param trees
	 *            树对象实例集合
	 * @return 父节点实例列表
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <M extends Tree> List<M> getParents(Collection<M> trees) {
		if (trees == null || trees.isEmpty()) {
			return new ArrayList<M>(0);
		}
		List<M> parents = new LinkedList<M>();
		for (M tree : trees) {
			M parent = (M) tree.getParent();
			while (parent != null) {
				if (!parents.contains(parent)) {
					parents.add(parent);
				}
				parent = (M) parent.getParent();
			}
		}
		return parents;
	}

	/**
	 * 获取树对象所有叶节点
	 * 
	 * @param tree
	 *            树对象实例
	 * @return 树对象列表
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <M extends Tree> List<M> getLeaves(M tree) {
		if (tree == null) {
			return new ArrayList<M>(0);
		}
		List<M> leaves = new LinkedList<M>();
		List<?> children = tree.getChildren();
		if (children.isEmpty()) {
			leaves.add(tree);
		} else {
			for (int i = 0; i < children.size(); i++) {
				leaves.addAll(getLeaves((M) children.get(i)));
			}
		}
		return leaves;
	}

	/**
	 * 获取树对象所有叶节点
	 * 
	 * @param trees
	 *            树对象数组
	 * @return 树对象列表
	 */
	@SuppressWarnings("rawtypes")
	public static <M extends Tree> List<M> getLeaves(M[] trees) {
		return trees == null || trees.length == 0 ? new ArrayList<M>(0)
				: getLeaves(Arrays.asList(trees));
	}

	/**
	 * 获取树对象所有叶节点
	 * 
	 * @param trees
	 *            树对象集合
	 * @return 树对象列表
	 */
	@SuppressWarnings("rawtypes")
	public static <M extends Tree> List<M> getLeaves(Collection<M> trees) {
		if (trees == null || trees.isEmpty()) {
			return new ArrayList<M>(0);
		}
		List<M> leaves = new LinkedList<M>();
		for (M tree : trees) {
			leaves.addAll(getLeaves(tree));
		}
		return leaves;
	}

	/**
	 * 获取树展开列表
	 * 
	 * @param tree
	 *            树对象实例
	 * @return 树对象列表
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <M extends Tree> List<M> getExpands(M tree) {
		if (tree == null) {
			return new ArrayList<M>(0);
		}
		List<M> expands = new LinkedList<M>();
		expands.add(tree);
		List<?> children = tree.getChildren();
		for (int i = 0; i < children.size(); i++) {
			expands.addAll(getExpands((M) children.get(i)));
		}
		return expands;
	}

	/**
	 * 获取树展开列表
	 * 
	 * @param trees
	 *            树对象数组
	 * @return 树对象列表
	 */
	@SuppressWarnings("rawtypes")
	public static <M extends Tree> List<M> getExpands(M[] trees) {
		return trees == null || trees.length == 0 ? new ArrayList<M>(0)
				: getExpands(Arrays.asList(trees));
	}

	/**
	 * 获取树展开列表
	 * 
	 * @param trees
	 *            树对象集合
	 * @return 树对象列表
	 */
	@SuppressWarnings("rawtypes")
	public static <M extends Tree> List<M> getExpands(Collection<M> trees) {
		if (trees == null || trees.isEmpty()) {
			return new ArrayList<M>(0);
		}
		List<M> expands = new LinkedList<M>();
		for (M tree : trees) {
			expands.addAll(getExpands(tree));
		}
		return expands;
	}

	/**
	 * 合并树
	 * 
	 * @param trees
	 *            树数组
	 * @return 合并后的树列表
	 */
	@SuppressWarnings("rawtypes")
	public static <M extends Tree> List<M> getMerges(M[] trees) {
		return trees == null || trees.length == 0 ? new ArrayList<M>(0)
				: getMerges(Arrays.asList(trees));
	}

	/**
	 * 合并树
	 * 
	 * @param trees
	 *            树集合
	 * @return 合并后的树列表
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <M extends Tree> List<M> getMerges(Collection<M> trees) {
		if (trees == null || trees.isEmpty()) {
			return new ArrayList<M>(0);
		}
		Map<M, List<M>> temp = new LinkedHashMap<M, List<M>>();
		for (M tree : trees) {
			if (!temp.containsKey(tree)) {
				temp.put(tree, new ArrayList<M>());
			}
			M parent = (M) tree.getParent();
			if (parent != null && trees.contains(parent)) {
				List<M> children = temp.get(parent);
				if (children == null) {
					children = new ArrayList<M>();
					temp.put(parent, children);
				}
				children.add(tree);
			}
		}
		List<M> roots = new ArrayList<M>(trees.size());
		for (Entry<M, List<M>> entry : temp.entrySet()) {
			M key = entry.getKey();
			key.setChildren(entry.getValue());
			M parent = (M) key.getParent();
			if (parent == null || !temp.containsKey(parent)) {
				roots.add(key);
			}
		}
		return roots;
	}

	/**
	 * 比较两个树对象数组是否相同
	 * 
	 * @param trees
	 *            树对象数组
	 * @param _trees
	 *            树对象数组
	 * @return true/false
	 */
	@SuppressWarnings("rawtypes")
	public static <M extends Tree> boolean isEqual(M[] trees, M[] _trees) {
		return trees == _trees
				|| (trees != null && _trees != null && trees.length == 0 && _trees.length == 0)
				|| isEqual(Arrays.asList(trees), Arrays.asList(_trees));
	}

	/**
	 * 比较两个树对象数组是否相同
	 * 
	 * @param trees
	 *            树对象数组
	 * @param _trees
	 *            树对象数组
	 * @param comparator
	 *            比较器对象
	 * @return true/false
	 */
	@SuppressWarnings("rawtypes")
	public static <M extends Tree> boolean isEqual(M[] trees, M[] _trees,
			Comparator<M> comparator) {
		return trees == _trees
				|| (trees != null && _trees != null && trees.length == 0 && _trees.length == 0)
				|| isEqual(Arrays.asList(trees), Arrays.asList(_trees),
						comparator);
	}

	/**
	 * 比较两个树对象集合是否相同
	 * 
	 * @param trees
	 *            树对象集合
	 * @param _trees
	 *            树对象集合
	 * @return true/false
	 */
	@SuppressWarnings("rawtypes")
	public static <M extends Tree> boolean isEqual(Collection<M> trees,
			Collection<M> _trees) {
		return isEqual(trees, _trees, new Comparator<M>() {

			@Override
			public int compare(M o1, M o2) {
				return o1.equals(o2) ? 0 : -1;
			}

		});
	}

	/**
	 * 比较两个树对象集合是否相同
	 * 
	 * @param trees
	 *            树对象集合
	 * @param _trees
	 *            树对象集合
	 * @param comparator
	 *            比较器对象
	 * @return true/false
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <M extends Tree> boolean isEqual(Collection<M> trees,
			Collection<M> _trees, Comparator<M> comparator) {
		if (trees == _trees
				|| (trees != null && _trees != null && trees.isEmpty() && _trees
						.isEmpty())) {
			return true;
		}
		for (M tree : trees) {
			M _tree = null;
			for (M m : _trees) {
				if (comparator.compare(tree, m) == 0) {
					_tree = m;
					break;
				}
			}
			boolean found = _tree == null ? false : isEqual(tree.getChildren(),
					_tree.getChildren(), comparator);
			if (!found) {
				return false;
			}
		}
		for (M _tree : _trees) {
			M tree = null;
			for (M m : trees) {
				if (comparator.compare(_tree, m) == 0) {
					tree = m;
					break;
				}
			}
			boolean found = tree == null ? false : isEqual(_tree.getChildren(),
					tree.getChildren(), comparator);
			if (!found) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 串联所有父节点字符串
	 * 
	 * @param tree
	 *            树对象实例
	 * @param sign
	 *            连接标记
	 * @return 当前节点及所有父节点字符串串联
	 */
	@SuppressWarnings("rawtypes")
	public static <M extends Tree> String toString(M tree, CharSequence sign) {
		return toString(tree, sign, 0, false);
	}

	/**
	 * 串联所有父节点字符串
	 * 
	 * @param tree
	 *            树对象实例
	 * @param sign
	 *            连接标记
	 * @param reverse
	 *            是否反转顺序
	 * @return 当前节点及所有父节点字符串串联
	 */
	@SuppressWarnings("rawtypes")
	public static <M extends Tree> String toString(M tree, CharSequence sign,
			boolean reverse) {
		return toString(tree, sign, 0, reverse);
	}

	/**
	 * 串联所有父节点字符串
	 * 
	 * @param tree
	 *            树对象实例
	 * @param sign
	 *            连接标记
	 * @param depth
	 *            连接深度（从1开始，小于1标识不限制深度）
	 * @return 当前节点及所有父节点字符串串联
	 */
	@SuppressWarnings("rawtypes")
	public static <M extends Tree> String toString(M tree, CharSequence sign,
			int depth) {
		return toString(tree, sign, depth, false);
	}

	/**
	 * 串联所有父节点字符串
	 * 
	 * @param tree
	 *            树对象实例
	 * @param sign
	 *            连接标记
	 * @param depth
	 *            连接深度（从1开始，小于1标识不限制深度）
	 * @param reverse
	 *            是否反转顺序
	 * @return 当前节点及所有父节点字符串串联
	 */
	@SuppressWarnings("rawtypes")
	public static <M extends Tree> String toString(M tree, CharSequence sign,
			int depth, boolean reverse) {
		if (tree == null || sign == null) {
			return null;
		}
		int level = 1;
		StringBuilder buffer = new StringBuilder(tree.toString());
		Tree parent = tree.getParent();
		while (parent != null && (depth < 1 || level++ < depth)) {
			if (reverse) {
				buffer.append(sign);
				buffer.append(parent.toString());
			} else {
				buffer.insert(0, sign);
				buffer.insert(0, parent.toString());
			}
			parent = parent.getParent();
		}
		return buffer.toString();
	}

	/**
	 * 获取多个树对象第一个共同父节点
	 * 
	 * @param trees
	 *            树对象数组
	 * @return 树对象
	 */
	@SuppressWarnings("rawtypes")
	public static <M extends Tree> M getAncestor(M[] trees) {
		return getAncestor(trees, new Comparator<M>() {

			@Override
			public int compare(M o1, M o2) {
				return o1.equals(o2) ? 0 : -1;
			}

		});
	}

	/**
	 * 获取多个树对象第一个共同父节点
	 * 
	 * @param trees
	 *            树对象数组
	 * @param comparator
	 *            对象比较器
	 * @return 树对象
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <M extends Tree> M getAncestor(M[] trees,
			Comparator<M> comparator) {
		if (trees == null || trees.length == 0) {
			return null;
		}
		M intersect = trees[0];
		outer: for (int i = 1; i < trees.length; i++) {
			while (intersect != null) {
				M tree = trees[i];
				while (tree != null) {
					if (comparator.compare(intersect, tree) == 0) {
						continue outer;
					}
					tree = (M) tree.getParent();
				}
				intersect = (M) intersect.getParent();
			}
			return null;
		}
		return intersect;
	}

	/**
	 * 获取多个树对象第一个共同父节点
	 * 
	 * @param trees
	 *            树对象集合
	 * @return 树对象
	 */
	@SuppressWarnings("rawtypes")
	public static <M extends Tree> M getAncestor(Collection<M> trees) {
		return getAncestor(trees, new Comparator<M>() {

			@Override
			public int compare(M o1, M o2) {
				return o1.equals(o2) ? 0 : -1;
			}

		});
	}

	/**
	 * 获取多个树对象第一个共同父节点
	 * 
	 * @param trees
	 *            树对象集合
	 * @param comparator
	 *            对象比较器
	 * @return 树对象
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <M extends Tree> M getAncestor(Collection<M> trees,
			Comparator<M> comparator) {
		return trees == null || trees.isEmpty() ? null : getAncestor(
				(M[]) trees.toArray(Beans.getArray(trees.iterator().next()
						.getClass(), 0)), comparator);
	}

}
