package projects.sample1.nodes.messages;

import projects.sample1.nodes.messages.Prepare_Req_Response.Phase1RequestType;

public class Prepare_Req extends PaxosMenssage{
	public Object target;
	public Prepare_Req(int from, int directedTo, int time, Object target){
		super(from, directedTo, time);
		this.target = target;
	}
	
	@Override
	public Prepare_Req clone(){
		return this;
	}
	
	public Prepare_Req_Response reponseCoordinator(int node, Phase1RequestType menssage){
		return new Prepare_Req_Response(node, from, time, menssage);
	}
}
