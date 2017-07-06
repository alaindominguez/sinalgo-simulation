package projects.sample1.nodes.messages;

import projects.sample1.nodes.messages.CheckCoordinatorStatusResponse.AliveCoordMensage;

public class CheckCoordinatorStatus extends SimulatorMenssage{
	
	public CheckCoordinatorStatus(int from, int directedTo, int time){
		super(from,directedTo, time);
	}
	private CheckCoordinatorStatus(int from, int directedTo, int time, AliveCoordMensage menssage){
		super(from,directedTo, time);
	}
	public CheckCoordinatorStatus clone(){
		return this;
	}
	public CheckCoordinatorStatusResponse reponseCoordinator(AliveCoordMensage menssage, int node){
		return new CheckCoordinatorStatusResponse(node, from, time, menssage);
	}
}
