package acs;

import java.io.Serializable;

public class AntGraph implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// as20_02
	protected double[][] m_delta;
    protected double[][] m_tau;
    
    protected int        m_nNodes;
    protected double     m_dTau0;
 
    
    public AntGraph(int nNodes, double[][] delta, double[][] tau)
    {
        if(delta.length != nNodes)
            throw new IllegalArgumentException("The number of nodes doesn't match with the dimension of delta matrix");
        
        m_nNodes = nNodes;
        m_delta = delta;
        m_tau   = tau;
    }
    // as02_20
    public void setDistances(double [][] distantDistances) {
    	int size=distantDistances[0].length;
    	m_delta= new double[size][size];
    	for (int r=0; r<size; r++)
    		for (int c=0; c<size; c++)
    			m_delta[r][c]=distantDistances[r][c];
    }
    public AntGraph(int nodes, double[][] delta)
    {
        this(nodes, delta, new double[nodes][nodes]);
        
        resetTau();
    }
    
    // as02_20
    public double[][] getFeromons() {
    	return (m_tau);
    }
    public synchronized double delta(int r, int s)
    {
        int k=m_delta.length;
        k=m_delta[0].length;
        k=k+k;
    	return m_delta[r][s];
    }
    
    public synchronized double tau(int r, int s)
    {
        return m_tau[r][s];
    }
    
    public synchronized double etha(int r, int s)
    {
        return ((double)1) / delta(r, s);
    }
    
    public synchronized int nodes()
    {
        return m_nNodes;
    }
    
    public synchronized double tau0()
    {
        return m_dTau0;
    }
    
    public synchronized void updateTau(int r, int s, double value)
    {
        m_tau[r][s] = value;
    }
    
    public void resetTau()
    {
        double dAverage = averageDelta();
        
        m_dTau0 = (double)1 / ((double)m_nNodes * (0.5 * dAverage));
        //as02_20
        //System.out.println("Average: " + dAverage);
        //System.out.println("Tau0: " + m_dTau0);
        
        for(int r = 0; r < nodes(); r++)
        {
            for(int s = 0; s < nodes(); s++)
            {
                m_tau[r][s] = m_dTau0;
            }
        }
    }
    
    public double averageDelta()
    {
        return average(m_delta);
    }
    
    public double averageTau()
    {
        return average(m_tau);
    }

    public String toString()
    {
        String str = "";
        String str1 = "";
        
        
        for(int r = 0; r < nodes(); r++)
        {
            for(int s = 0; s < nodes(); s++)
            {
                str += delta(r,s) + "\t";
                str1 += tau(r,s) + "\t";
            }
            
            str+= "\n";
        }
        
        return str + "\n\n\n" + str1;
    }
    
    // as 20_02_2016 
    public static double average(double matrix[][])
    {
        int k=matrix.length;
    	double dSum = 0;
        for(int r = 0; r < k; r++)
        {
            for(int s = 0; s < k; s++)
            {
                dSum += matrix[r][s];
            }
        }
        
        double dAverage = dSum / (double)(k * k);
        return dAverage;
    }
    // as20_02
    public  String getVisitCard() {
    	return(""+average(m_delta));
    }
}

