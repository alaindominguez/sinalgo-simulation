package projects.defaultProject.nodes.nodeImplementations.manager;

import java.util.ArrayList;

import projects.defaultProject.nodes.messages.CheckCoordinatorStatus;
import projects.defaultProject.nodes.messages.NewCoordinatorMenssage;
import projects.defaultProject.nodes.messages.NewCoordinatorMenssage.NewCoordMensage;
import projects.defaultProject.nodes.messages.SimulatorMenssage;
import projects.defaultProject.nodes.messages.CheckCoordinatorStatus.AliveCoordMensage;
import projects.defaultProject.nodes.nodeImplementations.MobileDevice;
import projects.defaultProject.nodes.nodeImplementations.MobileDevice.Status;
import sinalgo.nodes.timers.Timer;

public class CoordinatorRequest extends Thread{
	MobileDevice n;
	int lastTime;
	public CoordinatorRequest(MobileDevice n){
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
			if(n.ID==n.coord && (n.status==Status.Normal)){
				lastTime = n.time;
				n.broadcast(new NewCoordinatorMenssage(n.ID,-1, n.time));				
			}
			t.startRelative(MobileDevice.TIMETORESPONSE, n);
			if(lastTime==-1)
				continue;
			
			ArrayList<SimulatorMenssage> msg = n.received(NewCoordinatorMenssage.class,lastTime);
			lastTime = -1;
			
			if(n.status==Status.Normal){
				if(msg ==null)
					n.recover();
				else{
					ArrayList<Integer> coordIds = new ArrayList<>();
					//ArrayList<NewCoordinatorMenssage> news = new ArrayList<>();
					for(SimulatorMenssage m : msg){
						if(((NewCoordinatorMenssage)m).menssage == NewCoordMensage.AYC_COORD_YES){
							if(!coordIds.contains(((NewCoordinatorMenssage)m).getSourceNode())){
								//news.add((NewCoordinatorMenssage)m);
								coordIds.add(((NewCoordinatorMenssage)m).getSourceNode());
							} 
						}
					}
					if(coordIds.size()>0){
						n.merge(coordIds);
					}
				}
			}
			
		}
	}
}
