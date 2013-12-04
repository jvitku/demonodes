package org.hanns.demonodes.params;

import java.util.Collection;

import org.ros.namespace.GraphName;
import org.ros.node.parameter.ParameterTree;

public class ParameterTreeCrawler {

	private final String me = "[ParameterCrawler] ";

	private final ParameterTree t;

	public ParameterTreeCrawler(ParameterTree par){
		t = par;
	}


	public void printAll(){

		if(isEmpty()){
			System.err.println("parameterTree is empty!");
			return;
		}

		Collection<GraphName> l = t.getNames();
		GraphName[] g = l.toArray(new GraphName[0]);

		System.out.println("------------------------- parameters are:");
		for(int i=0; i<g.length; i++){
			//System.out.print("par: "+g[i]);
			System.out.println("par: "+g[i]+"  \tval: "+this.getParam(g[i]));
		}
		System.out.println("------------------------- ");

	}

	/**
	 * SInce we do not know the type of the parameter, 
	 * test the most common ones, 
	 * @see http://docs.rosjava.googlecode.com/hg/rosjava_core/html/getting_started.html
	 * 
	 * @param name name of the parameter
	 * @return value of the parameter casted to string
	 */
	private String getParam(GraphName name){
		for(int i=0;i<=nofcns; i++){
			//System.out.println
			try{
				return gp(name,i);
			}catch(Exception e){
			}
		}
		System.err.println("Parameter type not found!!!!");
		return null;
	}

	int nofcns=3;

	private String gp(GraphName name, int no) throws Exception{
		if(no<0 || no>nofcns){
			System.err.println("bad numner");
			return null;
		}
		switch(no){
		case 0:
			return String.valueOf(t.getBoolean(name));
		case 1:
			return String.valueOf(t.getDouble(name));
		case 2:
			return String.valueOf(t.getInteger(name));
		case 3:
			return t.getString(name);
		}
		throw new Exception();
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
		System.out.println("\n-------------------------------");


	}

	private boolean isEmpty(){
		if(t.getNames().isEmpty()){
			System.out.println(me+"xxxxxxxxxxxxxxxxxxxxx !!!!!!!!!!!!!!!!!!! Parameter three is empty");
			return true;
		}
		return false;
	}
}


