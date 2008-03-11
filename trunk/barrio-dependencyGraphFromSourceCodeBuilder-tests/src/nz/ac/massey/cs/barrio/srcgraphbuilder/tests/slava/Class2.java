package nz.ac.massey.cs.barrio.srcgraphbuilder.tests.slava;


@test.ExpectPublic()
public class Class2 {

	@test.ExpectUses("nz.ac.massey.cs.barrio.srcgraphbuilder.tests.slava.Class1")
	@test.ExpectPrivate()
	private class ClassA{
		
	}
}
