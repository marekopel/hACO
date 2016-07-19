package as;

import java.io.Serializable;

public class BasicRunParameters implements Serializable {
	private static final long serialVersionUID = -8706586585673399647L;
	protected Integer antNumber;
	protected Integer nodeNumber;
	protected Integer iterationNo;
	protected Double alpha;
	protected Double beta;
	public 	Double ro;
	public Double q0;
	protected boolean alwaysResetFeromons;
	public boolean eachIter=false;
	public boolean pathView=false;
	
	protected Integer bestRoutesNo; 
	
	public BasicRunParameters(int an, int iterno, double a, double b, double r, double q, int bestNo) {
		antNumber=an;
		iterationNo=iterno;
		alpha=a;
		beta= b;
		ro=r;
		q0=q;
		bestRoutesNo=bestNo;
	}
	public BasicRunParameters(BasicRunParameters src) {
		antNumber= new Integer(src.antNumber);
		nodeNumber= new Integer(src.nodeNumber);
		iterationNo= new Integer(src.iterationNo);
		alpha= new Double(src.alpha);
		beta = new Double(src.beta);
		ro =new Double(src.ro);
		q0 =new Double(src.q0);
		alwaysResetFeromons= src.alwaysResetFeromons;
		eachIter= src.eachIter;
		pathView= src.pathView;
	}
	public BasicRunParameters(MyProperties prop) {
		antNumber=prop.getInteger("nAnts", 10);
		iterationNo=prop.getInteger("nIterations",10);
		alpha=prop.getDouble("alpha", 0.2);
		beta= prop.getDouble("beta", 2.0);
		ro=prop.getDouble("ro", 0.2);
		q0=prop.getDouble("q0", 0.9);
		bestRoutesNo=prop.getInteger("bestRoutesNo", 10);
		alwaysResetFeromons= !prop.getBoolean("propagateFeromons", false);
		eachIter=prop.getBoolean("EachIter", false);
		pathView= prop.getBoolean("pathView", false);
	}
	public void setNNodes(int nn) {
		nodeNumber=nn;
	}
	public boolean getAlwaysResetFeromons() {
		return(alwaysResetFeromons);
	}
	public String getInfoCard() {
		String result="Ants: "+ antNumber;
		result+="\tNodes: "+nodeNumber;
		result+="\tA: "+alpha;
		result+="\tB: "+beta;
		result+="\tr: "+ro;
		result+="\tqo: "+q0;
		result+="\tIter: "+ iterationNo;
		result+="\t#Best: "+ bestRoutesNo;
		result+="\tfer. reset: "+alwaysResetFeromons;
		return(result);
	}
	public BasicRunParameters() {
		antNumber=78;
		iterationNo=123;
		alpha=0.22;
		beta=0.33;
		ro=0.21;
		q0=0.3;
	}
	public int getAntNumber() {
		return(antNumber);
	}
	public int getNodeNumber() {
		return(nodeNumber);
	}
	public int getIterationNumber() {
		return(iterationNo);
	}
	public int setantNumber(Integer i) {
		antNumber=i;
		return(1);
	}	
	public int setIterationNumber(Integer i) {
		iterationNo=i;
		return(1);
	}	
	public double getAlpha() {
		return(alpha);
	}
	public double getBeta() {
		return(beta);
	}
	public double getQ0() {
		return(q0);
	}
	public double getRo() {
		return(ro);
	}
}
