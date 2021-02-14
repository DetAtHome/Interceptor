# USB Interceptor
Man in the middle of your USB Connection between Desktop and CNC Mill

## DISCLAIMER
Of course no warranty whatsoever.
Do not even think of using this stuff in mission critical environement, it's all done in "Spikemode" so no TDD applied (shame on me)
All work in progress, maybe I'll have it released somewhen in the future.

## The Problem
I recently equipped my chinese 3018 with one of those cheap 500w spindles. Unfortunately unlike the original spindle the housing is not 
connected to the drill bit, so it would not close a circuit when z-probing. 
Solution is to connect to the top of the spindle axis. But only should be connected when probing, so I made a connection
by lowering a connector (a simple screw connected to a wire) using a servo which was driven by an additional arduino nano.
But to determine when to lower that it was necessary to inspect the g-code commands send over the wire.

## What it does
It "cuts in half" your toolchain between Candle and a CNCMill and allows you to intercept all the traffic in between. 
This way you can intercept and enhance GRBL commands. 
The workflows decide what will be done. The GUILogAndPass for example simply logs all that is happening and passes it to 
the mill. The MonitorProbingWorkflow reacts to probing and lowers the connector so that a automatical probing can be done.
The MonitorSpindleSpeedWorkflow can change the spindle speed of my spindle (that only had a knob for controlling the speed).
