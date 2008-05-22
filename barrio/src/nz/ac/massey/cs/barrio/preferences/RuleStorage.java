package nz.ac.massey.cs.barrio.preferences;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import nz.ac.massey.cs.barrio.rules.ReferenceRule;

public class RuleStorage {
	
	private String filename;
	
	public RuleStorage(String filename) {
		if(filename!=null && filename.length()>0) this.filename = filename;
		else filename = "rules.xml";
	}

	public void store(List<ReferenceRule> rules)
	{
		if(rules==null) return;
		try {
			XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename)));
			for(ReferenceRule rule:rules)
			{
				encoder.writeObject(rule);
			}
			encoder.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		}
		
	}

	public List<ReferenceRule> load() {
		List<ReferenceRule> result = new ArrayList<ReferenceRule>();
		XMLDecoder decoder = null;
		boolean isNext = true;
		try {
			decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filename)));
			
			while(isNext)
			{
				Object rule = decoder.readObject();
				if(rule instanceof ReferenceRule) result.add((ReferenceRule)rule);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e){
			isNext = false;
		}finally{
			decoder.close();
		}
		return result;
	}

}
