package projects.sample1.nodes.messages;

import java.util.ArrayList;

import sinalgo.nodes.messages.Message;

public class LeaderMensage extends Message{
	public enum MensageType{
		AYNORMAL, AYN_Replay, AYC, AYC_YES, NONE
	}
	ArrayList<Integer> lastNodes;
	MensageType mensage;
	int time;
	int sourceNode;
	int destiny;
	public LeaderMensage(MensageType mensage, int time, int destiny, int sourceNode){
		this.mensage = mensage;
		this.time=time;
		this.sourceNode = sourceNode;
		this.destiny = destiny;
		this.lastNodes = new ArrayList<>();
	}
	public void setNewNode(int ID){
		lastNodes.add(ID);
	}
	public boolean isVisited(int ID){
		for(Integer i : lastNodes)
			if(i.intValue()==ID)
				return true;
		return false;
	}
	public LeaderMensage clone(){
		return this;
	}
	public int getDestiny(){
		return destiny;
	}
	public int getTime(){
		return time;
	}
	public int getSourceNode(){
		return sourceNode;
	}
	public MensageType getMensage(){
		return mensage;
	}
}
