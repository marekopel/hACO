package runs;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;

import tsp.AntColony4TSP;
import acs.AntGraph;
import as.AntCommunityCargo;
import as.AntServerCommand;
import as.AsUtilities;
import as.MatrixData;
import as.MyProperties;
import as.OutputCargo;

public class GenericAntClient {
	protected String localName="localLocal";
	protected String hostName;
	protected int hostPort;
	protected int sleepTime;

	protected Socket sock;
	protected OutputStream os=null;
	protected ObjectOutputStream oos = null;
	protected java.io.InputStream is;
	protected ObjectInputStream ois;			

	protected Random rd= new Random();
	protected MyProperties localProperties= new MyProperties();
	protected static String defPropFileName="ColonyClient.prop";

	protected boolean debugMode;
	protected boolean oldStyleMode=false;

	int nAnts = localProperties.getInteger("nAnts", 50);
	int nNodes ;
	int nIterations = localProperties.getInteger("nIterations", 500);
	int nRepetitions = localProperties.getInteger("nRuns", 10);


	private static Random s_ran = new Random(System.currentTimeMillis());

	public static void main(String[] argv) {
		String name=defPropFileName;
		//name= "AntCommunity.prop";  // 4 local work
		if (argv.length>0)
			name=argv[0];
		GenericAntClient generClient = new GenericAntClient(name);
		if (generClient.oldStyleMode) {
			// old style call
			System.err.print("Standalone Client Colony\n");
			generClient.oldStyleRun();
			System.err.print("\nEnde of Old Style Run\n");
			System.exit(2);
		}
		System.err.print("Starting Client Colony: "+generClient.localName+"\n");		
		try {
			generClient.handleCommunication();
		} catch (Exception e) {
			System.err.print("Server had stopped working "+ generClient.localName);
			System.exit(1);
		}
		try {
			generClient.closeConnection();
		} catch (IOException e) {
			System.err.print("Can't close connection "+ generClient.localName);
			System.exit(1);
		}
		System.err.print("===>"+generClient.localName+ "end of work");
		System.exit(0);
	}
	public GenericAntClient () {

	}
	public GenericAntClient(String fName) {
		System.err.print("Starting AntColony: "+fName);
		try {
			FileReader fr= new FileReader(fName);
			localProperties.load(new FileReader(fName));
			fr.close();
		} catch (FileNotFoundException e) {
			System.err.print("Property file:"+fName+" openig error\n");
			System.exit(1);
		} catch (IOException e) {
			System.err.print("Property file:"+fName+" reading error\n");
			System.exit(2);
		}
		hostName = localProperties.getProperty("hostname", "localhost");
		localName= localProperties.getProperty("localname", "XXLocalXX");
		hostPort= localProperties.getInteger("port", 8981);
		debugMode= (localProperties.getInteger("debugMode", 0)==1);
		oldStyleMode= localName.equals("XXLocalXX");
		if (!oldStyleMode) {
			System.err.print("Client: "+localName+" waites for Community Server\n");
			initConnection();
		}
		else {
			System.err.print(" local Colony\n");
		}
	}
	public void handleCommunication() throws Exception {
		NumberFormat numberFormatter= NumberFormat.getNumberInstance(Locale.GERMAN);
		numberFormatter.setMaximumFractionDigits(5);
		int localRunNo=1;
		send(localName);   // >>>>> sending name to register by the client colony server
		AntServerCommand cmd=AntServerCommand.WORK;
		while (cmd!=AntServerCommand.STOP) {
			AntCommunityCargo cargo=null;
			long startTime= System.nanoTime();
			long dur;
			cargo  = (AntCommunityCargo) ois.readObject();  // <<<<<< data from the server	
			cmd=cargo.getCommand();
			String msg="[G0]\t"+localName+"\t"+cargo.getSolutionDescription()+"\n";
			//System.err.print(msg);
			
			switch (cmd) {
			case SLEEP: 	
				int slt=cargo.getSleepTime();
				Thread.sleep(slt);
				break;  // its the server that ends connection
			case EMPTYRUN:
				cargo.append2Log(msg+"EMPTY\t", startTime, System.nanoTime());
				dur=System.nanoTime()-startTime;
				cargo.setNanoDuration(dur);
				oos.writeObject(cargo);
				break;
			case WORK:
				
				AntCommunityCargo accNeu= testTSP(cargo);  // processing
				
				msg="[G02]\t"+localName+"\t"+accNeu.getSolutionDescription()+"\t"+"\tBSF\t"+numberFormatter.format(accNeu.getBsf())
						+"\t"+accNeu.getIterBsf()+"\t"+AsUtilities.getTime();
				System.err.println(msg+"loc. run no: "+localRunNo+"\n");		
				accNeu.setMessage("from : "+localName+msg);
				dur=System.nanoTime()-startTime;
				accNeu.setNanoDuration(dur);
				oos.writeObject(accNeu); // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
				localRunNo++;
				break;
			default:
				break;
			}
		}
	}
	public void handleCommunicationOld() throws Exception {
		int localRunNo=1;
		send(localName);   // >>>>> sending name to register by the client colony server
		AntServerCommand cmd=AntServerCommand.WORK;
		while (cmd!=AntServerCommand.STOP) {
			AntCommunityCargo cargo=null;
			AntCommunityCargo result=null;
			long startTime= System.nanoTime();
			cargo  = (AntCommunityCargo) ois.readObject();  // <<<<<< data from the server	

			cmd=cargo.getCommand();
			String msg=localName+"\t"+cmd.toString()+"\t"+AsUtilities.getDate()+"\t"+
					AsUtilities.getTime()+"\t"+cargo.getMessage()+"\t";
			switch (cmd) {
			case SLEEP: 	
				int slt=cargo.getSleepTime();
				Thread.sleep(slt);
				break;  // its the server that ends connection
			case EMPTYRUN:
				cargo.append2Log(msg+"EMPTY\t", startTime, System.nanoTime());
				OutputCargo as = new OutputCargo();
				OutputCargo.sendCargo(as, oos);
				break;
			case WORK:
				result=executeOneColonyRunX(cargo);
				msg+=" WORK\t BSF:\t"+cargo.getBsf()+"\t"+cargo.getIterBsf()+"_";
				result.setMessage(localName+"\t"+localRunNo+ "\t"+AsUtilities.getTime());
				result.append2Log(msg, startTime, System.nanoTime());
				System.err.println("run no: "+localRunNo+"\t"+result.getBsf()+"\t"+((System.nanoTime()-startTime)/1000000)+"\tfp:\t"+"\n");
				Vector<Integer> bp = result.getTheBestPath();
				OutputCargo outc = new OutputCargo(msg, bp, result.getBsf(), (System.nanoTime()-startTime)/1000000);
				double[][] f= result.getFeromens();
				outc.setFeromons(f);
				OutputCargo.sendCargo(outc, oos);  // >>>>>>>>>>>>>>>>>>>>>>>>>>
				System.err.println("CLIENT : "+outc.getMessage());				
				localRunNo++;
				break;
			default:
				break;
			}
		}
	}
	protected AntCommunityCargo executeOneColonyRunX(AntCommunityCargo cargo) {
		AntGraph graph;
		double [][] dist=cargo.getDistances();
		double [][] ferom = cargo.getFeromons();
		int nodeNo= cargo.getBasicParameters().getNodeNumber();
		graph = new AntGraph(nodeNo, dist, ferom);	
		//String msg="start=>AntColony\t"+graph.getVisitCard()+"\n";
		//cargo.append2Log(msg);
		AntColony4TSP antColony = new AntColony4TSP(graph, 
				cargo.getBasicParameters().getAntNumber(), 
				cargo.getBasicParameters().getIterationNumber());
		cargo.setBasicParameters();
		antColony.start();
		//AntCommunityServer.er.warning("Colony end of run "+localName+"\t"+graph.getVisitCard());
		//String msg= localName+"\t" + antColony.getBestPathValue() + "\t" + antColony.getLastBestPathIteration();
		cargo.setBsf(antColony.getBestPathValue());
		cargo.setIterBsf(antColony.getLastBestPathIteration());
		//cargo.append2Log(msg);
		dist=cargo.getDistances();
		Vector<Integer> bp= antColony.getBestPathVector();
		cargo.setTheBestPath(bp);
		cargo.addRoute(bp);
		double dd= cargo.getBSFdistance();
		cargo.addGenealogyItem(localName+"\t"+antColony.getBestPathValue()+"\t control\t"+dd);
			cargo.setFeromons(ferom);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return(cargo);
	}

