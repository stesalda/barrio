package nz.ac.massey.cs.barrio.srcgraphbuilder.tests.fields;

import java.util.*;

@test.ExpectUses("java.util.Date,nz.ac.massey.cs.barrio.srcgraphbuilder.tests.fields.Class2,java.lang.String")
public class Class1  {
	static Date d;
	public void m() {
		Class2 tmp = null;
		if (tmp==null) {
			String s = null;
		}
	}

}
