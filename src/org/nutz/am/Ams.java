package org.nutz.am;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.am.behavior.CheckBehavior;
import org.nutz.am.behavior.PopBehavior;
import org.nutz.am.behavior.PushBehavior;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.LinkedArray;

/**
 * 一个自动机的帮助函数集。包括根据配置文件创建自动机等
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Ams {

	/**
	 * 从一段配置文件，得到自动机的树形结构
	 * 
	 * @param str
	 *            配置文件
	 * @return 根节点
	 */
	public static Am parse(String str) {
		// 拆分行
		String[] lines = Strings.splitIgnoreBlank(str, "\n");
		int i = 0;

		/*
		 * 制作根自动机
		 */
		AmBean root = null;
		for (; i < lines.length; i++) {
			String line = lines[i];
			// 跳过注释
			if (line.startsWith("#"))
				continue;
			root = makeAmBean(lines[i])[0];
			break;
		}

		if (root.depth != 0)
			throw Lang.makeThrow("root AM should 0 depth :: %s", lines[0]);

		/*
		 * 建立工作栈
		 */
		LinkedArray<AmBean> stack = new LinkedArray<AmBean>(10);
		stack.push(root);

		/*
		 * 逐行分析
		 */
		for (i++; i < lines.length; i++) {
			String line = lines[i];
			// 跳过注释
			if (line.startsWith("#"))
				continue;
			AmBean[] ambs = makeAmBean(line);
			int d = ambs[0].depth;
			// 深度不能小于 1
			if (d <= 0)
				throw Lang.makeThrow("Out of root :: %s", lines[i]);

			// 弹栈找到合适的父
			while ((d - 1) != stack.last().depth && stack.size() > 1)
				stack.popLast();

			// 验证父
			if ((d - 1) != stack.last().depth)
				throw Lang.makeThrow("Fail to find parent for line (%d) :: %s", i + 1, lines[i]);

			// 加入
			stack.last().join(0, ambs);

			// 改变工作栈
			for (AmBean amb : ambs)
				stack.push(amb);
		}

		// System.out.println(root);
		// System.out.println(Strings.dup('=', 80));

		// 返回
		return root.toAm();
	}

	static AmBehavior[] parseBehaviors(String str) {
		if (Strings.isBlank(str))
			return new AmBehavior[0];

		// 拆分
		String[] ss = Strings.splitIgnoreBlank(str, "[ ,]");

		// 准备列表
		List<AmBehavior> list = new ArrayList<AmBehavior>(ss.length);

		// 分析 ...
		int i = 0;
		while (i < ss.length) {
			String s = ss[i];

			// Check
			if ("!".equals(s)) {
				List<String> slist = new ArrayList<String>(ss.length);
				i = _extract_array(i, list, ss, slist);
				list.add(new CheckBehavior(slist.toArray(new String[slist.size()])));
			}
			// Pop
			else if ("<".equals(s)) {
				List<String> slist = new ArrayList<String>(ss.length);
				i = _extract_array(i, list, ss, slist);
				list.add(new PopBehavior(slist.toArray(new String[slist.size()])));
			}
			// Push
			else if (">".equals(s)) {
				List<String> slist = new ArrayList<String>(ss.length);
				i = _extract_array(i, list, ss, slist);
				list.add(new PushBehavior(slist.toArray(new String[slist.size()])));
			}
			// 未知
			else {
				throw Lang.makeThrow("Unknown '%s' in behavior :: %s", s, str);
			}
		}

		// 返回
		return list.toArray(new AmBehavior[list.size()]);
	}

	private static int _extract_array(int i, List<AmBehavior> list, String[] ss, List<String> slist) {
		for (i++; i < ss.length; i++) {
			String next = ss[i];
			if (Strings.isBlank(next))
				continue;
			if ("!".equals(next) || ">".equals(next) || "<".equals(next))
				break;
			slist.add(next);
		}
		return i;
	}

	private static AmBean[] makeAmBean(String line) {
		// 正则匹配
		Matcher m = REG_AM.matcher(line);
		if (!m.find())
			throw Lang.makeThrow("Error conf line :: %s", line);
		// 提取信息
		int depth = Strings.trim(m.group(GI_DEPTH)).length();
		char[] cs = Strings.trim(m.group(GI_CHAR)).toCharArray();
		String amName = Strings.trim(m.group(GI_AM));
		String behaviors = Strings.trim(m.group(GI_BEHAVIOR));
		String nextName = Strings.trim(m.group(GI_NEXT));

		// 根据 cs 制作一组自动机
		AmBean[] ambs = new AmBean[cs.length];
		// 匿名自动机...
		int x = 0;
		AmBean amb;
		for (; x < cs.length - 1; x++) {
			amb = new AmBean();
			amb.depth = depth + x;
			amb.nextName = "?";
			setCs(amb, cs[x]);
			ambs[x] = amb;
		}
		// 结尾
		amb = new AmBean();
		amb.depth = depth + x;
		setCs(amb, cs[x]);
		amb.name = amName;
		amb.nextName = nextName;
		amb.behaviors = behaviors;
		ambs[x] = amb;
		return ambs;
	}

	private static void setCs(AmBean amb, char c) {
		switch (c) {
		case '*':
			amb.cs = null;
			break;
		case '_':
			amb.cs = CS_WHITESPACE;
			break;
		default:
			amb.cs = new char[]{c};
		}
	}

	public static boolean eq(Am[] ams0, Am[] ams1) {
		if (null == ams0 && null == ams1)
			return true;
		if (null == ams0 || null == ams1)
			return false;
		if (ams0.length != ams1.length)
			return false;
		for (int i = 0; i < ams0.length; i++) {
			if (!ams0[i].equals(ams1[i]))
				return false;
		}
		return true;
	}

	public static boolean eq(String s0, String s1) {
		if (null == s0 && null == s1)
			return true;
		if (null == s0 || null == s1)
			return false;
		return s0.equals(s1);
	}

	public static boolean eq(char[] cs0, char[] cs1) {
		if (null == cs0 && null == cs1)
			return true;
		if (null == cs0 || null == cs1)
			return false;
		if (cs0.length != cs1.length)
			return false;
		for (int i = 0; i < cs0.length; i++)
			if (cs0[i] != cs1[i])
				return false;
		return true;
	}

	public static final char[] CS_WHITESPACE = new char[]{' ', '\t'};

	// ---------------------------------------------------------------------正则表达式---
	private static final int GI_DEPTH = 1;
	private static final int GI_CHAR = 2;
	private static final int GI_AM = 3;
	private static final int GI_BEHAVIOR = 4;
	private static final int GI_NEXT = 6;
	private static final Pattern REG_AM = Pattern.compile("^([.]*)"
															+ "([ \t]*[^ \t]+[ \t]+)"
															+ "(Am[^ \t]*[ \t]+)"
															+ "([^|]*)"
															+ "([|])"
															+ "(.*)$");

}
