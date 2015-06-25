# EMFTA
(An FTA editor/visualizer that uses EMF)
                             Julien Delange <jdelange@andrew.cmu.edu>

## What it is?
This is an EMF-based FTA editor/visualizer. You can edit the content
in our FTA within Eclipse using a convenient representation (table)
and visualize a graphical (diagram) version.

If you are not familiar with what is an FTA, I would recommend
[to start reading wikipedia](http://en.wikipedia.org/wiki/Fault_tree_analysis) - this
is a method required by safety analysis methods (such as ARP4761).


## Why did you program that?
Most Fault-Tree Analysis tools are commercial and few open-source projects
are commercial and/or platform-dependent. Having an eclipse project
is way more convenient to interface with other modeling projects, 
especially the [OSATE](https://github.com/osate) tools used in many
of my research projects (yes, I did not do it for the safe of science).

Also, another reason was to try [Sirius](https://www.eclipse.org/sirius/), it
seems a nice way to design diagrams easily using EMF-based models. So, I wanted
to see what I can do within two evenings while coding on my porch.


## How it works?
Pretty simple: you create a modeling project, then, a basic FTA model
(using the *New* -> *Examples* menu in Eclipse) and then, right click
on the model and choose the appropriate representation (table
or tree). Diagram and table examples are shown below (see examples).

## Requirements
* An eclipse installation (tested on Luna, requires JavaSE-1.8)
* Sirius (the install provided with Luna)
* Some time (or a lot if you are a newbie)
* A brain (hard to find these days)


## Installation
The repository contains several projects. You will then need to import the following
projects into your environment:
* edu.cmu.emfta
* edu.cmu.emfta.edit
* edu.cmu.emfta.editor
* edu.cmu.emfta.tests

These first 4 files and Java projects, once imported they **should** build (almost) without any issue. If
you have any issue and/or these projects do not build, there is something wrong,
check your Eclipse installation.

Issue - Wrong version of Java: If the JRE System Library file in each of these projects is not followed by "[JavaSE1.8]", you will get a warning message along the lines of "Build path specifies...no JREs installed..strictly compatible with this environment" make the following changes in each of the 4 project files:
* Right click on "JRE System Library", select "Properties", in the pop-up menu, ensure that "Execution environment" is set to "JavaSE-1.8".
* Expand the "META-INF" folder, and open the "MANIFEST.MF" file, this file may open directly or open with a number of tabs, regardless find the "MAFIFEST.MF file and check the following line "Bundle-RequiredExecutionEnvironment: JavaSE-1.8". If the entry is not 1.8 change it to 1.8. 
* Clean all projects and the build execution environment messages should be resolved.

Continue to the next step ignoring any warning messages that are similar to the following:
* "The value of the field CutsetAction...is not used"
* "The folder "model/" does not exist in the workspace"

Once the initial four project files are build with (almost) no error messages, import the following two files:
* emfta.design
* example.emfta

Check to see that the "example.emfta" project is recognized as a Modeling Project file as designated by a small "M" symbol to the left of the folder name. If the symbol is not present, right-click  on the file name and select: "Configure -> Convert to Modeling Project".

In the initial versions of this project if you import the package "org.osate.aadl2.errormodel.emfta" Eclipse will generate a large number of error messages. This file is part the project to create a bridge to fault tree data in OSATE. Don't import this package if you just want to use emFTA. 


## Starting the Sirius perspective

The initial versions of emFTA are using the Sirius nightly development build, if something goes wrong at this point, upgrading the Sirius version to the development build may help. The development build URL is: http://download.eclipse.org/sirius/updates/nightly/latest/luna

Select (left-click) the emfta.design folder, and then from the main menu select "Run Configurations" in the pop-up menu select "Eclipse Application -> new application" At this point you can name the run configuration (hint emFTA) and then select run at the bottom of the window. If all of the defaults are correct, a new Eclipse window using the Sirius perspective will open.

The first time the Sirius perspective opens the design and example files may not be present. If this is the case then import the following files:
* emfta.design
* example.emfta

The **emfta.design** project contains everything to represent the Fault-Tree
with Sirius and the **example.emfta** contains the two examples models. Once
everything is imported, you should be able to open the examples in the navigator,
make a right click on the FTA Model and choose the diagram or table representation.

## Examples

### The lamp example
The lamp example shows a simple FTA related to a lack of light
in a room. Basically, you have no light if the bulb is broken
or there is no more power. This example is a basic FTA model
that captures these two conditions with the logical operation (the AND).


![Diagram of the Lamp Example](https://github.com/juli1/emfta/raw/master/example.emfta/imgs/example1-diagram.png "Diagram of the lamp example")


![Table of the Lamp Example](https://github.com/juli1/emfta/raw/master/example.emfta/imgs/example1-table.png "Table for editing the Gates/Events of the lamp example")


### The computer example
The computer example represents a fault-tree representing
the conditions when a computer crashes. In this
example, we consider the following events to be included in the FTA:
* Unhandled interrupt - the processor crashes
* Device broken - the device sends continuously interrupts so that the processor is always busy
* A divide by zero exception is raised when excuting a piece of software
* A fault is raised in the exception handler

The following pictures show the tables (to edit the events/gates) as well as the diagram representation.

![Diagram of the Computer Example](https://github.com/juli1/emfta/raw/master/example.emfta/imgs/example2-diagram.png "Diagram of the computer example")

![Table of the Computer Example](https://github.com/juli1/emfta/raw/master/example.emfta/imgs/example2-table.png "Table for editing the Gates/Events of the computer example")

## Bugs and Features Requests
If you find a bug, please report it on the issue tracker of the project.
Also, if you have any suggestion/idea to improve the tool, please also add it
on the issue tracker! Please also read the following section about the known
limitations and future work on this project. Bug fixes and improvements
will be considered according to my free time and the number of miles I run per day.

## Known Limitations and/or Future work
* Interface with AADL - especially the Error-Model Annex v2 (high priority)
* Compute of minimal cutsets (high priority)
* Use for security examples (medium priority)
* Make Simulation (low priority)
