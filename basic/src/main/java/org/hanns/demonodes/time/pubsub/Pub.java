package org.hanns.demonodes.time.pubsub;

import org.hanns.demonodes.params.ParameterTreeCrawler;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.Duration;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import rosgraph_msgs.Clock;


/**
 *  Time provider node,  @see http://wiki.ros.org/Clock
 *  
 *  If the /use_sim_time parameter is set, the ROS Time API will return time=0 until it has 
 *  received a value from the /clock topic [well, almost @see AbstractTimeNode]. 
 *  Then, the time will only be updated on receipt of a message from the /clock topic,
 *  and will stay constant between updates.
 *  
 *  Here, the Pub node publishes Clock messages on the clock topic. 
 *  These messages can be received normally by subscribers, or can serve as
 *  master time in the ROS network, if the nodes are started with the parameter /use_sim_time:=true 
 *  
 * @author Jaroslav Vitku
 *
 */
public class Pub extends AbstractNodeMain{

	protected final java.lang.String cl = "clock";
	private final int sleeptime = 1000;

	Publisher<rosgraph_msgs.Clock> pub;
	private Time tt;
	Duration dd;
	
	int speedup=10, slowdown=20; 

	/**
	 * Default name of the ROS node
	 */
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("Pub"); }

	/**
	 * Method called after launching the node. 
	 * After exiting this method, the node will stop working.
	 */
	@Override
	public void onStart(final ConnectedNode connectedNode) {

		ParameterTreeCrawler ptc = new ParameterTreeCrawler(connectedNode.getParameterTree());
		System.out.println("=========");
		ptc.printNames();
		pub = connectedNode.newPublisher(cl, rosgraph_msgs.Clock._TYPE);

		System.out.println("Now, getCurrentTime() method says: "+connectedNode.getCurrentTime().toString());

		tt = new Time(0);
		dd = new Duration(1, 10000);	// add some: sec, nsec
		System.out.println("---------Node started, initializing my own master Clock source with this: "+tt.nsecs);


		// ROS uses these cancellable loops
		connectedNode.executeCancellableLoop(new CancellableLoop() {

			int poc;
			
			@Override
			protected void setup() { 
				poc=0;
			}
			

			@Override
			protected void loop() throws InterruptedException {

				if((poc++) == speedup){
					System.out.println("\n\nSPEEDING UP the time!\n\n");
					dd = new Duration(0,500000000);	// set new duration to 0.5 sec
				}else if(poc==slowdown){
					System.out.println("\n\nSLOWING DOWN the time!\n\n");
					dd = new Duration(1,500000000);	// set new duration to 1.5 sec
				}
				
				Clock mess = pub.newMessage();
				mess.setClock(tt);

				pub.publish(mess);

				System.out.println("Now, getCurrentTime() method says: "+connectedNode.getCurrentTime().toString());
				System.out.println("SENDING this time value: "+tt.toString());

				tt=tt.add(dd);				// add duration
				Thread.sleep(sleeptime);

			}
		});
	}



}
