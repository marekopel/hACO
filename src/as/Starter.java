package as;

import javax.swing.JTextField;

public class Starter implements Runnable, ClockWork {
	protected int interTime=1000;
	protected int limit=10;
	protected int current=0;
	protected JTextField infoText =null; 
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while ((interTime=getInterval()) > 0) {
//			while (interval > 0) {
			try {
				doMyWork();
				Thread.sleep(interTime);
				//System.err.print("Limit="+limit+" ");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
	}
	public void doMyWork() {
		if (++current>=limit) {
			current=0;
			System.err.print("!\n");
		} else
			System.err.print(current+" ");
		if (infoText!=null) {
			infoText.setText(""+current);
		}
	}
	
	@Override
	public int getInterval() {
		// TODO Auto-generated method stub
		return interTime;
	}
	@Override
	public void setInterval(int inter) {
		// TODO Auto-generated method stub
		interTime=inter;
	}
	@Override
	public void setDispData(JTextField fld) {
		// TODO Auto-generated method stub
		infoText=fld;
	}
	public static void main(String[] args) {
		Starter st= new Starter();
		st.setInterval(100);
		Runnable runnable = st;
		Thread thread = new Thread(runnable);
		thread.start();		
		st.setInterval(1000);
		
	}
}
