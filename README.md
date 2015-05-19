# EMFTA
(An FTA editor/visualizer that uses EMF)
                             Julien Delange <jdelange@andrew.cmu.edu>

## What it is?
This is an EMF-based FTA editor/viauslzer. You can edit the content
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
on the model ane choose the appropriate representation (table
or tree). Diagram and table examples are shown below (see examples).

## Requirements
* An eclipse installation (tested on Luna)
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

Once these projects are imported, they **should** build without any issue. If
you have any issue and/or these projects do not build, there is something wrong,
check your Eclipse installation. Once you have these projects built,
start a new Eclipse installation (within Eclipse) and import
the other projects of the repository:
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

## Known Limitations and/or Future work
* Interface with AADL (high priority)
* Compute of minimal cutsets (high priority)
* Use for security examples (medium priority)
* Make Simulation (low priority)

## How to find support
You can try to drop me a line at <jdelange@andrew.cmu.edu>, time and likelyhood
to get a reply depends on my availabilities and the number of miles I run per day.