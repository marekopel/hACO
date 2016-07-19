package as;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatrixData implements Serializable {
	private static final long serialVersionUID = 1L;

	private int nodes;

	protected double[][] arrData;

	public int getNumberOfNodes() {
		return(nodes);
	}
	public void serializeMe(String fn) throws IOException {
		FileOutputStream fileOut =
				new FileOutputStream(fn); // .ser is the prefered file name extension
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(this);
		out.close();  // closing of both files is necessary
		fileOut.close();
	}

	public static MatrixData deserializeMe(String fn) throws IOException, ClassNotFoundException {
		MatrixData ob;
		//System.out.println((new File(".")).getAbsolutePath());
		FileInputStream fileIn = new FileInputStream(fn);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		ob = (MatrixData) in.readObject();  // casting is a MUST!!!
		in.close();
		fileIn.close();
		return(ob);
	}
	// create using array
	public MatrixData(double[][] dist) {
		nodes=dist[0].length;
		arrData= new double[nodes][nodes];
		for (int k=0; k<nodes; k++)
			for (int c=0; c<nodes; c++)
				arrData[k][c]=dist[k][c];
	}
	public MatrixData(MatrixData old) {
		nodes=old.nodes;
		arrData= new double[nodes][nodes];
		for (int r=0; r<nodes; r++)
			for (int c=0; c<nodes; c++)
				arrData[r][c]=old.arrData[r][c];
	}
	public double[][] getArray() {
		return(arrData);
	}

	// create a random array
	public MatrixData(int size) {
		nodes=size;
		arrData= new double[size][size];
		Random rd= new Random();
		for (int k=0; k<size; k++)
			for (int c=0; c<size; c++)
				arrData[k][c]=rd.nextDouble();

	}
	public double getInfoCard() {
		double res=0.0;
		for (int r=0; r<nodes; r++)
			for (int c=0; c<nodes; c++)
				res+=arrData[r][c];
		return(res);
	}
	static void testMe(String fns) throws Exception {
		MatrixData md= new MatrixData(20);
		md.serializeMe(fns);
		double[][] dOrg= md.getArray();
		MatrixData dn=MatrixData.deserializeMe(fns);
		double[][] dCopy=dn.getArray();
		for (int k=0; k<md.getNumberOfNodes(); k++)
			for (int j=0; j<md.getNumberOfNodes(); j++)
				if (dOrg[k][j]!=dCopy[k][j]) {
					System.err.print("BAD!!!");
					System.exit(3);
				}
		System.err.print("Its OK");
	}
	public void printMe() throws ParseException {
		String line="";
		NumberFormat nf= NumberFormat.getInstance();
		nf.setMaximumFractionDigits(4);
		for (int r=0; r<nodes; r++) {
			line="";
			for (int c=0; c<nodes; c++)
				System.out.print(nf.format(arrData[r][c]));
			System.out.print(line+"\n");
		}
	}
	public synchronized double getAvgPathLength() {
		double res=0.0;
		double [][] d=getArray();
		nodes= d[0].length;
		for (int r=0; r<nodes; r++) {
			for (int c=0; c<nodes; c++) {
				if (c!=r)
					res+=d[r][c];
			}
		}
		res=res/(double)(nodes*nodes - nodes);
		return (res);
	}
	public static double getRouteLength(Vector<Integer> segments, double[][] dist ) {
		int len=segments.size();
		if (len<2)
			return(0.0);
		double result=0.0;
		int from;
		int till= segments.get(0);
		for (int k=1; k<len; k++) {
			from=till;
			till= segments.get(k);
			result+=dist[from][till];
		}
		return(result);
	}
	public double getRouteLength(Vector<Integer> segments) {
		double res=0.0;
		double[][] dist= getArray();
		res=MatrixData.getRouteLength(segments, dist);
		return(res);
	}
	public static void generacje() throws Exception {

		MatrixData md;

		md= new MatrixData(5);
		md.serializeMe("CDN05.ser");
		md= new MatrixData(10);
		md.serializeMe("CDN10A.ser");
		md= new MatrixData(50);
		md.serializeMe("CDN50A.ser");
		md= new MatrixData(50);
		md.serializeMe("CDN50B.ser");

	}
	public void showDistance() throws Exception, IOException {
		String fn="CD50C.ser";
		MatrixData md=deserializeMe(fn);
		String line;
		Pattern pt= Pattern.compile("\\d+");
		System.err.print("Distances for: "+fn+" matrix\n");
		Vector<Integer> path= new Vector<Integer>();
		Scanner sc = new Scanner (System.in);
		while (sc.hasNextLine()) {
			line=sc.nextLine();
			Matcher m= pt.matcher(line);
			path.clear();
			while (m.find()) {
				String s=m.group().trim();
				int seg=Integer.parseInt(s);
				path.add(seg);
			}
			double dist=md.getRouteLength(path);
			System.out.println("distance: "+dist);
		}
		sc.close();
		System.err.print("Ende\n");
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		MatrixData.generacje();
		//MatrixData.oneTest();
		//MatrixData.testMe("xxxx.serx");
		//MatrixData m= new MatrixData(50);
		//m.showDistance();
		//testMe("dane.ser");
		System.exit(0);
	}

}
