package org.nutz.am;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.nutz.lang.Strings;
import org.nutz.lang.util.LinkedArray;

public class AmBean {

	/**
	 * 本节点深度，0 为根节点
	 */
	int depth;

	String name;

	String nextName;

	char[] cs;

	LinkedArray<AmBean> children;

	String behaviors;

	public Am toAm() {
		// 递归提炼出所有 Am 的名称
		Map<String, Am> map = _reg_name(new HashMap<String, Am>());

		// 转换
		Am root = map.get(name);
		_toAm(root, map);

		// 返回
		return root;
	}

	private void _toAm(Am am, Map<String, Am> map) {
		am.depth = depth;
		am.name = Strings.isBlank(name) ? null : name;
		am.cs = cs;
		am.behaviors = Ams.parseBehaviors(behaviors);
		am.next = map.get(nextName);

		if (null != children && !children.isEmpty()) {
			am.children = new Am[children.size()];
			for (int i = 0; i < am.children.length; i++) {
				AmBean amb = children.get(i);
				Am cld = map.get(amb.name);
				if (null == cld)
					cld = new Am();
				amb._toAm(cld, map);
				am.children[i] = cld;
				cld.parent = am;
			}
		}

	}

	private Map<String, Am> _reg_name(Map<String, Am> map) {
		if (!Strings.isBlank(name))
			map.put(name, new Am());
		if (children.size() > 0) {
			Iterator<AmBean> it = children.iterator();
			while (it.hasNext())
				it.next()._reg_name(map);
		}
		return map;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		String mark = null == cs ? "*" : (Ams.eq(cs, Ams.CS_WHITESPACE) ? "_" : new String(cs));
		String head = Strings.alignLeft(Strings.dup(" . ", depth) + "[" + mark + "]", 28, ' ');
		sb.append(String.format("%s : %10s : %50s | %s\n",
								head,
								Strings.sNull(name, "--"),
								Strings.sNull(behaviors, "--"),
								nextName));
		if (null != children && children.size() > 0) {
			Iterator<AmBean> it = children.iterator();
			while (it.hasNext())
				sb.append(it.next().toString());
		}

		return sb.toString();
	}

	/**
	 * 将一组自动机（一个 parents 链）组合到当前的节点中
	 * <p>
	 * 试图寻找，如果找到了，就递归加入，否则就创建一枝
	 * 
	 * @param ambs
	 *            自动机配置行
	 */
	void join(int off, AmBean[] ambs) {
		/*
		 * 木有 children，将给定的链表变树的一枝，插入一个 child
		 */
		if (null == children) {
			children = new LinkedArray<AmBean>(20);
			addToChildren(off, ambs);
		}
		// 试图在 children 中查找
		else {
			Iterator<AmBean> it = children.iterator();
			AmBean amb = ambs[off];
			while (it.hasNext()) {
				AmBean myAmb = it.next();
				/*
				 * 找到相等，递归
				 */
				if (Ams.eq(myAmb.cs, amb.cs)) {
					myAmb.join(off + 1, ambs);
					return;
				}
			}
			/*
			 * 没有找到，建立一枝
			 */
			addToChildren(off, ambs);
		}
	}

	private void addToChildren(int off, AmBean[] ambs) {
		LinkedArray<AmBean> clds = children;
		for (int i = off; i < ambs.length; i++) {
			AmBean amb = ambs[i];
			clds.push(amb);
			amb.children = new LinkedArray<AmBean>(20);
			clds = amb.children;
		}
	}
}
