package nz.ac.massey.cs.barrio.preferences;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import nz.ac.massey.cs.barrio.rules.ReferenceRule;

public class RuleStorage {
	
	private String filename;
	
	public RuleStorage(String filename) {
		if(filename!=null && filename.length()>0) this.filename = filename;
		else this.filename = "rules.xml";
	}

	public void store(List<ReferenceRule> rules)
	{
		if(rules==null) return;
		try {
			File file = new File(filename);
			System.out.println("[RuleStorage]: created file = "+file.createNewFile());
			System.out.println("[RuleStorage]: save to: "+file.getAbsolutePath());
			XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)));
			for(ReferenceRule rule:rules)
			{
				encoder.writeObject(rule);
			}
			encoder.close();
			System.out.println("[RuleStorage]: saved to: "+filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public List<ReferenceRule> load() {
		List<ReferenceRule> result = new ArrayList<ReferenceRule>();
		XMLDecoder decoder = null;
		boolean isNext = true;
		try {
			File file = new File(filename);
			if(file.createNewFile())
			{
				OutputStream stream = new FileOutputStream(file);
				stream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes("UTF-8"));
				stream.write("<java version=\"1.6.0_06\" class=\"java.beans.XMLDecoder\">".getBytes("UTF-8"));
				stream.write("</java>".getBytes("UTF-8"));
			}
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			decoder.close();
		}
		return result;
	}

}
