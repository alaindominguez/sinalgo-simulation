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
package projects.sample1.nodes.nodeImplementations;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.sun.org.apache.xerces.internal.util.Status;

import projects.defaultProject.nodes.timers.MessageTimer;
import projects.sample1.nodes.messages.CheckCoordinatorStatus;
import projects.sample1.nodes.messages.CheckCoordinatorStatusResponse;
import projects.sample1.nodes.messages.CheckCoordinatorStatusResponse.AliveCoordMensage;
import projects.sample1.nodes.messages.LeaderMensage;
import projects.sample1.nodes.messages.LeaderMensage.MensageType;
import projects.sample1.nodes.messages.ReadyMenssage;
import projects.sample1.nodes.messages.NewCoordinatorMenssage;
import projects.sample1.nodes.messages.PaxosMenssage;
import projects.sample1.nodes.messages.NewCoordinatorResponseMenssage.NewCoordMensage;
import projects.sample1.nodes.messages.SimulatorMenssage;
import projects.sample1.nodes.messages.Invitation;
import projects.sample1.nodes.messages.Invitation.InvitationType;
import projects.sample1.nodes.messages.InvitationResponse;
import projects.sample1.nodes.messages.InvitationResponse.InvitationRequestType;
import projects.sample1.nodes.nodeImplementations.manager.CoordinatorChecker;
import projects.sample1.nodes.nodeImplementations.manager.CoordinatorRequest;
import projects.sample1.nodes.nodeImplementations.manager.InvitationManager;
import projects.sample1.nodes.nodeImplementations.manager.TransactionManager;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.nodes.timers.Timer;
import sinalgo.tools.logging.Logging;
import sinalgo.runtime.Runtime;

/**
 * The absolute dummy node. Does not do anything. Good for testing network topologies.
 */
public class MobileDevice extends Node {
	static int nodeNumber = -1;
	public static int  TIMETORESPONSE = -1;
	static int waitTimeCoordinatorCheker = -1;
	static int waitTimeInvitationCheker = 1;
	
	public static TransactionManager transMan = new TransactionManager();
	
	public static Hashtable<Integer, Color> nodeColor = new Hashtable<Integer, Color>();
	protected static Random r=new Random();
	
	int AYN_ANSWER_RESPONSE;
	
	public static Logging nodeLog=Logging.getLogger("logs.txt");
	
	public enum Status{
		Normal, Election, Reorganiz, Down
	}
	MensageType last; 
	
	public Status status;
	public int coord;
	public int time;
	public ArrayList<Integer> active;
	int halted; // Id del proceso que inicio la eleccion 
	
	CoordinatorChecker coordMonitor;
	CoordinatorRequest monitorUp;
	InvitationManager invitationManager;
	
	
	ArrayList<SimulatorMenssage> mensages; 
	
	
	
	static ArrayList<ArrayList<Integer>> groups = new ArrayList<ArrayList<Integer>>();
	static int oldTime = 0;
	public static int leaderCount = 0;
	
