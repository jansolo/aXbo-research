aXbo-research
=============

Open source version of the client software package for the aXbo Sleep Phase Alarm Clock. The commercial variant (which is free too, but uses a commercial Look-and-Feel and Installer, that can not be open sourced) is build from this sources.


Sources
-------

Clone the sources from:
git pull https://github.com/jansolo/aXbo-research.git

The master branch will be updated infrequently. All development is done on the development branch. You need to change to the development branch, if you want to see the latest changes:

	git checkout --track origin/development


Build
-----

aXbo research can be build from Netbeans (8.0) or command line. 

Netbeans:
Open the project with Netbeans 8.0. The project is setup to build und run immediatly from Netbeans. No additional dependencies are required

Command line:
A build from command line requires JDK 1.7* and ant. JAVA_HOME must be set. Ant needs to be on the path.

	export JAVA_HOME=/path/to/jdk
	export ANT_HOME=/path/to/ant
	cd aXbo-research/aXbo-research
	$ANT_HOME/bin/ant clean jar

The binaries are copied to the dist directory.

Additionally the build script supports the generation of an app wrapper (``aXbo research.app``.) on Mac OSX.

	$ANT_HOME/bin/ant bundle-aXbo-research


Run
---

aXbo research requires a JRE 1.7*. Both 32-bit and 64-bit JVMs are supported on Mac OSX, Windows and Linux. To run aXbo research from command line:

    cd dist
    $JAVA_HOME/bin/java -jar axbo.jar

Linux users may require to set the ``java.library.path`` system property to the directory of rxtxSerial.so native libraries:

    cd dist
    $JAVA_HOME/bin/java -Djava.library.path=. -jar axbo.jar

aXbo uses the ``CP2102 USB to UART Bridge Controller``from SiLabs. Therefore the used Linux kernel needs to include the corresponding modules (cp210x). For most standard distributions the modules should be already there. 
Additionally Linux users need to have correct permissions to read and write from the serial interface (/dev/ttyUSB0 on Ubuntu). Typically assigning the group ``dailout`` to the user should be sufficient. 

On Mac OSX simply start "aXbo research.app".


Development
-----------

The sources include project files for NetBeans 7.4. Simply open the project with Netbeans 7.4 and start coding. 


Architecture
------------

TODO


Additional Resources
--------------------

A detailed documentation (german) of the aXbo communication protocol can be found here:
https://github.com/jansolo/aXbo-research/blob/development/aXbo-research/doc/aXbo%20PC%20Protokol.pdf


Contribute
----------

If you like to contribute, there are some areas of specific interest:

- [ ] A nice GUI for creating custom sound packages. The required utility classes for creating sound packages are already there.
- [ ] Uploading firmware updates to aXbo. The current firmware update tool supports Windows only.
- [ ] Rewrite/Rethink the AxboCommandUtil class. This is old and very ugly code. I did not come up with a better solution yet.

Discussion
----------

Please discuss all topics regarding the development of aXbo-research in following mail group:

https://groups.google.com/forum/#!forum/axbo-research-dev
