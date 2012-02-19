package org.nutz.am;

/**
 * 封装了一个自动机能产生的节点。 自动机需要这个接口提供的少量方法来装配这个节点
 * <p>
 * 在正常使用的时候，你最好继承 AbstrctNd，它提供了更丰富的方法
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Nd {

	/**
	 * 实现类将根据这个字符串来初始化节点
	 * 
	 * @param str
	 *            节点内容
	 */
	void valueOf(String str);

	/**
	 * 增加自己的子节点
	 * 
	 * @param child
	 *            子节点
	 */
	void add(Nd child);

	/**
	 * 设置节点的名称
	 * 
	 * @param name
	 *            节点的名称
	 */
	void setName(String name);

	/**
	 * @return 节点名称
	 */
	String getName();

}
