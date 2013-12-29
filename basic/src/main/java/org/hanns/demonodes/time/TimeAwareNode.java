package org.hanns.demonodes.time;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import org.ros.message.Time;

import ctu.nengoros.rosparam.ParameterTreeCrawler;
import ctu.nengoros.time.AbstractTimeNode;

/**
 * This node does the similar thing as: @see org.hanns.demonodes.pubsub.MinMaxFloat, 
 * but the resulting minimum and maximum values multiplies by current simulation time
 * in seconds.
 * 
 * @see <a href="http://nengoros.wordpress.com/rosjava-tutorials/">nengoros rosjava tutirals</a>
 * 
 * Current time is:
 * <ul>
 * 	<li>Nengo simulation time (by default) (that is simulated time, @see <a href="http://wiki.ros.org/Clock">clock</a> ) </li>
 * 
 * 	<li>The global WallTime (if the RosUtils.setTimeIgnore() is used) 
 * (pretty arbitrary increasing number)</li>
 * 
 * 	<li>Time provided from an external source (if the RosUtils.setTimeSlave() 
 * is used) (experimental)</li>
 * </ul>
 * 
 * @author Jaroslav Vitku
 *
 */
public class TimeAwareNode extends AbstractTimeNode{

	public static final String name = "TimeAwareNode";
	public static final String me = "["+name+"] ";
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(name); }

	private final java.lang.String rosIn = "org/hanns/demonodes/pubsub/IN";
	protected final java.lang.String rosOut = "org/hanns/demonodes/pubsub/OUT";

	private float min;
	private float max;
	
	// just a helper for printing current parameters
	private ParameterTreeCrawler ptc;

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		super.onStart(connectedNode);

		System.out.println("Node started, initializing!");
		final Log log = connectedNode.getLog();
		
		ptc = new ParameterTreeCrawler(connectedNode.getParameterTree());
		log.info(me+"Listing all parameters from the parameter tree:");
		log.info(ptc.getAll());
		
		// define the publisher
		final Publisher<std_msgs.Float32MultiArray> publisher = 
				connectedNode.newPublisher(rosOut, std_msgs.Float32MultiArray._TYPE);

		// subscribe to given topic
		Subscriber<std_msgs.Float32MultiArray> subscriber = 
				connectedNode.newSubscriber(rosIn, std_msgs.Float32MultiArray._TYPE);
		
		subscriber.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			// print messages to console
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {
				float[] data = message.getData();
				//log.info("received these data: "+toAr(data));
				
				/**
				 * Here is the main part: getCurrentTime() provides either WallTime or the simulated time.
				 */
				Time t = connectedNode.getCurrentTime();					
				float seconds = (float)(t.secs+t.nsecs/1000000000.0);
				//log.info("seconds:"+seconds);
				min = min(data)*seconds;
				max = max(data)*seconds;
				log.info("Current time is: "+seconds+"");
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
