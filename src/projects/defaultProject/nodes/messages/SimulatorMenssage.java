package projects.defaultProject.nodes.messages;

import java.util.ArrayList;

import sinalgo.nodes.messages.Message;

public abstract class SimulatorMenssage extends Message{
	int from;
	int directedTo;
	int time;
	ArrayList<Integer> lastNodes;
	public SimulatorMenssage(int from, int directedTo, int time){
		this.from = from;
		this.directedTo = directedTo;
		this.time = time;
		
		lastNodes = new ArrayList<>();
		lastNodes.add(from);
	}
	public int getDestiny(){
		return directedTo;
	}
	public int getTime(){
		return time;
	}
	public int getSourceNode(){
		return from;
	}
	public boolean isVisited(int ID){
		for(Integer i : lastNodes)
			if(i.intValue()==ID)
				return true;
		return false;
	}
	public void setVisited(int ID){
		lastNodes.add(ID);
	}
}
