# Simple (Drag and drop template for) NeuralModule. 
#
# This NeuralModule represents subscriber ROS(java) node (node with inputs) in the Nengo simulator as a SimpleNode. 
#
# author Jaroslav Vitku
#
# For more information how to make such template, see: http://nengo.ca/docs/html/advanced/dragndrop.html 
# For mode information about integration with ROS, see the script demonodes/projectTemplate/python/myNetwork.py 

import nef
from ctu.nengoros.modules.impl import DefaultAsynNeuralModule as NeuralModule 
#	from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils

# node utils..
title='demoPublisher'
label='demoPublisher'
icon='demoPublisher.png'

# parameters for initializing the node
params=[
('name','Select name for NeuralModule interfacing the demoPublisher',str),
('independent','Can be group independent? (pushed into namespace?) select true',bool)
]

# try to instantiate node with given parameters (e.g. check name..)
def test_params(net,p):
    try:
       net.network.getNode(p['name'])
       return 'That name is already taken'
    except:
        pass


def make(net,name='NeuralModule which interfaces demoPublisher with the Nengo simulator', independent=True, useQuick=True):

    finder = "org.hanns.myPackage.DemoPublisher";

    # create group with a name
    g = NodeGroup(name, independent);    				# Create group of nodes (represented as SimpleNode in the GUI)
    g.addNode(finder, "publisher", "java");  				# start java node and name it subscriber in the ROS network
    
    neuron = NeuralModule('Subscriber_'+name, g) 	    # Construct the neural module (small ROS network: node+modem )
    neuron.createDecoder("hanns/demo/pubsub", "int",7)  # Define IO: termination = input of neuron (n*int)

    many=net.add(neuron)                    			# add it into Nengo simulator

