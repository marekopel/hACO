package as;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Vector;

import tsp.Ant4TSP;
import tsp.AntColony4TSP;

public class AntCommunityCargo implements Serializable {
	private static final long serialVersionUID = 1L;

	private static int id=1;

	protected AntServerCommand command;
	public BasicRunParameters basicParams;
	protected Integer sleepTime;
	protected Vector<String> genealogy;
	protected MatrixData distances;
	protected MatrixData feromons;
	//protected Vector<Vector<Integer>> bestRoutes;
	protected String msg="";
	protected Vector<String> forLog;
	protected Double bsf;
	protected Integer iterBsf;
	protected Long nanoDuration= new Long(0);  // the duration of the last operation
	protected Vector<Integer> theBestPath;

	protected double fingerPrint=0.0;

	protected int[] oldPositions=null;

	public void setOldPositions(int[] oldies) {
		if (oldPositions==null)
			oldPositions= new int[oldies.length];
		for (int k=0; k<oldies.length; k++)
			oldPositions[k]=oldies[k];
	}
	public String getPositionsInfo() {
		String result="{no postions}";
		if (oldPositions==null)
			return(result);
		int seq=0;
		result="{ ";
		for (int p: oldPositions) {
			if (seq>4)
				return(result);
			result+=p+", ";
			seq++;
		}
		return(result+"} ");
	}
	public int[] getOldPositions() {
		return(oldPositions);
	}
	// copy constructor
	public AntCommunityCargo (AntCommunityCargo src) {
		command= src.command;
		basicParams= new BasicRunParameters(src.basicParams);
		sleepTime=src.sleepTime;
		genealogy= new Vector<String>();
		for (String s: src.genealogy)
			genealogy.add(s);
		distances= new MatrixData (src.distances);
		feromons= new MatrixData (src.feromons);
		/*
		bestRoutes= new Vector<Vector<Integer>>();
		for (Vector<Integer> vi: src.bestRoutes) {
			Vector<Integer> route= new Vector<Integer>();
			for (Integer r: vi) 
				route.add(r);
			bestRoutes.add(route);
		} */
		msg= new String(src.msg);
		forLog= new Vector<String>();
		for (String s: src.forLog)
			forLog.add(s);
		bsf= new Double(src.bsf);
		iterBsf= new Integer(src.iterBsf);
		nanoDuration= new Long(src.nanoDuration);
		theBestPath= new Vector<Integer>();
		for (Integer it: src.theBestPath)
			theBestPath.add(it);
		fingerPrint= src.fingerPrint;
		if (src.oldPositions!=null) {
			oldPositions= new int[src.oldPositions.length];
			for (int k=0; k<oldPositions.length; k++)
				oldPositions[k]=src.oldPositions[k];
		}
	}

