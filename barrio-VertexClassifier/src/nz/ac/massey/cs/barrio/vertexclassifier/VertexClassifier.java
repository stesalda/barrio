package nz.ac.massey.cs.barrio.vertexclassifier;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import edu.uci.ics.jung.graph.Vertex;
import nz.ac.massey.cs.barrio.classifier.Classifier;
import nz.ac.massey.cs.barrio.rules.ReferenceRule;

public class VertexClassifier implements Classifier{

	public void classify(Vertex v, List<ReferenceRule> rules) 
	{
		List<String> references = getReferences(v.getUserDatum("reference").toString());
		List<String> classifications = new ArrayList<String>();
		
		for(ReferenceRule rule:rules)
		{
//			//boolean negate1 = rule.getCondition1().isNegated();
//			//String cond1 = rule.getCondition1().getReference();
//			
//			if(rule.getCondition2()==null)
//			{
//				if(isSattisfying(negate1, cond1, references, classifications))
//					classifications.add(rule.getResult());
//			}
//			else
//			{//
//				boolean negate2 = rule.getCondition2().isNegated();
//				String cond2 = rule.getCondition2().getReference();
//				if(isSattisfying(negate1, cond1, references, classifications) && isSattisfying(negate2, cond2, references, classifications))
//					classifications.add(rule.getResult());
//			}
		}
		
		StringBuffer buffer = new StringBuffer();
		for(String cl:classifications)
		{
			buffer.append(cl);
			buffer.append('|');
		}
	}
	
	
	private boolean isSattisfying(boolean negate1, String cond1, List<String> references, List<String> classifications) 
	{
		if(!negate1 && (containsReference(cond1, references) || containsReference(cond1, classifications))) return true;
		if(negate1 && !(containsReference(cond1, references) || containsReference(cond1, classifications))) return true;
		return false;
	}
	
	
	
	private boolean containsReference(String cond, List<String> references)
	{
		String condition = null;
		if(cond.endsWith("*")) condition = cond.substring(0, cond.lastIndexOf('.'));
		else condition = cond;
		
		for(String ref:references)
		{
			if(ref.startsWith(condition)) return true;
		}
		return false;
	}

	
	private List<String> getReferences(String str)
	{
		StringTokenizer st = new StringTokenizer(str, "|");
		List<String> references = new ArrayList<String>();
		while(st.hasMoreElements())
		{
			Object obj = st.nextElement();
			references.add((String) obj);
		}
		return references;
	}

}
