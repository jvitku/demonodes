# Create network with two NeuralModules. 
# First is demoPublisher (sends 7 integers) and the second one is demoSubscriber (receives 7 integers and does nothing).
#
# by Jaroslav Vitku [vitkujar@fel.cvut.cz]

import nef
#from ca.nengo.model import Units
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils
# Note that modules without outputs (Nengo-decoder) cannot use synchronous mode (DefaultNeuralModule).
from ctu.nengoros.modules.impl import DefaultAsynNeuralModule as AsyncNeuralModule
# The simulator waits each simulation step for the output in the synchronous mode, this case:
from ctu.nengoros.modules.impl import DefaultNeuralModule as SyncNeuralModule


################# Create Network and add it to nengo (this must be here first) 
net=nef.Network('Publisher - Subscriber Simple Demo')
# delete old (toplevel) network and replace it with the newly CREATED one
net.add_to_nengo()  

################# setup the ROS utils (optional) 
#RosUtils.setAutorun(False)     # Do we want to autorun roscore and rxgraph? (tru by default)
#RosUtils.prefferJroscore(True)  # preffer jroscore before the roscore? 

mod  = "ctu.nengoros.comm.nodeFactory.modem.impl.DefaultModem"; # Use the default modem

################# Create neural module which contains the publisher ROS node
publisher = "org.hanns.myPackage.DemoPublisher"; # Java (ROS) node that does this job

# Create group of nodes
# This group is represented in Nengo as one SimpleNode, can contain one or entire network of ROS nodes.
# Independent groups are pushed into its own unique namespace (ROS comm. does not interfere)
g = NodeGroup("Publishing", True);        		# Create independent group? => True. 
g.addNC(publisher, "Publisher", "java");    	# start java node and name it finder
g.addNC(mod,"Modem","modem")     	        	# add modem to the group
g.startGroup()

modem = g.getModem()
module = SyncNeuralModule('Publisher', modem) 	# construct the Neural Module
module.createDecoder("hanns/demo/pubsub", "int",7) # define the data link
many=net.add(module)                    		# add it into the network


################# create neural module which contains the publisher ROS node
subscriber = "org.hanns.myPackage.DemoSubscriber";

g2 = NodeGroup("Subscribing", True);        	
g2.addNC(publisher, "Subscriber", "java");    	
g2.addNC(mod,"Modem","modem")     	        	
g2.startGroup()

modem2 = g.getModem()
module2 = AsyncNeuralModule('Subscriber', modem2)
module2.createEncoder("hanns/demo/pubsub", "int",7)
many=net.add(module2)                    		

################# Wire it together
net.connect(module.getOrigin('hanns/demo/pubsub'),module2.getTermination('hanns/demo/pubsub'))

print 'Configuration complete.'

