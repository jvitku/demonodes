package org.hanns.myPackage;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

/**
 * Demo node: subscribe to given topic and write received array-of-integer messages into the log ros-console.
 * 
 * @author Jaroslav Vitku , based on original rosjava_core tutorials
 *
 */
public class DemoSubscriber extends AbstractNodeMain{
	
	// topic used for communication 
	protected final java.lang.String topic = "hanns/demo/pubsub";
	
	// configured also in python/nef/templates/demoSubscriber.py 
	private final int dataLength = 7;
	
	/**
	 * Default name of the ROS node
	 */
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("DemoSubscriber"); }

	/**
	 * Method called after launching the node. 
	 * After exiting this method, the node will stop working.
	 */
	@Override
	public void onStart(final ConnectedNode connectedNode) {

		System.out.println("Node started, initializing!");
		final Log log = connectedNode.getLog();

		// subscribe to given topic
		Subscriber<std_msgs.Int32MultiArray> subscriber = 
				connectedNode.newSubscriber(topic, std_msgs.Int32MultiArray._TYPE);
		
		subscriber.addMessageListener(new MessageListener<std_msgs.Int32MultiArray>() {
			// print messages to console
			@Override
			public void onNewMessage(std_msgs.Int32MultiArray message) {
				int[] data = message.getData();
				if(data.length != dataLength)
					log.error("Received message has unexpected length of"+data.length+"!");
				else
					log.info("received these data: "+toAr(data));
			}
		});

		log.info("HEY! Node ready now!");
	}

	private String toAr(int[] f){
		String out = "";
		for(int i=0;i<f.length; i++)
			out = out+"  "+f[i];
		return out;		
	}
}
