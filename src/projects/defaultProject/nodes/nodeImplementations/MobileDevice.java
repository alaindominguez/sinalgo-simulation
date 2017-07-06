/*
 Copyright (c) 2007, Distributed Computing Group (DCG)
                    ETH Zurich
                    Switzerland
                    dcg.ethz.ch

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 - Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

 - Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the
   distribution.

 - Neither the name 'Sinalgo' nor the names of its contributors may be
   used to endorse or promote products derived from this software
   without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package projects.defaultProject.nodes.nodeImplementations;


import java.util.ArrayList;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.sun.org.apache.xerces.internal.util.Status;

import projects.defaultProject.nodes.messages.CheckCoordinatorStatus;
import projects.defaultProject.nodes.messages.LeaderMensage;
import projects.defaultProject.nodes.messages.LeaderMensage.MensageType;
import projects.defaultProject.nodes.messages.NewCoordinatorMenssage.NewCoordMensage;
import projects.defaultProject.nodes.messages.ReadyMenssage;
import projects.defaultProject.nodes.messages.NewCoordinatorMenssage;
import projects.defaultProject.nodes.messages.SimulatorMenssage;
import projects.defaultProject.nodes.messages.CheckCoordinatorStatus.AliveCoordMensage;
import projects.defaultProject.nodes.messages.Invitation;
import projects.defaultProject.nodes.messages.Invitation.InvitationType;
import projects.defaultProject.nodes.messages.InvitationResponse;
import projects.defaultProject.nodes.messages.InvitationResponse.InvitationRequestType;
import projects.defaultProject.nodes.nodeImplementations.manager.CoordinatorChecker;
import projects.defaultProject.nodes.nodeImplementations.manager.CoordinatorRequest;
import projects.defaultProject.nodes.nodeImplementations.manager.InvitationManager;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.nodes.timers.Timer;

/**
 * The absolute dummy node. Does not do anything. Good for testing network topologies.
 */
public class MobileDevice extends Node {
	
	public static int  TIMETORESPONSE = 5;
	
	int AYN_ANSWER_RESPONSE;
	
	public enum Status{
		Normal, Election, Reorganiz, Down
	}
	MensageType last; 
	
	public Status status;
	public int coord;
	public int time;
	public ArrayList<Integer> active;
	int halted; // Id del proceso que inicio la eleccion 
	
	Thread coordMonitor;
	Thread expiredMensageMonitor;
	Thread monitorUp;
	Thread invitationManager;
	
	ArrayList<SimulatorMenssage> mensages; 
	
	
	@Override
	public void handleMessages(Inbox inbox) {
		while(inbox.hasNext()) {
			Message msg = inbox.next();
			if(msg instanceof SimulatorMenssage) {
				if(((SimulatorMenssage) msg).getSourceNode()==ID){
					inbox.remove();
					continue;
				}
				else if(((SimulatorMenssage) msg).getDestiny()==ID){
					if(msg instanceof CheckCoordinatorStatus){
						if (ID==coord)
							broadcast(((CheckCoordinatorStatus) msg).reponseCoordinator(AliveCoordMensage.AYC_YES, coord));
						else
							broadcast(((CheckCoordinatorStatus) msg).reponseCoordinator(AliveCoordMensage.AYC_NOT, coord));
					}
					/*else if(msg instanceof NewCoordinatorMenssage){
						
					}*/
					mensages.add((SimulatorMenssage)msg);
				}
				else if(((SimulatorMenssage) msg).getDestiny()==-1){
					// Aqui recibir los mensajes del coordenador buscando nuevos o comprobando nodos
					if(msg instanceof NewCoordinatorMenssage){
						if(((NewCoordinatorMenssage)msg).getSourceNode()!=coord){
							broadcast(((NewCoordinatorMenssage)msg).reponseCoordinator(NewCoordMensage.AYC_COORD_YES, ID));
						}
						else{
							//broadcast(((NewCoordinatorMenssage)msg).reponseCoordinator(NewCoordMensage.AYC_COORD_NOT, ID));
						}
					}
					resendMsg((SimulatorMenssage)msg);
				}
				else{
					resendMsg((SimulatorMenssage)msg);
				}
			}
			inbox.remove();
		}
	}

	@Override
	public void preStep() {
		time++;
	}

