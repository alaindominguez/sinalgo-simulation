package projects.defaultProject.nodes.messages;

import projects.defaultProject.nodes.messages.CheckCoordinatorStatus.AliveCoordMensage;

public class NewCoordinatorMenssage extends SimulatorMenssage {
	public enum NewCoordMensage{
		AYC_COORD, AYC_COORD_YES, AYC_COORD_NOT
	}
	
	public NewCoordMensage menssage;
	
	public NewCoordinatorMenssage(int from, int directedTo, int time){
		super(from,directedTo, time);
		menssage = NewCoordMensage.AYC_COORD;
	}
	public NewCoordinatorMenssage(int from, int directedTo, int time, NewCoordMensage menssage){
		super(from,directedTo, time);
		this.menssage = menssage;
	}
	
	@Override
	public NewCoordinatorMenssage clone(){
		return this;
	} 
	public NewCoordinatorMenssage reponseCoordinator(NewCoordMensage menssage, int node){
		return new NewCoordinatorMenssage(node, from, time, menssage);
	}
	
}
