package org.nutz.am;

import java.util.LinkedList;
import java.util.List;

import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;

public abstract class AmNd implements Nd {

	private String name;

	protected LinkedList<Nd> children;

	public AmNd() {
		children = new LinkedList<Nd>();
	}

	@SuppressWarnings("unchecked")
	public <T extends Nd> void each(Each<T> callback) {
		int len = children.size();
		int i = 0;
		for (Nd nd : children)
			try {
				callback.invoke(i++, (T) nd, len);
			}
			catch (ExitLoop e) {
				break;
			}
			catch (ContinueLoop e) {}
			catch (LoopException e) {
				throw Lang.wrapThrow(e);
			}
	}

	/**
	 * @return 取得自己的子节点列表
	 */
	public List<Nd> getChildren() {
		return children;
	}

	/**
	 * @return 自己第一个子节点
	 */
	public Nd getFirstChild() {
		return children.getFirst();
	}

	/**
	 * @return 自己最后一个子节点
	 */
	public Nd getLastChild() {
		return children.getLast();
	}

	/**
	 * @return 子节点数量
	 */
	public int size() {
		return children.size();
	}

	/**
	 * @return 子节点是否为空
	 */
	public boolean isEmpty() {
		return children.isEmpty();
	}

	@Override
	public void add(Nd child) {
		children.add(child);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}
