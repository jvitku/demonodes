package org.hanns.demonodes.params;

import org.apache.commons.logging.Log;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.parameter.ParameterTree;

import ctu.nengoros.rosparam.ParameterTreeCrawler;

public class Setter extends AbstractNodeMain{

	Log log;

	private final String me="setter";
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		//connectedNode.
		ParameterTree pt = connectedNode.getParameterTree();
		ParameterTreeCrawler ptc = new ParameterTreeCrawler(pt);
		ptc.printAll();
		
		//System.out.println("parameter tree is empty? "+pt.get)
		
		System.out.println("\n-- \n");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		pt.set("use_sim_time", true);
		//pt.set(GraphName.of("/use_sim_time"), true);
		//pt.set("/use_sim_time", true);
		//pt.set(arg0, arg1)
		
		ptc.printAll();
		
		ParameterTree ptt = connectedNode.getParameterTree();
		ParameterTreeCrawler ptcc = new ParameterTreeCrawler(ptt);
		ptcc.printAll();
	}

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(me); }
}