	@Override
	public void handleMessages(Inbox inbox) { 
		while(inbox.hasNext()) {
			Message msg = inbox.next();
			if(msg instanceof SimulatorMenssage) {
				
				if(((SimulatorMenssage)msg).getDestiny()!=ID)
					resendMsg((SimulatorMenssage)msg);
				((SimulatorMenssage)msg).setVisited(ID);
				
				if(((SimulatorMenssage) msg).getSourceNode()==ID){
					//inbox.remove();
					continue;
				}
				else if(((SimulatorMenssage) msg).getDestiny()==ID){
					if(msg instanceof CheckCoordinatorStatus){
						if (ID==coord){
							broadcast(((CheckCoordinatorStatus) msg).reponseCoordinator(AliveCoordMensage.AYC_YES, coord));
							//MessageTimer tim = new MessageTimer(((CheckCoordinatorStatus) msg).reponseCoordinator(AliveCoordMensage.AYC_YES, coord));
							//tim.startRelative(1, this);
						}
					}
					else
						mensages.add((SimulatorMenssage)msg);
				}
				else if(((SimulatorMenssage) msg).getDestiny()==coord && msg instanceof Invitation)
					mensages.add((SimulatorMenssage)msg);
				else if(((SimulatorMenssage) msg).getDestiny()==-1){
					// Aqui recibir los mensajes del coordenador buscando nuevos o comprobando nodos
					
					if(msg instanceof NewCoordinatorMenssage){
						if(ID == coord){
							broadcast(((NewCoordinatorMenssage)msg).reponseCoordinator(NewCoordMensage.AYC_COORD_YES, ID, coord));
							//MessageTimer tim = new MessageTimer(((NewCoordinatorMenssage)msg).reponseCoordinator(NewCoordMensage.AYC_COORD_YES, ID));
							//tim.startRelative(1, this);
							//nodeLog.logln("Enviado respuesta de coordenador time: "+time+ " coord: "+coord);
						}
						else if(((NewCoordinatorMenssage)msg).getSourceNode()!=coord){
							broadcast(((NewCoordinatorMenssage)msg).reponseCoordinator(NewCoordMensage.AYC_COORD_YES, coord, coord));
						}
					}
					//nodeLog.logln("Mensaje indirecto: "+msg.toString()+" time: "+time);
				}
			}
			//inbox.remove();
		}
	}

	@Override
	public void preStep() {
		//nodeLog.logln("ID: "+ID+" status: "+status+ "leader: " + (ID==coord));
		if(coord!=ID)
			setDefaultDrawingSizeInPixels(1);
		else
			setDefaultDrawingSizeInPixels(2);
		
		if(nodeNumber==-1){
			nodeNumber = Runtime.nodes.size();
			TIMETORESPONSE = 2*Math.min(5, nodeNumber);
			waitTimeCoordinatorCheker = TIMETORESPONSE+2;
		}
		
		invitationManager.run();
		
		if(ID==coord)
			setRadioIntensity(2);
		
	}

	@Override
	public void init() {
		//status = Status.Down;
		//coord = ID;
		//active = new ArrayList<>();
		
		//nodeLog = Logging.getLogger(ID+"_NodeLog.txt");
		
		Color c = new Color((int)(r.nextDouble()*255),(int)(r.nextDouble()*255),(int)(r.nextDouble()*255));
		nodeColor.put(ID, c);
		
		transMan.addCoordinator(ID);
		
		time = 0;
		
		recover();
		//last = MensageType.NONE;
		//coord = 1;
		
		mensages = new ArrayList<SimulatorMenssage>();
		
		//Chequea si existe coordinador
		coordMonitor = new CoordinatorChecker(this);
		/*coordMonitor.start();*/
		
		
		//Chequea si encuentra nuevo coordinador
		monitorUp = new CoordinatorRequest(this);
		/*monitorUp.start();*/
		
		//Chequea si existen nuevas invitaciones
		invitationManager = new InvitationManager(this);
		/*invitationManager.start();*/
		
	}

	@Override
	public void neighborhoodChange() {}

