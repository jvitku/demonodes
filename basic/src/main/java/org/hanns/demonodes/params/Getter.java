package org.hanns.demonodes.params;

import org.apache.commons.logging.Log;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.parameter.ParameterTree;

import ctu.nengoros.rosparam.ParameterTreeCrawler;

/**
 * Just prints out all parameters from the parameter tree.
 * 
 * @author Jaroslav Vitku
 *
 */
public class Getter extends AbstractNodeMain{

	Log log;

	private final String me="getter";
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		ParameterTree pt = connectedNode.getParameterTree();
		ParameterTreeCrawler ptc = new ParameterTreeCrawler(pt);
		ptc.printAll();
	}

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(me); }
}
