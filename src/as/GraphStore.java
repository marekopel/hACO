package as;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.Vector;

import com.google.gson.Gson;

import runs.AntCommunityServer;



public class GraphStore  extends Starter {
	protected double ileZmieniac= 10.0;
	protected double rozmiarZmian=0.5;
	protected int nodes=50;
	protected MatrixData matrix;
	protected double[][] myArray;
	private Random rd= new Random();
	protected boolean dynamicGraph;
	protected boolean showChanges = false;

	//protected AntGraph samGraph=null;

	public GraphStore(MyProperties pr) {
		try {
			ileZmieniac= pr.getDouble("pool", 10.0);
			rozmiarZmian= pr.getDouble("scope", 0.5);
			nodes=pr.getInteger("nNodes", 10);
			interTime =  pr.getInteger("interval", 7000);
			dynamicGraph=pr.getProperty("dynamicGraph","true").toLowerCase().trim().equals("true"); 
			showChanges=pr.getProperty("showChanges","true").toLowerCase().trim().equals("true"); 
		} 
		catch(Exception ex) {
			AntCommunityServer.theLogger.severe("Error while reading graph properties");
			System.exit(9);
		}
		String serName=pr.getProperty("serialname");
		if (serName==null)
			matrix= new MatrixData(nodes);
		else {
			try {
				matrix= MatrixData.deserializeMe(serName);

				
//				Gson gson = new Gson();
//				System.out.println(gson.toJson(matrix));
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				AntCommunityServer.theLogger.severe("Error while deserializing matrix file "+serName);
				System.exit(3);
			}
			nodes=matrix.getNumberOfNodes();
		}
	}
	public double[][] getDistanceMatrix() {
		return(matrix.getArray());
	}
	public int getNumberOfNodes() {
		return (nodes);
	}
	public double getDistanse(Vector<Integer> path) {
		double res=Double.MAX_VALUE;
		if (path.size()<2)
			return(res);
		int from=path.get(0);
		int till=path.get(1);
		res=myArray[from][till];
		for (int i=1; i<path.size()-1; i++) {
			from=till;
			till=path.get(i+1);
			res+=myArray[from][till];
		}
		return(res);
	}
	protected double makeMore(double doZmiany) {
		int k = (int) Math.floor ((double)nodes*rd.nextDouble());
		int j = (int) Math.floor ((double)nodes*rd.nextDouble());
		double org=myArray[k][j];
		double val=(1.0-org)*rd.nextDouble();
		val=rd.nextDouble()*rozmiarZmian;
		if (val+org>1.0)
			return(doZmiany);
		if (val>doZmiany) 
			val=doZmiany;
		myArray[k][j]=org+val;
		myArray[j][k]=org+val;
		return(doZmiany-val);
	}

	protected double makeLess(double doZmiany) {
		int k = (int) Math.floor ((double)nodes*rd.nextDouble());
		int j = (int) Math.floor ((double)nodes*rd.nextDouble());
		double org=myArray[k][j];
		double val=org*rd.nextDouble();
		val=rd.nextDouble()*rozmiarZmian;
		if (org-val<0.0)
			return doZmiany;
		if (val>doZmiany) 
			val=doZmiany;
		myArray[k][j]= org-val;
		myArray[j][k]= org-val;
		return(doZmiany-val);
	}

	// as20_02
	public  String getVisitCard() {
		double r= matrix.getAvgPathLength();
		return("Graph Change: avg: "+String.format("%.5f", r));
	}
	public synchronized void modifyDistanceMatrix() {
		double doZmiany;
		myArray=matrix.getArray();
		doZmiany=ileZmieniac;
		while (doZmiany>0.0) {
			//System.err.print(doZmiany+"\t");
			doZmiany=makeMore(doZmiany);
		}
		//System.err.print(doZmiany+"\n===\n");
		doZmiany=ileZmieniac;
		while (doZmiany>0.0) {
			//System.err.print(doZmiany+"\t");
			doZmiany=makeLess(doZmiany);
		}
		matrix = new MatrixData(myArray);
		if (showChanges) {
			AntCommunityServer.theLogger.warning("Distance modification "+getVisitCard()+"\n");
		}
		myArray= new double[1][1];		
	}
	public static MyProperties setDefPropData() {
		MyProperties pr = new MyProperties();
		pr.setProperty("pool", "10.0");
		pr.setProperty("scope", "0.5");
		pr.setProperty("nodes", "10");
		pr.setProperty("interval", "10000");
		pr.setProperty("serialname", "CD50A.ser");
		return(pr);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyProperties pr = new MyProperties();
		pr=setDefPropData();
		GraphStore gs=new GraphStore(pr);
		gs.setInterval(100);
		Runnable runnable = gs;
		Thread thread = new Thread(runnable);
		thread.start();		
		gs.setInterval(10);
	}
	public  static GraphStore initGraphStore(MyProperties prop) {
		GraphStore graphStore= new GraphStore(prop);
		int inter=prop.getInteger("interval",1000);
		graphStore.setInterval(inter);
		Runnable runnable = graphStore;
		Thread thread = new Thread(runnable);
		thread.start();	
		return (graphStore);
	}
	protected String timeString() {
		String result;
		SimpleDateFormat sdf= new SimpleDateFormat("HH:mm.ss");
		result= sdf.format((new GregorianCalendar()).getTime());
		return(result);	
	}
	public void doMyWork() {
		if (!dynamicGraph)
			return;
		modifyDistanceMatrix();
		double d=matrix.getAvgPathLength();
			/*
			if (++current>=limit) {
				current=0;
				System.err.print("GraphStore: " +d +" ["+timeString()+"]\n");
			} 
			else
				System.err.print(d+" ");
		}
		*/
		if (infoText!=null) {
			infoText.setText(String.format("%.5f", d));
		}
	}
}
