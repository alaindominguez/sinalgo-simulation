package projects.sample1.nodes.messages;

import projects.sample1.nodes.messages.Invitation.InvitationType;

public class InvitationResponse extends SimulatorMenssage{
	public enum InvitationRequestType{ACCEPTED, NON_ACCEPTED}
	public InvitationRequestType menssage;
	public InvitationResponse(int from, int directedTo, int time){
		super(from, directedTo, time);
		menssage = InvitationRequestType.ACCEPTED;
	}
	public InvitationResponse(int from, int directedTo, int time, InvitationRequestType menssage){
		super(from, directedTo, time);
		menssage = InvitationRequestType.ACCEPTED;
	}
	
	@Override
	public InvitationResponse clone(){
		return this;
	}
}
