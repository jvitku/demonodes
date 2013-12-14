package org.hanns.demonodes.privateParams;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import ctu.nengoros.rosparam.impl.PrivateRosparam;

/**
 * Define number inputs and outputs by means of command line parameters.
 * 
 * This node does: sum(DataInputs)*sum(ConfigInputs) + a*sin(b*t)
 * 
 * Set the parameters from the command line as follows:
 * 
 * ./runner org.hanns.demonodes.privateParams.DefineIO _a:=3 _b:=2 _shouldLog:=true _noDataInputs:=2 _noDataOutputs:=10 _noDataConfigs:=1
 *  
 * @author Jaroslav Vitku
 *
 */
public class DefineIO extends AbstractNodeMain{

	public static final String NAME = "SetPrivateParametersNode";
	public final String me = "["+NAME+"] ";

	private PrivateRosparam r;	// handle private parameters

	protected final java.lang.String topicOut = "org/hanns/demonodes/params/";

	public final String topicDataOutBase = "org/hanns/demonodes/privateParams/dataOut_";
	public final String topicDataInBase = "org/hanns/demonodes/privateParams/dataIn_";
	public final String topicConfigInBase = "org/hanns/demonodes/privateParams/configIn_";

	private Publisher<std_msgs.Float32MultiArray>[] dataOutputs;
	private Subscriber<std_msgs.Float32MultiArray>[] dataInputs;
	private Subscriber<std_msgs.Float32MultiArray>[] configInputs;

	private float[] currentInputs;
	private float[] currentConfigs;

	/**
	 * parameters are here
	 */
	public int noDataInputs;
	public int noConfigInputs;
	public int noDataOutputs;

	public int a,b;
	public boolean shouldLog;

	/**
	 * default values of parameters
	 */
	public final int noDataInputsDEF = 2;
	public final int noConfigInputsDEF = 1;
	public final int noDataOutputsDEF = 10;
	public int defA = 1, defB=1;
	public final boolean defShouldLog = false;

	private float y; 
	private double t;

	private double small = 0.01;
	private Random rand = new Random();

	Log log;

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(NAME); }

	@Override
	public void onStart(final ConnectedNode connectedNode){
		log = connectedNode.getLog();
		
		// read parameters
		this.configureNode(connectedNode);

		log.info("----------------------------------------------------------------------");
		log.info("\nNode started, parameters are:\na="+a+"\nb="+b+"\nshouldLog="+shouldLog+"\n"+
				"noDataInputs="+noDataInputs+"\nnoConfigInputs="+noConfigInputs+"\nnoDataOutpus="
				+noDataOutputs);
		log.info("----------------------------------------------------------------------");

		// setup the nodes IO
		this.createConfigInputs(connectedNode);
		this.createDataInputs(connectedNode);
		this.createDataOutputs(connectedNode);

		// do this forever:
		connectedNode.executeCancellableLoop(new CancellableLoop() {

			@Override
			protected void setup() {
				y = 0;
				t = 0;
			}

			@Override
			protected void loop() throws InterruptedException {

				float sumInputs = 0;
				for(int i=0; i<currentInputs.length; i++){
					sumInputs += currentInputs[i];
				}
				float sumConfigs = 0;
				for(int i=0; i<currentConfigs.length; i++){
					sumConfigs += currentConfigs[i];
				}
				
				t = connectedNode.getCurrentTime().toSeconds();
				y = (float) (sumInputs*sumConfigs + a*Math.sin(b*t));
				
				if(shouldLog)
					System.out.println("(sums of inputs: "+sumInputs+", "+sumConfigs+") the result "
							+ "of computation is: "+y+"  \tdelivering with noise to all outputs");
				
				publishValue(y);
				
				Thread.sleep(200);
			}
		});
	}
	
	/**
	 * Read private parameters potentially passed to the node. 
	 */
	private void configureNode(ConnectedNode connectedNode){
		r = new PrivateRosparam(connectedNode);
		
		noDataInputs = r.getMyInteger("noDataInputs", noDataInputsDEF);
		noDataOutputs = r.getMyInteger("noDataOutputs", noDataOutputsDEF);
		noConfigInputs = r.getMyInteger("noConfigInputs", noConfigInputsDEF);

		a = r.getMyInteger("a", defA);
		b = r.getMyInteger("b",defB);
		shouldLog = r.getMyBoolean("shouldLog", defShouldLog);
	}

	@SuppressWarnings("unchecked")
	private void createDataInputs(ConnectedNode connectedNode){
		dataInputs = new Subscriber[noDataInputs];
		currentInputs = new float[noDataInputs];
		for(int i=0; i<dataInputs.length; i++){
			final int j = i;
			dataInputs[i] = connectedNode.newSubscriber(topicDataInBase+Integer.toString(i), std_msgs.Float32MultiArray._TYPE);
			currentInputs[i] = 0;
			dataInputs[i].addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
				// print messages to console
				@Override
				public void onNewMessage(std_msgs.Float32MultiArray message) {
					float[] data = message.getData();
					if(data.length != 1)
						log.error("Received message has unexpected length of"+data.length+"!");
					else{
						if(shouldLog)
							System.out.println("RECEIVED value on input "+j+" with the value: "+data[0]);
						currentInputs[j] = data[0]; 
					}
				}
			});
		}
	}

	@SuppressWarnings("unchecked")
	private void createConfigInputs(ConnectedNode connectedNode){
		configInputs = new Subscriber[noConfigInputs];
		currentConfigs = new float[noConfigInputs];

		for(int i=0; i<configInputs.length; i++){
			final int j=i;

			configInputs[j] = connectedNode.newSubscriber(topicConfigInBase+Integer.toString(i), std_msgs.Float32MultiArray._TYPE);
			currentConfigs[j] = 0;
			configInputs[j].addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
				// print messages to console
				@Override
				public void onNewMessage(std_msgs.Float32MultiArray message) {
					float[] data = message.getData();
					if(data.length != 1)
						log.error("Received config message has unexpected length of"+data.length+"!");
					else{
						if(shouldLog)
							System.out.println("RECEIVED value on input "+j+" with the value: "+data[0]);
						currentConfigs[j] = data[0]; 
					}
				}
			});
		}

	}

	@SuppressWarnings("unchecked")
	private void createDataOutputs(ConnectedNode connectedNode){
		dataOutputs = new Publisher[noDataOutputs];
		for(int i=0; i<dataOutputs.length; i++){
			dataOutputs[i] = connectedNode.newPublisher(topicDataOutBase+Integer.toString(i), std_msgs.Float32MultiArray._TYPE);
		}
	}

	
	private void publishValue(float y){
		
		float out; 
		for(int i=0; i<dataOutputs.length; i++){
			out = (float) (y+small*rand.nextDouble());
			
			std_msgs.Float32MultiArray mess = dataOutputs[i].newMessage();	
			mess.setData(new float[]{out});								
	        dataOutputs[i].publish(mess);
	        
	        if(shouldLog)
	        	System.out.println("Sending data to output no.:"+i+".. These data: "+out);
		}
	}
}

