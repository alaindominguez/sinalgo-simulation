package projects.defaultProject.nodes.nodeImplementations.manager;

import java.util.ArrayList;

import projects.defaultProject.nodes.messages.Invitation;
import projects.defaultProject.nodes.messages.NewCoordinatorMenssage;
import projects.defaultProject.nodes.messages.SimulatorMenssage;
import projects.defaultProject.nodes.messages.NewCoordinatorMenssage.NewCoordMensage;
import projects.defaultProject.nodes.messages.ReadyMenssage;
import projects.defaultProject.nodes.nodeImplementations.MobileDevice;
import projects.defaultProject.nodes.nodeImplementations.MobileDevice.Status;
import sinalgo.nodes.timers.Timer;

public class InvitationManager extends Thread{
	MobileDevice n;
	int lastTime;
	public InvitationManager(MobileDevice n){
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
			//t.startRelative(MobileDevice.TIMETORESPONSE, n);	
			
			ArrayList<SimulatorMenssage> msg = n.received(Invitation.class,-1);
			
			if(n.status==Status.Normal && msg!=null){
				SimulatorMenssage tmp=null;
				int bigerTime=-1;
				for(SimulatorMenssage m : msg){
					if(bigerTime < m.getTime()){
						bigerTime = m.getTime();
						tmp = m;
					}
				}
				//int oldCoord = n.coord;
				n.status = Status.Election;
				n.coord = tmp.getSourceNode();
				n.broadcast(((Invitation)tmp).reponseCoordinator(n.ID));
				
				t.startRelative(MobileDevice.TIMETORESPONSE, n);
				msg = n.received(ReadyMenssage.class,-1);
				if(msg==null)
					n.recover();
				
				/*if(n.ID==oldCoord){
					for(Integer i : n.active){
						n.broadcast(new Invitation(tmp.getSourceNode(), i, tmp.getTime()));
					}
				}*/
				
			}
			
		}
	}
}
