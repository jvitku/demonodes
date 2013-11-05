package org.hanns.myPackage.fuzzy.membership;

import static org.junit.Assert.*;

import org.junit.Test;

import ctu.nengoros.RosRunner;

public class CommTests extends ctu.nengoros.nodes.RosCommunicationTest{

	/**
	 * Testing of triangle could be more extensive.
	 */
	@Test
	public void triangleTest() {
		
		RosRunner gate = runNode("org.hanns.myPackage.fuzzy.membership.impl.Triangular");
		RosRunner rr = runNode("org.hanns.myPackage.fuzzy.membership.ThreeParamTester");
		ThreeParamTester mt = (ThreeParamTester)rr.getNode();
		
		// setup the node (online) and check results of computation
		mt.changeMembershipFcnParameter(-0.5f,0,0); 
		mt.changeMembershipFcnParameter(-0.5f,0,0.5f);
		
		assertTrue(mt.computeRempotely(10)==0);
		assertTrue(mt.computeRempotely(-10)==0);
		assertTrue(mt.computeRempotely(5)==0);
		assertTrue(mt.computeRempotely(-5000)==0);
		assertTrue(mt.computeRempotely(200)==0);
		
		assertTrue(mt.computeRempotely(-0.5f) == 0);
		assertTrue(mt.computeRempotely(0.5f) == 0);
		
		assertTrue(mt.computeRempotely(0) == 1);
		
		assertTrue(mt.computeRempotely(-0.25f) == 0.5f);
		assertTrue(mt.computeRempotely(0.25f) == 0.5f);
		
		rr.stop();
		gate.stop();
	}
}
