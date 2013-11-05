package org.hanns.myPackage.fuzzy.membership;

import org.hanns.myPackage.gates.MisoAbstractGate;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import std_msgs.Float32MultiArray;

/**
 * @author Jaroslav Vitku vitkujar@fel.cvut.cz
 * 
 */
public abstract class Membership extends MisoAbstractGate<std_msgs.Float32MultiArray> {

	protected float alpha=0, beta=0, gamma=0, delta=0;

	protected Subscriber<Float32MultiArray> alphaSub, betaSub, gammaSub, deltaSub;

	protected float x=0, y=0;						// data

	protected void send(){
		super.awaitCommunicationReady();
		
		if(publisherA==null)
			return;

		std_msgs.Float32MultiArray out = publisherA.newMessage();
		out.setData(new float[]{y});
		publisherA.publish(out);
		log.info("Received data, publishing this: " + out.getData()[0] + " !! on topic: "+outAT+" y is: "+y);
	}

	protected abstract float compute();

	/**
	 * Each membership function has some constraints on parameter values, check them here.
	 */
	protected abstract void checkRanges();

	public int getSleepTime(){ return this.sleepTime; }

	/**
	 * It should be fulfilled that: a>=b, if not
	 * choose average value between these two and set both to this value.
	 * 
	 * @param a
	 * @param b
	 * @return average between a b 	 */
	protected float getAverage(float a, float b){
		if(b<a){
			return b+((a-b)/2);
		}
		System.err.println("Membership functino:correctValues: values are already OK!");
		return -1;
	}


	/**
	 * Register input/output into the network.
	 * 
	 * @param connectedNode
	 */
	protected void getDataChannel(ConnectedNode connectedNode){
		// data input - x
		subscriberA = connectedNode.newSubscriber(inAT, std_msgs.Float32MultiArray._TYPE);

		subscriberA.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				x = message.getData()[0];
				y = compute();
				send();
				//System.out.println("received data on AAAA; responding to: ("+a+","+b+")="+y);
			}
		});

		// data output - y
		publisherA = connectedNode.newPublisher(outAT, std_msgs.Float32MultiArray._TYPE);
	}

	protected void initAlpha(ConnectedNode connectedNode){
		alphaSub = connectedNode.newSubscriber(confAT, Float32MultiArray._TYPE);

		// after receiving new configuration, recompute and re-send new data
		alphaSub.addMessageListener(new MessageListener<Float32MultiArray>() {
			@Override
			public void onNewMessage(Float32MultiArray message) {
				alpha = message.getData()[0];
				//y = compute();
				//send();
				checkRanges();
			}
		});
	}

	protected void initBeta(ConnectedNode connectedNode){
		betaSub = connectedNode.newSubscriber(confBT, Float32MultiArray._TYPE);

		betaSub.addMessageListener(new MessageListener<Float32MultiArray>() {
			@Override
			public void onNewMessage(Float32MultiArray message) {
				beta = message.getData()[0];
				//y = compute();
				//send();
				checkRanges();
			}
		});
	}

	protected void initGamma(ConnectedNode connectedNode){
		gammaSub = connectedNode.newSubscriber(confCT, Float32MultiArray._TYPE);

		gammaSub.addMessageListener(new MessageListener<Float32MultiArray>() {
			@Override
			public void onNewMessage(Float32MultiArray message) {
				gamma = message.getData()[0];
				//y = compute();
				//send();
				checkRanges();
			}
		});
	}

	protected void initDelta(ConnectedNode connectedNode){
		deltaSub = connectedNode.newSubscriber(confDT, Float32MultiArray._TYPE);

		deltaSub.addMessageListener(new MessageListener<Float32MultiArray>() {
			@Override
			public void onNewMessage(Float32MultiArray message) {
				delta = message.getData()[0];
				//y = compute();
				//send();
				checkRanges();
			}
		});
	}

}
