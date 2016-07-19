package as;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyProperties extends Properties {
	private static final long serialVersionUID = 1L;
	
	protected  String getFirstToken(String str) {
		String result=null;
		Pattern pattern = Pattern.compile("\\S+");
		Matcher matcher = pattern.matcher(str);
		if (!matcher.find())
			return("XXX");
		result=matcher.group();
		return(result.toLowerCase().trim());
	}
	public String getWord(String name, String def) {
		String s= getProperty(name).trim();
		if (s==null)
			return(def);
		s=getFirstToken(s);
		return(s);
	}
	public boolean getBoolean(String name, boolean def) {
		String s;
		try {
			s= getProperty(name).trim();
		if (s==null)
			return(def);
		} catch (Exception ex) {
			return(def);
		}
		s=getFirstToken(s);
		return (s.equals("yes")|| s.equals("true") || s.equals("1") ||
				s.equals("tak") || s.equals("ja"));
	}
	public int getInteger(String name, int def) {
		try {
			String s= getProperty(name).trim();
			if (s==null)
				return(def);
			s=getFirstToken(s);
			if (s==null)
				return(def);
			def=Integer.parseInt(s);
		} catch (Exception ex) {
			return (def);
		}
		return(def);
	}
	public String getBaseInfo() {
		String res = "Ants\t"+getProperty("nAnts");
		res+="\tIter\t"+getProperty("nIterations");
		res+="\tSerialName\t"+getProperty("serialname");
		res+="\tRuns\t"+getProperty("nRuns");
		return(res);
	}
	public double getDouble(String name, double def) {
		String s= getProperty(name).trim();
		if (s==null)
			return(def);
		try {
			s=getFirstToken(s);
			if (s==null)
				return(def);
			s=s.replaceAll(",", ".");
			def=Double.parseDouble(s);
		} catch (Exception ex) {
			return (def);
		}
		return(def);
	}
	public double getComputationLoad() {
		double cls=getInteger("nAnts",1)*getInteger("nIterations",1)
				*getInteger("nRuns",1)*getInteger("nNodes",1);
		cls= cls/(50.0*50.0*50.0);
		return(cls);
	}
	public double getPowerMerics(long startTime) {
		long duration= System.currentTimeMillis()-startTime;
		double power=(getComputationLoad()*60000.0)/(double)duration;
		return(power);
	}
	public String getPowerPathInfo(long startTime, Vector<Double> pathLegths) {
		NumberFormat numberFormatter= NumberFormat.getNumberInstance(Locale.GERMAN);
		numberFormatter.setMaximumFractionDigits(5);
		long duration= System.currentTimeMillis()-startTime;
		int cls=getInteger("nAnts",1)*getInteger("nIterations",1)
				*getInteger("nRuns",1)*getInteger("nNodes",1);
		cls= cls/(50*50*50);
		double power=(double)(cls*60000)/(double)duration; 
		String s=
		("End of Local Run run:\tmilis:\t"+duration+"\tCLS:\t"+cls+"\tpower: "
				+numberFormatter.format(power)+"\n");
		double avgPath=AsUtilities.getMeanValue(pathLegths);
		double span=AsUtilities.getOneSpan(pathLegths);
		s+=("avg path: "+numberFormatter.format(avgPath)+"\t<="+
				numberFormatter.format(avgPath-span)+"\t"+
				numberFormatter.format(avgPath+span)+"=>\n");
		return(s);
	}
}
