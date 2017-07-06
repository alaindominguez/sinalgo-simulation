package projects.sample1.nodes.nodeImplementations;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.omg.CORBA.TRANSACTION_MODE;

import projects.sample1.nodes.messages.CheckCoordinatorStatus;
import projects.sample1.nodes.messages.PaxosMenssage;
import projects.sample1.nodes.messages.Prepare_Req;
import projects.sample1.nodes.messages.Prepare_Req_Response;
import projects.sample1.nodes.messages.Prepare_Req_Response.Phase1RequestType;
import projects.sample1.nodes.messages.QuorumRequest;
import projects.sample1.nodes.messages.QuorumRequestResponse;
import projects.sample1.nodes.messages.SimulatorMenssage;
import projects.sample1.nodes.messages.PaxosSucceed;
import projects.sample1.nodes.nodeImplementations.MobileDevice.Status;
import projects.sample1.nodes.nodeImplementations.manager.TransactionManager;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.nodes.timers.Timer;
import sinalgo.tools.logging.Logging;

public class Paxos extends MobileDevice{
	
	public static Logging nodeLog=Logging.getLogger("logs_Paxos.txt");
	
	
	ArrayList<Prepare_Req_Response> learnerList = new ArrayList();
	ArrayList<QuorumRequestResponse> voterList = new ArrayList();
	
	ArrayList<Object> commited = new ArrayList<>();
	
	Object uncomittedTarget;
	int uncomittedTime=-1;
	int uncomittedLeader =-1;
	
	int transaction=1;
	
