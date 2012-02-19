package org.nutz.am.behavior;

import org.nutz.am.AmBehavior;
import org.nutz.lang.Lang;

public class PushBehavior implements AmBehavior {

	private String[] ndNames;

	public PushBehavior(String[] ndNames) {
		if (null == ndNames || ndNames.length == 0)
			throw Lang.makeThrow("Push Nothing");
		this.ndNames = ndNames;
	}
	
	public String toString() {
		return String.format("> %s ", Lang.concat(", ", ndNames));
	}

}
