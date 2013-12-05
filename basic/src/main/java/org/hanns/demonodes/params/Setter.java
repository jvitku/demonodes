package org.hanns.demonodes.params;

import org.apache.commons.logging.Log;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.parameter.ParameterTree;

import ctu.nengoros.rosparam.ParameterTreeCrawler;

/**
 * Writes a parameter to the tree, can be seen by other nodes even after
 * this node is down.
 * 
 * In order to use this node with simulated time, the parameter use_sim_time has to be:
 * -either in the parameter tree BEFORE starting this node
 * -passed as a commandLine remapping in the format: /use_sim_time:=true
 * 
 * @author Jaroslav Vitku
 *
 */
public class Setter extends AbstractNodeMain{

	Log log;

	private final String me="setter";
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		
		ParameterTree pt = connectedNode.getParameterTree();
		ParameterTreeCrawler ptc = new ParameterTreeCrawler(pt);
		System.out.println("BEFORE SETTING----------------");
		ptc.printAll();
		
		System.out.println("\n-- \n");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		pt.set("use_sim_time", true);
		
		System.out.println("AFTER SETTING----------------");
		ptc.printAll();
		
		ParameterTree ptt = connectedNode.getParameterTree();
		ParameterTreeCrawler ptcc = new ParameterTreeCrawler(ptt);
		ptcc.printAll();
	}

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(me); }
}
