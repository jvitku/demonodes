package org.hanns.demonodes.time.pubsub;

import org.hanns.demonodes.params.ParameterTreeCrawler;
import org.hanns.demonodes.time.AbstractTimeNode;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

/**
 * Shows that time can be received as a normal message. 
 * If the node is not launched with the parameter for use the remote time, the Wall clock (time from OS) will be used.
 * To use time publushed by time provider, use this command line parameter (e.g. from the project folder):
 * 
 * java -cp bin/:../../nengo/lib-rosjava/* org.ros.RosRun org.hanns.demonodes.time.pubsub.Sub use_sim_time:=true
 *
 * 
 * @author Jaroslav Vitku
 *
 */
public class Sub extends AbstractTimeNode {
	// paaaaaaaaaaraaaaaaaaaaaameeeeeeeeee
	String cl = "/clock";
	private final int sleeptime = 300;
	Subscriber<rosgraph_msgs.Clock> subscriber;
	
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("Sub"); }
	
	@Override
	public void onStart(ConnectedNode connectedNode){
		super.onStart(connectedNode);
		
		ParameterTreeCrawler ptc = new ParameterTreeCrawler(connectedNode.getParameterTree());
		System.out.println("=========");
		ptc.printNames();
		
		// subscribe to given topic
		subscriber = connectedNode.newSubscriber(cl, rosgraph_msgs.Clock._TYPE);

		subscriber.addMessageListener(new MessageListener<rosgraph_msgs.Clock>() {
			// print messages to console
			@Override
			public void onNewMessage(rosgraph_msgs.Clock message) {
				Time tt = message.getClock();
				
					System.out.println("----RECEIVED message with these data:"+tt.toString());
				}
		});
		
		this.showTime(connectedNode);
	}

	private void showTime(final ConnectedNode connectedNode){

		// ROS uses these cancellable loops
		connectedNode.executeCancellableLoop(new CancellableLoop() {
			private int poc;

			@Override
			protected void setup() {
				poc = 0;
			}

			@Override
			protected void loop() throws InterruptedException {
				
				System.out.println("(mess.no.+"+poc+++") Now, getCurrentTime() method says: "+connectedNode.getCurrentTime().toString());
				Thread.sleep(sleeptime);
			}
		});
	}
}
