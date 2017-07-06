package projects.sample1.nodes.messages;

import projects.sample1.nodes.messages.NewCoordinatorResponseMenssage.NewCoordMensage;

public class NewCoordinatorResponseMenssage extends SimulatorMenssage{
	public enum NewCoordMensage{
		AYC_COORD, AYC_COORD_YES, AYC_COORD_NOT
	}
	public int coord;
	public NewCoordMensage menssage;
	
	public NewCoordinatorResponseMenssage(int from, int directedTo, int time, int cood){
		super(from,directedTo, time);
		menssage = NewCoordMensage.AYC_COORD_YES;
		this.coord = coord;
	}
	public NewCoordinatorResponseMenssage(int from, int directedTo, int time, NewCoordMensage menssage, int cood){
		super(from,directedTo, time);
		this.menssage = menssage;
		this.coord = coord;
	}
	@Override
	public NewCoordinatorResponseMenssage clone(){
		return this;
	} 
}
