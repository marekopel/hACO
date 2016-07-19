package as;

import java.util.Random;
import java.util.Vector;

import runs.AntCommunityServer;

public class CargoPool {
	protected AntCommunityCargo pool[];
	protected double vals [];

	protected boolean randomMode=false;
	protected int currentItem=0;

	protected Vector<AntCommunityCargo> fifo= null;
	private Random rnd= new Random();

	public CargoPool(int size, boolean random) {
		pool = new AntCommunityCargo[size];
		vals = new double[size];
		for (int k=0; k<size; k++) {
			pool[k]=null;
			vals[k]=Double.MAX_VALUE;
		}
		randomMode=random;
	}
	public CargoPool() {
		fifo= new Vector<AntCommunityCargo>();
	}

	protected AntCommunityCargo getFifoCargo() throws Exception {
		if (fifo.size()==0)
			throw new Exception("no cargo items");
		AntCommunityCargo result = fifo.get(0);
		fifo.remove(0);
		return(result);
	}
	public AntCommunityCargo getCargo() throws Exception {
		if (fifo!=null) {
			return(getFifoCargo());
		}
		int idx=0;
		int filledItems=0;
		for (int k=0; k<pool.length; k++)
			if (pool[k]!=null)
				filledItems++;
		if (filledItems==0)
			throw new Exception("no cargo items");
		if (filledItems==1)
			return(pool[idx]);
		if (randomMode) {
			idx= (int) rnd.nextDouble()*filledItems;
		} else {
			idx=currentItem % filledItems;
			currentItem++;
		}
		return(pool[idx]);
	}
	protected void setVals() {
		for (int k=0; k<pool.length; k++) {
			if (pool[k]!=null) 
				vals[k]=pool[k].getBSFdistance();
		}
	}
	protected int bestIndex() {
		int best=-1;
		double bestV=Double.MAX_VALUE;
		for (int k=0; k<vals.length; k++) {
			if (vals[k]<bestV) {
				best=k;
				bestV=vals[k];
			}
		}
		return(best);
	}
	protected int worstIndex() {
		int worst=0;
		for (int k=0; k<vals.length; k++)
			if (vals[k]==Double.MAX_VALUE)
				return(k); 
		double worstV=vals[0];
		for (int k=1; k<vals.length; k++) {
			if (vals[k]>worstV) {
				worst=k;
				worstV=vals[k];
			}
		}
		return(worst);
	}
	protected boolean storeFifoCargo(AntCommunityCargo cc, String msg) {
		double bsf=Double.MAX_VALUE;
		fifo.add(cc);
		for (AntCommunityCargo c: fifo) {
			if (bsf>c.getBSFdistance())
				bsf=c.getBSFdistance();
		}
		if (bsf==Double.MAX_VALUE)
			msg+="\tdouble.MAX\n";
		else
			msg+="\tbsf\t["+AsUtilities.double2String(bsf,4)+"]\n";
		AntCommunityServer.theLogger.severe(msg);
		return(true);
	}
	public boolean storeCargo(AntCommunityCargo cc) {

		boolean result=false;
		double currentDist=cc.getBSFdistance();
		String msg="CargoPool input: \t";
		if (currentDist==Double.MAX_VALUE)
			msg+="empty cargo";
		else
			msg+=AsUtilities.double2String(currentDist,5);
		if (fifo!=null) {
			return(storeFifoCargo(cc, msg));
		}
		setVals();
		int idx=worstIndex();
		double worstVal=vals[idx];
		if (worstVal>=currentDist) {
			pool[idx]=cc;
			if (worstVal==Double.MAX_VALUE)
				msg+="\tThe first one";
			else
				msg+="\treplaces\t"+AsUtilities.double2String(worstVal,5);
			result=true;			
		} else {
			msg+="\tignored";
		}
		setVals();
		idx=bestIndex();
		if (idx>=0)
			msg+="\t["+AsUtilities.double2String(vals[idx],5)+"]\n";
		else
			msg+="\t[Initialization]\n";

		AntCommunityServer.theLogger.severe(msg);
		return(result);
	}
	/*
	public boolean storeCargoOld(AntCommunityCargo cc) {
		double currentDist=cc.getBSFdistance();
		double dispDist=currentDist;
		if (dispDist==Double.MAX_VALUE)
			dispDist=-1;
		String msg="CargoPool input: \t"+AsUtilities.double2String(dispDist,5);
		//AntCommunityServer.theLogger.severe("");
		if (filledItems==0 && useJustOne) {
			pool[filledItems++]=cc;
			AntCommunityServer.theLogger.severe(msg+"\tthe first and the olny One\n");
			return(true);
		}

		if (useJustOne) {
			AntCommunityServer.theLogger.severe(msg+"\tignored\n");
			return(true);
		}
		if (filledItems<pool.length) {
			AntCommunityServer.theLogger.severe(msg+"\trookie\n");			
			pool[filledItems++]=cc;
			return(true);
		}
		int badPos=0;
		double badLen=pool[0].getBSFdistance();
		for (int i=1; i<filledItems; i++) {
			// searching for the worst solution
			if (pool[i].getBSFdistance()<badLen) {
				badLen=pool[i].getBSFdistance();
				badPos=i;
			}
		}
		if (badLen>currentDist) {
			pool[badPos]=cc;
			AntCommunityServer.theLogger.severe(msg+"\treplacing: "+
			badLen+"\tby "+currentDist+"\n");						
			return(true);
		}
		AntCommunityServer.theLogger.severe(msg+"\tnot good enough, ignored: "
		+currentDist+"\n");
		return(false);
	}
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
