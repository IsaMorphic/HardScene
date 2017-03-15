# HardScene Protocol
Here is the official documentation on the HardScene protocol

# Login packet

Maximum packet length: 99 bytes

Packet data charset: UTF-8

Example packet: "Player321~!password123"

1. Client name = Packet data before "~!"
2. Client token = Packet data after "~!"

Note: Token may not be required to connect

# Message packet

Maximum packet length: 512 bytes

Packet data charset: UTF-8

Example packet: "Hello World"

1. Message = Packet data