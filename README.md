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

### Integration with NengoROS
In order to use this collection of nodes in Nengoros, you have to add a dependency of `nengo/simulator-ui` sub-project on your project. To do this, you have to:
#### Add your project to the multi-project Nenoros build
1. Edit the `nengo/settings.gradle` file and add the name of your new project there. So the result could be:

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

	Where the version and name of your project is defined in the `projectTemplate/build.gradle` file, see:


		// Define the version and name of my meta-package
		version             = '0.0.1-SNAPSHOT'
		group               = 'org.hanns'			// this is entire.package.name

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



### Launching your Nodes as an Ordinary Part of ROS
The second possibility is to launch these nodes as standalone applications, so there is no need for the Nengo simulator. Moreover, these nodes can communicate with help of either *jroscore* from the Nengoros project or *roscore* from typical ROS installation. In order to launch these nodes:

1. Create script which is able to launch these nodes. From the root of your project, run:

		./gradlew installApp
2. Run your favorite ROS core, e.g. from the root of nengoros project:

		./jroscore/jroscore 

3. Run the auto-generated startup script with the name of node to launch, e.g. on unix system:

		./build/install/projectTemplate/bin/projectTemplate org.hanns.myPackage.DemoPublisher

Note that you can change some properties of nodes by adding optional command line arguments, for more information, see: http://wiki.ros.org/Remapping%20Arguments


TODO
---------

* 	The tool should generate also files for ROS meta-package (for catkin support (package.xml et al)).