	@Override
	public void init() {
		//status = Status.Down;
		//coord = ID;
		//active = new ArrayList<>();
		time = 0;
		
		recover();
		//last = MensageType.NONE;
		
		mensages = new ArrayList<SimulatorMenssage>();
		
		coordMonitor = new CoordinatorChecker(this);
		coordMonitor.start();
		
		monitorUp = new CoordinatorRequest(this);
		monitorUp.start();
		
		expiredMensageMonitor = new ExpiredMensageChecker(this);
		expiredMensageMonitor.start();
		
		invitationManager = new InvitationManager(this);
		invitationManager.start();
		
	}

	@Override
	public void neighborhoodChange() {}

	@Override
	public void postStep() {}
	
	@Override
	public String toString() {
		String s = "Node(" + this.ID + ") [";
		Iterator<Edge> edgeIter = this.outgoingConnections.iterator();
		while(edgeIter.hasNext()){
			Edge e = edgeIter.next();
			Node n = e.endNode;
			s+=n.ID+" ";
		}
		return s + "]";
	}

	@Override
	public void checkRequirements() throws WrongConfigurationException {}
	
	//--------------------------------------------------------------------
	
	public void merge(ArrayList<Integer> coordIds){
		int lastTime = time;
		if(coord==ID && status == Status.Election){
			status = Status.Election;
			for(Integer id : coordIds){
				broadcast(new Invitation(ID, id.intValue(),lastTime));
			}
			Timer t = new Timer() {
				
				@Override
				public void fire() {
					// TODO Auto-generated method stub
				}
			};
			t.startRelative(MobileDevice.TIMETORESPONSE, this);
			
			ArrayList<SimulatorMenssage> msg = received(InvitationResponse.class,lastTime);
			
			if(msg==null){
				return;
			}
			
			status = Status.Reorganiz;
			for(SimulatorMenssage m : msg){
				if(!active.contains(((InvitationResponse)m).getSourceNode())){
					if(((InvitationResponse)m).menssage == InvitationRequestType.ACCEPTED)
						active.add(((InvitationResponse)m).getSourceNode());
					else
						recover();
				//broadcast(((Invitation)m).reponseCoordinator(InvitationType.ACCEPTED_ANS, ID));
				}
			}
			
			//t.startRelative(MobileDevice.TIMETORESPONSE, this);
			lastTime = time;
			for(Integer i : active){
				broadcast(new ReadyMenssage(ID, i.intValue(), time));
			}
			t.startRelative(MobileDevice.TIMETORESPONSE, this);
			msg = received(ReadyMenssage.class,lastTime);
			if(msg==null){
				recover();
				return;
			}
			
			if(msg.size()!=active.size())
				recover();
			else status = Status.Normal;
		}
	}
	
	
	private void resendMsg(SimulatorMenssage msg){
		Iterator<Edge> edgeIter = this.outgoingConnections.iterator();
		while(edgeIter.hasNext()){
			Edge e = edgeIter.next();
			Node n = e.endNode;
			if(!msg.isVisited(n.ID)){
				msg.setVisited(ID);
				send(msg,n);
			}
		}
	}
	
	public ArrayList<SimulatorMenssage> received(Class<?> type, int time){
		ArrayList<SimulatorMenssage> found = new ArrayList<>();
		boolean result = false;
		for(SimulatorMenssage m : mensages){
			if(type.isInstance(m) && m.getTime()==time){
				if(m.getTime()==time){
					result = true;
					found.add(m);
				}
			}
		}
		for(SimulatorMenssage m : found){
			mensages.remove(m);
		}
		if(found.size()>0)
			return found;
		else 
			return null;
	}
	public void findExpiredMenssage(){
		ArrayList<SimulatorMenssage> remove = new ArrayList<>();
		for(int i=0;i<mensages.size();i++){
			if(time - mensages.get(i).getTime()>TIMETORESPONSE+1)
				remove.add(mensages.get(i));
		}
		for(SimulatorMenssage l : remove)
			mensages.remove(l);
	}
	public void recover(){
		//status = Status.Election;
		coord = ID;
		//status = Status.Reorganiz;
		status = Status.Normal;
		active = new ArrayList<>();
	}
	
	
	class ExpiredMensageChecker extends Thread{
		MobileDevice n;
		public ExpiredMensageChecker(MobileDevice n){
			this.n = n; 
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Timer t = new Timer() {
				@Override
				public void fire() {
				}
			};
			while(true){
				t.startRelative(TIMETORESPONSE+1, n);
				n.findExpiredMenssage();
			}
		}
	}
	
}
