package projects.sample1.nodes.nodeImplementations.manager;

import java.lang.Thread.State;
import java.util.ArrayList;

import projects.defaultProject.nodes.timers.MessageTimer;
import projects.sample1.nodes.messages.CheckCoordinatorStatus;
import projects.sample1.nodes.messages.CheckCoordinatorStatusResponse;
import projects.sample1.nodes.messages.CheckCoordinatorStatusResponse.AliveCoordMensage;
import projects.sample1.nodes.messages.LeaderMensage;
import projects.sample1.nodes.messages.LeaderMensage.MensageType;
import projects.sample1.nodes.messages.SimulatorMenssage;
import projects.sample1.nodes.nodeImplementations.MobileDevice;
import projects.sample1.nodes.nodeImplementations.MobileDevice.Status;
import sinalgo.nodes.timers.Timer;

public class CoordinatorChecker{
	MobileDevice n;
	int lastTime;
	int lastCoord;
	boolean waiting=false;
	Timer t ;
	public CoordinatorChecker(MobileDevice n){
		this.n= n;
		lastTime = -1;
		lastCoord = -1;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
		if(n.ID!=n.coord && (n.status==Status.Normal || n.status==Status.Reorganiz)){
			//n.nodeLog.logln("Intentando contactar el coordinador");
			
			lastTime = n.time;
			lastCoord = n.coord;
			
			t = new Timer() {
				
				@Override
				public void fire() {
					// TODO Auto-generated method stub
					//n.nodeLog.logln(lastTime+" -- "+lastCoord);
					ArrayList<SimulatorMenssage> msg = n.received(CheckCoordinatorStatusResponse.class,lastTime);
					lastTime = -1;
					
					if((n.status==Status.Normal || n.status==Status.Reorganiz) 
							&& lastCoord==n.coord &&
							(msg ==null || ((CheckCoordinatorStatusResponse)msg.get(0)).menssage==AliveCoordMensage.AYC_NOT)){
						n.nodeLog.logln(n.coord+" Coordinador no encontrado node: "+n.ID+" time: "+n.time);
						n.recover();
					}
					else if(n.status==Status.Normal || n.status==Status.Reorganiz){
						//n.nodeLog.logln("Chequeo de coordinador");
					}
					waiting = false;
				}
			};
			
			n.broadcast(new CheckCoordinatorStatus(n.ID,n.coord, n.time));
			//MessageTimer tim = new MessageTimer(new CheckCoordinatorStatus(n.ID,n.coord, n.time));
			//tim.startRelative(1, n);
			waiting = true;
			t.startRelative(MobileDevice.TIMETORESPONSE, n);
			/*if(lastTime==-1)
				continue;*/
			
		}
	}
	public boolean isExecuting(){ return waiting;}
}
