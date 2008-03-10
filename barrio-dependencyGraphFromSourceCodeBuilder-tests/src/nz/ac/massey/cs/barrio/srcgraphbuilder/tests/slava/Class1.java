package nz.ac.massey.cs.barrio.srcgraphbuilder.tests.slava;

@test.ExpectUses("Class2, Class3")
public class Class1 {
	
	@test.ExpectUses("Class1")
	@test.ExpectPrivate("Class2")
	private class Class2{
		
	}
	
	@test.ExpectUses("Class1")
	@test.ExpectPublic("Class3")
	public class Class3{
		
	}

}
