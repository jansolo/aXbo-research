aXbo-research
=============

Open source version of the client software package for the aXbo Sleep Phase Alarm Clock. The commercial variant (which is free too, but uses a commercial Look-and-Feel and Installer, that can not be open sourced) is build from this sources.


Sources
-------

Clone the sources from:
git pull https://github.com/jansolo/aXbo-research.git

The master branch will be updated only very infrequently synchronized with the releases of the commercial variant. All development is done on the development branch. You need to change to the development branch, if you want to see the latest changes:

	git checkout --track origin/development


Build
-----

aXbo research can be build from Netbeans (7.3) or command line. 

Netbeans:
Open the project with Netbeans 7.3. The project is setup to build und run immediatly from Netbeans. No additional dependencies are required

Command line:
A build from command line requires JDK 1.6+ and ant. JAVA_HOME must be set. Ant needs to be on the path.

	export JAVA_HOME=/path/to/jdk
	export ANT_HOME=/path/to/ant
	cd aXbo-research/aXbo-research
	$ANT_HOME/bin/ant clean jar

Additionally the build script supports the generation of an app wrapper (aXbo research.app) on Mac OSX.

	$ANT_HOME/bin/ant bundle-aXbo-research


Run
---

aXbo research requires a JRE 1.6+. On Windows and Linux only 32-bit JREs are supported, because I was not able to create fully functional 64-bit RXTX native libraries yet. To run aXbo research from command line:

    cd dist
    $JAVA_HOME/bin/java -jar axbo.jar

On Mac OSX one can simply start "aXbo research.app".

Development
-----------

Netbeans 7.3

TODO


Architecture
------------

TODO