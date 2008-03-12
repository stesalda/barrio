package nz.ac.massey.cs.barrio.srcgraphbuilder.tests.innerclasses;

import java.util.Calendar;
import java.util.Date;

@test.ExpectUses("nz.ac.massey.cs.barrio.srcgraphbuilder.tests.innerclasses.Class1.Class11,java.util.Calendar,java.lang.String")
public class Class1 {
	private Calendar calendar = null;
	@test.ExpectUses("nz.ac.massey.cs.barrio.srcgraphbuilder.tests.innerclasses.Class1,java.util.Date")
	private class Class11 {
		private Date date = null;
	}
	public String doSomething() {return "";}

}
