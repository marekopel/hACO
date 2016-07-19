package as;

import java.io.Serializable;
import java.util.Vector;

public class OneRunDescription implements Serializable {

	private static final long serialVersionUID = 1L;
	private String colonyName;
	private long startTime;
	private long durationTime;
	private boolean oldFeromons;
	private Vector<Vector<Integer>> bestRoutes;
	private Double bestRouteLength;

	public OneRunDescription() {
		colonyName="Anna";
		startTime=System.currentTimeMillis();
		durationTime=10;
		oldFeromons=true;
		bestRouteLength=1.1;
		bestRoutes= new Vector<Vector<Integer>>();
		bestRoutes.add(new Vector<Integer>());
		bestRoutes.get(0).add(1);
	}

	public String getColonyName() {
		return colonyName;
	}

	public void setColonyName(String colonyName) {
		this.colonyName = colonyName;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getDurationTime() {
		return durationTime;
	}

	public void setDurationTime(long durationTime) {
		this.durationTime = durationTime;
	}

	public boolean isOldFeromons() {
		return oldFeromons;
	}

	public void setOldFeromons(boolean oldFeromons) {
		this.oldFeromons = oldFeromons;
	}

	public Vector<Vector<Integer>> getBestRoutes() {
		return bestRoutes;
	}

	public void setBestRoutes(Vector<Vector<Integer>> bestRoutes) {
		this.bestRoutes = bestRoutes;
	}

	public Double getBestRouteLength() {
		return bestRouteLength;
	}

	public void setBestRouteLength(Double bestRouteLength) {
		this.bestRouteLength = bestRouteLength;
	}
}
