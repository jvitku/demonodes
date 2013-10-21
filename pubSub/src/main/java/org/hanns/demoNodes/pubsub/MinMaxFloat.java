package org.hanns.demoNodes.pubsub;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;


/**
 * Get vector of 4 floats, select min and max and publish them.
 * 
 * @author Jaroslav Vitku
 *
 */
public class MinMaxFloat extends AbstractNodeMain {

	private final java.lang.String ann2ros = "ann2rosFloatArr";
	protected final java.lang.String ros2ann = "ros2annFloatArr";

	private float min;
	private float max;
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("FloatArrPubSub"); }

	@Override
	public void onStart(final ConnectedNode connectedNode) {

		System.out.println("Node started, initializing!");
		final Log log = connectedNode.getLog();

		// define the publisher
		final Publisher<std_msgs.Float32MultiArray> publisher = 
				connectedNode.newPublisher(ros2ann, std_msgs.Float32MultiArray._TYPE);

		// subscribe to given topic
		Subscriber<std_msgs.Float32MultiArray> subscriber = 
				connectedNode.newSubscriber(ann2ros, std_msgs.Float32MultiArray._TYPE);
		
		subscriber.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			// print messages to console
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				//log.info("received these data: "+toAr(data));
				min = min(data);
				max = max(data);
				//log.info("publishing this: min: "+(double)min+"   max: "+(double)max);
				
				// publish
				std_msgs.Float32MultiArray fl = publisher.newMessage();	
				fl.setData(new float[]{min,max});								
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
	
	private float max(float[] vals){
		float mx = Float.MIN_VALUE;
		for(int i=0;i<vals.length; i++)
			if(mx<vals[i])
				mx=vals[i];
		return mx;
	}
		
	private float min(float[] vals){
		float min = Float.MAX_VALUE;
		for(int i=0;i<vals.length; i++)
			if(min>vals[i])
				min=vals[i];
		return min;	
	}
	
}
