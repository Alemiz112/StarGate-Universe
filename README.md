# StarGate-Universe
[![Build Status](https://travis-ci.org/Alemiz112/StarGate-Universe.svg?branch=master)](https://travis-ci.org/Alemiz112/StarGate-Universe) [![Jenkins](http://jenkins.mizerak.eu/job/StarGate-Universe/badge/icon)](http://jenkins.mizerak.eu/job/StarGate-Universe/)
> This is stable and fast plugin for nukkit that allows server connect to WaterDog plugin StarGate. It make easyer communication between server. Includes API fur custom packets, transfering players and more 
</br> Download [here](http://jenkins.mizerak.eu/job/StarGate-Universe/)!

## 🎯Features:
- Fast communication between servers
- Custom packets
- Moving players between servers (API)

More features will be added very soon

## 🔧API
You can access StarGate-Universe by ``StarGateUniverse.getInstance()``
#### Avalibe Functions
- ``RegisterPacket(StarGatePacket packet)`` Really simple method for registring Packet
- ``putPacket(StarGatePacket packet, String clientName)`` This allows you to send packet. Packet must be registered first
- ``transferPlayer(Player player, String server, String client)`` This we use to transfer Player between servers
- ``kickPlayer(Player player, String reason, String client)``  Kick player from any server connected to StarGate network
- ``isOnline(Player player, String client)`` Check if player is online. Sends back response 'true!server' or 'false'. Examples [here](https://github.com/Alemiz112/StarGate-Universe/tree/master/src/tests#playeronline-response).
- ``forwardPacket(String destClient, String localClient, StarGatePacket packet)`` Using ForwardPacket you can forward packet to other client/server
- ``addServer(String address, String port, String name, String client)`` Add server to list and allows players to transfer
- ``removeServer(String name, String client)`` Remove server from server list

Client variable in API is used to specify destination proxy (if more instances are used). It is not important to define it, if one one proxy is used.
##### Example:
```java
Player player = PLUGIN.getServer().getPlayer("alemiz003");
String server = "lobby2";

StarGateUniverse.getInstance().transferPlayer(player, server);
```
To more examples look [here](https://github.com/Alemiz112/StarGate-Universe/tree/master/src/tests)!

#### 📦Packet Handling
Received Packets are handled by ``CustomPacketEvent``. Official Packets are handled (if needed) automaticly</br></br>
Accessing Packet from Event:</br>
```java
public StarGatePacket getPacket() {
  return packet;
}
```
#### 📞ResponseCheckTask
Response checking is useful when we want to get some data created by packet back to client.</br> 
For more info please consider looking [here](https://github.com/Alemiz112/StarGate-Universe/tree/master/src/tests).

#### ⚙️Creating Own Packets
For better understanding please read [StarGatePacket](https://github.com/Alemiz112/StarGate-Universe/blob/master/src/alemiz/sgu/packets/StarGatePacket.java) and [WelcomePacket](https://github.com/Alemiz112/StarGate-Universe/blob/master/src/alemiz/sgu/packets/WelcomePacket.java)

#### Docker Implementation
If you have servers inside Docker container this is for you. StarGate allows you to create/remove/start/stop any container. Using `ServerManagePacket` you can set container exposed ports and env. variables too.
 Working example can be found [here](https://github.com/Alemiz112/StarGate-Universe/tree/master/src/tests#docker-implementation-example).
#### Convertor
Convertor is used for ``encoding`` and ``decoding`` packets. We can use it for static and nonstatic usage</br>
Functions:</br>
- ``packetStringData(String packetString)`` Exports packetString to data array
- ``putInt(int integer)`` Pushes Integer to array
- ``putString(String string)`` Pushes String to array
- ``getString(int key)`` Returns String from array by key value
- ``getPacketString()`` Returns packetString from array data

- ``static getInt(String string)`` Returns Integer from String
- ``static getPacketString(String[] strings)`` Returns packetString from given array
- ``static getPacketStringData(String packetString)`` Returns array data from given string

##### Example (nonstatic):
```java
Convertor convertor = new Convertor(getID());

convertor.putString(server);
convertor.putInt(tps);
convertor.putInt(players);

this.encoded = convertor.getPacketString();
```
##### Example (static):
```java
String[] data = Convertor.getPacketStringData(packetString);
int PacketId = Convertor.getInt(data[0]);
```
