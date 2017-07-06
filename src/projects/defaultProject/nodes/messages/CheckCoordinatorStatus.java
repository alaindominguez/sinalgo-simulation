package projects.defaultProject.nodes.messages;

public class CheckCoordinatorStatus extends SimulatorMenssage{
	public enum AliveCoordMensage{
		AYC_There, AYC_YES, AYC_NOT
	}
	public AliveCoordMensage menssage;
	public CheckCoordinatorStatus(int from, int directedTo, int time){
		super(from,directedTo, time);
		menssage = AliveCoordMensage.AYC_There;
	}
	private CheckCoordinatorStatus(int from, int directedTo, int time, AliveCoordMensage menssage){
		super(from,directedTo, time);
		this.menssage = menssage;
	}
	public CheckCoordinatorStatus clone(){
		return this;
	}
	public CheckCoordinatorStatus reponseCoordinator(AliveCoordMensage menssage, int node){
		return new CheckCoordinatorStatus(node, from, time, menssage);
	}
}
