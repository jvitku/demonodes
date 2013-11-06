package org.hanns.demonodes.time;

import org.apache.commons.logging.Log;
import org.ros.Parameters;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.parameter.ParameterTree;

/**
 * Extend this class in order to write ROS node with time representation.
 *  
 * The main purpose of this class is workaround of these: 
 * http://code.google.com/p/rosjava/issues/detail?id=148
 * 
 * http://code.google.com/p/rosjava/issues/detail?id=120
 * 
 * Call the super.onStart(connectedNode) first in your class. 
 *  
 * @author Jaroslav Vitku
 *
 */
public abstract class AbstractTimeNode extends AbstractNodeMain{

	Log log;

	private final String me="[AbstractTimeNode] ";
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		ParameterTree pt = connectedNode.getParameterTree();		

		if(pt.has(Parameters.USE_SIM_TIME)){
			boolean waited=false;
			// try to read the current time (time provider node probably not started yet)
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
		}else{
			System.err.println(me+"Error! This node is supposed to be launched" +
					" with parameter: /use_sim_time:=true, "+
					"but this parameter is not set! Will use the wall time instead.");
		}
		log = connectedNode.getLog();
	}


}
