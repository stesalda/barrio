package nz.ac.massey.cs.barrio.vertexclassifier;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.UserData;
import nz.ac.massey.cs.barrio.classifier.Classifier;
import nz.ac.massey.cs.barrio.rules.ReferenceRule;
import nz.ac.massey.cs.barrio.rules.RuleCondition;

public class VertexClassifier implements Classifier{

	public void classify(Vertex v, List<ReferenceRule> rules) 
	{
		List<String> references = new ArrayList<String>();
		if(v.getUserDatum("reference")!=null) references = getReferences(v.getUserDatum("reference").toString());
		System.out.println("[VertexClassifier]: class "+v.getUserDatum("class.packageName")+"."+v.getUserDatum("class.name")+" references = " + references);
		List<String> classifications = new ArrayList<String>();
		
		for(ReferenceRule rule:rules)
		{
			if(isSattisfyingRule(references, rule) && !classifications.contains(rule.getResult())) 
				classifications.add(rule.getResult());
		}
		v.setUserDatum("classification", classifications, UserData.SHARED);	
		System.out.println("[VertexClassifier]: classifications = " + v.getUserDatum("classification").toString());
	}
	
	
	private boolean isSattisfyingRule(List<String> references, ReferenceRule rule) 
	{
		for(RuleCondition condition:rule.getConditions())
		{
			if(!isSattisfyingCondition(references, condition)) return false;
		}
		return true;
	}
	
	
	
	private boolean isSattisfyingCondition(List<String> references, RuleCondition condition) {
		String value = condition.getValue();
		if(value.endsWith("*")) value = value.substring(0, value.lastIndexOf('*'));
		
		boolean contains = false;
		for(String ref:references)
		{
			if(ref.startsWith(value)) contains = true;
		}
		
		if(condition.isNegated()) return !contains;
		return contains;
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
