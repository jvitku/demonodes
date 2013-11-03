# Create two neural modules, demoPublisher and demoSubscriber and connect them by means of ROS network.
#
# Compared to pubsub_independent.py, this demo shows how to ROS nodes can be connected in the Nengo simulator.
# Each of Neural Modules has own modem and ROS node, both can be pushed into one namespace, so the ROS communication
# will not interfere with other nodes (here DemoPublisher with DemoSubscriber using the same topic). This is 
# set by the parameter last parameter in: g = NodeGroup("PubSubDemo", True);      // true => independent communication  			
#
# by Jaroslav Vitku [vitkujar@fel.cvut.cz]
#

import nef
from ca.nengo.math.impl import FourierFunction
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model import Units
#from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule
from ctu.nengoros.modules.impl import DefaultAsynNeuralModule as AsynNeuralModule
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils

pub = "org.hanns.demonodes.pubsub.DemoPublisher"
sub = "org.hanns.demonodes.pubsub.DemoSubscriber"

################# setup the ROS utils (optional) 
#RosUtils.setAutorun(False)     # Do we want to autorun roscore and rxgraph? (tru by default)
#RosUtils.prefferJroscore(True)  # preffer jroscore before the roscore

# creates nef network and adds it to nengo (this must be first in the script) 
net=nef.Network('Two Neural modules containing ROS nodes with communication shielded from each other')
net.add_to_nengo()  # here: delete old (toplevel) network and replace it with the newly CREATED one
					# any poteintial running ROS nodes are sopped here

################ Run two groups of nodes (conenct them in nengo)
g = NodeGroup("Publisher", True);        			
g.addNode(pub, "Publisher", "java");     	# add the publisher node
module = AsynNeuralModule("Publisher", g)    		
module.createDecoder("org/hanns/demonodes/pubsub", "float", 7) 
net.add(module)	

g2 = NodeGroup("Subbscriber", True);        			
g2.addNode(sub, "Subbscriber", "java");     	# add the publisher node
module2 = AsynNeuralModule("Subscriber", g2)    		
module2.createEncoder("org/hanns/demonodes/pubsub", "float", 7)
net.add(module2)	

net.connect(module.getOrigin('org/hanns/demonodes/pubsub'),module2.getTermination('org/hanns/demonodes/pubsub'))						

print 'Configuration complete - communication between ROS nodes is handled by the Nengo now'
print 'Note that both nodes are running:'
print '-Publisher periodically publishes data'
print '-Subscriber does not receive any data, because Nengo simulation is not running and data are not sent'
print 'Try to start the simulation'
print ''
print 'Note that due to asynchornous communication, the sample rate of Nengo is faster than publishing rate of Publisher'



