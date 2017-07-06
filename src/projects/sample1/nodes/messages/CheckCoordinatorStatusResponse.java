package projects.sample1.nodes.messages;

public class CheckCoordinatorStatusResponse extends SimulatorMenssage{
	public enum AliveCoordMensage{
		AYC_There, AYC_YES, AYC_NOT
	}
	public AliveCoordMensage menssage;
	public CheckCoordinatorStatusResponse(int from, int directedTo, int time, AliveCoordMensage menssage){
		super(from,directedTo, time);
		this.menssage = menssage;
	}
	public CheckCoordinatorStatusResponse clone(){
		return this;
	}
}
