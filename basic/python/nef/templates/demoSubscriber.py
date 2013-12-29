# Simple (Drag and drop template for) NeuralModule. 
#
# This NeuralModule represents subscriber ROS(java) node (node with inputs) in the Nengo simulator as a SimpleNode. 
#
# author Jaroslav Vitku
#
# For more information how to make such template, see: http://nengo.ca/docs/html/advanced/dragndrop.html 
# For mode information about integration with ROS, see the script demonodes/projectTemplate/python/myNetwork.py

import nef
# Note that you CAN'T make run ROS node which does not publish any messages in synchronous mode
#   as the simulator will wait for the message each simulation step.
#   TODO: check for non-publishing nodes in the Nengo simulator and ignore them in synchronous mode
from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule # synchronous mode - cannot use here set modem as asynchronous

from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup

# node utils..
title='demoSubscriber'
label='demoSubscriber'
icon='demoSubscriber.png'

# Define the parameters for initializing the node
params=[
('name','Select name for NeuralModule interfacing the demoSubscriber',str),
('independent','Can be group independent? (pushed into namespace?) select true',bool)
]

# try to instantiate node with given parameters (e.g. check name..)
def test_params(net,p):
    try:
       net.network.getNode(p['name'])
       return 'That name is already taken'
    except:
        pass


def make(net,name='NeuralModule which interfaces demoSubscriber with the Nengo simulator', independent=True, useQuick=True):

    node = "org.hanns.demonodes.pubsub.DemoSubscriber";
    
    g = NodeGroup(name, independent);    			# Create group of nodes (represented as SimpleNode in the GUI)
    g.addNode(node, "subscriber", "java");  	    # start java node and name it subscriber in the ROS network
    module = NeuralModule('Subscriber_'+name, g, False) 	# Construct the neural module in the asynchronous mode
    module.createEncoder("org/hanns/demonodes/pubsub", "float", 7) # Define IO: termination = input of neuron (n*int)

    many=net.add(module)                    		# add it into Nengo simulator

