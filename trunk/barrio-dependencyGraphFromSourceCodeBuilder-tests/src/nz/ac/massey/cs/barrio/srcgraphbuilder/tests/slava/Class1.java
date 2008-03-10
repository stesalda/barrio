package nz.ac.massey.cs.barrio.srcgraphbuilder.tests.slava;

@test.ExpectUses("nz.ac.massey.cs.barrio.srcgraphbuilder.tests.slava.Class2, nz.ac.massey.cs.barrio.srcgraphbuilder.tests.slava.Class3")
@test.ExpectPublic()
public class Class1 {
	
	@test.ExpectUses("nz.ac.massey.cs.barrio.srcgraphbuilder.tests.slava.Class1")
	@test.ExpectPrivate()
	private class Class2{
		
	}
	
	@test.ExpectUses("nz.ac.massey.cs.barrio.srcgraphbuilder.tests.slava.Class1")
	@test.ExpectPublic()
	public class Class3{
		
	}

}
