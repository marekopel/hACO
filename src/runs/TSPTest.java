package runs;
/**
 * AntApplication.java
 *
 * @author Created by Omnicore CodeGuide
 */

import java.text.NumberFormat;
import java.util.*;
import java.io.*;

import tsp.AntColony4TSP;
import acs.*;
import as.AsUtilities;
import as.MatrixData;
import as.MyProperties;

public class TSPTest
{
	//private static Random s_ran = new Random(System.currentTimeMillis());
	private static Random s_ran = new Random(2121238121);
	private static  double d[][] = null;
	protected static boolean eachIter;
	protected static boolean showPath;

	protected int nAnts;
	
	protected static MyProperties prop;  

	public static void main(String[] args) throws Exception {
		TSPTest.setEnvironment("AntCommunity.prop");
		String logName= prop.getProperty("logFileName", "AntLog.txt");
		PrintWriter pw;
		pw = new PrintWriter(logName);
		String info=AsUtilities.getDate()+"\t"+AsUtilities.getTime()+"\t"+prop.getBaseInfo()+"\t";
		pw.println(info);
		int repNo=prop.getInteger("nRuns", 30);
		for (int i=0; i<repNo; i++) {
			TSPTest t= new TSPTest();
			Vector<String> sLines= t.baza(args);
			for (String s: sLines) {
				int krokNorma=i*t.nAnts/50;
				pw.print(krokNorma+"\t"+s+"\n");
				System.out.print(krokNorma+"\t"+s+"\n");  // ZLE!!!
			}
			System.err.print("nr kol.:"+i+"\n");
		}
		pw.close();
		AsUtilities.endOfWork();
	}

	public static void mainY(String[] args)
	{
		// Print application prompt to console.
		System.out.println("AntColonySystem for TSP");

		int nAnts = 50;
		int nNodes = 50;
		int nIterations = 500;
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


		//        double t[][] = new double[nNodes][nNodes];

		if (d==null) {
			for(int i = 0; i < nNodes; i++)
				for(int j = i + 1; j < nNodes; j++)
				{
					d[i][j] = s_ran.nextDouble();
					d[j][i] = d[i][j];
					//                t[i][j] = 1; //(double)1 / (double)(nNodes * 10);
					//                t[j][i] = t[i][j];
				}
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
	public static void setEnvironment(String propName) throws Exception {
		prop = new MyProperties();
		FileReader fr= new FileReader(propName);
		prop.load(fr);
		fr.close();
		String serialName= prop.getWord("serialname", "CD50A.ser");
		MatrixData md=MatrixData.deserializeMe(serialName);
		d=md.getArray();
		eachIter= prop.getBoolean("EachIter", false);
		showPath= prop.getBoolean("pathView", false);
	}

	public Vector<String> baza(String[] args)
	{
		//System.out.println("AntColonyX 4 TSP");        
		nAnts = prop.getInteger("nAnts", 50);
		int nNodes = 50;
		int nIterations = prop.getInteger("nIterations", 500);
		int nRepetitions = 1;

		Vector<String> res= new Vector<String>();

		NumberFormat numberFormatter= NumberFormat.getNumberInstance(Locale.GERMAN);
		numberFormatter.setMaximumFractionDigits(5);

		String s1="";
		if (d==null) {
			for(int i = 0; i < nNodes; i++)
				for(int j = i + 1; j < nNodes; j++)
				{
					d[i][j] = s_ran.nextDouble();
					d[j][i] = d[i][j];
				}
		} else {
			nNodes=d.length;
		}

		AntGraph graph = new AntGraph(nNodes, d);

		for(int i = 0; i < nRepetitions; i++)
		{
			graph.resetTau();
			AntColonyMonitor antColony = new AntColonyMonitor(graph, nAnts, nIterations);
			antColony.setMonitor(eachIter, showPath);
			antColony.start();
			res=antColony.getMonitor();
			s1="==>\t"+numberFormatter.format(antColony.getBestPathValue());
			s1=s1 + "\t" + antColony.getLastBestPathIteration();
		}
		res.add(s1);
		return(res);
	}

}

