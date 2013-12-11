package org.hanns.demonodes.time.turtle;

import geometry_msgs.Twist;

import org.ros.concurrent.CancellableLoop;
import org.ros.message.Duration;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

//import ctu.nengoros.
import ctu.nengoros.rosparam.ParameterTreeCrawler;
import rosgraph_msgs.Clock;

public class TurtleController extends AbstractNodeMain{

	private final String myTopic = "turtle1/cmd_vel";
	Publisher<geometry_msgs.Twist> publisher;

	Publisher<rosgraph_msgs.Clock> timePublisher;
	protected final java.lang.String cl = "/clock";
	private final int sleeptime = 1000;

	private final String me = "[TwistBackend] ";

	Time t;
	Duration slowD = new Duration(0,500000);
	Duration fastD = new Duration(3,5000000);

	ParameterTreeCrawler ptc;
	
	@Override
	public void onStart(final ConnectedNode connectedNode){
		publisher = connectedNode.newPublisher(myTopic, geometry_msgs.Twist._TYPE);
		timePublisher = connectedNode.newPublisher(cl, rosgraph_msgs.Clock._TYPE);

//		t = connectedNode.getCurrentTime();
		t = new Time(0);
		ptc = new ParameterTreeCrawler(connectedNode.getParameterTree());

		connectedNode.executeCancellableLoop(new CancellableLoop() {

			int poc;
			boolean slow;
			@Override
			protected void setup() {
				poc=0;
				slow = true;
			}


			@Override
			protected void loop() throws InterruptedException {
				ptc.printNames();
				ptc.printAll();
				poc++;

				if(poc % 10==0){
					if(slow)
						slow = false;
					else
						slow = true;
				}
				if(slow)
					t = t.add(slowD);
				else
					t = t.add(fastD);


				// send time 
				Clock mess = timePublisher.newMessage();
				mess.setClock(t);
				timePublisher.publish(mess);

				System.out.println("SENDING this time value: "+t.toString());
				System.out.println("SENDING this data: ");
				
				// send command
				float[] f = generateData();
				send(f);
				toAr(f);
				Thread.sleep(sleeptime);
			}
		});
	}


	private float[] generateData(){
		float[] out = new float[]{0,0,1,1,0,0};

		//out[2] = 5*(float) Math.sin(t.toSeconds());
		
	//	out[4] = 5*(float) Math.cos(t.toSeconds());

		return out;
	}


	private void send(float[] data){
		if(data.length != 6){
			System.err.println(me+"Wrong length of the message!");
			return;
		}
		Twist rosMessage = publisher.newMessage();

		rosMessage.getAngular().setX(data[0]);
		rosMessage.getAngular().setY(data[1]);
		rosMessage.getAngular().setZ(data[2]);

		rosMessage.getLinear().setX(data[3]);
		rosMessage.getLinear().setY(data[4]);
		rosMessage.getLinear().setZ(data[5]);

		publisher.publish(rosMessage);
	}

	private void toAr(float[] data){
		System.out.println(" ");
		for(int i=0;i<data.length; i++)
			System.out.print(" "+data[i]);
		System.out.println(" ");
	}

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("turtlecommander"); }
}


