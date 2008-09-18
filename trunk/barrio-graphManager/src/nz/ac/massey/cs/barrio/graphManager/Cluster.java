package nz.ac.massey.cs.barrio.graphManager;

import java.util.List;

public class Cluster {
	
	private String name;
	private List<Clazz> classes;

	public List<Clazz> getClasses() {
		return classes;
	}

	public void setClasses(List<Clazz> classes) {
		this.classes = classes;
	}

	public Clazz getClazz(String name)
	{
		for(Clazz c: classes)
		{
			String clazzName = c.getName();
			if(clazzName.equals(name)) return c;
		}
		return null;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
