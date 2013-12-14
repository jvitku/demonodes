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

from ctu.nengoros.modules.impl import DefaultAsynNeuralModule as AsynNeuralModule

import basic_nodes

# creates nef network and adds it to nengo (this must be first in the script) 
net=nef.Network('Create a simple Neural Module which is configured by this Jython script')
net.add_to_nengo()  # here: delete old (toplevel) network and replace it with the newly CREATED one

################# setup the ROS utils (optional) 
#RosUtils.setTimeIgnore() 	
#RosUtils.setTimeMaster() 	# used by default
#RosUtils.setTimeSlave() 	# note: experimental, TODO
#RosUtils.setAutorun(False)     # Do we want to autorun roscore and rxgraph? (tru by default)
#RosUtils.prefferJroscore(True)  # preffer jroscore before the roscore? 


################# add ROS node with configuration stored in `command`

rosparam =   	"org.hanns.demonodes.privateParams.SetPrivateParameters"
command = [rosparam, '_a:=2', '_b:=3', 'shouldLog:=true']


g = NodeGroup("Sinus", True);        			# create independent (True) group called..
g.addNode(command, "Sinus", "java");     		# start java node and name it finder
module = AsynNeuralModule("Configured Neural Module", g)# construct the Neural Module

# connect output
module.createDecoder("org/hanns/demonodes/params/", "float", 1)    # ..called ORIGIN of SimpleNode
many = net.add(module)

# create ANN with 100 neurons which approximates it's input 
A=net.make('A',neurons=50,dimensions=2,radius=8)    				# identity transformation implemented by population of neurons
net.connect(many.getOrigin('org/hanns/demonodes/params/'),A)		# connect the origin on our module to the network

print 'Configuration complete.'
