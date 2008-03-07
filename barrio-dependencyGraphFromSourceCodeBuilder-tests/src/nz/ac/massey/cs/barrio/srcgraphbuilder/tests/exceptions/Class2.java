package nz.ac.massey.cs.barrio.srcgraphbuilder.tests.exceptions;


@test.ExpectUses("java.lang.UnsupportedOperationException")
public class Class2  {

	void m() {
		throw new UnsupportedOperationException();
	}
}
