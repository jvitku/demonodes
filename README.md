Creating and Using ROS Nodes in the NengoROS System
================================================

Author Jaroslav Vitku [vitkujar@fel.cvut.cz]


About
------

This repository contains set of nodes for purposes of demonstration of NengoROS integration. It also contains projectTemplate which can serve as a starting point for creating new (Nengo)ROS nodes in Java.

This is a part of NengoRos multi-project build, so there is no need to install any of the demo nodes. 


Running the NengoROS Demos
---------------------------

In the complete installation of Nengoros, all demo scripts from this repository are symlinked into the folder `nengo/simulator-ui/nr-demo`. To run these nodes, run `Nengo` (or `nengo-cl`):
	
	cd nengo/simulator-ui/
	./nengo

and open the demo script by writing the command into the Nengo console:
	
	run nr-demo/minmaxint.py


Template for Creating New Project with ROS Nodes
-----------------------------------------------

The script `helper` is able to initialize new Nengoros project for you. Running the script: 

	./demonodes/helper -i
	
will copy the `proejctTemplate` into the `nengo/` workspace. Feel free to rename it. You can now edit the example ROS nodes under `src/main/java`.

Building the Project
----------------------

In order to build your project run from the root of your project:

	./gradlew build
	
To refresh (e.g. after renaming the folder) the eclipse project:

	./gradlew eclipse
	
To install the project into the local maven repository:

	./gradlew install
	

Usage of Newly Created ROS Nodes
---------------------------------


There are several possibilities how to run your newly created nodes:

### Running the nodes independently
Each ROSjava node produced in this way is 100% compatible with ROS infrastructure and can be launched either under [roscore](http://wiki.ros.org/roscore) from ROS installation or [jroscore](https://github.com/jvitku/jroscore) from the nengoros project. To launch the node it is necessary to:

Create the runnable application by launching from the project root:

	./gradlew installApp

Run the core, e.g. for jroscore:

	cd nengoros/jroscore && ./jroscore

Launch the generated script with the full node name in a new terminal:

	cd nengoros/projectTemplate
	./build/install/projectTemplate/bin/projectTemplate org.hanns.demonodes.pubsub.DemoPublisher
	
Launch subscriber to receive messages in a new terminal:

	cd nengoros/projectTemplate
	./build/install/projectTemplate/bin/projectTemplate org.hanns.demonodes.pubsub.DemoSubscriber

Now, there should be two independently running ROSjava nodes which communicate over the TCP/IP via the ROS network. Note that you can change some properties of nodes by adding optional command line arguments, for more information, see [remapping arguments](http://wiki.ros.org/Remapping%20Arguments).

### Integration with NengoROS
In order to use this collection of nodes in Nengoros, you have to add a dependency of `nengo/simulator-ui` sub-project on your project. To do this, you have to:
#### Add your project to the multi-project Nenoros build
1. Edit the `settings.gradle` file and add the name of your new project there. So the result could be:

		// howto: '[folderName]:[subFolderName]:[projectName==folderName]'  (no package names here)
		include 'logic:gates', 'projectTemplate'

		
2. add the dependency to the `nengo/simulator-ui` project by editing the `nengo/simulator-ui/rosjava.build.gradle`, so the result could be:	


		dependencies {
		    	compile project(':nengo:simulator')
		    	compile fileTree(dir: 'lib', include: '**/*.jar')
		    	compile 'ros.rosjava_core:rosjava:0.0.0-SNAPSHOT'
		    	compile 'org.hanns.logic:gates:0.0.1-SNAPSHOT'
				//
				// howto: '[projectFolder]:[projectName]' 
				compile project('projectTemplate')
		}


3. Recompile and reinstall the Nengoros project by running:

		./tool -nf

4. If you want to launch Nengo from class files, add new dependency to the `simulator-ui/nengo` (and `nengo-cl`) scripts. That is e.g. to the local maven repository:

		NODES=$NODES:$HOME/.m2/repository/org/hanns/projectTemplate/0.0.1-SNAPSHOT/projectTemplate-0.0.1-SNAPSHOT.jar 

#### Launch your Nodes from the Nengo Simulator

From now, you can create simple python script which creates representation of your ROS nodes in the Nengo. Example of simple script, which:

* creates network
* for each ROS node creates NeuralModule
* places these new neural modules into the network
* connect them with other components

An example of how to create the script is contained in `projectTemplate/python/myNetwork.py`. It is good to symlink this script into the `scripts` folder:

	cd nengo/simulator-ui/scripts
	ln -s ../../../projectTemplate/python/myNetwork.py

Then, to run the script, simply run the `./nengo` and then write into the command-line interface: `run` with relative path to your script, so e.g.:
	
	run scripts/myNetwork.py
	

#### Adding Drag and Drop Icon into the Nengo GUI
This is not required, but allows you to easily add nodes into the simulation in the GUI. For more information about how to add a GUI template into the Nengo simulator, see the tutorial from the authors of Nengo on: http://nengo.ca/docs/html/advanced/dragndrop.html .
Basically, you have to:

1. Add image of each node (group of nodes)under: `nengo/simulator-ui/images/nengoIcons`
2. For each new node (group of nodes), add an initialization script under: `nengo/simulator-ui/python/nef/templates`. You can use an example template contained here, under `projectTemplate/python/nef/templates/myTemplate.py`.
3. Add these new templates into the `nengo/simulator-ui/python/nef/templates/__init__.py` file
4. Restart Nengo, you should see your nodes in the left panel



TODO
---------

* 	The tool should generate also files for ROS meta-package (for catkin support (package.xml et al)).
