HANNS project - Nodes for [....]
================================================

Author Jaroslav Vitku [vitkujar@fel.cvut.cz]


About
------
These projects are part of [Nengoros](https://github.com/jvitku/nengoros) system - simulator of large scale hybrid neural networks.


Demos
-------

Most of the tutorials can be found in README.md from the folder above, or [online](https://nengoros.wordpress.com/tutorials/). 

Here are some of other, smaller tutorials:


### Send the Clock data over the ROS network

Shows how to send Time information over the ROS network, to run the demo, start some roscore:

	cd nengoros/jroscore && ./jroscore
	
Start a node which periodically publishes Clock data (e.g. run from the class files) from this folder run:
	
	java -cp bin/:../../nengo/lib-rosjava/* org.ros.RosRun org.hanns.demonodes.time.pubsub.Pub
	

Start a node which subscribes to the topic `clock` and receives messages normally, run this one:
	
	java -cp bin/:../../nengo/lib-rosjava/* org.ros.RosRun org.hanns.demonodes.time.pubsub.Sub

Note that both nodes use `Wall` clock (time provided by the OS), so the subscriber also prints out these data, something like:

	GetCurrentTIme method says this: 1383703785:529000000

This time cannot be manipulated well. In order be able to control the speed of simulation, or playback the data, the ROS provides alternative for synchronizing Clock in the network.

### Synchronizing Clock in the ROS Network


We can run the same nodes, but now, we will setup the network where one ROS node will dictate the speed of time. 

To do this, start ROS nodes with the parameter telling them to use the simulation time. This can be set e.g. from the command line. Start the publisher as normally. By publishing Clock data to the clock topic, this node now dictates the speed of time for other nodes:

	java -cp bin/:../../nengo/lib-rosjava/* org.ros.RosRun org.hanns.demonodes.time.pubsub.Pub
	
Now run the subscriber which listens to the time (and also independently of this receives Clock messages in the code):

	java -cp bin/:../../nengo/lib-rosjava/* org.ros.RosRun org.hanns.demonodes.time.pubsub.Sub /use_sim_time:=true
	

	
	