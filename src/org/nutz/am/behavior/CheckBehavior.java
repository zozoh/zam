package org.nutz.am.behavior;

import org.nutz.am.AmBehavior;
import org.nutz.lang.Lang;

public class CheckBehavior implements AmBehavior {

	private String[] ndNames;

	public CheckBehavior(String[] ndNames) {
		if (null == ndNames || ndNames.length == 0)
			throw Lang.makeThrow("Check Nothing");
		this.ndNames = ndNames;
	}

	public String toString() {
		return String.format("! %s ", Lang.concat(", ", ndNames));
	}

}
