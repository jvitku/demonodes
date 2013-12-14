package org.hanns.demonodes.privateParams;


import org.apache.commons.logging.Log;
import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import ctu.nengoros.rosparam.impl.PrivateRosparam;

/**
 * Just a simple ROS node computing value of equation: y=a*sin(b*t), 
 * where parameters a,b and shouldLog can be set from the command line as follows:
 * 
 * ./runner org.hanns.demonodes.privateParams.SetPrivateParameters _a:=11 _b:=10 _shouldLog:=true
 * 
 * Note that in case of restarting node without parameters, the original ones are still 
 * stored on the parameter server, so they will be set according to actual values in the 
 * parameter server (ros core).
 * 
 * @author Jaroslav Vitku
 *
 */
public class SetPrivateParameters extends AbstractNodeMain{

	public static final String NAME = "SetPrivateParametersNode";
	public final String me = "["+NAME+"] ";

	private PrivateRosparam r;	// handle private parameters


	protected final java.lang.String topicOut = "org/hanns/demonodes/params/";

	private Publisher<std_msgs.Float32MultiArray> publisher;

	/**
	 * parameters are here
	 */
	public int a;
	public int b;
	public boolean shouldLog;

	/**
	 * default values of parameters
	 */
	public final int defA = 1;
	public final int defB = 1;
	public final boolean defShouldLog = false;

	private float y; 
	private double t;

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(NAME); }

	@Override
	public void onStart(final ConnectedNode connectedNode){

		// here is how you read private parameters
		r = new PrivateRosparam(connectedNode);
		a = r.getMyInteger("a", defA);
		b = r.getMyInteger("b",defB);
		shouldLog = r.getMyBoolean("shouldLog", defShouldLog);


		final Log log = connectedNode.getLog();
		
		log.info("----------------------------------------------------------------------");
		log.info("\nNode started, parameters are: a="+a+" ,b="+b+" ,shouldLog="+shouldLog+"\n");
		log.info("----------------------------------------------------------------------");

		// define the publisher
		publisher = connectedNode.newPublisher(topicOut, std_msgs.Float32MultiArray._TYPE);

		log.info(me+"Node ready now! Starting computation..");

		// ROS uses these cancellable loops
		connectedNode.executeCancellableLoop(new CancellableLoop() {

			@Override
			protected void setup() {
				y = 0;
				t = 0;
			}

			@Override
			protected void loop() throws InterruptedException {
				std_msgs.Float32MultiArray mess = publisher.newMessage();	

				t = connectedNode.getCurrentTime().toSeconds();
				y = (float)compute(t);
				

				mess.setData(new float[]{y});			
				publisher.publish(mess);
				if(shouldLog)
					log.info(me+"time:"+t+", \ty = a*sin(b*t) = "+y);
				
				Thread.sleep(200);
			}
		});

	}
	
	private double compute(double seconds){ return a*Math.sin(seconds*b); }

}
