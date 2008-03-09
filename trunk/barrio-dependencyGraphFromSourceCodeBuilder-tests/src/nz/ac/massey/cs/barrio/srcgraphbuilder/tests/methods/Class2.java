package nz.ac.massey.cs.barrio.srcgraphbuilder.tests.methods;

import java.util.*;

@test.ExpectUses("java.util.Date,java.util.Calendar,java.lang.Exception,java.util.List")
public class Class2  {

	public void m(java.util.List l) {
		Date d = null;
		if (d==null) {
			try {
				Calendar c = null;
			}
			catch (Exception x) {}
		}	
	}
}
