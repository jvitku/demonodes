package ctu.hanns.projectTemplate;

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
public class demoPublisher extends AbstractNodeMain {

	protected final java.lang.String topic = "demoPubSub";
	private final int maxLen = 5;
	private final Random r = new Random();


	/**
	 * Default name of the ROS node
	 */
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("FloatArrPubSub"); }

	/**
	 * Method called after launching the node. 
	 * After exiting this method, the node will stop working.
	 */
	@Override
	public void onStart(final ConnectedNode connectedNode) {

		System.out.println("Node started, initializing!");
		final Log log = connectedNode.getLog();

		// define the publisher
		final Publisher<std_msgs.Int32MultiArray> publisher = 
				connectedNode.newPublisher(topic, std_msgs.Int32MultiArray._TYPE);
		
		log.info("HEY! Node ready now! Starting sending mesages..");
		
		// ROS uses these cancellable loops
		connectedNode.executeCancellableLoop(new CancellableLoop() {
		      private float poc;

		      @Override
		      protected void setup() {
		        poc = 0;
		      }
		      
		      @Override
		      protected void loop() throws InterruptedException {
		        std_msgs.Int32MultiArray mess = publisher.newMessage();	// init message
		        
		        int[] data = generateData();
		        
		        mess.setData(data);									// set message data
		        publisher.publish(mess);							// send message
		        log.info("Sending message no.:"+poc+", and sending these vals "+toAr(data));
		        poc=poc++;
		        Thread.sleep(100);
		      }
		    });
	}

	private int[] generateData(){
		int[] out = new int[1+r.nextInt(maxLen-1)];
		
		for(int i=0;i<out.length; i++)
			out[i] = r.nextInt();
		
		return out;
	}
	
	protected String toAr(int[] data){
		String out = "";
		for(int i=0;i<data.length; i++)
			out = out+"  "+data[i];
		return out;		
	}
	

}
