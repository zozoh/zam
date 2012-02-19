package org.nutz.am.behavior;

import org.nutz.am.AmBehavior;
import org.nutz.lang.Lang;

public class PopBehavior implements AmBehavior {

	private String[] ndNames;

	public PopBehavior(String[] ndNames) {
		if (null == ndNames)
			this.ndNames = new String[0];
		else
			this.ndNames = ndNames;
	}

	public String toString() {
		return String.format("< %s ", Lang.concat(", ", ndNames));
	}

}
