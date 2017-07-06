package projects.sample1.nodes.nodeImplementations.manager;

import java.util.ArrayList;

import projects.defaultProject.nodes.timers.MessageTimer;
import projects.sample1.nodes.messages.Invitation;
import projects.sample1.nodes.messages.NewCoordinatorMenssage;
import projects.sample1.nodes.messages.SimulatorMenssage;
import projects.sample1.nodes.messages.ReadyMenssage;
import projects.sample1.nodes.messages.ReadyMenssage.ReadyType;
import projects.sample1.nodes.nodeImplementations.MobileDevice;
import projects.sample1.nodes.nodeImplementations.MobileDevice.Status;
import sinalgo.nodes.timers.Timer;

public class InvitationManager {
	MobileDevice n;
	int lastTime;
	boolean waiting = false;
	public InvitationManager(MobileDevice n){
		this.n= n;
		lastTime = -1;
	}
	
	public void run() {
		// TODO Auto-generated method stub
			
			//t.startRelative(MobileDevice.TIMETORESPONSE, n);	
			
			ArrayList<SimulatorMenssage> msg = n.received(Invitation.class,-1);
			//n.nodeLog.logln("Analizando existencia de invitacion time: "+n.time+ " estado"+ ((n.status==Status.Normal)?" normal":" no normal"));
			if(n.status==Status.Normal && msg!=null){
				
				//n.nodeLog.logln("Invitacion recibida time: "+n.time);
				n.status = Status.Election;
				SimulatorMenssage tmp=null;
				int bigerTime=-1;
				for(SimulatorMenssage m : msg){
					if(bigerTime < m.getTime() && m.getDestiny()==n.coord){
						bigerTime = m.getTime();
						tmp = m;
					}
				}
				if(tmp==null){
					//n.nodeLog.logln("Invitacion ignorada time: "+n.time);
					n.status = Status.Normal;
					return;
				}
				//int oldCoord = n.coord;
				if(n.coord==n.ID){
					n.leaderCount--;
					n.transMan.removeCoordinator(n.ID);
				}
				
				if(n.ID==n.coord){
					for(Integer i : n.active){
						n.broadcast(new Invitation(tmp.getSourceNode(), i, tmp.getTime()));
					}
				}
				
				n.active = new ArrayList<>();
				
				n.coord = tmp.getSourceNode();
				
				//n.setColor(MobileDevice.nodeColor.get(n.coord));
				
				waiting = true;
				n.broadcast(((Invitation)tmp).reponseCoordinator(n.ID));
				n.status = Status.Normal;
				
				
				
			}
	}
	public boolean isExecuting(){ return waiting;}
}
