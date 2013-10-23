/**
 * These nodes show how to use simple ROS nodes in the Nengo simulator.
 * 
 * A (group of) ROS node(s) can be represented as NeuralModule in the Nengoros simulator.
 * NeuralModule can be either synchronous or asynchronous 
 * (@see ctu.nengoros.modules.impl.DefaultAsynNeuralModule , @see ctu.nengoros.modules.impl.DefaultNeuralModule).
 * In case of synchonous mode: simulator sends data each simulation step and receives data asynchronously.
 * In case of synchronous mode: simulator sends data each step and waits for new data after each step.    
 *      
 */
/**
 * @author Jaroslav Vitku
 *
 */
package org.hanns.demonodes.pubsub;