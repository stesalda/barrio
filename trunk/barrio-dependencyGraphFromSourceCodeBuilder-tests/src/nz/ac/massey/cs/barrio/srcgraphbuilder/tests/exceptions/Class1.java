package nz.ac.massey.cs.barrio.srcgraphbuilder.tests.exceptions;

import java.io.IOException;

@test.ExpectUses("java.io.IOException,java.sql.SQLException")
public interface Class1  {

	void m() throws IOException, java.sql.SQLException;
}
