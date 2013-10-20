# Create network with two NeuralModules. 
# First is demoPublisher (sends 7 integers) and the second one is demoSubscriber (receives 7 integers and does nothing).
#
# by Jaroslav Vitku [vitkujar@fel.cvut.cz]

import nef
from ca.nengo.math.impl import FourierFunction
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model import Units
from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils

# creates nef network and adds it to nengo (this must be first in the script) 
net=nef.Network('Publisher - Subscriber Simple Demo')
net.add_to_nengo()  # here: delete old (toplevel) network and replace it with the newly CREATED one

################################## 
################# setup the ROS utils (optional) 
#RosUtils.setAutorun(False)     # Do we want to autorun roscore and rxgraph? (tru by default)
#RosUtils.prefferJroscore(True)  # preffer jroscore before the roscore? 

mod  = "ctu.nengoros.comm.nodeFactory.modem.impl.DefaultModem"; # custom modem here

##################################
################# create neural module which contains the publisher ROS node
publisher = "org.hanns.myPackage.DemoPublisher";      # Java (ROS) node that does this job

# # create group with a name
g = NodeGroup("Publishing", True);        # create independent group called..
g.addNC(publisher, "Publisher", "java");    # start java node and name it finder
g.addNC(mod,"Modem","modem")     	        # add modem to the group
g.startGroup()

modem = g.getModem()
module = NeuralModule('Publisher', modem) # construct the Neural Module
module.createDecoder("hanns/demo/pubsub", "int",7) # define the data link
many=net.add(module)                    # add it into the network

##################################
################# create neural module which contains the publisher ROS node
subscriber = "org.hanns.myPackage.DemoSubscriber";

# create group with a name
g2 = NodeGroup("Subscribing", True);        # create independent group called..
g2.addNC(publisher, "Subscriber", "java");    # start java node and name it finder
g2.addNC(mod,"Modem","modem")     	        # add modem to the group
g2.startGroup()

modem2 = g.getModem()
module2 = NeuralModule('Subscriber', modem2) # construct the Neural Module
module2.createEncoder("hanns/demo/pubsub", "int",7) # define the data link
many=net.add(module2)                    # add it into the network

#################################
################# Wire it together
net.connect(module.getOrigin('hanns/demo/pubsub'),module2.getTermination('hanns/demo/pubsub'))


print 'Configuration complete.'
