package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.polito.tdp.meteo.DAO.MeteoDAO;


public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private List<Rilevamento> partenza = new ArrayList<Rilevamento>();
	private List<Rilevamento> soluzioneMigliore;
	private double costoSoluzioneMigliore;
	
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
	
	
	public String trovaSequenza(int mese) {
		
		String res = "";
		
		ArrayList<Rilevamento> parziale = new ArrayList<Rilevamento>();
		soluzioneMigliore = new ArrayList<Rilevamento>();
		costoSoluzioneMigliore = 10000;
		HashMap<String,Rilevamento> tracciaCitta = new HashMap<String,Rilevamento>();
		int numeroCitta = meteoDAO.getAllLocalita().size();
		
		partenza.addAll(this.meteoDAO.getAllRilevamentiMese(mese));	
		
		this.creaCombinazione(parziale,0,tracciaCitta,numeroCitta);
		
		for(Rilevamento r:soluzioneMigliore){
			res += r.getLocalita() + "\n";
		}

		return res;
	}
	
	private void creaCombinazione(ArrayList<Rilevamento> parziale, int livello, HashMap<String,Rilevamento> tracciaCitta, int numeroCitta) {  
		
		// Livello = indice nell'insieme partenza
		int costo = 0;
		costo = sommaCosto(parziale,livello);	
		int numerovisite;
		
		int contatoreCitta = 0;	
				
		if (livello == partenza.size() && costo<costoSoluzioneMigliore && tracciaCitta.size() == numeroCitta && parziale.size()>= soluzioneMigliore.size()) {
			//System.out.println(parziale);
			soluzioneMigliore = new ArrayList<>(parziale);
			System.out.println(costo);
			costoSoluzioneMigliore = costo;
			numerovisite = parziale.size();
		}
		
		//alla fine dello scorrimento della lista
		if (livello==partenza.size()) {
			return;
		}		
		

		if (parziale.size()>0){
			
			//fa in modo che non si considerino due date dello stesso giorno
			while (parziale.get(parziale.size()-1).getData().equals(partenza.get(livello).getData()) && livello<partenza.size()-1) {
				livello++;
			}
		
			//non ci si può spostare prima di tre giorni
			while(partenza.get(livello).getData().getDate()-parziale.get(parziale.size()-1).getData().getDate()<3 && livello<partenza.size()-1)
				livello++;
		}
					
		
		parziale.add(partenza.get(livello));
		tracciaCitta.put(partenza.get(livello).getLocalita(), partenza.get(livello));
		creaCombinazione(parziale,livello+1,tracciaCitta,numeroCitta);
		
		parziale.remove(partenza.get(livello));
		tracciaCitta.remove(partenza.get(livello).getLocalita());
		creaCombinazione(parziale,livello+1,tracciaCitta,numeroCitta);		
	}
	
	public int numeroCitta() {
		return meteoDAO.getAllLocalita().size();
	}
	
	public int sommaCosto(ArrayList<Rilevamento> parziale, int livello) {
		
		int somma = 0;
		
		for (Rilevamento r:parziale)
			somma += r.getUmidita();
		
		if(parziale.size()>1) {
			if (!parziale.get(parziale.size()-1).getLocalita().equals(parziale.get(parziale.size()-2).getLocalita()))
				somma += 100;
		}	
		
		return somma;
	}
	
	
	

}