	// to store data received from a client
	public void integrateOutput(OutputCargo oCargo, boolean integrateFeromons ) {
		msg=oCargo.message;
		theBestPath= oCargo.bestPath;
		/*
		bestRoutes.clear();
		bestRoutes.add(oCargo.bestPath);
		 */
		bsf=oCargo.bestValue;
		if (integrateFeromons)
			feromons= new MatrixData(oCargo.feromons);;
	}
	public double getFingerPrint() {
		return(fingerPrint);
	}
	public void setNanoDuration(Long dur) {
		nanoDuration=dur;
	}
	public Long getNanoDuration() {
		return(nanoDuration);
	}
	public void setTheBestPath(Vector<Integer> p) {
		theBestPath=p;
	}
	public Vector<Integer> getTheBestPath() {
		return(theBestPath);
	}
	public double getRoutesFingerPrint() {
		int res=0;
		/*for (Vector<Integer> v1: bestRoutes)
			for (Integer ii: v1)
				res+=ii;
		 */
		return (res);
	}
	public AntCommunityCargo () {
		command=AntServerCommand.SLEEP;
		basicParams= new BasicRunParameters();
		sleepTime=12;
		genealogy= new Vector<String>();
		distances= null;
		feromons= null;
		//bestRoutes= new Vector<Vector<Integer>>();
		forLog= new Vector<String>();
		bsf=Double.MAX_VALUE;
		theBestPath= new Vector<Integer>();
		iterBsf=-1;
		++id;
	}
	public AntCommunityCargo(MyProperties prop){
		command=AntServerCommand.SLEEP;
		basicParams= new BasicRunParameters(prop);
		sleepTime=prop.getInteger("sleepTime", 100);
		genealogy= new Vector<String>();
		setArrays(prop);
		//bestRoutes= new Vector<Vector<Integer>>();
		forLog= new Vector<String>();
		bsf=Double.MAX_VALUE;
		iterBsf=-1;	
		theBestPath= new Vector<Integer>();
		++id;
	}
	public int getId() {
		return(id);
	}
	public void setBsf(double d) {
		bsf=d;
	}
	public double getBsf() {
		return(bsf);
	}
	public void setIterBsf(int it) {
		iterBsf=it;
	}
	public int getIterBsf() {
		return(iterBsf);
	}
	public Vector<String> getGenealogy() {
		return(genealogy);
	}
	public void addGenealogyItem(String s) {
		genealogy.add(s);
	}
	protected void setArrays(MyProperties prop) {
		String serialFileName=prop.getWord("serialname", "");
		MatrixData md=null;
		if (serialFileName.length()<2) {
			md= new MatrixData(prop.getInteger("nnodes", 23));
		}
		else {
			try {
				md= MatrixData.deserializeMe(serialFileName);
			} catch (Exception e) {
				System.err.print("Matrix deserialization ERROR: "+serialFileName+"\n");;
				System.exit(4);
			}	
		}
		distances=md;
		prop.setProperty("nNodes", ""+md.getNumberOfNodes());
		int nNodes=prop.getInteger("nNodes", 12);
		basicParams.setNNodes(nNodes);
		feromons= new MatrixData(nNodes);
		resetFeromons();
	}
	public void updateDistances(MatrixData dist) {
		distances=dist;
	}
	public int getNumberOfGenerations() {
		return(genealogy.size());
	}
	public void addRoute(Vector<Integer> rts) {
		//bestRoutes.add(rts);
	}
	/*
public Vector<Integer> getLastRoute() {

	if (bestRoutes.size()>0)
		return(bestRoutes.get(bestRoutes.size()-1));
	else
		return(null);
}
	 */
	public void setCommand(AntServerCommand commnad) {
		this.command=commnad;
	}
	public String getMessage() {
		return(msg);
	}
	public void setMessage(String m) {
		msg=m;
	}
	public void append2Log(String msg) {
		forLog.add(msg);
	}
	public void append2Log(String msg, long tStart, long tCurrent) {
		forLog.add(msg+"\t"+(tCurrent-tStart)+"\tmikro\t"+(tCurrent-tStart)/1000+"\n");
	}
	public Vector<String> get4Log() {
		return(forLog);
	}
	public void clearLog() {
		forLog.clear();
	}
	public String getLastMessage() {
		String s=forLog.get(forLog.size()-1);
		return(s);
	}
	public AntServerCommand getCommand() {
		return(command);
	}
	public Integer getSleepTime() {
		return(sleepTime);
	}
	public void setDistances(double[][] dist) {
		distances = new MatrixData(dist);
	}
	public void setFeromons(double[][] fer) {
		feromons= new MatrixData(fer);
	}
	public double[][] getDistances() {
		double[][] result=distances.getArray();
		return(result);
	}
	public Vector<Integer> getBestPath() {
		return(theBestPath);
	}
	public double getBSFdistance() {
		if (theBestPath==null)
			return(Double.MAX_VALUE);
		if (theBestPath.size()<2)
			return(Double.MAX_VALUE);
		Vector<Integer> bsf=theBestPath;
		double res= distances.getRouteLength(bsf);
		return(res);
	}
	public double[][] getFeromonsOld() {
		double[][] result;
		if (feromons==null) {
			int nodes=basicParams.getAntNumber();
			result= new double[nodes][nodes];
			result=resetArray(result);
		} else {
			result=feromons.getArray();
		}
		return(result);		
	}
	public boolean emptyFeromons() {
		return(feromons==null);
	}
	public double[][] getFeromons() {
		double[][] result;
		if (feromons==null) {
			int nodes=basicParams.getAntNumber();
			result= new double[nodes][nodes];
			result=resetArray(result);
		} else {
			result=feromons.getArray();
		}
		return(result);		
	}
	public BasicRunParameters getBasicParameters() {
		return (basicParams);
	}
	protected double[][] resetArray(double[][] dist) {
		int m_nNodes= dist[0].length;
		double[][] result= new double[m_nNodes][m_nNodes];
		MatrixData md= new MatrixData(dist);
		double dAverage = md.getAvgPathLength();
		double m_dTau0 = (double)1 / ((double)m_nNodes * (0.5 * dAverage));
		for(int r = 0; r < m_nNodes; r++)
		{
			for(int s = 0; s < m_nNodes; s++)
			{
				result[r][s] = m_dTau0;
			}
		}
		return(result);
	}
	public double[][] getFeromens() {
		double[][] fer= feromons.getArray();
		return(fer);
	}
	public void resetFeromons(double[][] dist) {
		double [][] fer= resetArray(dist);
		setFeromons(fer);	
	}
	public void resetFeromons() {
		double[][] ferArr= feromons.getArray();
		resetFeromons(ferArr);
	}
	public void serializeMe(String fn) throws IOException {
		FileOutputStream fileOut =
				new FileOutputStream(fn); // .ser is the prefered file name extension
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(this);
		out.close();  // closing of both files is necessary
		fileOut.close();
	}
	public static AntCommunityCargo deserializeMe(String fn) throws IOException, ClassNotFoundException {
		AntCommunityCargo ob;
		FileInputStream fileIn = new FileInputStream(fn);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		ob = (AntCommunityCargo) in.readObject();  // casting is a MUST!!!
		in.close();
		fileIn.close();
		return(ob);
	}

