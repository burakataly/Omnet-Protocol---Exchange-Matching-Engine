# Omnet Protocol

This project is a semester project for an occupational elective course. It implements a simple **exchange matching engine** using pure Java and Maven. The project consists of several components that work together to handle client connections, buy/sell orders, and maintain an OrderBook. The communication between the client and the server is based on a custom binary protocol implemented over TCP.

## Project Structure

**MatchingEngine**:
- The core component that handles incoming messages from clients like heartbeats, orders.
- Maintains the OrderBook class and uses it to handle sell/buy orders.
- Uses a TcpServer object to send responses or listening for requests.

**TcpServer**:
- Opens a TCP server and listens for new connections.
- Manages each client with a ClientHandle object.
- Uses ThreadPools to handle multiple clients concurrently.

**ClientHandle**:
- Handles a single client connection.
- Reads incoming messages and forwards them to the MatchingEngine.
- MatchingEngine uses the corresponding ClientHandle object to send responses to a client

**OuchGateway**:
- Acts as an intermediary between the application layer and the TCP layer of the client side.
- It sends periodic heartbeat messages to keep the connection alive and forwards messages between the client and the server.
- Handles protocol-specific message formatting.

**OuchClient**:
- The core Application layer of the client side.
- Sends buy/sell orders to the MatchingEngine through the OuchGateway.

**TcpClient**:
- Manages the socket connection from client to server.
- listens the raw byte data from the server and forwards it to the OuchGateway.

## How to Run

1. Run `MatchingEngineMain.java` to start the matching engine server.
2. Run `OuchClientMain.java` to start the client side. Use the sendOrder method of OuchClient to send buy/sell orders.

## Protocol Description

The communication protocol between the client and the server is a custom binary protocol designed for efficient, low-overhead and reliable message exchange.

### Message Structure

|                  | Offset | Length  | Note                                         |
|------------------|--------|---------|----------------------------------------------|
| Start of message | 0      | 1       | 0xAA                                         |
| Length           | 1      | 2       | It shows the length of command and data      |
| Command          | 3      | 1       | Message type                                 |
| Data             | 4      | N       | Custom data for the specific message         |
| Crc              | 4+N    | 1       | XOR calculation of all bytes until crc       |

### Command Types

| Commands   | Value | Note                       | Sample message including crc | Response                             |
|------------|-------|----------------------------|------------------------------|--------------------------------------|
| HEARTBEAT  | 0x48  | Periodic heartbeat message | AA 00 05 48 00 00 00 01 E6   | ACK contains +1 heartbeat seq number |
| SEND ORDER | 0x4F  | Send buy or sell order     | AA 00 05 48 00 00 00 01 E6   |                                      |

#### Heartbeat Command

|         | Value                | Length | Note                                               |
|---------|----------------------|--------|----------------------------------------------------|
| Command | 0x48                 | 1      |                                                    |
| Data    | Heartbeat seq number | 4      | Shows sequence number. Server responds with +1 seq |

#### Send Order Command

|            | Value          | Length | Note                                  |
|------------|----------------|--------|---------------------------------------|
| Command    | 0x4F           | 1      |                                       |
| Side       | 'B'/'S'        | 1      | 'B' for Buy order, 'S' for Sell order |
| Symbol     | commodity name | 5      | 5 character symbol name. 'APPLE'      |
| Quantity   |                | 4      | quantity to sell/buy                  |
| Price      |                | 4      | Price                                 |

### Sample Messages

| Sample   | Content                          |
|----------|----------------------------------|
| Request  | AA 00 05 48 00 00 00 01 E6       |
| Response | AA 00 05 48 00 00 00 02 E5       |

### CRC Calculation

XOR all bytes in the message until crc. Take mod 256 of the result.

### Future Work
- FIX Protocol Integration: Future versions of this project aim to include support for the FIX protocol, enabling standardized and interoperable financial messaging.