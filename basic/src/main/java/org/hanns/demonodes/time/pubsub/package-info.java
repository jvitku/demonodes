/**
 * Demo about sending time Clock data over the ROS network: one ROS node is a clock master 
 * (typically some simulator) and others read the time.
 * 
 * The topic clock, with message type rosgraph_msgs.Clock is used for synchronizing 
 * time in the ROS network (e.g. speed-up, replay etc). @see http://wiki.ros.org/Clock
 * 
 * Demos:
 * 1) 	-start Sub node with normal time:
 * 			./runner org.hanns.demonodes.time.pubsub.Sub /use_sim_time:=false
 * 		-(false by default)
 * 		-observe normal clock
 * 
 * 2) 	-start Sub node with simulated time:
 * 			./runner org.hanns.demonodes.time.pubsub.Sub /use_sim_time:=true  
 * 		-(node is waiting for the time provider)
 * 		-start Pub node, the time provider:
 * 			./runner org.hanns.demonodes.time.pubsub.Pub /use_sim_time:=false
 * 		-(node speeds up and slows down its own time, this time is received by the Sub node asynchronously)
 * 
 * 3) 	-it is possible to set /use_sim_time value to the parameter tree
 * 		-(from the folder jrosparam) run this command:
 * 			./jrosparam set /use_sim_time true
 * 		-now, all ROS nodes will be launched with simulated time by default
 * 
 * Details about these demos are in the demonodes/basic/README.md
 * 
 */
/**
 * @author Jaroslav Vitku
 *
 */
package org.hanns.demonodes.time.pubsub;