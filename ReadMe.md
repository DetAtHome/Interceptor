# USB Interceptor
Man in the middle of your USB Connection between Desktop and CNC Mill

## DISCLAIMER
Of course no warranty whatsoever.
Do not even think of using this stuff in mission critical environement, it's all done in "Spikemode" so no TDD applied (shame on me)
All work in progress, maybe I'll have it released somewhen in the future.

## What it does
It "cuts in half" your USB Wire and allows you to intercept all the traffic in between. 
Run this on a Raspberry Pi to have additional control by powering GPIO pins on G-Code commands or introduce even new commands.
As of now it is intended to intercept G-Code commands sent to a CNC mill and introspect them for additional actions.
Also see the examples section (tbd).

## Why do I need such a complicated setup
Well, often you simply have no other choice then communication to your appliance using serial. Still you might want to 
inspect or even alter the data "on the fly". So you need some computer power in "mid-air". The Rasperry is the best fit,
I fiddled around with two arduinos, or a nodeMCU but to no avail. 

But why can't I simply connect using only USB Cables? Well both desktop and RPI claim to be the bus master, so nope, you
cannot. At least not as I know of, perhaps someone finds another solution. Anyway, the serial adapters are real cheap, so
why not use them.

## What it needs
- A Raspberry Pi (2B will suffice) equipped with a JRE.
- An USB2Serial Adapter (CH340, CH341 or FTDI)
- 3 Jumper Wires
- A wallwart to power the Raspberry Pi
- A wireless connector to connect to the Pi via WiFi (temporary for setup, but recommended to have in in anyway)

## How to setup
Setup your Raspberry Pi to be connected to the desktop, by connecting the USB Wire to the Serial Connection.
So it is Desktop to SerialAdapter (FTDI using SiliconLabs driver or a CH34x USB2Serial adapter).
From the adapter connect:

- Ground to Ground (Pin 06, upper third if you count from where the SD Card sits).
- RX to TXD0 (Pin 08, upper fourth)
- TX to RXD0 (Pin 10, upper fifth)

Check with putty if you can log in with that setup. You need to know the COMPort (on windows machines) or where you need to connect to on *uxes, should be ttyUSBx

Connect your applicance to a free USBPort on the Pi. 
The command ```lsusb``` should show you a USB Serial Adapter, this is where you send your serial commands to.
Check with ```ls-al /dev/``` what tty devices are available.
there should be at least ttyUSB0 and ttyAMA0 (don't know if it is always ttyAMA0 that you want to use, but chances are 
this is it).
Check whether you can use the serial. Connect to you pi using ssh (there are tons of tutorials around of how to do). So 
the serial port is freed. Connect a serial monitor (putty should do, arduino ide serial monitor will also do).
Send something to the serial by

```echo Hello > /dev/ttyAMA0```

Most likely you will see a "Permission denied" error. That is what took the most time to find out.

To cure that, it is needed to add your Pi user to the dialout group.
``` sudo usermod -a -G dialout pi```

Unfortunately, for me this did not suffice, so I had to add the pi user to the tty group as well:

```sudo usermod -a -G tty pi```

Even that is not enough. To free the serial port it is necessary to not allow a login from a serial connection. To avoid
that there are two ways: Either the raspi config allows configuration using

```sudo raspi-config``` under the advanced options. Sometimes there is no menu point ```serial``` in the config menu. In this case the script from here


```sudo wget https://raw.githubusercontent.com/lurch/rpi-serial-console/master/rpi-serial-console -O /usr/bin/rpi-serial-console && sudo chmod +x /usr/bin/rpi-serial-consolesudo wget https://raw.githubusercontent.com/lurch/rpi-serial-console/master/rpi-serial-console -O /usr/bin/rpi-serial-console && sudo chmod +x /usr/bin/rpi-serial-console```

can be installed (cation pitfall: the script does not grant execute rights, add them with ```sudo a+x /usr/bin/rpi-serial-console```). Run
```sudo rpi-serial-console status``` for status and ```sudo rpi-serial-console disable``` 
for disabling. 

After a reboot the ```echo Hello > /dev/ttyAMA0``` command should result in a Hello printed on the terminal window of your 
desktop.

///////////////
Excurse. This is no longer needed as I switched from RXTX to jserialcomm. But perhaps someone might find the information useful anyway:
Unfortunately even this does not suffice. Because rxtx implementation for java is not too smart, it will only look for 
serials according to a name pattern that ```/dev/ttyAMA0``` does not fulfill. So you need to create a symlink to the serial. 

To do you need to create a file named (the name is important): /etc/udev/rules.d/80-serial.rules. You can do with vi: ```sudo vi /etc/udev/rules.d/80-serial.rules```. Paste in this content:

```$xslt
KERNEL=="ttyAMA0", SYMLINK+="ttyS5",GROUP="dialout",MODE:=0666
KERNEL=="ttyACM0", SYMLINK+="ttyS6",GROUP="dialout",MODE:=0666
``` 

Save and reboot.
(Tested for RPI 2b, RPI 3 and 4 may behave different)
/////////////////

Build Interceptor.jar using ```gradle jar``` task.

Copy over the dependend libs:  pi4j-core.jar and jSerialComm-2.6.2.jar next to the Interceptor.jar.

Now you can start the interceptor:

```sudo java -cp ./jSerialComm-2.6.2.jar:./pi4j-core.jar:Interceptor.jar de.dbconsult.interceptor.Interceptor mill USB pc AMA0```
best go create a shell script for that command. The name of the appliance in my case is mill connected to the USB port, and the desktop is hooked up using the /dev/ttyAMA0 serial. USB and AMA0 are "jSerialComm friendly names".

 That will start a default workflow that simply logs all traffic between the desktop and a device.

