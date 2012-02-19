package org.nutz.am;

import org.nutz.lang.Lang;

import static org.nutz.lang.Strings.*;

/**
 * 一个自动机
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Am {

	/**
	 * 本节点深度，0 为根节点
	 */
	int depth;

	/**
	 * 自动机名称，null 表示匿名自动机，它唯一的行为，就是要查找下一个自动机
	 */
	String name;

	/**
	 * 本节点如果有自动机，且执行完成，那么下一个自动机是什么
	 * <p>
	 * 如果为 null，则继续读输入流，根据 bins 来决定 <br>
	 * 如果 bins 决定不了，则抛错
	 */
	Am next;

	/**
	 * 本节可以由于这些字符而建立，null 表示可以接受任何字符
	 */
	char[] cs;

	/**
	 * 记录自己的父自动机节点
	 */
	Am parent;

	/**
	 * 本节点的下属节点，如果本节点 am 为null，又没有下属节点，那么解析的工作就会停止，进而抛错
	 * <p>
	 * 下属节点中，0 这个位置为 ANY 的默认位置，如果没有 ANY，则 bins[0] 为 null
	 */
	Am[] children;

	/**
	 * null 或者 空 表示没有任何操作
	 * <p>
	 * 操作类型为:
	 * <ul>
	 * <li>压入工作栈 : 需要子类，提供压入的 Nd 实例
	 * <li>弹一次工作栈
	 * <li>验证工作栈是否符合预期
	 * </ul>
	 */
	AmBehavior[] behaviors;

	public boolean equals(Object obj) {
		if (null == obj)
			return false;
		if (obj instanceof Am) {
			Am am = (Am) obj;
			if (depth != am.depth)
				return false;
			if (!Ams.eq(cs, am.cs))
				return false;
			if (!Ams.eq(name, am.name))
				return false;
			if (!Ams.eq(getMyNextName(), am.getMyNextName()))
				return false;
			if (!Ams.eq(children, am.children))
				return false;

			return true;
		}
		return false;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		/*
		 * 打印自身
		 */
		if (null != name) {
			int myDepth = getMyDepth();
			String mark = dup("  ", myDepth) + getMark();
			String head = dup(".", myDepth) + mark;
			String myName = name;
			if (myDepth > 0) {
				head = alignLeft(head, 15, ' ');
				myName = alignLeft(name, 20, ' ');
			}

			sb.append(head);
			sb.append(" ");
			sb.append(myName);
			sb.append(" ");
			sb.append(null == behaviors ? "" : Lang.concat(behaviors));
			sb.append(" | ");
			sb.append(null == next ? "?" : next.name);
			sb.append("\n");
		}
		/*
		 * 打印 children
		 */
		if (null != children && children.length > 0) {
			for (Am cld : children)
				sb.append(cld.toString());
		}

		return sb.toString();
	}

	private String getMyNextName() {
		return null == next ? "?" : next.name;
	}

	private int getMyDepth() {
		if (null != parent && null == parent.name)
			return parent.getMyDepth();
		return depth;
	}

	private String getMark() {
		String mk = null == cs ? "*" : (Ams.eq(cs, Ams.CS_WHITESPACE) ? "_" : new String(cs));
		if (null != parent && null == parent.name)
			return parent.getMark() + mk;
		return mk;
	}
}
