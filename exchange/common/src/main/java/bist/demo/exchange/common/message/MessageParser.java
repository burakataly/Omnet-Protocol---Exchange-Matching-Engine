package bist.demo.exchange.common.message;

import bist.demo.exchange.common.Constants;

import java.nio.ByteBuffer;
import java.util.EnumMap;

public class MessageParser {

    private final EnumMap<Command, MessageHandler> parsers;

    public MessageParser(MessageHandler defaultHandler) {
        parsers = new EnumMap<>(Command.class);

        for (Command omnetCommand : Command.values()) { //her command için default handler
            parsers.put(omnetCommand, defaultHandler);
        }
    }

    public void addParser(Command command, MessageHandler handler) {
        parsers.put(command, handler); //yeni handler default'u ezecek
    }

    public ByteBuffer parseReceivedMessage(ByteBuffer receivedMessage) {
        if(receivedMessage == null) return null;
        int size = receivedMessage.remaining();
        System.out.println("---------------------------------------------");
        System.out.printf("Received size: %d\n", size);

        byte[] receivedBytes = new byte[size];
        receivedMessage.get(receivedBytes);
        System.out.printf("Received buffer: %s\n", HexShower.convertToHexString(receivedBytes));

        receivedMessage.position(0);

        byte start = receivedMessage.get();
        if(start != Constants.START_BYTE){
            System.out.printf("ERROR: %s...\n", MessageParserResult.WRONG_MESSAGE_START);
            return null;
        }

        short dataLength = receivedMessage.getShort();
        if(dataLength + Constants.MESSAGE_LENGTH_EXCEPT_DATA_AND_COMMAND != size){
            System.out.printf("ERROR: %s...\n", MessageParserResult.SIZE_MISMATCH);
            return null;
        }

        byte commandValue = receivedMessage.get();
        Command command = Command.isValidCommand(commandValue);

        if(command == null){
            System.out.printf("ERROR: %s...\n", MessageParserResult.UNKNOWN_COMMAND);
            return null;
        }

        boolean crcResult = CommonUtils.CRCChecker(receivedBytes);

        if(crcResult){
            System.out.println("CRC is true");
        }
        else{
            System.out.printf("ERROR: %s...\n", MessageParserResult.CRC_MISMATCH);
            return null;
        }

        System.out.println("---------------------------------------------");

        ByteBuffer receivedData = ByteBuffer.allocate(dataLength - 1);

        for(int i = 0; i < dataLength - 1; i++){
            receivedData.put(receivedMessage.get());
        }

        receivedData.flip();

        receivedMessage.get();
        byte endByte = receivedMessage.get();
        if(endByte != Constants.END_BYTE){
            System.out.printf("ERROR: %s...\n", MessageParserResult.WRONG_MESSAGE_END);
            return null;
        }

        MessageHandler handler = parsers.get(command);

        return handler.handle(command, receivedData); //response döndürülür
    }
}

