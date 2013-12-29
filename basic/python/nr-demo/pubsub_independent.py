# Create two neural modules, demoPublisher and demoSubscriber and connect them by means of ROS network.
#
# This demo shows that Nengoros can launch also sub-networks of ROS nodes (more than one node), which 
# may or may-not communicate with Nengoros. 
#
# by Jaroslav Vitku [vitkujar@fel.cvut.cz]
#

import nef
from ca.nengo.math.impl import FourierFunction
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model import Units
from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils

pub = "org.hanns.demonodes.pubsub.DemoPublisher"
sub = "org.hanns.demonodes.pubsub.DemoSubscriber"

################# setup the ROS utils (optional) 
#RosUtils.setAutorun(False)     # Do we want to autorun roscore and rxgraph? (tru by default)
#RosUtils.prefferJroscore(True)  # preffer jroscore before the roscore

# creates nef network and adds it to nengo (this must be first in the script) 
net=nef.Network('Neural module - sub-network of ROS nodes - independent of Nengo simulation')
net.add_to_nengo()  # here: delete old (toplevel) network and replace it with the newly CREATED one
					# any poteintial running ROS nodes are sopped here

################ Run group of nodes which is independent of Nengo simulator 
# In this case, nodes are staarted after adding into the simulator (as usual) 
# and stopped after removing from the network. Their communication is not affected by Nengo
#
# This provides handy alternative for launching arbitrary ROS nodes.
#g = NodeGroup("PubSubDemo", False);					# will not "push" topics of node communication into own namespace
g = NodeGroup("PubSubDemo", True);
g.addNode(pub, "IndependentPublisher", "java");     	# add the publisher node
g.addNode(sub, "IndependentSubscriber", "java");     	# add the subscriber node
module = NeuralModule("Independent_PubSubDemo", g)    		

net.add(module)	# nodes are launched here

print 'Configuration complete, nodes launched and already communicate, see the commandline.'
print 'Try deleting the node from GUI.'
