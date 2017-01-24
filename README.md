# HardScene
A free server frame implementation designed to work with HardScene-Client.

# Commands

01. $ help - Displays a listing equal or close to this copy
02. $ ban (ip) - Attemps to ban the IP from the server
03. $ pardon (ip) - Attempts to remove the IP from banned clients
04. $ kick (id) - Attemps to kick the specified client from the server
05. $ check - Attempts to check if the server is running or not
06. $ toggle - Attempts to toggle the online state of the server
07. $ reload - Attempts to reload the legacy configuration file
08. $ list [/a] - Attemps to list the connected clients on the server
09. $ tell (id) (msg) - Sends a message to the specified client
10. $ broadcast (msg) - Broadcasts a message to the server

# Notes for running HardScene

1. HardScene config file is located in the root (hardscene.properties)
2. HardScene leverages the port you specify in the config file
3. Anyone connected to the IRC aren't talking directly with other people, the IRC acts as a relay

# How to connect to the server

1. Download HardScene-Client.jar from https://github.com/Speentie8081/HardScene-Client
2. Navigate to the file in terminal and type java -jar HardScene-Client.jar
3. Type in the external IP for the remote server followed by :28894 (or whatever you specified in config)
4. After entering the address, you will be asked for a display name, type anything you want here
5. If all goes well, after entering in all the information, you will be connected to the server