	public void doProc1() {
		while (true) {
			String t=localName;
			Double d= rd.nextDouble();
			try {
				send(t);
				send(d);
				t=getString();
				Double dResponse=getDouble();
				System.out.println("my solution: "+d+" from Server: "+ dResponse+" "+t);
				if (sleepTime>0)
					Thread.sleep(5000);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("IO Exception");
				break;
			} catch (ClassNotFoundException e) {
				System.out.println("ClassNotFoundException");
				System.exit(1);
			}  catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	public void initConnection() {
		boolean noServer=true;
		while (noServer) {
			try {
				sock = new Socket(hostName, hostPort);
				os = sock.getOutputStream();
				oos = new ObjectOutputStream(os);
				is = sock.getInputStream();
				ois = new ObjectInputStream(is);
				noServer=false;
				System.err.print("Connection ["+ hostName+"] to the server has been established!\n");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					System.err.print("Sleep client ex. \n");
					System.exit(0);
				}
			}
		}
	}

	/** Hold one conversation with the named hosts echo server 
	 * @throws IOException */
	public void send(Object input) throws IOException {
		oos.writeObject(input);  // SENDING			
	}
	public String getString() throws ClassNotFoundException, IOException {
		String res="";
		res = (String) ois.readObject();  // RECIEVING
		return(res);
	}
	public Double getDouble() throws ClassNotFoundException, IOException {
		Double res=0.0;
		res = (Double) ois.readObject();  // RECIEVING
		return(res);
	}
	public void closeConnection() throws IOException {			
		oos.close();
		os.close();
		ois.close();
		is.close();
		sock.close();					
	}

	public static void proba(String[] args) throws Exception {
		TSPTest.main(args);  
	}

	protected void oldStyleRun() {
		double[][] dists=null;
		String fn=localProperties.getWord("serialname", "CD50C.ser");
		try {
			MatrixData ms= MatrixData.deserializeMe(fn);
			dists=ms.getArray();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(4);
		}
		String msg="Old Style:\tAnts\t"+localProperties.getInteger("nAnts", -1)
				+"\tkeep Tau\t"+localProperties.getBoolean("propagateFeromons", false)
				+"\titer\t"+localProperties.getInteger("nIterations", -1)
				+"\truns\t"+localProperties.getInteger("nRuns", -1)+"\n";
		System.out.print(msg);
		NumberFormat numberFormatter= NumberFormat.getNumberInstance(Locale.GERMAN);
		numberFormatter.setMaximumFractionDigits(5);
		
		Vector<Double> pathLegths= new Vector<Double> ();
		long startTime=System.currentTimeMillis();
		localProperties.setProperty("nNodes", ""+ dists[0].length);
		AntGraph gr= new AntGraph(dists[0].length, dists);
		gr.resetTau();
		boolean resetFer= !localProperties.getBoolean("propagateFeromons", false);
		int rep=localProperties.getInteger("nRuns", 12);
		for (int k=0; k<rep; k++) {
			double [][] feromons=gr.getFeromons();
			AntCommunityCargo tc=testTSP(dists,feromons);
			pathLegths.add(tc.getBsf());
			System.out.print(""+k+"\t"+numberFormatter.format(tc.getBsf())+"\n");
			gr= new AntGraph(dists[0].length, dists, tc.getFeromens());
			if (resetFer)
				gr.resetTau();
		}
		String sInfo=localProperties.getPowerPathInfo(startTime, pathLegths);
		System.err.print(sInfo);
		System.exit(2);
	}
	protected double[][] copyArray(double[][] ar) {
		int size= ar[0].length;
		double[][] res= new double[size][size];
		for (int r=0; r<size; r++)
			for (int c=0; c<size; c++)
				res[r][c]=ar[r][c];
		return(res);
	}
	public AntCommunityCargo testTSP(double[][] distances, double[][] tau)
	{
		int nNodes=distances[0].length;
		double d[][] = copyArray(distances);
		double f[][] = copyArray(tau);
		int nAnts=localProperties.getInteger("nAnts", -1);
		int nIter= localProperties.getInteger("nIterations", -1);
		AntGraph graph = new AntGraph(nNodes, d, f);
		AntColony4TSP antColony = new AntColony4TSP(graph, nAnts, nIter);
		antColony.start();
		AntCommunityCargo result= new AntCommunityCargo();
		
		result.setTheBestPath(antColony.getBestPathVector());
		result.setBsf(antColony.getBestPathValue());
		result.setIterBsf(antColony.getLastBestPathIteration());
		double [][] fer=antColony.getGraph().getFeromons();
		result.setFeromons(fer);
		return(result);
	}
	// this is it!!
	
	public AntCommunityCargo testTSP(AntCommunityCargo cargo)
	{
		AntCommunityCargo result= new AntCommunityCargo(cargo);
		int nNodes=cargo.getBasicParameters().getNodeNumber();
		int nAnts=cargo.getBasicParameters().getAntNumber();
		int nIter= cargo.getBasicParameters().getIterationNumber();
		double [][] d=result.getDistances();
		double [][] f= result.getFeromens();
		int oldies[] = result.getOldPositions();
		/*
		String info="("+cargo.getPositionsInfo()+") ";
		info+=AsUtilities.get2dSumm(d)+"\t"+AsUtilities.get2dSumm(f)+"\n";
		System.err.print("[G4] cargo przed runem:\t"+info);
		*/
		AntGraph graph = new AntGraph(nNodes, d, f);
		//AntColony4TSP antColony = new AntColony4TSP(graph, nAnts, nIter);
		AntColony4TSP antColony = new AntColony4TSP(graph, nAnts, nIter, oldies);
		antColony.start();
		
		result.setTheBestPath(antColony.getBestPathVector());
		result.setBsf(antColony.getBestPathValue());
		result.setIterBsf(antColony.getLastBestPathIteration());
		double [][] fer=antColony.getGraph().getFeromons();
		result.setFeromons(fer);
		oldies= antColony.getOldPositions();
		result.setOldPositions(oldies);
		/*
		info="("+result.getPositionsInfo()+") ";
		double ddd[][]=result.getFeromens();
		System.out.println("XXX"+AsUtilities.get2dSumm(fer)+"===="+AsUtilities.get2dSumm(ddd));
		info+=AsUtilities.get2dSumm(d)+"\t"+AsUtilities.get2dSumm(fer)+"\t ? "+result.getSolutionDescription()+"\n";
		System.err.print("[G5] result po runie:\t"+info);
		*/
		return(result);
	}

	public OutputCargo testTSP(double[][] distances)
	{
		// Print application prompt to console.
		System.out.println("Old Style run");
		OutputCargo tc= new OutputCargo();
		
		int nAnts = localProperties.getInteger("nAnts", 50);
		int nNodes = distances[0].length;
		int nIterations = localProperties.getInteger("nIterations", 500);
		int nRepetitions = localProperties.getInteger("nRepetitions", 10);

		boolean resetTau= localProperties.getBoolean("resettau", true);
		NumberFormat numberFormatter= NumberFormat.getNumberInstance(Locale.GERMAN);
		numberFormatter.setMaximumFractionDigits(5);
		double d[][] = distances;
		AntGraph graph = new AntGraph(nNodes, d);
		for(int i = 0; i < nRepetitions; i++)
		{
			if (i==0 || resetTau)
				graph.resetTau();
			AntColony4TSP antColony = new AntColony4TSP(graph, nAnts, nIterations);
			antColony.start();
			String s1=numberFormatter.format(antColony.getBestPathValue());
			System.out.println(i + "\t" + s1 + "\t" + antColony.getLastBestPathIteration());                
		}
		return (tc);
	}
	public static void testTSPOld(String[] args)
	{
		// Print application prompt to console.
		System.out.println("AntColonyGener");

		int nAnts = 50;
		int nNodes = 50;
		int nIterations = 400;
		int nRepetitions = 30;

		NumberFormat numberFormatter= NumberFormat.getNumberInstance(Locale.GERMAN);
		numberFormatter.setMaximumFractionDigits(5);

		for (int i = 0; i < args.length; i+=2)
		{
			if(args[i].equals("-a"))
			{
				nAnts = Integer.parseInt(args[i + 1]);
				System.out.println("Ants: " + nAnts);
			}
			else if(args[i].equals("-n"))
			{
				nNodes = Integer.parseInt(args[i + 1]);
				System.out.println("Nodes: " + nNodes);
			}
			else if(args[i].equals("-i"))
			{
				nIterations = Integer.parseInt(args[i + 1]);
				System.out.println("Iterations: " + nIterations );
			}
			else if(args[i].equals("-r"))
			{
				nRepetitions = Integer.parseInt(args[i + 1]);
				System.out.println("Repetitions: " + nRepetitions);
			}
		}

		if(nAnts == 0 || nNodes == 0 || nIterations == 0 || nRepetitions == 0)
		{
			System.out.println("One of the parameters is wrong");
			return;
		}


		double d[][] = new double[nNodes][nNodes];
		//        double t[][] = new double[nNodes][nNodes];

		for(int i = 0; i < nNodes; i++)
			for(int j = i + 1; j < nNodes; j++)
			{
				d[i][j] = s_ran.nextDouble();
				d[j][i] = d[i][j];
				//                t[i][j] = 1; //(double)1 / (double)(nNodes * 10);
				//                t[j][i] = t[i][j];
			}

		AntGraph graph = new AntGraph(nNodes, d);

		try
		{
			ObjectOutputStream outs = new ObjectOutputStream(new FileOutputStream("c:\\temp\\" + nNodes + "_antgraph.bin"));
			outs.writeObject(graph);
			outs.close();

			//            ObjectInputStream ins = new ObjectInputStream(new FileInputStream("c:\\temp\\" + nNodes + "_antgraph.bin"));
			//            graph = (AntGraph)ins.readObject();
			//            ins.close();

			FileOutputStream outs1 = new FileOutputStream("c:\\temp\\" + nNodes + "_antgraph.txt");

			for(int i = 0; i < nNodes; i++)
			{
				for(int j = 0; j < nNodes; j++)
				{
					outs1.write((graph.delta(i,j) + ",").getBytes());
				}
				outs1.write('\n');
			}

			outs1.close();

			PrintStream outs2 = new PrintStream(new FileOutputStream("c:\\temp\\" + nNodes + "x" + nAnts + "x" + nIterations + "_results.txt"));

			for(int i = 0; i < nRepetitions; i++)
			{
				graph.resetTau();
				AntColony4TSP antColony = new AntColony4TSP(graph, nAnts, nIterations);
				antColony.start();
				String s1=numberFormatter.format(antColony.getBestPathValue());
				System.out.println(i + "\t" + s1 + "\t" + antColony.getLastBestPathIteration());                
			}
			outs2.close();
		}
		catch(Exception ex)
		{
			System.err.print("Exception!\n");
		}
		System.out.print("End of Work\n");
	}
	/*
	protected AntCommunityCargo executeOneColonyRun(AntCommunityCargo cargo) {
		AntGraph graph;
		AntColony4TSP antColony;
		BasicRunParameters params= cargo.getBasicParameters();
		int nAnts= params.getAntNumber();
		int nNodes = params.getNodeNumber();
		int nIterations = params.getIterationNumber();
		double [][] delta= cargo.getDistances();
		double [][] tau=cargo.getFeromons();
		graph= new AntGraph(nNodes, delta, tau); 
		antColony = new AntColony4TSP(graph, nAnts, nIterations);
		AntColony4TSP.setParams(params.getAlpha());
		Ant4TSP.setParameters(params.getBeta(), params.getQ0(), params.getQ0());
		antColony.start();
		// output
		Vector<Integer> bestPath= antColony.getBestPathVector();
		cargo.addRoute(bestPath);
		tau=graph.getFeromons();
		cargo.setFeromons(tau);
		return(cargo);
	}
	protected AntCommunityCargo executeOneColonyRunPop(AntCommunityCargo cargo) {
		AntGraph graph;
		AntColonyMonitor antColony;
		long milis=System.currentTimeMillis();
		String timeStart=AsUtilities.getTimeMili(milis);
		BasicRunParameters params= cargo.getBasicParameters();
		int nAnts= params.getAntNumber();
		int nNodes = params.getNodeNumber();
		int nIterations = params.getIterationNumber();
		double [][] delta= cargo.getDistances();
		double [][] tau=cargo.getFeromons();
		graph= new AntGraph(nNodes, delta, tau); 
		graph.resetTau();		
		antColony = new AntColonyMonitor(graph, nAnts, nIterations);
		antColony.setMonitor(params.eachIter, params.pathView);
		cargo.setBasicParameters();
		antColony.start();
		// output
		Vector<Integer> bestPath= antColony.getBestPathVector();
		cargo.addRoute(bestPath);
		tau=graph.getFeromons();
		cargo.setFeromons(tau);
		milis=System.currentTimeMillis()-milis;
		String info=localName+"\t"+antColony.getBestPathValue() + "\t" + antColony.getLastBestPathIteration()+"\t"+timeStart+
				"\t"+milis;
		cargo.append2Log(info);
		for (String s: antColony.getMonitor()) {
			cargo.append2Log(s);
		}
		return(cargo);
	}
	 */

} 
