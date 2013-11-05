package org.hanns.myPackage.fuzzy.membership;

import static org.junit.Assert.fail;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.hanns.myPackage.gates.MisoAbstractGate;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.nodes.topicParticipant.ConnectedParticipantPublisher;
import ctu.nengoros.nodes.topicParticipant.ConnectedParticipantSubscriber;
import ctu.nengoros.nodes.topicParticipant.ParticipantPublisher;
import ctu.nengoros.nodes.topicParticipant.ParticipantSubscriber;


public abstract class FcnAbstractTester extends MisoAbstractGate<std_msgs.Float32MultiArray>{

	Log log;

	// configuration (alpha, beta..)
	protected Publisher<std_msgs.Float32MultiArray> alphaPub, betaPub;

	protected float currentAlpha = 0, currentBeta = 0;

	// input data to membership function - x
	protected Publisher<std_msgs.Float32MultiArray> publisher;
	// output of tested node - y
	protected Subscriber<std_msgs.Float32MultiArray> subscriber;

	protected int receivedMessages = 0;
	protected volatile boolean waitingForResponse = true;
	protected float response = 0;

	protected final int maxwait = 5000, waittime = 2;	//ms

	// check if my communication is connected somewhere?
	public boolean requireGateRunning = true;

	protected Random r = new Random();

	public boolean somethingReceived(){ return receivedMessages > 0; }

	protected void awaitResponse(){
		int poc = 0;

		while(waitingForResponse){
			if(waittime*poc++ > maxwait){
				System.out.println("Message from ROS node not received in given time of "+maxwait+" ms");
				fail("Message from ROS node not received in given time of "+maxwait+" ms");
				return;
			}
			try {
				Thread.sleep(waittime);
			} catch (InterruptedException e) {
				e.printStackTrace();
				fail("Could not sleep");
			}
		}
	}

	public float changeMembershipFcnParameter(float alpha, float beta){

		super.awaitCommunicationReady();
		
		this.checkCorrectChanges(alpha, beta);

		if(alpha !=currentAlpha){
			return this.changeAlpha(alpha);
		}else if(beta != currentBeta){
			return this.changeBeta(beta);
		}
		
		if(r.nextBoolean())
			return this.changeBeta(beta);
		else
			return this.changeBeta(beta);
	}

	/**
	 * Do not call this externally, call changeMembershipFcnParameter instead
	 * @param alpha
	 * @return
	 */
	protected float changeAlpha(float alpha){
		std_msgs.Float32MultiArray out = alphaPub.newMessage();
		out.setData(new float[]{alpha});
	//	this.waitingForResponse = true;
		alphaPub.publish(out);
		currentAlpha = alpha;
		log.info("Changing remote alpha to: \"" + out.getData()+" and waiting for new x");
//		this.awaitResponse();
		return response;
	}

	protected float changeBeta(float beta){
		std_msgs.Float32MultiArray out = betaPub.newMessage();
		out.setData(new float[]{beta});
	//	this.waitingForResponse = true;
		betaPub.publish(out);
		currentBeta = beta;
		log.info("Changing remote beta to: \"" + out.getData()+" and waiting for new x");
//		this.awaitResponse();
		return response;
	}

	protected void checkCorrectChanges(float alpha, float beta){
		if(alpha!=currentAlpha && beta!=currentBeta){
			System.err.println("Parameter values should be changed only one at a time for consistent results!");
			fail("Parameter values should be changed only one at a time for consistent results!");
		}
	}

	/**
	 * Send data (x) to be computed by the membership function
	 * 
	 * @param x
	 * @return membership degrees
	 */
	public float computeRempotely(float x){

		super.awaitCommunicationReady();
		
		std_msgs.Float32MultiArray out = publisher.newMessage();
		out.setData(new float[]{x});
		this.waitingForResponse = true;
		publisher.publish(out);
		log.info("Sending data (x) to be computed remotely, sending: " + out.getData()[0]);
		this.awaitResponse();
		System.out.println(".............sent this"+x +"and received this:"+response);
		return response;	//y
	}

	@Override
	public void onStart(ConnectedNode connectedNode){
		log = connectedNode.getLog();

		this.connectTestedNodeInput(connectedNode);

		this.connectTestedNodeOutput(connectedNode);

		this.connectTestedNodeConfigs(connectedNode);
	}


	/**
	 * This to be overridden/extended for more than two-parameter membership funcitons.
	 *  
	 * @param connectedNode
	 */
	protected void connectTestedNodeConfigs(ConnectedNode connectedNode){
		// build and register both publishers
		alphaPub = connectedNode.newPublisher(confAT, std_msgs.Float32MultiArray._TYPE);
		betaPub = connectedNode.newPublisher(confBT, std_msgs.Float32MultiArray._TYPE);

		if(requireGateRunning){
			super.participants.registerParticipant(
					new ConnectedParticipantPublisher<std_msgs.Float32MultiArray>(alphaPub));
			super.participants.registerParticipant(
					new ConnectedParticipantPublisher<std_msgs.Float32MultiArray>(betaPub));
		}else{
			super.participants.registerParticipant(
					new ParticipantPublisher<std_msgs.Float32MultiArray>(alphaPub));
			super.participants.registerParticipant(
					new ParticipantPublisher<std_msgs.Float32MultiArray>(betaPub));
		}
	}

	protected void connectTestedNodeInput(ConnectedNode connectedNode){
		// data output - y
		publisher = connectedNode.newPublisher(inAT, std_msgs.Float32MultiArray._TYPE);

		if(requireGateRunning){
			super.participants.registerParticipant(
					new ConnectedParticipantPublisher<std_msgs.Float32MultiArray>(publisher));
		}else{
			super.participants.registerParticipant(
					new ParticipantPublisher<std_msgs.Float32MultiArray>(publisher));
		}
	}

	/**
	 * Assume that most of gates have one output.
	 * 
	 * @param connectedNode
	 */
	protected void connectTestedNodeOutput(ConnectedNode connectedNode){

		// subscribe to gate output
		subscriber = connectedNode.newSubscriber(outAT, std_msgs.Float32MultiArray._TYPE);

		// create listener
		subscriber.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				response = message.getData()[0];	// TODO, time series here
				receivedMessages++;
				log.info("Received these data: "+response);
				waitingForResponse = false;
			}
		});

		// this thing ensures that at least one subscriber is registered
		if(this.requireGateRunning)
			super.participants.registerParticipant(
					new ConnectedParticipantSubscriber<std_msgs.Float32MultiArray>(subscriber));
		else
			super.participants.registerParticipant(
					new ParticipantSubscriber<std_msgs.Float32MultiArray>(subscriber));
	}
}
