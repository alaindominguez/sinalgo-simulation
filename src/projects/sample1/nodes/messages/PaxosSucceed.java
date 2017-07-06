package projects.sample1.nodes.messages;

public class PaxosSucceed extends PaxosMenssage{
	public PaxosSucceed(int from, int directedTo, int time){
		super(from, directedTo, time);
	}
	
	@Override
	public PaxosSucceed clone(){
		return this;
	}
}
