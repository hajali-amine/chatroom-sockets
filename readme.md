# ChatRoom

A mini-project using *java.net* __sockets__ and __AWT__.
<br>

it was an assignement given to us during a Distributed Applications session on our second year at INSAT, as an introduction to Java's __sockets__, __threads__ and __AWT__.

## Description:

The project consists of a simple client/server application.
<br>

The clients will be able to chat with eachother using an interface implemented with AWT.
<br>

The sockets will represent the connection between the client program and the server program.

## Example - a server & 2 clients and a server:

<p align="center">
  <img src="" alt="sequence" />
</p>

__Notes:__ 
* In case of *n* clients, when the client*i* send a message, serverThread*i* will broadcast the message to all of the other *n-1* serverThreads.
* Whenever a client joins or leaves, all of the other clients will get notified.
* To leave, all you have to do is type __quit__.
* If you close the chat window, the others will be notified that you left.
  
## Steps:

1. Execute the __server.Main__.
2. Execute as many __client.Client__ as you wish.
3. Don't forget to enjoy!
