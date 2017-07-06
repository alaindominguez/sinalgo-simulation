package projects.sample1.nodes.messages;

import projects.sample1.nodes.messages.NewCoordinatorResponseMenssage.NewCoordMensage;

public class NewCoordinatorMenssage extends SimulatorMenssage {
	
	
	public int coord;
	
	public NewCoordinatorMenssage(int from, int directedTo, int time, int cood){
		super(from,directedTo, time);
		this.coord = coord;
	}
	
	@Override
	public NewCoordinatorMenssage clone(){
		return this;
	} 
	public NewCoordinatorResponseMenssage reponseCoordinator(NewCoordMensage menssage, int node, int coord){
		return new NewCoordinatorResponseMenssage(node, from, time, menssage, coord);
	}
	
	
}
