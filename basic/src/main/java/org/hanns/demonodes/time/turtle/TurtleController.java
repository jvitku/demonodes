package org.hanns.demonodes.time.turtle;

import org.ros.internal.message.Message;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;


public class TurtleController extends AbstractNodeMain{

	private final String myTopic = "pose";

	@Override
	public void onStart(ConnectedNode connectedNode){
		publisher = connectedNode.newPublisher(myTopic, MYTYPE);
		rosMessage = publisher.newMessage();	
		
		MessageListener<geometry_msgs.Twist> ml = 
				new MessageListener<geometry_msgs.Twist>() {

			@Override
			public void onNewMessage(geometry_msgs.Twist f) {
	//			thisone.checkDimensionSizes(f);
				thisone.setReceivedRosMessage(f);		// save obtained Message
				thisone.fireOnNewMessage(f);			// inform Nengo about new ROS message
			}
			
	}
	

	// type of messages we can process here
	public static final String MYTYPE="geometry_msgs/Twist";
	
	private String myTopic;
	
	private final int messageLength = 6;	// 2x3 floats: linear and angular velocity in x,y,z
	private geometry_msgs.Twist rosMessage;
	
	Publisher<geometry_msgs.Twist> publisher;
	Subscriber<geometry_msgs.Twist> subscriber;

	private boolean pub;	// whether to publish or subscribe
	
	private final String me = "[TwistBackend] ";
	

	/**
	 * This thing gets data of given format and publishes them as a ROS message 
	 * to a specified topic. This methods hold the necessary transformations and publishing.
	 */
	@Override
	public void publish(float[] data){
		if(!pub){
			System.err.println(me+"I am set only to subscribe!");
			return;
		}
		if(data.length != messageLength){
			System.err.println(me+"wrong dimension of data: message: "+turtlesim.Pose._TYPE+
					" is composed of "+messageLength+" floats");
			return;
		}
		rosMessage = publisher.newMessage();
		
		rosMessage.getAngular().setX(data[0]);
		rosMessage.getAngular().setY(data[1]);
		rosMessage.getAngular().setZ(data[2]);
		
		rosMessage.getLinear().setX(data[3]);
		rosMessage.getLinear().setY(data[4]);
		rosMessage.getLinear().setZ(data[5]);
		
        publisher.publish(rosMessage);
	}

	public void setReceivedRosMessage(geometry_msgs.Twist mess){
		this.rosMessage=mess;
	}
	
	/**
	 * On each new message from ROS: store it, fire event: Backend:NewMessage.
	 * @param me this class
	 * @return ROS message listener
	 */
	private MessageListener<geometry_msgs.Twist> buildML(final TwistBackend thisone){
		
		MessageListener<geometry_msgs.Twist> ml = 
				new MessageListener<geometry_msgs.Twist>() {

			@Override
			public void onNewMessage(geometry_msgs.Twist f) {
	//			thisone.checkDimensionSizes(f);
				thisone.setReceivedRosMessage(f);		// save obtained Message
				thisone.fireOnNewMessage(f);			// inform Nengo about new ROS message
			}
		};
		return ml;
	}
	
	/**
	 * Get message data (this receives arrays of ints, just cast them to floats).
	 */
	@Override
	public float[] decodeMessage(Message mess) {
		
		float[] data = new float[messageLength];
		// read linear velocity
		data[0] = (float) (((geometry_msgs.Twist)mess).getLinear()).getX();
		data[1] = (float) (((geometry_msgs.Twist)mess).getLinear()).getY();
		data[2] = (float) (((geometry_msgs.Twist)mess).getLinear()).getZ();
		
		// read angular velocity
		data[3] = (float) (((geometry_msgs.Twist)mess).getAngular()).getX();
		data[4] = (float) (((geometry_msgs.Twist)mess).getAngular()).getY();
		data[5] = (float) (((geometry_msgs.Twist)mess).getAngular()).getZ();
		
		return data;
	}

	@Override
	public GraphName getDefaultNodeName() {
		// TODO Auto-generated method stub
		return null;
	}
	

	
}
