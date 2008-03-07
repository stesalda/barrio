package nz.ac.massey.cs.barrio.srcgraphbuilder.tests.exceptions;


@test.ExpectUses("java.lang.NullPointerException,java.lang.ArrayIndexOutOfBoundsException")
public class Class3  {

	void m() {
		try {
			this.toString();
		}
		catch (NullPointerException x) {}
		catch (ArrayIndexOutOfBoundsException x){}
		finally{}
	}
}
