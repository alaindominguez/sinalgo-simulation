package projects.sample1.nodes.messages;


public class Prepare_Req_Response extends PaxosMenssage{
	
	public Phase1RequestType menssage;
	
	public enum Phase1RequestType{ACCEPTED, NON_ACCEPTED}
	
	@Override
	public Prepare_Req_Response clone(){
		return this;
	}
	
	public Prepare_Req_Response(int from, int directedTo, int time, Phase1RequestType menssage){
		super(from, directedTo, time);
		this.menssage = menssage;
	}
}
