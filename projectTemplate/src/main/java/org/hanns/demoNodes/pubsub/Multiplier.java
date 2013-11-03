package org.hanns.demonodes.pubsub;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

/**
 * This ROS node multiplies data on input and publishes the result. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class Multiplier extends AbstractNodeMain{
	
	private final java.lang.String topicIn = "org/hanns/demonodes/pubsub/IN";
	protected final java.lang.String topicOut = "org/hanns/demonodes/pubsub/OUT";
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("Multiplier"); }

	@Override
	public void onStart(final ConnectedNode connectedNode) {

		System.out.println("Node started, initializing!");
		final Log log = connectedNode.getLog();

		// define the publisher
		final Publisher<std_msgs.Float32> publisher = 
				connectedNode.newPublisher(topicOut, std_msgs.Float32._TYPE);

		// subscribe to given topic
		Subscriber<std_msgs.Float32MultiArray> subscriber = 
				connectedNode.newSubscriber(topicIn, std_msgs.Float32MultiArray._TYPE);
		
		subscriber.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			// print messages to console
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				log.info("received these data: "+toAr(data));
				float result = 1, tmp=0;
				for(int i=0; i<data.length; i++){
					tmp = result*data[i];
					result = tmp;
				}
				log.info("publishing the multiplied result: "+result);
				
				// publish
				std_msgs.Float32 fl = publisher.newMessage();	
				fl.setData(result);								
				publisher.publish(fl);						
			}
		});

		log.info("HEY! Node ready now!");
	}

	protected String toAr(float[] f){
		String out = "";
		for(int i=0;i<f.length; i++)
			out = out+"  "+f[i];
		return out;		
	}
}
