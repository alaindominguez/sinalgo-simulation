package projects.sample1.nodes.nodeImplementations.manager;

import java.util.ArrayList;

import projects.defaultProject.nodes.timers.MessageTimer;
import projects.sample1.nodes.messages.CheckCoordinatorStatus;
import projects.sample1.nodes.messages.NewCoordinatorMenssage;
import projects.sample1.nodes.messages.NewCoordinatorResponseMenssage;
import projects.sample1.nodes.messages.NewCoordinatorResponseMenssage.NewCoordMensage;
import projects.sample1.nodes.messages.SimulatorMenssage;
import projects.sample1.nodes.nodeImplementations.MobileDevice;
import projects.sample1.nodes.nodeImplementations.MobileDevice.Status;
import sinalgo.nodes.timers.Timer;

public class CoordinatorRequest {
	MobileDevice n;
	int lastTime;
	Timer t;
	boolean waiting= false;
	public CoordinatorRequest(MobileDevice n){
		this.n= n;
		lastTime = -1;
	}
	public void run() {
		// TODO Auto-generated method stub
			t = new Timer() {
				
				@Override
				public void fire() {
					// TODO Auto-generated method stub
					//n.nodeLog.logln("Intentando obtener respuesta de nuevos coord time: "+n.time);
					ArrayList<SimulatorMenssage> msg = n.received(NewCoordinatorResponseMenssage.class,lastTime);
					lastTime = -1;
					if(n.status==Status.Normal && n.coord==n.ID){
						if(msg !=null){
							ArrayList<Integer> coordIds = new ArrayList<>();
							//ArrayList<NewCoordinatorMenssage> news = new ArrayList<>();
							for(SimulatorMenssage m : msg){
								if(((NewCoordinatorResponseMenssage)m).menssage == NewCoordMensage.AYC_COORD_YES 
										&& ((NewCoordinatorResponseMenssage)m).getSourceNode()!=n.coord){
									
									if(!coordIds.contains(((NewCoordinatorResponseMenssage)m).getSourceNode())){
										//news.add((NewCoordinatorMenssage)m);
										coordIds.add(((NewCoordinatorResponseMenssage)m).getSourceNode());
									} 
								}
							}
							//n.nodeLog.logln("recibiendo # coordinadores time: "+n.time + " # "+coordIds.size());
							if(coordIds.size()>0){
								//n.nodeLog.logln("recibidos # coordinadores time: "+n.time + " # "+coordIds.get(0));
								n.merge(coordIds);
							}
							/*else if(msg.size()!=n.active.size()){
								n.recover();
							}*/
						}
					}
					waiting = false;
				}
			};
			
			if(n.ID==n.coord && (n.status==Status.Normal)){
				lastTime = n.time;
				n.broadcast(new NewCoordinatorMenssage(n.ID,-1, n.time, n.coord));
				//MessageTimer tim = new MessageTimer(new NewCoordinatorMenssage(n.ID,-1, n.time));
				//tim.startRelative(1, n);
				//n.nodeLog.logln("Buscando nuevo coordinador time: "+n.time);
				waiting = true;
				//n.nodeLog.logln("executed");
				t.startRelative(MobileDevice.TIMETORESPONSE, n);
			}
	}
	public boolean isExecuting(){ return waiting;}
}
