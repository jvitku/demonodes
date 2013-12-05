package org.hanns.demonodes.time.pubsub;

import java.util.Map;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.rosparam.ParameterTreeCrawler;

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
public class Subb extends AbstractNodeMain {
	// paaaaaaaaaaraaaaaaaaaaaameeeeeeeeee
	private final int sleeptime = 300;
	private final String me = "[Pubb] ";
	Subscriber<rosgraph_msgs.Clock> subscriber;


	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("Sub"); }

	@Override
	public void onStart(ConnectedNode connectedNode){

		//super.onStart(connectedNode);

		ParameterTreeCrawler ptc = new ParameterTreeCrawler(connectedNode.getParameterTree());
		ptc.printAll();
		System.out.println(GraphName.of("use_sim_time"));
		Map<GraphName,GraphName> map = connectedNode.getResolver().getRemappings();
		
		System.out.println("remapped?? "+
				connectedNode.getResolver().getRemappings().containsKey(GraphName.of("/use_sim_time")));
		
		boolean waited=false;
		// try to read the current time (time provider node probably not started yet)
		// here, the CalcellableLoop cannot be executed without time provider, 
		// so until the time provider is found, the node is hard to kill:-)
		while(true){
			try{
				
				connectedNode.getCurrentTime();
				System.out.println(me+"time found, it is:"+connectedNode.getCurrentTime());
				break;
			}catch (NullPointerException e) {
				try {
					if(waited)
						System.out.print(".");
					else{
						waited = true;
						System.out.print(me+"waiting for sim_time provider..");
					}

					Thread.sleep(100);
				} catch (InterruptedException e1) {
				}
			}
		}

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
