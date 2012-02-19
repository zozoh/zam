package org.nutz.am;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.nutz.lang.Files;

public class AmsTest {

	@Test
	public void test_simple_parse() {
		Am root = Ams.parse(Files.read("org/nutz/am/am.txt"));
		System.out.println(root);

		String s = root.toString();

		Am root2 = Ams.parse(s);

		assertTrue(root.equals(root2));
	}

	private MockNdFactory ndfa;

	@Before
	public void before() {
		ndfa = new MockNdFactory();
	}
}
