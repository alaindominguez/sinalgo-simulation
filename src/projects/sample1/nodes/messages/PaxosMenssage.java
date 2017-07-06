package projects.sample1.nodes.messages;

import java.util.ArrayList;

import sinalgo.nodes.messages.Message;

public abstract class PaxosMenssage extends Message{
	int from;
	int directedTo;
	int time;
	ArrayList<Integer> lastNodes;
	public PaxosMenssage(int from, int directedTo, int time){
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
	@Override 
	public String toString(){
		return super.toString()+" time: "+time+" from: "+from+" to: "+directedTo+"---";
	}
	public void setVisited(int ID){
		lastNodes.add(ID);
	}
}
