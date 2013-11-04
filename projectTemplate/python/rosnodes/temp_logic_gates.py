# This is python interface for the project logic/gates. This helps using particular ROsJava nodes in
# Jython scripts for Nengo simulator.
#
# Import this in your scirpt in order to use it, for examples see ../nr-demo/ scripts.
#
# Note: that Decreasing and IncreasingLinear membership functions are TODO (not working in Nengo GUI some..why)
#
# author Jaroslav Vitku

import nef
from ctu.nengoros.comm.nodeFactory import NodeGroup as NodeGroup
from ctu.nengoros.comm.rosutils import RosUtils as RosUtils
# Note that modules without outputs (Nengo-decoder) cannot use synchronous mode (DefaultNeuralModule).
from ctu.nengoros.modules.impl import DefaultAsynNeuralModule as AsynNeuralModule
# The simulator waits each simulation step for the output in the synchronous mode, this case:
from ctu.nengoros.modules.impl import DefaultNeuralModule as NeuralModule

################# Create Neural Modules containing the ROS nodes

# fuzzy membership functions
ftriangle 	= "org.hanns.myPackage.fuzzy.membership.impl.Triangular";


# Initialize ROS(java) node implementning AND function.
#
# Example how to use this in the simulation:
#
# myAND = logic_gates.and_node("ahoj")
# net.add(myAND)
# net.connect(myAND.getOrigin('logic/gates/ina'), myAND.getTermination('hanns/demo/pubsub'))
def fuzzyMemTriangle(name):									# ____|\____
	g = NodeGroup("FuzzyMemTriangle", True);
	g.addNode(ftriangle,"FuzzyMemTriangle","java");
	module = NeuralModule(name+'_FuzzyMemTriangle', g)
	module.createEncoder("logic/gates/ina", "float", 1)		# x
	module.createEncoder("logic/gates/confa", "float", 1) 	# alpha
	module.createEncoder("logic/gates/confb", "float", 1) 	# beta
	module.createEncoder("logic/gates/confc", "float", 1) 	# gamma
	module.createDecoder("logic/gates/outa", "float", 1)	# y
	return module

def fuzzyAsynMemTriangle(name):
	g = NodeGroup("AsynFuzzyMemTriangle", True);
	g.addNode(ftriangle,"AsynFuzzyMemTriangle","java");
	module = AsynNeuralModule(name+'_AsynFuzzyMemTriangle', g)
	module.createEncoder("logic/gates/ina", "float", 1)		# x
	module.createEncoder("logic/gates/confa", "float", 1) 	# alpha
	module.createEncoder("logic/gates/confb", "float", 1) 	# beta
	module.createEncoder("logic/gates/confc", "float", 1) 	# gamma
	module.createDecoder("logic/gates/outa", "float", 1)	# y
	return module