	@Override
	public void postStep() {
		
		if(time%(Integer.MAX_VALUE-10)==0)
			time = 0;
		
		if(time%TIMETORESPONSE==0)
			findExpiredMenssage();
		
		if(time%(2*TIMETORESPONSE)==0 && !coordMonitor.isExecuting() && coord!=ID){
			coordMonitor.run();
		}
		
		
		
		if((((int)time/(TIMETORESPONSE/2))%nodeNumber)+1==ID && !monitorUp.isExecuting() && coord==ID){
			//System.out.println(((((int)time/TIMETORESPONSE)%nodeNumber)+1)+" "+"time: "+time);
			nodeLog.logln("executed");
			monitorUp.run();
		}
		if(oldTime!=time)
			timeChange();
		analyse();
		
		time++;
	}
	private void timeChange(){
		oldTime = time;
		nodeLog.logln("time: "+time+" group number: "+groups.size()+" leader count: "+leaderCount);
		/*for(int i=0;i<groups.size();i++){
			nodeLog.logln("grup: "+i);
			for(int j=0;j<groups.get(i).size();j++){
				nodeLog.log("miembro: "+groups.get(i).get(j)+" ");
			}
			nodeLog.logln();
		}*/
		
		groups = new ArrayList<>();
	}
	private void DFS(ArrayList<Integer> visited, Node n){
		if(!visited.contains(n.ID)){
			visited.add(n.ID);
			
			Iterator<Edge> edgeIter = n.outgoingConnections.iterator();
			while(edgeIter.hasNext()){
				Edge e = edgeIter.next();
				Node ch = e.endNode;
				DFS(visited,ch);
			}
		}
	}
	private void analyse(){
		boolean found = false;
		for(ArrayList<Integer> group : groups){
			if(inGroup(group, ID)){
				found = true;
				break;
			}
		}
		if(!found){
			ArrayList<Integer> group = new ArrayList<>();
			DFS(group, this);
			groups.add(group);
		}
	}
	private static boolean inGroup(ArrayList<Integer> group, int ID){
		for(Integer id : group){
			if(id.intValue()==ID){
				return true;
			}
		}
		return false;
	}
	
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
		
		if(coord==ID && status == Status.Normal){
			nodeLog.logln("nodo: "+ID+" --> "+coordIds.toString());
			status = Status.Election;
			
			for(Integer id : coordIds){
				broadcast(new Invitation(ID, id.intValue(),lastTime));
			}
			Timer t = new Timer() {
				
				@Override
				public void fire() {
					// TODO Auto-generated method stub
					ArrayList<SimulatorMenssage> msg = received(InvitationResponse.class,lastTime);
					
					if(msg==null){
						nodeLog.logln("recover "+ID);
						recover();
						return;
					}
					
					status = Status.Reorganiz;
					for(SimulatorMenssage m : msg){
						if(!active.contains(((InvitationResponse)m).getSourceNode())){
							if(((InvitationResponse)m).menssage == InvitationRequestType.ACCEPTED)
								active.add(((InvitationResponse)m).getSourceNode());
							else{
								recover();
								return; 
							}
						//broadcast(((Invitation)m).reponseCoordinator(InvitationType.ACCEPTED_ANS, ID));
						}
					}

					status = Status.Normal;
				
				}
			};
			t.startRelative(MobileDevice.TIMETORESPONSE, this);
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
				//MessageTimer tim = new MessageTimer(msg,n);
				//tim.startRelative(1, this);
			}
		}
	}
	
	public ArrayList<SimulatorMenssage> received(Class<?> type, int time){
		
		ArrayList<SimulatorMenssage> found = new ArrayList<>();
		
		/*nodeLog.log("Mensajes en lista: ");
		for(SimulatorMenssage m : mensages){
			nodeLog.log(m.toString()+"  ");
		}
		nodeLog.logln();*/
		
		for(SimulatorMenssage m : mensages){
			if(type.isInstance(m) && (m.getTime()==time || time==-1)){
				found.add(m);
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
			if(time - mensages.get(i).getTime()>2*TIMETORESPONSE+1)
				remove.add(mensages.get(i));
		}
		for(SimulatorMenssage l : remove)
			mensages.remove(l);
	}
	public void recover(){
		
		setColor(nodeColor.get(ID));
		transMan.addCoordinator(ID);
		
		if(ID==coord)
			leaderCount--;
		leaderCount++;
		
		//status = Status.Election;
		//nodeLog.logln("Recover node: "+ID+" time: "+time);
		coord = ID;
		//status = Status.Reorganiz;
		status = Status.Normal;
		active = new ArrayList<>();
	}
	
}
