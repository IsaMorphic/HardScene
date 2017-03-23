# HardScene
A free server frame implementation designed to work with HardScene-Client.

# Commands

01. $ help - Displays a listing equal or close to this copy
02. $ ban (ip) - Attemps to ban the specified IP from the server
03. $ pardon (ip) - Attempts to remove the specified IP from banned clients
04. $ kick (id) - Attemps to kick the specified client from the server
05. $ check - Attempts to check if the server is running or not
06. $ stop - Attempts to shutdown the HardScene server safely
07. $ reload - Attempts to reload the legacy configuration file
08. $ list [/a] - Attemps to list the connected clients on the server
09. $ tell (id) (msg) - Sends a message to the specified client
10. $ broadcast (msg) - Broadcasts a message to the server

# Notes for running HardScene

1. The HardScene server config file is located in the startup directory (hardscene.properties)
2. The HardScene server leverages the port you specify in the config file, otherwise it uses 28894 by default
3. Anyone connected to the irc server aren't directly talking with the other people, the irc acts as a relay

# How to connect to the server

# Connecting using HardScene-Client (supported)
1. Download HardScene-Client.jar from https://github.com/Speentie8081/HardScene-Client
2. Navigate into the directory containing the file in terminal and type java -jar HardScene-Client.jar
3. Type in the external IP for the remote server followed by :28894 (or whatever port you specified in the config)
4. After entering the address, you should then be asked for a display name, type anything you want here
5. After that you will be asked for an auth token, this is not required by any release so far of legacy HardScene
6. Finally, if all went well, you will then be connected to the remote irc server managed by HardScene

# Connecting using your web browser (recommended)
1. Connect to the external ip of the remote server followed by :28894 (or whatever port you specified in the config)
1. If the page is seemingly stuck trying to load, try refreshing the page a couple times
2. You will be then asked for a display name, type any username you would like here
3. After that you will be asked for an auth token, this is not required by any release so far of legacy HardScene
4. After entering your username and auth token into the text boxes provided to you the webpage, click "Login"
5. Finally, if all went well, you will then be connected to the remote irc server managed by HardScene
