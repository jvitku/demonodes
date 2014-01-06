package org.hanns.demonodes.pubsub;

import java.util.Random;
import org.apache.commons.logging.Log;
import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

/**
 * Demo ROS node: publish array of integers on the given topic.
 * 
 * @author Jaroslav Vitku, based on original rosjava_core tutorials
 *
 */
public class DemoPublisher extends AbstractNodeMain {

	protected final java.lang.String topicOut = "org/hanns/demonodes/pubsub";
	// length of the message is used by the demoPublisher.py script 
	private final int dataLength = 7;
	private final Random r = new Random();
	
	Publisher<std_msgs.Float32MultiArray> publisher;
	

	/**
	 * Default name of the ROS node
	 */
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("DemoPublisher"); }

	/**
	 * Method called after launching the node. 
	 * After exiting this method, the node will stop working.
	 */
	@Override
	public void onStart(final ConnectedNode connectedNode) {

		System.out.println("Node started, initializing!");
		final Log log = connectedNode.getLog();

		// define the publisher
		publisher = connectedNode.newPublisher(topicOut, std_msgs.Float32MultiArray._TYPE);
		
		log.info("HEY! Node ready now! Starting sending mesages..");
		
		connectedNode.executeCancellableLoop(new CancellableLoop() {
		      private int poc;

		      @Override
		      protected void setup() {
		        poc = 0;
		      }
		      
		      @Override
		      protected void loop() throws InterruptedException {
		        std_msgs.Float32MultiArray mess = publisher.newMessage();	// init message
		        
		        float[] data = generateData();
		        
		        mess.setData(data);									// set message data
		        publisher.publish(mess);							// send message
		        log.info("Sending message no.:"+poc+", and sending these vals "+toAr(data));
		        System.out.println("Sending message no.:"+poc+", wit these data:"+toAr(data));
		        poc++;
		        Thread.sleep(100);
		      }
		    });
	}

	private float[] generateData(){
		float[] out = new float[this.dataLength];
		
		for(int i=0;i<out.length; i++)
			out[i] = r.nextInt();
		
		return out;
	}
	
	protected String toAr(float[] data){
		String out = "";
		for(int i=0;i<data.length; i++)
			out = out+"  "+data[i];
		return out;		
	}
	

}
