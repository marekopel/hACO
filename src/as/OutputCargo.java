package as;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import acs.AntGraph;

public class OutputCargo implements Serializable {
	private static final long serialVersionUID = 1L;
	String message;
	public Vector<Integer> bestPath;
	long timing;
	public AntGraph graph; 
	public int bestIter;
	public double bestValue;
	public double[][] feromons;

	
	public OutputCargo(String msg, Vector<Integer> bp, double len, long time) {
		message=msg;
		bestPath=bp;
		timing=time;
		bestValue=len;
	}
	public OutputCargo () {
		message="empty cargo";
		timing=0;
		bestValue=0.0;
		bestPath=new Vector<Integer>();
		bestPath.add(0);
		feromons= new double[1][1];
		feromons[0][0]=0.2;
	}
	public void setFeromons(double dd[][]) {
		int size=dd[0].length;
		feromons= new double[size][size];
		for (int r=0; r<size; r++)
			for (int c=0; c<size; c++)
				feromons[r][c]=dd[r][c];
	}
	public double[][] getFeromons() {
		return(feromons);
	}
	public long getSignature() {
		long res=message.hashCode();
		int size=feromons[0].length;
		for (int r=0; r<size; r++)
			for (int c=0; c<size; c++)
				res+=feromons[r][c];
		for (Integer p: bestPath)
			res+=p;
		return(res);
	}
	public static OutputCargo readCargo (ObjectInputStream ois) throws Exception, IOException {
		OutputCargo outC= (OutputCargo) ois.readObject();
		long signCargo=outC.getSignature();
		long sign= (Long) ois.readObject();
		if (signCargo!=sign) {
			System.err.print("bad sign: "+outC.getMessage()+"\t"+signCargo+"\t"+sign+"\n");
		}
		return(outC);
	}
	public static void sendCargo(OutputCargo out, ObjectOutputStream oos) throws Exception {
		Long sign=out.getSignature();
		oos.writeObject(out);
		oos.writeObject(sign);
	}
	public String getMessage() {
		return(message);
	}
	public Vector<Integer> getBestPath() {
		return(bestPath);
	}
	public double getLength() {
		return(bestValue);
	}
	public long getTime() {
		return(timing);
	}
	public String toString() {
		String res=message+"\t";
		//res+=bestPath+"\t"+timing+"\t["+bestPath.get(0)+"...\t"+bestPath.get(bestPath.size()-1)+"]";
		res+="\t"+timing+"\t["+bestPath.get(0)+"...\t"+bestPath.get(bestPath.size()-1)+"]";
		return(res);
	}
}
