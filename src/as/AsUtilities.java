package as;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsUtilities {
	public enum ConfidenceLevel {
		lOW (1.65),
		STD (1.96),
		HIGH (2.58);
		double val;
		ConfidenceLevel (double v) {
			val=v;
		}
		public double getValue() {
			return(val);
		}
	}
	public static double get2dSumm(double [][] d) {
		double r =0.0;
		if (d==null)
			return(-1.0);
		for (int k=0; k<d[0].length; k++)
			for (int c=0; c<d[0].length; c++)
				r+=d[k][c];
		return(r);
	}
	public double getMeanValue(double[] dd) {
		double res=0.0;
		double sum=0.0;
		for (double d: dd)
			sum+=d;
		res=sum/dd.length;
		return(res);
	}
	public static double getMeanValue(Vector<Double> v) {
		double res=0.0;
		double sum=0.0;
		for (double d: v)
			sum+=d;
		res=sum/v.size();
		return(res);
	}
	public double getStdVariation(double[] dd) {
		double res=0.0;
		double sum=0.0;
		double mean=getMeanValue(dd);
		for (double d: dd) {
			sum+=(d-mean)*(d-mean);
		}
		sum/=(double) (dd.length-1);
		res=Math.sqrt(sum);
		return(res);
	}
	public static double getStdVariation(Vector<Double> v) {
		double res=0.0;
		double sum=0.0;
		double mean=getMeanValue(v);
		for (double d: v) {
			sum+=(d-mean)*(d-mean);
		}
		sum/=(double) (v.size()-1);
		res=Math.sqrt(sum);
		return(res);
	}
	public static double getOneSpan(Vector<Double> dd, ConfidenceLevel cf) {
		double res=0.0;
		double stdErr=getStdVariation(dd)/Math.sqrt((double)dd.size() );
		double f=cf.getValue();
		res=f*stdErr;
		return(res);
	}
	public static double getOneSpan(Vector<Double> dd) {
		return(getOneSpan(dd, ConfidenceLevel.STD));
	}
	public static double[][]copyArray(double [][] d) {
		int size=d.length;
		double res[][] = new double[size][size];
		for (int r=0; r<size; r++)
			for (int c=0; c<size; c++)
				res[r][c]=d[r][c];
		return(res);
	}
	public double getOneSpan(double [] dd, ConfidenceLevel cf) {
		double res=0.0;
		double stdErr=getStdVariation(dd)/Math.sqrt((double)dd.length );
		double f=cf.getValue();
		res=f*stdErr;
		return(res);
	}
	public static String double2String(double d) {
		return(double2String(d, 5));
	}
	public static String double2String(double d, int prec) {
		NumberFormat numberFormatter= NumberFormat.getNumberInstance(Locale.GERMAN);
		numberFormatter.setMaximumFractionDigits(prec);
		String res=numberFormatter.format(d);
		return(res);
	}
	public static String integer2String(long lg) {
		NumberFormat numberFormatter= NumberFormat.getNumberInstance(Locale.GERMAN);
		String res=numberFormatter.format(lg);
		return(res);		
	}
	public static String getTime() {
		return(getTime(System.currentTimeMillis()));
	}
	public static String getTime(long d) {
		String res="";
		SimpleDateFormat fmt= new SimpleDateFormat("HH:mm:ss");
		Date dt = new Date(d);
		res= fmt.format(dt);
		return(res);
	}
	public static String getTimeMili(long d) {
		String res="";
		SimpleDateFormat fmt= new SimpleDateFormat("HH:mm:ss.SSSS");
		Date dt = new Date(d);
		res= fmt.format(dt);
		return(res);
	}
	public static String getDate() {
		return(getDate(System.currentTimeMillis()));		
	}
	public static void endOfWork() {
		System.err.print(AsUtilities.getTime()+"\tEnde!\n");
	}
	public static String getDate(long d) {
		String res="";
		SimpleDateFormat fmt= new SimpleDateFormat("dd.MM.yyyy");
		Date dt = new Date(d);
		res= fmt.format(dt);
		return(res);
	}
	public static double getPathLength(int [] path, double[][] dist) {
		double result=0.0;
		int from=path[0];
		for (int k=1; k<path.length; k++) {
			int till= path[k];
			result+=dist[from][till];
			from=till;
		}
		return result;
	}
	public double[] readValues() {
		Vector<Double> data= new Vector<Double>();
		String line;
		Pattern pat= Pattern.compile("\\d+[.,]\\d+");
		Scanner sc= new Scanner(System.in);
		while (sc.hasNextLine()) {
			line=sc.nextLine();
			if (line.startsWith("****"))
				break;
			Matcher m= pat.matcher(line);
			if (!m.find())
				continue;
			line=m.group().replaceAll(",", ".");
			data.add(Double.parseDouble(line));
		}
		sc.close();
		double [] result= new double[data.size()];
		int k=0;
		for (Double d: data)
			result[k++]=d.doubleValue();
		return(result);
	}
	static public double[] toArr(Vector<Double> vec) {
		double[] result= new double[vec.size()];
		int k=0;
		for (Double d: vec)
			result[k++]=d;
		return(result);		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AsUtilities asut= new AsUtilities();
		double d[]=  {59 ,54,123,23,2,34,34,41,3,33,33,22 };
		d=asut.readValues();
		double r= asut.getOneSpan(d, ConfidenceLevel.STD);
		double m=asut.getMeanValue(d);
		System.out.print("mean: "+m+" <=="+(m-r)+", "+(m+r)+" ==>\n");
		System.exit(1);
	}

}
