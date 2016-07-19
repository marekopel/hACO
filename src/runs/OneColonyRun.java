package runs;

import java.io.FileReader;
import java.util.Vector;

import as.MyProperties;

public class OneColonyRun {

	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//AntCommunityCargo cargo;
		MyProperties serverProp;
		String serPropName="AntCommunity.prop";
		serverProp= new MyProperties();
		FileReader fr= new FileReader(serPropName);
		serverProp.load(fr);
		fr.close();
		int repetitionNo=serverProp.getInteger("nRuns", 1);
		for (int i=0; i<repetitionNo; i++) {
			/*
			cargo= new AntCommunityCargo(serverProp);
			GenericAntClient cl= new GenericAntClient();
			cl.executeOneColonyRunPop(cargo);
			Vector<String> infos=cargo.get4Log();
			for (String s: infos) {
				System.out.println(i+"=>"+s);
			}
			*/
			TSPTest t= new TSPTest();
			Vector<String> s=t.baza(args);
			System.out.print(i+"\t"+s+"\n");
		}
		System.err.print("End of Work\n");
		System.exit(0);
	}
}
