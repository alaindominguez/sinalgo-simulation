package projects.sample1.nodes.messages;

import projects.sample1.nodes.messages.Prepare_Req_Response.Phase1RequestType;

public class QuorumRequestResponse extends PaxosMenssage{
	
	public Phase1RequestType menssage;
	
	//public enum Phase1RequestType{ACCEPTED, NON_ACCEPTED}	
	
	public QuorumRequestResponse(int from, int directedTo, int time, Phase1RequestType menssage){
		super(from, directedTo, time);
		this.menssage = menssage;
	}
	
	@Override
	public QuorumRequestResponse clone(){
		return this;
	}
}
