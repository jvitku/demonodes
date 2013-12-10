# Demo which shows how the NengoRos can be setup to use its own simulation time and ROS nodes to use WallTime.
# @see: @see: http://nengoros.wordpress.com/rosjava-tutorials/
# @see: http://wiki.ros.org/roscpp/Overview/Time
#
# Demo is similar to the `basic/minmax.py` (starts two external ROS nodes), important are
# lines after this one: '################# setup the ROS utils (optional)'.
#
#
# by Jaroslav Vitku [vitkujar@fel.cvut.cz]

import nef
from ca.nengo.math.impl import FourierFunction
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model import Units
from ctu.nengoros.modules.impl import DefaultAsynNeuralModule as NeuralModule
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils

import basic_nodes

# creates nef network and adds it to nengo (this must be first in the script) 
net=nef.Network('Demo of simple Neural modules which find min and max, multiplies them by the current time and publishes')
net.add_to_nengo()  # here: delete old (toplevel) network and replace it with the newly CREATED one

################# setup the ROS utils (optional) 
RosUtils.setTimeIgnore()
#RosUtils.setTimeMaster()	# used by default
#RosUtils.setTimeSlave() 	# note: experimental, TODO

#RosUtils.setAutorun(False)     # Do we want to autorun roscore and rxgraph? (tru by default)
#RosUtils.prefferJroscore(True)  # preffer jroscore before the roscore? 


################# add ROS nodes
finderA = basic_nodes.sync_timeaware_node("MinMax Synchronous Float Finder - Values multiplied by the current time") 	# build the neural module
many=net.add(finderA)                        							# add it into the network

#Create a white noise input function with params: baseFreq, maxFreq [rad/s], RMS, seed
generator=FunctionInput('Randomized input', [FourierFunction(.1, 10,1, 12),
    FourierFunction(.4, 20,1.5, 11),
    FourierFunction(.1, 10,0.9, 10),
    FourierFunction(.5, 11,1.6, 17)],Units.UNK) 

net.add(generator) 													# Add to the network and connect to neuron
net.connect(generator,	finderA.getTermination('org/hanns/demonodes/pubsub/IN'))# connect generator to the termnation on our neural module

# create ANN with 100 neurons which approximates it's input 
A=net.make('A',neurons=50,dimensions=2,radius=8)    				# identity transformation implemented by population of neurons
net.connect(finderA.getOrigin('org/hanns/demonodes/pubsub/OUT'),A)	# connect the origin on our module to the network

print 'Configuration complete.'

print 'In this simulation, all started ROS nodes use WallTime (which is pretty arbitrary)'
print 'Nengo uses its own time represetnation'