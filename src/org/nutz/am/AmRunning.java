package org.nutz.am;

import java.io.IOException;
import java.io.Reader;

/**
 * 提供了自动的运行环境，封装了工作栈（压入，弹出），以及无回退式读取
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @param <T>
 */
public class AmRunning<T extends Nd> {

	/**
	 * 工作栈
	 */
	private Nd[] queue;

	/**
	 * 指向工作栈的顶部下标，初始为 -1
	 */
	private int iHead;

	/**
	 * 读取流
	 */
	private Reader r;

	/**
	 * 记录最后一次读取到的 char
	 */
	private char current;

	public Nd pop() {
		if (iHead < 0)
			return null;
		return queue[iHead--];
	}

	public void push(Nd nd) {
		queue[++iHead] = nd;
	}

	public boolean isEmpty() {
		return null == top();
	}

	public Nd top() {
		if (iHead < 0)
			return null;
		return queue[iHead];
	}

	public char next() {
		try {
			current = (char) r.read();
			return current;
		}
		catch (IOException e) {
			return (char) -1;
		}
	}

	public char current() {
		return current;
	}

	public boolean isEnd() {
		return -1 == current;
	}

	AmRunning(int queueSize) {
		queue = new Nd[queueSize];

	}

	AmRunning() {
		this(100);
	}

}
