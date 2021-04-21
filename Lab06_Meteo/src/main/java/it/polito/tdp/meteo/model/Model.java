package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private MeteoDAO meteoDAO;

	public Model() {
		this.meteoDAO = new MeteoDAO();
	}

	public String getUmiditaMedia(int mese) {
		
		String result = "";
		
		for (String s:this.meteoDAO.getAllLocalita()) {
			result += s + String.format(" %.2f", this.meteoDAO.getAvgRilevamentiLocalitaMese(mese, s)) + "\n";			
		}
		
		return result.trim();
	}
	
	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {
		return "TODO!";
	}
	
	
	

}
