package as;

import java.io.Serializable;
import java.util.Vector;

public class DoubleMatrix implements Serializable {
	private static final long serialVersionUID = 1L;
	Vector<Vector<Double>> data; 

	public DoubleMatrix(double[][] dist) {
		setValues(dist);
	}
	private void setValues(double[][] dist) {
		int nodes=dist[0].length;
		data= new Vector<Vector<Double>>();
		for (int r=0; r<nodes; r++) {
			Vector<Double> row= new Vector<Double>();  //row
			for (int k=r+1; k<nodes; k++) 
				row.add(new Double(dist[r][k]));
			data.add(row);
		}		
	}
	public DoubleMatrix() {
		double [][] proba= { {0.0, 0.1, 0.2}, {0.1, 0.0, 1.2}, {0.2, 1.2, 0.0 }};
		setValues(proba);
	}
	public DoubleMatrix(Vector<Vector<Double>> dd) {
		data=dd;
	}
	
	public double[][] toArray() {
		int nodes=data.size();
		double[][] res= new double[nodes][]; // the rows
		for (int r=0; r<nodes; r++)
			res[r]=new double[nodes]; // the columns
		for (int k=0; k<nodes; k++)
			res[k][k]=0.0;
		for (int r=0; r<nodes; r++) {
			for (int k=r+1, vk=0; k<nodes; k++, vk++) {
				Double d= data.get(r).get(vk);
				res[r][k]=d;
				res[k][r]=d;
			}
		}
		return(res);
	}
	public boolean isEqual(double [][] arr) {
		double [][] ja=toArray();
		int nodes=arr.length;
		for (int r=0; r<nodes; r++)
			for (int k=0; k<nodes; k++)
				if (arr[r][k]!=ja[r][k])
					return(false);
		return(true);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double [][] proba= { {0.0, 0.1, 0.2}, {0.1, 0.0, 1.2}, {0.2, 1.2, 0.0 }};
		DoubleMatrix dist= new DoubleMatrix(proba);
		if (dist.isEqual(proba)) 
			System.out.print("OK");
		else
			System.out.print("ERR");
		System.exit(9);
	}


}
