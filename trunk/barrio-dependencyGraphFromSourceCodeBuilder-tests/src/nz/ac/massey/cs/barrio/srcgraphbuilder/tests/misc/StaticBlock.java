package nz.ac.massey.cs.barrio.srcgraphbuilder.tests.misc;

import java.util.Date;

@test.ExpectUses("java.util.Date,java.lang.System")
public abstract class StaticBlock {
	static {
		System.out.println(new Date());
	}
}
