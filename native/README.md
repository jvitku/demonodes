About
----------

This is the simplest native application written in C++ for purposes of demonstration how to use native application from the Nengoros simulator.

The application writes messages into console (however, Nengoros does not redirect its STDOUT by default), but it writes messages also into the out_helloworld.txt file. 

When launching from Nengoros, this file will be located under `nengo/simulator-ui`.

author Jaroslav Vitku


Usage
-------

To compile, run:

	g++ helloworld.cpp

To run:

	./a
	
Nengoros Integration
--------------------

Nengoros is able to launch native applications externally. To do this, it needs to know its name and relative path. This application contains demo script under `python/nr-demo` and can be linked/copied in a standard way:

	./linkdata -c native/


Then, the application can be launched by running the following script in Nengo console:  `nenro/simulator-ui/nr-demo/native/helloworld.py`, that is:

	run nr-demo/native/helloworld.py
	

To check whether the application is running, see the output text file: `nengo/simulator-ui/out_helloworld.txt` or its process, that is e.g. on OS X:

	ps xm | grep demonodes
	
which returns something as:
	
	5953 s001  S+     0:00.01 ../../demonodes/native/a.out __ns:=HelloworldCPP __name:=Helloworld
 

