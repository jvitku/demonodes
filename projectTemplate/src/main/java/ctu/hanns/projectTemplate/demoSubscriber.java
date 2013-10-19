package ctu.hanns.projectTemplate;

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
public class demoSubscriber extends AbstractNodeMain{

	protected final java.lang.String topic = "demoPubSub";

	/**
	 * Default name of the ROS node
	 */
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("demoSubscriber"); }

	/**
	 * Method called after launching the node. 
	 * After exiting this method, the node will stop working.
	 */
	@Override
	public void onStart(final ConnectedNode connectedNode) {

		System.out.println("Node started, initializing!");
		final Log log = connectedNode.getLog();

		// subscribe to given topic
		Subscriber<std_msgs.Float32MultiArray> subscriber = 
				connectedNode.newSubscriber(topic, std_msgs.Float32MultiArray._TYPE);
		
		subscriber.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			// print messages to console
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				log.info("received these data: "+toAr(data));						
			}
		});

		log.info("HEY! Node ready now!");
	}

	private String toAr(float[] f){
		String out = "";
		for(int i=0;i<f.length; i++)
			out = out+"  "+f[i];
		return out;		
	}
}
