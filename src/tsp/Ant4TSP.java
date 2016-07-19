package tsp;

import java.util.*;

import acs.Ant;
import acs.AntGraph;


public class Ant4TSP extends Ant
{
	// as2-02 not final
	/*
	private static final double B    = 2;
    private static final double Q0   = 0.8;
    private static final double R    = 0.1;
    */
    private static double B    = 2;
    private static double Q0   = 0.8;
    private static double R    = 0.1;
    
    private static final Random s_randGen = new Random(System.currentTimeMillis());
        
    // as20_02
    protected Hashtable<Integer, Integer> m_nodesToVisitTbl;
        
    public Ant4TSP(int startNode, Observer observer)
    {
        super(startNode, observer);
    }
    
    // as20_02
    public static void setParameters(double b, double q0, double r) {
    	B=b;
    	Q0=q0;
    	R=r;
    }
    public void init()
    {
        super.init();
        
        final AntGraph graph = s_antColony.getGraph();
        
        // inizializza l'array di città da visitare
        // as20_02
        m_nodesToVisitTbl = new Hashtable<Integer, Integer>(graph.nodes());
        for(int i = 0; i < graph.nodes(); i++)
            m_nodesToVisitTbl.put(new Integer(i), new Integer(i));
        
        // Rimuove la città corrente
        m_nodesToVisitTbl.remove(new Integer(m_nStartNode));
        
//      nExplore = 0;
    }

    public int stateTransitionRule(int nCurNode)
    {
        final AntGraph graph = s_antColony.getGraph();
        
        // generate a random number
        double q    = s_randGen.nextDouble();
        int nMaxNode = -1;
        
        if(q <= Q0)  // Exploitation
        {
//            System.out.print("Exploitation: ");
            double dMaxVal = -1;
            double dVal;
            int nNode;
            
            // search the max of the value as defined in Eq. a)
            Enumeration<Integer> enuma = m_nodesToVisitTbl.elements();
            while(enuma.hasMoreElements())
            {
                // select a node
                nNode = ((Integer)enuma.nextElement()).intValue();
                
                // check on tau
                if(graph.tau(nCurNode, nNode) == 0)
                    throw new RuntimeException("tau = 0");
                
                // get the value
                dVal = graph.tau(nCurNode, nNode) * Math.pow(graph.etha(nCurNode, nNode), B);
                
                // check if it is the max
                if(dVal > dMaxVal)
                {
                    dMaxVal  = dVal;
                    nMaxNode = nNode;
                }
            }
        }
        else  // Exploration
        {
//              System.out.println("Exploration");
            double dSum = 0;
            int nNode = -1;
            
            // get the sum at denominator
            Enumeration<Integer> enuma = m_nodesToVisitTbl.elements();
            while(enuma.hasMoreElements())
            {
                nNode = ((Integer)enuma.nextElement()).intValue();
                if(graph.tau(nCurNode, nNode) == 0)
                    throw new RuntimeException("tau = 0");
                
                // Update the sum
                dSum += graph.tau(nCurNode, nNode) * Math.pow(graph.etha(nCurNode, nNode), B);
            }
            
            if(dSum == 0)
                throw new RuntimeException("SUM = 0");
            
            // get the average value
            double dAverage = dSum / (double)m_nodesToVisitTbl.size();
            
            // search the node in agreement with eq. b)
            enuma = m_nodesToVisitTbl.elements();
            while(enuma.hasMoreElements() && nMaxNode < 0)
            {
                nNode = ((Integer)enuma.nextElement()).intValue();
                
                // get the value of p as defined in eq. b)
                // as20_02
                //double p =
                //    (graph.tau(nCurNode, nNode) * Math.pow(graph.etha(nCurNode, nNode), B)) / dSum;
                
                // if the value of p is greater the the average value the node is good
                if((graph.tau(nCurNode, nNode) * Math.pow(graph.etha(nCurNode, nNode), B)) > dAverage)
                {
                    //System.out.println("Found");
                    nMaxNode = nNode;
                }
            }
            
            if(nMaxNode == -1)
                nMaxNode = nNode;
       }
                 
        if(nMaxNode < 0)
            throw new RuntimeException("maxNode = -1");
        
        // delete the selected node from the list of node to visit
        m_nodesToVisitTbl.remove(new Integer(nMaxNode));
        
        return nMaxNode;
    }
    
    public void localUpdatingRule(int nCurNode, int nNextNode)
    {
        final AntGraph graph = s_antColony.getGraph();
        
        // get the value of the Eq. c)
        double val =
            ((double)1 - R) * graph.tau(nCurNode, nNextNode) +
            (R * (graph.tau0()));
        
        // update tau
        graph.updateTau(nCurNode, nNextNode, val);
    }
    
    public boolean better(double dPathValue1, double dPathValue2)
    {
        return dPathValue1 < dPathValue2;
    }

    public boolean end()
    {
        return m_nodesToVisitTbl.isEmpty();
    }
}

