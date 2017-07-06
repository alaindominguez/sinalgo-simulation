package projects.defaultProject.nodes.messages;

import projects.defaultProject.nodes.messages.NewCoordinatorMenssage.NewCoordMensage;

public class ReadyMenssage extends SimulatorMenssage{
	public enum ReadyType{
		READY, READY_ANS
	}
	
	public ReadyType menssage;
	
	public ReadyMenssage(int from, int directedTo, int time){
		super(from,directedTo, time);
		menssage = ReadyType.READY;
	}
	public ReadyMenssage(int from, int directedTo, int time, ReadyType menssage){
		super(from,directedTo, time);
		this.menssage = menssage;
	}
	
	@Override
	public ReadyMenssage clone(){
		return this;
	} 
	public ReadyMenssage reponseCoordinator(ReadyType menssage, int node){
		return new ReadyMenssage(node, from, time, menssage);
	}
}
