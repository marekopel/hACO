package tsp;

import java.util.*;

import acs.Ant;
import acs.AntColony;
import acs.AntGraph;

public class AntColony4TSP extends AntColony
{
    //protected static final double A = 0.1;
    // as20_02 nor final
    protected static double A = 0.1;
    
 // as0305_2016 
    protected int[] oldPositions=null;
    
    public AntColony4TSP(AntGraph graph, int ants, int iterations)
    {
        super(graph, ants, iterations);
    }
    
    // as20_02
    public static void setParams(double a) {
    	A=a;
    }
    public AntColony4TSP(AntGraph graph, int ants, int iterations, int[] oldies)
    {
        super(graph, ants, iterations);
        if (oldies==null)
        	oldPositions=oldies;
        else {
        	oldPositions= new int[oldies.length];
        	for (int k=0; k<oldies.length; k++)
        		oldPositions[k]=oldies[k];
        }
    }

    public int[] getOldPositions() {
    	return(oldPositions);
    }
    protected Ant[] createAnts(AntGraph graph, int nAnts)
    {
        boolean firstUse=(oldPositions==null);
        if (firstUse) {
        	oldPositions= new int[nAnts];
        }
    	Random ran = new Random(System.currentTimeMillis());
        Ant4TSP.reset();
        Ant4TSP.setAntColony(this);
        Ant4TSP ant[] = new Ant4TSP[nAnts];
        for(int i = 0; i < nAnts; i++)
        {
        	// as0305  rozlokowanie mrówek wg.staregu uk³adu gdy trzeba
        	if (firstUse) {
        		int where=(int)(graph.nodes() * ran.nextDouble());
        		ant[i] = new Ant4TSP(where, this);
        		oldPositions[i]=where;
        	}
        	else {
        		ant[i]= new Ant4TSP(oldPositions[i], this);
        	}
        }
        
        return ant;
    }
    
    protected void globalUpdatingRule()
    {
        double dEvaporation = 0;
        double dDeposition  = 0;
        
        for(int r = 0; r < m_graph.nodes(); r++)
        {
            for(int s = 0; s < m_graph.nodes(); s++)
            {
                if(r != s)
                {
                    // get the value for deltatau
                    double deltaTau = //Ant4TSP.s_dBestPathValue * (double)Ant4TSP.s_bestPath[r][s];
                        ((double)1 / Ant4TSP.s_dBestPathValue) * (double)Ant4TSP.s_bestPath[r][s];
                                    
                    // get the value for phermone evaporation as defined in eq. d)
                    dEvaporation = ((double)1 - A) * m_graph.tau(r,s);
                    // get the value for phermone deposition as defined in eq. d)
                    dDeposition  = A * deltaTau;
                    
                    // update tau
                    m_graph.updateTau(r, s, dEvaporation + dDeposition);
                }
            }
        }
    }
}
