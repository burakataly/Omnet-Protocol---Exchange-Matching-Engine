
# How to run
> 1. Run MatchingEngineMain.java
> 2. Run OuchGatewayMain.java

# Protocol Description:


|                  | Offset | Length  | Note                                         |
|------------------|--------|---------|----------------------------------------------|
| Start of message | 0      | ***1*** | 0xAA                                         |
| Length           | 1      | ***2*** | It shows the length of command and data      |
| Command          | 3      | ***1*** | Message type                                 |
| Data             | 4      | ***N*** | Custom data for the specific message         |
| Crc              | 4+N    | ***1*** | ***XOR*** calculation of all bytes until crc |


# Command Types:

| Commands   | Value | Note                       | Sample message including crc | Response                             |
|------------|-------|----------------------------|------------------------------|--------------------------------------|
| HEARTBEAT  | 0x48  | Periodic heartbeat message | AA 00 05 48 00 00 00 01 E6   | ACK contains +1 heartbeat seq number |
| SEND ORDER | 0x4F  | Send buy or sell order     | AA 00 05 48 00 00 00 01 E6   | |


## Heartbeat Command

|         | Value                | Length | Note                                               |
|---------|----------------------|--------|----------------------------------------------------|
| Command | 0x48                 | 1      |                                                    |
| Data    | Heartbeat seq number | 4      | Shows sequence number. Server responds with +1 seq |

## Send Order Command

|            | Value          | Length | Note                                  |
|------------|----------------|--------|---------------------------------------|
| Command    | 0x4F           | 1      |                                       |
| Side       | 'B'/'S'        | 1      | 'B' for Buy order, 'S' for Sell order |
| Symbol     | commodity name | 5      | 5 character symbol name. 'APPLE'      |
| Quantity   |                | 4      | quantity to sell/buy                  |
| Price      |                | 4      | Price                                 |


>
>| Sample   | Content                          |
>|----------|----------------------------------|
>| Request  | ***AA 00 05 48 00 00 00 01 E6*** |
>| Response | ***AA 00 05 48 00 00 00 02 E5*** |


# CRC calculation:

> XOR all bytes in the message until crc. Take mod 256 of the result.
>
> 0xAA ^ 0x00 ^ 0x05 ^ 0x48 ^ 0x00 ^ 0x00 ^ 0x00 ^ 0x01 = 230 = 0xE6

# Matching Engine

> - Opens TCP server and listens new connections.
> - Replies gateway requests.
> - Closes client connection in case of no valid message received for 30 seconds

# Ouch Gateway

> - Opens TCP client to the server.
> - Sends one heartbeat message per 5 seconds.



# Homework 1

> - Deadline: 04.12.2024 before the class.
>
> 1. Ouch Gateway should send proper heartbeat message. (OuchGateway.java)
> 2. Heartbeat message should contain heartbeat sequence number starting from 1. (OuchGateway.java)
> 3. Server should calculate CRC and handle the message if CRC is correct. (MatchingEngine.java)
> 4. Server should reply with next sequence number. (MatchingEngine.java)
> 5. Ouch gateway should listen incoming messages. (You can check ClientHandle.java to see how server does it.)
> 6. Ouch gateway should calculate CRC and handle the message if CRC is correct. (OuchGateway.java)

# Homework 2

> - Deadline: 18.12.2024 before the class.
>
> 1. Develop Orderbook class
>2. Orderbook.handleNewOrder method:
>>   1. Add suy orders into buyOrderList in descending price order.
>>   2. Add sell orders into sellOrderList in ascending price order.
>>   3. Match buy and sell orders if prices are same.
> 3. Orderbook.print method:
>>    1. Print orderbook name at first row.
>>    2. Print match list.
>>    3. Print buy orders.
>>    4. Print sell orders.
>
> Example
>
> Orderbook name: APPLE
> Matches:
>> 1. Match 10@45
>> 2. Match 20@46
>
> Buy orders:
>> 1. Buy 10@46
>> 2. Buy 20@45
>
> Sell orders:
>> 1. Sell 30@47
>> 2. Sell 20@48I have t