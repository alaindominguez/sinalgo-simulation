package projects.sample1.nodes.nodeImplementations.manager;

import java.util.ArrayList;

public class TransactionManager {
	ArrayList<Integer> coordinators = new ArrayList<>();
	ArrayList<String> transactions = new ArrayList<>();
	
	int index=0;
	int transactionNumber=0;
	
	public TransactionManager(){
		for(int i=1;i<5000;i++)
			transactions.add("transaction "+i);
	}
	
	public void removeCoordinator(Integer coord){
		if(coordinators.contains(coord)){
			coordinators.remove(coord);
		}
	}
	public void addCoordinator(Integer coord){
		coordinators.add(coord);
	}	
	public boolean haveMoreTransaction(){
		return transactionNumber<transactions.size();
	}
	public String getTransaction(){
		return transactions.get(transactionNumber);
	}
	public void commit(Object transaction){
		if(((String)transaction).equalsIgnoreCase("transaction "+(transactionNumber+1)))
			transactionNumber++;
		//System.out.println(transactionNumber);
	} 
}
