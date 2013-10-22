# Create network with two NeuralModules. 
#
# First is demoPublisher (sends 7 integers) and the second one is demoSubscriber (receives 7 integers and does nothing).
#
# by Jaroslav Vitku [vitkujar@fel.cvut.cz]

import nef
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils
# Note that modules without outputs (Nengo-decoder) cannot use synchronous mode (DefaultNeuralModule).
from ctu.nengoros.modules.impl import DefaultAsynNeuralModule as AsyncNeuralModule
# The simulator waits each simulation step for the output in the synchronous mode, this case:
from ctu.nengoros.modules.impl import DefaultNeuralModule as SyncNeuralModule

################# Create Network and add it to nengo (this must be here first) 
net=nef.Network('Publisher - Subscriber Simple Demo')
# Delete the old (toplevel) network and replace it with the newly CREATED one
net.add_to_nengo()  

################# setup the ROS utils (optional) 
#RosUtils.setAutorun(False)     # Do we want to autorun roscore and rxgraph? (tru by default)
#RosUtils.prefferJroscore(True)  # preffer jroscore before the roscore? 

################# Create Neural Module which contains the publisher ROS node
publisher = "org.hanns.demoNodes.DemoPublisher";# Java (ROS) node that does this job

# Create group of nodes
# This group is represented in Nengo as one SimpleNode, can contain one or entire network of ROS nodes.
# Independent groups are pushed into its own unique namespace (ROS comm. does not interfere)
g = NodeGroup("Publishing", True);        		# Create independent group? => True. 
# addNode() pass usually: what to launch (string), name, type {modem,java,native}
g.addNode(publisher, "Publisher", "java");    	# start java node and name it finder
module = SyncNeuralModule('Publisher', g) 	    # construct the Neural Module
module.createDecoder("hanns/demo/pubsub", "int",7) # define the data link
many=net.add(module)                    		# add it into the network

################# Create Neural Module which contains the publisher ROS node
subscriber = "org.hanns.demoNodes.DemoSubscriber";

g2 = NodeGroup("Subscribing", True);        	
g2.addNode(publisher, "Subscriber", "java");    	

# starts NeuralModule (nengoros term.) = SimpleNode (Nengo term.) = group of ROS nodes. Now add IO.
module2 = AsyncNeuralModule('Subscriber', g2)   
module2.createEncoder("hanns/demo/pubsub", "int",7)
many=net.add(module2)

################# Wire it together
net.connect(module.getOrigin('hanns/demo/pubsub'), module2.getTermination('hanns/demo/pubsub'))

print 'Configuration complete.'

