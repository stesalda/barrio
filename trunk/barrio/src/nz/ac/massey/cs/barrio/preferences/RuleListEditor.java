package nz.ac.massey.cs.barrio.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import nz.ac.massey.cs.barrio.rules.ReferenceRule;

import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;

public class RuleListEditor extends ListEditor{
	
	private List<ReferenceRule> rules;
	
	public RuleListEditor(String name, String label, Composite parent)
	{
		super(name, label, parent);
		rules = new ArrayList<ReferenceRule>();
	}

	@Override
	protected String createList(String[] items) {
		StringBuffer buffer = new StringBuffer();
		for(String s:items)
		{
			buffer.append(s);
			buffer.append('|');
		}
		return buffer.toString();
	}

	@Override
	protected String getNewInputObject() {
		AddRuleDialog dialog = new AddRuleDialog(getShell(), null);
		return dialog.open().toString();
	}

	@Override
	protected String[] parseString(String stringList) {
		StringTokenizer st = new StringTokenizer(stringList, "|");
		List<Object> list = new ArrayList<Object>();
		while(st.hasMoreElements())
		{
			Object obj = st.nextElement();
			list.add(obj);
//			ReferenceRule rule = new ReferenceRule((String) obj);
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	public List<ReferenceRule> getRules() {
		return rules;
	}

}
