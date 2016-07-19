package experiments;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import as.*;

public class GainStudies {

	protected double[] gains;
	protected Scanner sc;
	protected PrintWriter pw;
	protected int iterNo;
	protected int repNo;
	protected int antNo;
	protected double theFirst;
	protected double normComplex;
	
	// 07.04.2016	14:47:04Ans	50   	Iter	10	SerialName	CD50C.ser
	protected int getIntValue(String line, String name) throws Exception {
		Pattern pat= Pattern.compile(name+"\\s+(\\d+)");
		Matcher m = pat.matcher(line);
		if (m.find()) {
			String number=m.group(1);
			int res= Integer.parseInt(number);
			return(res);
		}
		throw new Exception();
	}
	//0	1	2,72747 
	//0	==>	2,36534	7 
	protected void procLine(String line) {
		if (line.contains("==>"))
			return;
		Pattern pat= Pattern.compile("(\\d+)\\D+(\\d+)\\D+(\\d+,\\d+)");
		Matcher m = pat.matcher(line);
		if (!m.find())
			return;
		int iterNo=Integer.parseInt(m.group(2));
		double d=Double.parseDouble(m.group(3).replaceAll(",","."));
		gains[iterNo]+=d;
	}
	protected void procLineGain(String line) {
		if (line.contains("==>"))
			return;
		Pattern pat= Pattern.compile("(\\d+)\\D+(\\d+)\\D+(\\d+,\\d+)");
		Matcher m = pat.matcher(line);
		if (!m.find())
			return;
		int iterNo=Integer.parseInt(m.group(2));
		double d=Double.parseDouble(m.group(3).replaceAll(",","."));
		if (iterNo==1) {
			theFirst=d;
			return;
		}
		gains[iterNo]+=theFirst-d;
	}
	protected void printResultsGain() throws IOException {
		
		for (int k=2; k<gains.length; k++) {
			pw.print((int) (k*normComplex)+"\t"+AsUtilities.double2String(gains[k]/(double)repNo)+
					"\t"+ k+
					"\n");
		}
		pw.close();
	}
	protected void printResults() throws IOException {
		
		for (int k=1; k<gains.length; k++) {
			for (int n=(int) (k*normComplex); n<(k+1)*normComplex; n++)
			pw.print(AsUtilities.double2String(gains[k]/(double)repNo)+"\t"+ k+"\n")		;
		}
		pw.close();
	}
	public void procData(String inFileName) throws Exception {
		sc= new Scanner(new File(inFileName));
		String line=sc.nextLine();
		iterNo=getIntValue(line, "Iter");
		gains= new double[iterNo+2];
		repNo=getIntValue(line, "Runs");
		antNo=getIntValue(line, "Ants");
		normComplex=antNo/50;
		pw.print(line+"\n");
		while (sc.hasNextLine()) {
			line=sc.nextLine();
			procLine(line);
		}
		sc.close();
	}
	public static void main(String[] args) throws Exception {
		String fn="OneColonyLog.txt";
		if (args.length>0)
			fn=args[0];
		GainStudies gs= new GainStudies();
		gs.pw = new PrintWriter(fn+".results");
		gs.procData(fn);
		gs.printResults();
		AsUtilities.endOfWork();
		System.exit(1);
	}
}
