package projects.defaultProject.nodes.nodeImplementations.manager;

import java.lang.Thread.State;
import java.util.ArrayList;

import projects.defaultProject.nodes.messages.CheckCoordinatorStatus;
import projects.defaultProject.nodes.messages.CheckCoordinatorStatus.AliveCoordMensage;
import projects.defaultProject.nodes.messages.LeaderMensage;
import projects.defaultProject.nodes.messages.LeaderMensage.MensageType;
import projects.defaultProject.nodes.messages.SimulatorMenssage;
import projects.defaultProject.nodes.nodeImplementations.MobileDevice;
import projects.defaultProject.nodes.nodeImplementations.MobileDevice.Status;
import sinalgo.nodes.timers.Timer;

public class CoordinatorChecker extends Thread{
	MobileDevice n;
	int lastTime;
	public CoordinatorChecker(MobileDevice n){
		this.n= n;
		lastTime = -1;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			Timer t = new Timer() {
				
				@Override
				public void fire() {
					// TODO Auto-generated method stub
				}
			};
			if(n.ID!=n.coord && (n.status==Status.Normal || n.status==Status.Reorganiz)){
				lastTime = n.time;
				n.broadcast(new CheckCoordinatorStatus(n.ID,n.coord, n.time));				
			}
			t.startRelative(MobileDevice.TIMETORESPONSE, n);
			if(lastTime==-1)
				continue;
			
			ArrayList<SimulatorMenssage> msg = n.received(CheckCoordinatorStatus.class,lastTime);
			lastTime = -1;
			
			if((n.status==Status.Normal || n.status==Status.Reorganiz) && 
					(msg ==null || ((CheckCoordinatorStatus)msg.get(0)).menssage==AliveCoordMensage.AYC_NOT)){
				n.recover();
			}
		}
	}
}
