package runs;

import java.io.FileReader;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import tsp.AntColony4TSP;
import acs.AntGraph;
import as.AntCommunityCargo;
import as.AsUtilities;
import as.GraphStore;
import as.MyProperties;

public class BasicRun implements Runnable
{
	protected static Random s_ran = new Random(System.currentTimeMillis());

	protected int nAnts = 10;
	protected int nNodes = 50;
	protected int nIterations = 10;
	protected int nRuns = 10;
	protected String distancesFn="CD50A.ser";
	protected MyProperties prop;
	protected MyProperties localProperties;
	
	protected AntCommunityCargo cargo;

	protected double d[][] = null;     
	protected AntGraph graph;
	
	protected int interval;	
	protected GraphStore graphStore;
	protected boolean dynamicGraph=true;
	
	protected boolean allwaysReset;

	public BasicRun(MyProperties prop) {
		this.prop=prop;
		localProperties= new MyProperties();
		try {
			FileReader fr;
			fr = new FileReader("ColonyClient.prop");
			localProperties.load(fr);
			fr.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(1);
		}
		readParameters();
		graphStore=GraphStore.initGraphStore(prop);
		cargo= new  AntCommunityCargo(prop);
		double[][] dist=graphStore.getDistanceMatrix();
		cargo.setDistances(dist);
		cargo.resetFeromons(dist);
		try {
			initLogger();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(9);
		}
	}
	public BasicRun(int nA, int nN, int nI, int nR, String dist) {
		nAnts=nA;
		nNodes= nN;
		nRuns=nR;
		nIterations=nI;
		distancesFn=dist;
	}
	protected void setStaticRandom() {
		d= new double[nNodes][ nNodes];
		for(int i = 0; i < nNodes; i++)
			for(int j = i + 1; j < nNodes; j++)
			{
				d[i][j] = s_ran.nextDouble();
				d[j][i] = d[i][j];
			}      		
	}
	public BasicRun() {
		System.out.println("AntColonySystem for TSP");
		setStaticRandom();
		graph = new AntGraph(nNodes, d);
	}

	protected void runOneColony() {
		System.err.print("sgs=>\t"+graphStore.getVisitCard()+"\n"); /*
		System.err.print("sgs=>\t"+gs.getVisitCard()+"\n");
		System.err.print("sgs=>\t"+gs.getVisitCard()+"\n");
		System.err.print("sgs=>\t"+gs.getVisitCard()+"\n");
		System.err.print("sgs=>\t"+gs.getVisitCard()+"\n"); */
		for(int i = 0; i < nRuns; i++)
		{	
			if (dynamicGraph) {
				d=graphStore.getDistanceMatrix();
				nNodes=graphStore.getNumberOfNodes();
				graph = new AntGraph(nNodes, copyMatrix(d));			
			}
			if (allwaysReset)
				graph.resetTau();
			//System.err.print("sgs=>AntColony\t"+gs.getVisitCard()+"\n");
			System.err.print("start=>AntColony\t"+graph.getVisitCard()+"\n");
			AntColony4TSP antColony = new AntColony4TSP(graph, nAnts, nIterations);
			antColony.start();
			System.err.print("\nfinish=>AntColony\t"+graph.getVisitCard()+"\n");
			System.out.println(i + "\t" + antColony.getBestPathValue() + "\t" + antColony.getLastBestPathIteration());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	protected void runOneColony(AntCommunityCargo cargo) {
		if (!isOK())
			return;
		String localName=localProperties.getWord("localname", "AC Client");
		AntCommunityServer.theLogger.severe(cargo.getInfoCard(true)+"\t"+localName);
		for(int i = 0; i < nRuns; i++)
		{				
			double [][] dist=graphStore.getDistanceMatrix();
			double [][] ferom = cargo.getFeromons();
			int nodeNo= cargo.getBasicParameters().getNodeNumber();
			graph = new AntGraph(nodeNo, dist, ferom);	
			AntCommunityServer.theLogger.warning("start=>AntColony\t"+graph.getVisitCard()+"\n");
			AntColony4TSP antColony = new AntColony4TSP(graph, 
					cargo.getBasicParameters().getAntNumber(), 
					cargo.getBasicParameters().getIterationNumber());
			cargo.setBasicParameters();
			antColony.start();
			AntCommunityServer.theLogger.warning("Colony end of run "+localName+"\t"+graph.getVisitCard());
			AntCommunityServer.theLogger.warning(i + "\t" + antColony.getBestPathValue() + "\t" + antColony.getLastBestPathIteration()+"\n");
			if (cargo.getBasicParameters().getAlwaysResetFeromons())
				cargo.resetFeromons(ferom);		
			ferom= graph.getFeromons();
			cargo.setFeromons(ferom);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected boolean isOK() {
		boolean res=true;
		Scanner sc=new Scanner(System.in);
		System.out.println("Run parametrs:\n"+cargo.getInfoCard(false)+"\nIs it OK?");
		String resp=sc.nextLine();
		res=resp.toLowerCase().startsWith("ok")|| resp.toLowerCase().startsWith("yes")||
				resp.toLowerCase().startsWith("tak");
		sc.close();
		return(res);
	}
	public double[][] copyMatrix(double[][] d) {
		int nn=d.length;
		double[][] res= new double[nn][nn];
		for (int i=0; i<nn; i++)
			for (int k=0; k<nn; k++)
				res[i][k]=d[i][k];
		return(res);
	}
	protected void readParameters() {
		nAnts= prop.getInteger("nAnts", 10);
		nNodes= prop.getInteger("nNodes", 30);
		nIterations= prop.getInteger("nIterations", 10);
		nRuns= prop.getInteger("nRuns", 10);
		interval= prop.getInteger("interval", 100);
		distancesFn=prop.getProperty("serialname", "CD50A.ser");
		allwaysReset= prop.getWord("allwaysReset", "no").equals("true");
		if (!prop.getProperty("dynamicGraph", "no").equals("no")) {
			dynamicGraph=true;
		}
	}
	protected void initLogger() throws Exception {
		Handler handler;
		handler = new FileHandler(prop.getProperty("logFileName", "ACSLoger.txt").trim());
		AntCommunityServer.theLogger.addHandler(handler);
		AntCommunityServer.theLogger.setLevel(Level.ALL);
		handler.setFormatter(new Formatter() {
			@Override
			public String format(LogRecord record) {
				// TODO Auto-generated method stub
				String res;
				long milis= record.getMillis();
				res=AsUtilities.getTime(milis)+"\t";
				res+=record.getLevel()+"\t";
				res+=record.getMessage();
				return res;
			}
		});		
	}
	public static void main(String[] args) throws Exception
	{
		BasicRun bRun=null;
		if (args.length==0)
			bRun=new BasicRun();
		else {
			MyProperties prop= new MyProperties();
			FileReader  fr= new FileReader(args[0]);
			prop.load(fr);
			bRun=new BasicRun(prop);
		}
		bRun.runOneColony(bRun.cargo);
		System.err.print("End of Basic Run\n");
		System.exit(0);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
}