	public String getInfoCard(boolean fullVersion) {
		String result;
		if (fullVersion) {
			result="["+basicParams.getInfoCard()+"]\t";
			result+="Dist: "+distances.getInfoCard();
			result+="\tFer: "+feromons.getInfoCard();
			return(result);
		}
		result="Ants/Iter/Generacja\t"+basicParams.getAntNumber()+
				"\t"+basicParams.getIterationNumber()+"\t"+
				genealogy.size();
		return(result);
	}
	public String getSolutionDescription() {
		NumberFormat numberFormatter= NumberFormat.getNumberInstance(Locale.GERMAN);
		numberFormatter.setMaximumFractionDigits(5);
		String bsfStr="DoubleMax";
		if (bsf==null || bsf==Double.MAX_VALUE)
			bsfStr=numberFormatter.format(bsf);
		String result="[C1]\t"+bsfStr+"\t"+iterBsf+"\tmilis\t"+
				numberFormatter.format((double)nanoDuration/1000.0);
		double fer[][]= feromons.getArray();
		double d=AsUtilities.get2dSumm(fer);
		if (d==Double.MAX_VALUE)
			result+="\t server feromony MAXVAL";
		result+="\t server feromony "+AsUtilities.get2dSumm(fer);
		return(result);
	}
	public void setBasicParameters() {
		// AntColony4TSP
		AntColony4TSP.setParams(basicParams.getAlpha());
		//Ant
		Ant4TSP.setParameters(basicParams.getBeta(), basicParams.getQ0(), basicParams.getRo());
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Vector<Vector<String>> tt= new Vector<Vector<String>>();
		tt.add(new Vector<String>());
		tt.add(new Vector<String>());
		tt.add(new Vector<String>());
		tt.get(0).add("abc");
		tt.get(0).add("234");
		tt.get(2).add("one");
		tt.get(2).add("two");
		System.out.println(tt);
		ByteArrayOutputStream bout= new ByteArrayOutputStream();
		ObjectOutputStream outByte = new ObjectOutputStream(bout);
		outByte.writeObject(tt);
		//outByte.
		System.out.print(bout.toString());
		//Vector<Vector<String>> tx;
		//ByteArrayInputStream bin= new ByteArrayInputStream(outByte.);


		AntCommunityCargo acc= new AntCommunityCargo();
		acc.serializeMe("a.ser");
		acc=null;
		acc=AntCommunityCargo.deserializeMe("a.ser");
		System.exit(1);

	}

}
