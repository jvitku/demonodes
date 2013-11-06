package org.hanns.demonodes.time;

import java.util.ArrayList;
import java.util.Collection;

import org.ros.namespace.GraphName;
import org.ros.node.parameter.ParameterTree;

public class ParameterTreeCrawler {

	private final String me = "[ParameterCrawler]";
	
	private final ParameterTree t;

	public ParameterTreeCrawler(ParameterTree par){
		t = par;
	}


	public void printAll(){
		System.out.println("aaaaaaaaaall names are: "+t.getNames().toString());
		
		
		System.out.println("nssssaaaaames "+t.getNames().toArray().toString());
	}
	
	
	public void printNames(){
		if(isEmpty())
			return;
		
		Collection<GraphName> l = t.getNames();
		GraphName[] g = l.toArray(new GraphName[0]);

		System.out.println("------------------------- names:");
		for(int i=0; i<g.length; i++){
			System.out.print(" "+g[i]);
		}
		System.out.println("------------------------- ------");
			
		
	}
	
	private boolean isEmpty(){
		if(t.getNames().isEmpty()){
			System.out.println(me+"Parameter three is empty");
			return true;
		}
		return false;
	}
}


