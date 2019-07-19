# StarGate-Universe
[![Build Status](https://travis-ci.org/Alemiz112/StarGate-Universe.svg?branch=master)](https://travis-ci.org/Alemiz112/StarGate-Universe)
> This is stable and fast plugin for nukkit that allows server connect to WaterDog plugin StarGate. It make easyer communication between server. Includes API fur custom packets, transfering players and more

## 🎯Features:
- Fast communication between servers
- Custom packets
- Moving players between servers (API)

More features will be added very soon

## 🔧API
You can access StarGate-Universe by ``StarGateUniverse.getInstance()``
#### Avalibe Functions
- ``transferPlayer(Player player, String server)`` This we use to transfer Player between servers
- ``RegisterPacket(StarGatePacket packet)`` Really simple method for registring Packet
- ``putPacket(StarGatePacket packet)`` This allows you to send packet. Packet must be registered first
- ``kickPlayer(Player player, String reason)``  Kick player from any server connected to StarGate network
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
#### ⚙️Creating Own Packets
For better understanding please read [StarGatePacket](https://github.com/Alemiz112/StarGate-Universe/blob/master/src/alemiz/sgu/packets/StarGatePacket.java) and [WelcomePacket](https://github.com/Alemiz112/StarGate-Universe/blob/master/src/alemiz/sgu/packets/WelcomePacket.java)
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
