# Demo which shows how the NengoRos can be setup to use externally provided simulation time.
# In this case, the Nengo simulator will subscribe to the /clock topic. At each time step it will
# wait for a new stime tick (new time value) on this topic. If new value received, one Nengo simulation step 
# is made. 
#
# @see: @see: http://nengoros.wordpress.com/rosjava-tutorials/
#
# This demo shows the scenario where time is provided from external source, e.g. real world robotic system 
# or some external (environment) simulator.
#
# Note that external time provider can be launched completely externally or as an ordinary NeuralModule
# from the Nengoros. But in both cases command line parameter /use_sim_time:=false .
#
# Note that this is not ideally implemented and not tested very much, so the received time is used in the 
# NodeThreadPool.step() method, but e.g. the Nengo interactive simulaiton GUI does not reflect these values.
#
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
#RosUtils.setTimeIgnore() 	
#RosUtils.setTimeMaster() 	# used by default
RosUtils.setTimeSlave() 	# note: experimental, TODO

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
print 'In this configuration, the Nengo is launched as a TimeSlave '
print 'and waits for data on the /clock topic.'
print 'Now it is expected that you start some external TimeProvider, which'
print 'publishes these data'

print 'Note that this is experimental and e.g. GUI does not reflect time values received.'
print 'Note that ROS nodes will not be completely inifialized before any time data received'

print ''
print 'To run the clockMaster, use e.g. from the folder nengoros/demonodes/basic the command:'
print './runner org.hanns.demonodes.time.pubsub.Pub /use_sim_time:=false'

