package org.hanns.myPackage.fuzzy.membership.impl;

import org.hanns.myPackage.fuzzy.membership.Membership;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;

/**
 * Compute triangle fuzzy membership function.
 * @see: file full of errors: http://www.inf.ufpr.br/aurora/disciplinas/topicosia2/livros/search/FR.pdf
 * 
 * @author Jaroslav Vitku
 */
public class Triangular extends Membership {

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of("FuzzyTriangleMembership"); }

	/**
	 * Compute triangle membership function,
	 * @see: http://www.inf.ufpr.br/aurora/disciplinas/topicosia2/livros/search/FR.pdf 
	 */
	@Override
	protected float compute() {
		if(x<alpha || x>=gamma)
			return 0;

		if(x<=beta)
			return ((x-alpha)/(beta-alpha));

		return -((x-gamma)/(gamma-beta));
	}

	@Override
	public void checkRanges() {

		if(beta < alpha){
			//alpha = super.getAverage(alpha, beta);
			alpha = beta;
		}
		// probably cannot move beta already
		if(gamma < beta){
			gamma = beta;
		}
	}

	@Override
	public void onStart(ConnectedNode connectedNode){
//		super.onStart(connectedNode);
		
		log = connectedNode.getLog();
		this.getDataChannel(connectedNode);
		
		this.initAlpha(connectedNode);
		this.initBeta(connectedNode);
		this.initGamma(connectedNode);

		super.nodeIsPrepared();	// indicate that everything is configured
	}
	


}
