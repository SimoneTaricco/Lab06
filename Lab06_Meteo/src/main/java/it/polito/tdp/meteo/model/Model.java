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
		int costo = sommaCosto(parziale,livello);
		
		System.out.println(parziale);
		
		//alla fine dello scorrimento della lista
		if (livello==partenza.size()) {			
			if (costo<costoSoluzioneMigliore && tracciaCitta.size() == numeroCitta && parziale.size() >= soluzioneMigliore.size()) {
				System.out.println(parziale);
				soluzioneMigliore = new ArrayList<>(parziale);
				costoSoluzioneMigliore = costo;
			}
			return;
		}	
		
		if (!aggiuntaValida(partenza.get(livello),parziale)) {
			return;
		} 
					
		parziale.add(partenza.get(livello));
		//tracciaCitta.put(partenza.get(livello).getLocalita(), partenza.get(livello));
		creaCombinazione(parziale,livello+1,tracciaCitta,numeroCitta);
		
		parziale.remove(partenza.get(livello));
		//tracciaCitta.remove(partenza.get(livello).getLocalita());
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
	
	private boolean aggiuntaValida(Rilevamento prova, List<Rilevamento> parziale) {
		
		//verifica giorni massimi
		//contiamo quante volte la città 'prova' era già apparsa nell'attuale lista costruita fin qui
		int conta = 0;
		for (Rilevamento precedente:parziale) {
			if (precedente.getLocalita().equals(prova.getLocalita()))
				conta++; 
		}
		if (conta >= NUMERO_GIORNI_CITTA_MAX)
			return false;
		
		/*if (parziale.size() > 0 && prova.getData().equals(parziale.get(parziale.size()-1).getData()))
			return false;*/
		
		// verifica dei giorni minimi
		if (parziale.size()==0) //primo giorno posso inserire qualsiasi città
				return true;
		if (parziale.size()==1 || parziale.size()==2) {
			//siamo al secondo o terzo giorno, non posso cambiare
			//quindi l'aggiunta è valida solo se la città di prova coincide con la sua precedente
			System.out.println("Size: 1");
			if (prova.getLocalita().equals(parziale.get(parziale.size()-1).getLocalita()))
				return true;
			else
				return false; 
		}
		
		//nel caso generale, se ho già passato i controlli sopra, non c'è nulla che mi vieta di rimanere nella stessa città
		//quindi per i giorni successivi ai primi tre posso sempre rimanere
		if (parziale.get(parziale.size()-1).getLocalita().equals(prova.getLocalita()))
			return true; 
		
		// se cambio città mi devo assicurare che nei tre giorni precedenti sono rimasto fermo 
		if (parziale.get(parziale.size()-1).getLocalita().equals(parziale.get(parziale.size()-2).getLocalita()) 
		&& parziale.get(parziale.size()-2).getLocalita().equals(parziale.get(parziale.size()-3).getLocalita()))
			return true;
			
		return false;
		
	}
	
	
	

}