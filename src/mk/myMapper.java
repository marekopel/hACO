package mk;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

import java.util.*;
import java.io.*;

import runs.AntCommunityServer;
import tsp.*;
import acs.*;
import as.AntCommunityCargo;
import as.AsUtilities;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.google.gson.Gson;

public class myMapper extends Mapper<LongWritable, Text, FloatWritable, Text> {

	private static Random s_ran = new Random(System.currentTimeMillis());

	protected void map(LongWritable offset, Text line, Context context)
			throws IOException, InterruptedException {

		String curLine = line.toString();
//		System.out.println("offset --> " + offset + ", line: " + curLine);
		System.out.println("offset --> " + offset);
		/*
		 * String words[] = curLine.split(" "); for (String word:words){
		 * context.write(new Text(word), new IntWritable(1)); }
		 */// System.out.println(curLine);

		Gson gson = new Gson();
		AntCommunityCargo cargo = gson.fromJson(curLine, AntCommunityCargo.class);
		System.out.println("Id: "+cargo.getMessage() );
		
		
/*		
		
		AntGraph graph;
		double[][] dist = cargo.getDistances();
		double[][] ferom = cargo.getFeromons();
		int nodeNo = cargo.getBasicParameters().getNodeNumber();
		graph = new AntGraph(nodeNo, dist, ferom);
		AntCommunityServer.theLogger.warning("start=>AntColony\t"
				+ graph.getVisitCard() + "\n");
		AntColony4TSP antColony = new AntColony4TSP(graph, cargo
				.getBasicParameters().getAntNumber(), cargo
				.getBasicParameters().getIterationNumber());
		cargo.setBasicParameters();
		antColony.start();
		AntCommunityServer.theLogger.warning("\t"
				+ antColony.getBestPathValue() + "\t"
				+ antColony.getLastBestPathIteration() + "\n");
		double currPathLength;
		dist = cargo.getDistances();
		int[] path = antColony.getBestPath();
		currPathLength = AsUtilities.getPathLength(path, dist);
		AntCommunityServer.theLogger.warning("currlent path length: "
				+ currPathLength + "\n");
		Vector<Integer> bp = antColony.getBestPathVector();
		cargo.addRoute(bp);
		if (cargo.getBasicParameters().getAlwaysResetFeromons()) {
			graph.resetTau();
			ferom = graph.getFeromons();
		}
		cargo.setFeromons(ferom);


		
*/
		long startTime= System.nanoTime();
		long dur;		
		
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

		
// RESET TAU!!!!!
		graph.resetTau();
		
		AntColony4TSP antColony = new AntColony4TSP(graph, nAnts, nIter, oldies);
		antColony.start();
		
		result.setTheBestPath(antColony.getBestPathVector());
		result.setBsf(antColony.getBestPathValue());
		result.setIterBsf(antColony.getLastBestPathIteration());
		double [][] fer=antColony.getGraph().getFeromons();
		result.setFeromons(fer);
		oldies= antColony.getOldPositions();
		result.setOldPositions(oldies);
		
					
		dur=System.nanoTime()-startTime;
		result.setNanoDuration(dur);		
		
		
		context.write(new FloatWritable((float)antColony.getBestPathValue()), new Text(gson.toJson(result)));
	}

}
