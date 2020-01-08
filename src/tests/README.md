# Example Files
This folder can be used for educational purposes. You will find here some helpful examples.

## ResponseCheckTask
Can be used for checking response requested by packet. For that we can use own ResponseCheck class
 or official classes: ``SimpleResponseCheckTask``, ``EnchanedResponseCheckTask``.
 
 - SimpleResponseCheckTask ([example](https://github.com/Alemiz112/StarGate-Universe/blob/master/src/tests/SimpleResponseCheckTask.java)) can be used by code savers
 - EnchanedResponseCheckTask ([example](https://github.com/Alemiz112/StarGate-Universe/blob/master/src/tests/EnchanedResponseCheckTask.java)) is more clearer and in separated file
##### Own Class Response Check:
If you want to create your own method of response checking [CustomResponseCheckTask](https://github.com/Alemiz112/StarGate-Universe/blob/master/src/tests/CustomResponseCheckTask.java) can help you.
##  PlayerOnline Response
For checking players status and server we have official PlayerOnlinePacket.
Here we will show how to handle response created by this packet.</br>
We will again use [ResponseCheckTask](https://github.com/Alemiz112/StarGate-Universe/blob/master/src/alemiz/sgu/tasks/ResponseCheckTask.java) for extending our class. 
Our Example class is named [OnlineExample](https://github.com/Alemiz112/StarGate-Universe/blob/master/src/tests/OnlineExample.java). This time we will create command that will send PlayerOnlinePacket 
and then will check for response. Command class can be found [here](https://github.com/Alemiz112/StarGate-Universe/blob/master/src/tests/OnlineCommand.java).