	@Override
	public void handleMessages(Inbox inbox) {
		super.handleMessages(inbox);
		inbox.reset();
		
		while(inbox.hasNext()) { 
			Message msg = inbox.next();
			if(msg instanceof PaxosMenssage ){
				
				((PaxosMenssage)msg).setVisited(ID);
				if(((PaxosMenssage)msg).getDestiny()!=ID)
					resendMsg((PaxosMenssage)msg);
				
				
				if(msg instanceof Prepare_Req_Response && ((PaxosMenssage)msg).getDestiny()==ID){
					Prepare_Req_Response respmsg = (Prepare_Req_Response)msg;
					if(!learnerList.contains(respmsg)){
						//nodeLog.logln("Time: "+time+" Leader: "+ID+" Receiving Prepare response from "+respmsg.getSourceNode());
						learnerList.add(respmsg);
					}
					
				}
				else if(msg instanceof QuorumRequestResponse && ((PaxosMenssage)msg).getDestiny()==ID){
					QuorumRequestResponse respmsg = (QuorumRequestResponse)msg;
					if(!voterList.contains(respmsg)){
						//nodeLog.logln("Time: "+time+" Leader: "+ID+" Receiving Quorum response from "+respmsg.getSourceNode());
						voterList.add(respmsg);
					}
				}
				else if(msg instanceof Prepare_Req && ((PaxosMenssage)msg).getSourceNode()!=ID){
					Prepare_Req prepmsg = (Prepare_Req)msg;
					if(prepmsg.getTime()>uncomittedTime){
						//nodeLog.logln("Time: "+time+" Learner: "+ID+" sending ACK prepare response to "+prepmsg.getSourceNode());
						broadcast(prepmsg.reponseCoordinator(ID, Phase1RequestType.ACCEPTED));
						uncomittedTarget = prepmsg.target;
						uncomittedTime = prepmsg.getTime();
						uncomittedLeader = prepmsg.getSourceNode();
					}
					else if(prepmsg.getTime()<uncomittedTime){
						//nodeLog.logln("Time: "+time+" Learner: "+ID+" sending NACK prepare response to "+prepmsg.getSourceNode());
						broadcast(prepmsg.reponseCoordinator(ID, Phase1RequestType.NON_ACCEPTED));
					}
				}
				else if(msg instanceof QuorumRequest && ((PaxosMenssage)msg).getSourceNode()!=ID && ((PaxosMenssage)msg).getSourceNode()==uncomittedLeader){
					QuorumRequest quomsg = (QuorumRequest)msg;
					if(quomsg.getTime()>uncomittedTime){
						//nodeLog.logln("Time: "+time+" Voter: "+ID+" sending ACK quorum response to "+quomsg.getSourceNode());
						broadcast(quomsg.reponseCoordinator(ID, Phase1RequestType.ACCEPTED));
						uncomittedTarget = quomsg.target;
					}
					else if(quomsg.getTime()<uncomittedTime){
						//nodeLog.logln("Time: "+time+" Voter: "+ID+" sending NACK quorum response to "+quomsg.getSourceNode());
						broadcast(quomsg.reponseCoordinator(ID, Phase1RequestType.NON_ACCEPTED));
					}
				}
				else if(msg instanceof PaxosSucceed && ((PaxosMenssage)msg).getSourceNode()!=ID && ((PaxosMenssage)msg).getSourceNode()==uncomittedLeader){
					/*if(commited.contains(uncomittedTarget))
						return;
					//nodeLog.logln("Time: "+time+" Learner: "+ID+" commiting transaction "+uncomittedTarget);
					commited.add(uncomittedTarget);*/
					
					setColor(nodeColor.get(uncomittedLeader));
					
					uncomittedTime = -1;
					uncomittedTarget = null;
					uncomittedLeader =-1;
					
				}
			}
		}
	}
	@Override
	public void preStep() {
		super.preStep();
		
		try{
			TimeUnit.MILLISECONDS.sleep(25);
		}catch(Exception e){}
	}
	@Override
	public void init() {
		super.init();
	}
	@Override
	public void postStep() {
		super.postStep();
		
		
		
		
		if(time%Math.max(TIMETORESPONSE,nodeNumber)==ID && coord==ID){
			//broadcast(((Invitation)m).reponseCoordinator(InvitationType.ACCEPTED_ANS, ID));
			learnerList = new ArrayList();
			//ArrayList<Integer> group = new ArrayList();
			//DFS(group,this);
			
			/*if(!transMan.haveMoreTransaction()){
				System.exit(1);
			}*/
			uncomittedTarget= "operation "+ID+"_"+transaction;//transMan.getTransaction();
			uncomittedTime = time;
			uncomittedLeader = coord;
			
			broadcast(new Prepare_Req(ID, -1, time,uncomittedTarget));
			Node actual = this;
			Timer phase1 = new Timer() {
				
				@Override
				public void fire() {
					// TODO Auto-generated method stub
					
					//nodeLog.logln("Entro");
					for(Prepare_Req_Response response : learnerList){
						if(response.menssage==Phase1RequestType.NON_ACCEPTED){
							//nodeLog.logln("Transaccion del lieder"+actual.ID+" no aceptada por "+response.getSourceNode());
							return;
						}
					}
					if(!(learnerList.size()>nodeNumber/2)){
						//nodeLog.logln("Aborted quorum");
						return;
					}
					
					voterList = new ArrayList<>();
					broadcast(new QuorumRequest(ID, -1, time,uncomittedTarget));
					uncomittedTarget=uncomittedTarget;
					Timer phase2 = new Timer(){
						@Override
						public void fire() {
							// TODO Auto-generated method stub
							int count = 0;
							for(QuorumRequestResponse voter : voterList)
								if(voter.menssage==Phase1RequestType.ACCEPTED)
									count++;
							if(count>nodeNumber/2){
								nodeLog.logln("Time: "+time+" Leader: "+ID+" commiting transaction "+uncomittedTarget);
								transaction++;
								broadcast(new PaxosSucceed(ID, -1, time));
								transMan.commit(uncomittedTarget);
								setColor(nodeColor.get(ID));
							}
						}
					};
					//nodeLog.logln("Time: "+time+" Leader: "+ID+" Requesting for voter");
					phase2.startRelative(TIMETORESPONSE, actual);
				}
			};
			//nodeLog.logln("Time: "+time+" Leader: "+ID+" Requesting for learner");
			
			phase1.startRelative(TIMETORESPONSE, this);
			
		}
		
	}
	@Override
	public void checkRequirements() throws WrongConfigurationException {
		super.checkRequirements();
	}
	@Override
	public void neighborhoodChange() {
		super.neighborhoodChange();
	}
	
	
	private void resendMsg(PaxosMenssage msg){
		/*if(time-msg.getTime()>TIMETORESPONSE+1)
			return;*/
		Iterator<Edge> edgeIter = this.outgoingConnections.iterator();
		while(edgeIter.hasNext()){
			Edge e = edgeIter.next();
			Node n = e.endNode;
			if(!msg.isVisited(n.ID)){
				send(msg,n);
				//MessageTimer tim = new MessageTimer(msg,n);
				//tim.startRelative(1, this);
			}
		}
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
}
