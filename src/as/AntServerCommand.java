package as;

import java.io.Serializable;

public enum AntServerCommand implements Serializable {
	WORK, 
	SLEEP, // no monitor data
	STOP,  
	EMPTYRUN; // just monitor for transmission time
	public static AntServerCommand toCommand(String s) {
		s=s.toUpperCase().trim();
		if (s.equals("WORK"))
			return(WORK);
		if (s.equals("SLEEP"))
			return(SLEEP);
		if (s.equals("EMPTYRUN"))
			return(EMPTYRUN);
		if (s.equals("STOP"))
			return(STOP);
		
		/*
		switch(s) {
		case "WORK": return(WORK);
		case "SLEEP": return(SLEEP);
		case "EMPTYRUN": return(EMPTYRUN);
		case "STOP": return(STOP);
		}
		*/
		return(STOP);
	}
}
