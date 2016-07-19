package acs;

import java.util.Vector;

import as.AsUtilities;
import tsp.AntColony4TSP;

public class AntColonyMonitor extends AntColony4TSP {
	
	protected Vector<String> monit= new Vector<String> ();
	protected boolean eachIter=false;  // true shows data on each iteration
	protected boolean showPath=false; 	// true shows path

	public AntColonyMonitor(AntGraph graph, int ants, int iterations) {
		super(graph, ants, iterations);
	}
	public void setMonitor(boolean each, boolean path) {
		eachIter=each;
		showPath=path;
	}
    public Vector<String> getMonitor() {
    	return(monit);
    }
	public synchronized void start()
    {
        // creates all ants
        m_ants  = createAnts(m_graph, m_nAnts);     
        m_nIterCounter = 0;
        // loop for all iterations
        while(m_nIterCounter < m_nIterations)
        {
            // run an iteration
            iteration();
            if (eachIter) {
            	String res= m_nIterCounter+"\t"+AsUtilities.double2String(getBestPathValue());
            	monit.add(res);
            	if (showPath) {
            		int[]p=getBestPath();
            		res="[";
            		for (int i=0; i<p.length; i++)
            			res+=p[i]+"\t";
            		res+="]";
                	monit.add(res);
            	}
            }
            try
            {
                wait();
            }
            catch(InterruptedException ex)
            {
                ex.printStackTrace();
            }
            
            // synchronize the access to the graph
            synchronized(m_graph)
            {
                // apply global updating rule
                globalUpdatingRule();
            }
        }      
        if(m_nIterCounter == m_nIterations)
        {
       //     m_outs.close();
        }
    }
}
