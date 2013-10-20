# Simple (Drag and drop template for) NeuralModule. 
# This NeuralModule represents subscriber ROS(java) node (node with inputs) in the Nengo simulator as a SimpleNode. 
#
# author Jaroslav Vitku
#
# for more information how to make such template, see: http://nengo.ca/docs/html/advanced/dragndrop.html 
#

import nef
import math
from ca.nengo.math.impl import FourierFunction
from ca.nengo.model.impl import FunctionInput
from ca.nengo.model import Units
#from nengoros.neurons.impl.test import SecondOne as NeuralModule
from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule
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


def make(net,name='NeuralModule which interfaces demoPublisher with the Nengo simulator', 
independent=True, useQuick=True):

    finder = "org.hanns.myPackage.DemoPublisher";
    modemClass = "ctu.nengoros.comm.nodeFactory.modem.impl.DefaultModem";

    # create group with a name
    g = NodeGroup(name, independent);    		# create independent group called..
    g.addNC(finder, "publisher", "java");  	# start java node and name it finder
    g.addNC(modemClass,"Modem","modem")     	# add modem to the group
    g.startGroup()

    modem = g.getModem()
    neuron = NeuralModule('Subscriber_'+name, modem) # construct the neural module 
    #neuron.createEncoder("hanns/demo/pubsub", "int",7)   # termination = input of neuron (nxint)
    neuron.createDecoder("hanns/demo/pubsub", "int",7)  # origin = output of neural module 

    many=net.add(neuron)                    # add it into the network

