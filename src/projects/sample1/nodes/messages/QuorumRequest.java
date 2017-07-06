package projects.sample1.nodes.messages;

import projects.sample1.nodes.messages.Prepare_Req_Response.Phase1RequestType;

public class QuorumRequest extends PaxosMenssage{
	public Object target;
	public QuorumRequest(int from, int directedTo, int time, Object target){
		super(from, directedTo, time);
		this.target = target;
	}
	
	@Override
	public QuorumRequest clone(){
		return this;
	}
	
	public QuorumRequestResponse reponseCoordinator(int node, Phase1RequestType menssage){
		return new QuorumRequestResponse(node, from, time, menssage);
	}
}
