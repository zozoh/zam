package org.nutz.am;

/**
 * 封装根据 Nd 名称，获取实例的行为
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface NdFactory {

	/**
	 * @param name
	 *            节点名称
	 * @return 实例
	 */
	Nd make(String name);

}
