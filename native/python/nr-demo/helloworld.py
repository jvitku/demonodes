# Demo showing how the nengoros is able to launch Neural Module (group of nodes) consisting of:
#   -java default modem      = modem - class that converts data between ROS-Nengo. This one without data transfer.
#   -native C++ application  = native application (this one does not support ROS, so no communication).

# as a result, NeuralModule in nengo contains entire turtlesim:
#   -inputs to neuron control the turtle (linear and angular speeds)
#   -outputs from neuron correspond to turtle sensori data ( X,Y,theta, linear and angular speeds)

# note: that turtle application may run probably only on Ubuntu 12.04 64bit, ROS installation recommended
 
# by Jaroslav Vitku

import nef
from ca.nengo.math.impl import FourierFunction
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model import Units
from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule
from ctu.nengoros.modules.impl import DefaultAsynNeuralModule as AsynNeuralModule
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils

net=nef.Network('C++ Hello World - not a ROS node')
net.add_to_nengo()  


################# setup the ROS utils (optional) 
#RosUtils.setAutorun(False)     # Do we want to autorun roscore and rxgraph? (tru by default)
RosUtils.prefferJroscore(False) # Turlte prefers roscore before jroscore (don't know why..) 


helloworld = "../../demonodes/native/a.out"  

g = NodeGroup('HelloworldCPP', True); 
g.addNode(helloworld, "Helloworld", "native");
module = AsynNeuralModule("Helloworld", g) 
# when using this one: Nengo waits X ms each time step for message from node
# module = AsynNeuralModule("Helloworld", g)  
net.add(module);

#Create a white noise input function with parameters: baseFreq, maxFreq (rad/s), RMS, Seed
input=FunctionInput('Randomized input', [FourierFunction(.5, 10, 6, 12),
    FourierFunction(2, 11, 5, 17)],
    Units.UNK) 
net.add(input)  

print "OK, configuration done."
print "The simulator consists of one dummy node without IO (just a representation of external native process) and one funciton generator."
