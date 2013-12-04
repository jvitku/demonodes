package org.hanns.demonodes.params;

import org.apache.commons.logging.Log;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.parameter.ParameterTree;

public class Setter extends AbstractNodeMain{

	

	Log log;

	private final String me="setter";
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		ParameterTree pt = connectedNode.getParameterTree();
		ParameterTreeCrawler ptc = new ParameterTreeCrawler(pt);
		ptc.printAll();
		
		//System.out.println("parameter tree is empty? "+pt.get)
		
		System.out.println("\n-- \n");
		
		//pt.set("use_sim_time", true);
		//pt.set(GraphName.of("/use_sim_time"), true);
		//pt.set("/use_sim_time", true);
		//pt.set(arg0, arg1)
		
		
		ptc.printAll();
	}

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(me); }
}
