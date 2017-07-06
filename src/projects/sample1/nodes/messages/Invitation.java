package projects.sample1.nodes.messages;

import projects.sample1.nodes.messages.NewCoordinatorResponseMenssage;

public class Invitation extends SimulatorMenssage{
	public enum InvitationType{INVITE, ACCEPT, ACCEPTED_ANS}
	InvitationType menssage;
	public Invitation(int from, int directedTo, int time){
		super(from, directedTo, time);
		menssage = InvitationType.INVITE;
	}
	private Invitation(int from, int directedTo, int time,InvitationType menssage){
		super(from, directedTo, time);
		this.menssage = menssage;
	}
	@Override
	public Invitation clone(){
		return this;
	}
	public InvitationResponse reponseCoordinator(int node){
		return new InvitationResponse(node, from, time);
	}
}
