package nz.ac.massey.cs.barrio.srcgraphbuilder.tests.slava;


@test.ExpectModifier("public")
public class Class2 {

	@test.ExpectUses("nz.ac.massey.cs.barrio.srcgraphbuilder.tests.slava.Class1")
	@test.ExpectModifier("private")
	private class ClassA{
		
	}
}
