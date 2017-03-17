# HardScene Protocol
Here is the official documentation on the HardScene protocol

This protocol should be the same throughout all HardScene-based projects

# Login packet

Maximum packet length: 99 bytes

Packet data charset: UTF-8

Example packet data: "Player321~!password123"

1. Client name = Packet data before "~!"
2. Client token = Packet data after "~!"

Note: Client token may not be required to connect

# Message packet

Maximum packet length: 512 bytes

Packet data charset: UTF-8

Example packet data: "Hello World"

1. Client message = Packet data
