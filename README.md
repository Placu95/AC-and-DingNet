# AC-and-DingNet


## Description case study
The aim of the project is realize a pervasive real-time system for smart-city.
The system use real-time data to create a city map of quality air.
The application will use the map to define route to a destination that avoid areas with poor air quality.\\
Data can be produced by two types of sensors:

- **Fixed**: positioned along the roads and at intersections
- **Mobile**: placed on public transport or bicycles

Sensors will be placed in DingNet network. DingNet is a network LoRa-over-MQTT where every device send data to all the gateways inside the communication range. Then the gateways publish sensors data on MQTT server for the application, like the following image.

![](LoRa.png)

## System structure

The system will be composed from 4 principles building block, (see image below):

1. **DingNet simulator** -> simulates the sensors network scattered around the city, that communicate sensed data to the gateways of DingNet network using LoRa technology. The gateways then publish data on a MQTT server.
0. **MQTT broker** -> intermediary between the sensor/actuator network and Protelis program.
0. **Protelis MQTT back-end** -> entry point of Protelis program. The back-end hides the real network topology, so the Protelis program can use a logical proximity based network. In this case the communication will be based on publish/subscribe pattern with MQTT.
0. **Protelis program** -> the aggregate program that will identify areas with good air quality and it will define the requested routes

The communication between the DingNet simulator and the Protelis program can be bi-directional.

![](DingNet_en.png)

## RoadMap

The necessary steps to obtain the system are the following:

1. design and implement the building block "Protelis MQTT backend". It require to:
    - implement the communication between the DingNet network and the Protelis program
    - implement communication between Protelis nodes
    - define a neighborhood policy
1. modify the DingNet simulator in order to:
    - use real MQTT server
    - fix some bug
    - define a function to generate sensors data in a spatio-temporal domain
    - define new type of mote, that can be require a path
    - support mobile motes with delayed start
1. realize the aggregate program
1. realize the same program with A*-search to evaluate the different solutions

## From simulated network to real network
The final system will be a valid simulator of the real system, because to switch to the real system will be necessary to replace only the building block "DingNet simulator", (see image below).
The physical network will communicate its own sensed data through the MQTT broker, so all other building blocks won't have to be modified.

![](real_DingNet_en.png)

Similarly it will be possible to simulate the execution of the Protelis program also on Alchemist simulator to evaluate it in a large-scale systems. In this case, the structure of the system will consist of three building blocks (see image belo), all already available.

![](system_with_alchemist.png)
