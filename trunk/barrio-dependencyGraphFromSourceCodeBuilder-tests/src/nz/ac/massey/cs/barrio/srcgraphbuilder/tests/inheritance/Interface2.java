package nz.ac.massey.cs.barrio.srcgraphbuilder.tests.inheritance;

import java.io.Serializable;

@test.ExpectExtends("java.io.Serializable,java.lang.Comparable")
public interface Interface2 extends Serializable,Comparable {
}
