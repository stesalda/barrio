package nz.ac.massey.cs.barrio.preferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

import nz.ac.massey.cs.barrio.rules.ReferenceRule;

public class RuleStorage {
	
	private String filename = "rules.xml";
	
	public void storeRules(List<?> rules)
	{
		if(rules==null) return;
		try 
		{
			File file = new File(filename);
			PrintStream out = new PrintStream(new FileOutputStream(file));
			out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			out.println("<rule-set>");
			for(Object obj:rules)
			{
				out.println("<rule>");
				if(obj instanceof ReferenceRule)
				{
					ReferenceRule rule = (ReferenceRule) obj;
					out.print("<if-references not=\"");
					out.print(rule.isNegated());
					out.println("\">");
				}
				out.println("</rule>");
			}
			out.println("</rule-set>");
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
