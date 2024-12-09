package bist.demo.exchange.common.message;

import java.nio.ByteBuffer;

public class CommonUtils {
    public static boolean CRCChecker(byte[] array){
        int expectedCRC = array[array.length - 1];
        int calculatedCRC = calculateCRC(array);
        return expectedCRC == calculatedCRC;
    }

    public static int calculateCRC(byte[] array){
        int calculatedCRC = array[0];
        for (int i = 1; i < array.length - 1; i++) {
            calculatedCRC ^= array[i];
        }
        return calculatedCRC % 256;
    }

    public static ByteBuffer createOuchMessage(int command, byte[] data){
        int dataLength = data.length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(dataLength + 5);
        byteBuffer.put((byte) 0xAA);
        byteBuffer.putShort((short) (1 + data.length));
        byteBuffer.put((byte) command);
        byteBuffer.put(data);
        byteBuffer.put((byte) calculateCRC(byteBuffer.array())); //byteBuffer.array() tüm
        // allocate edilen bufferı array olarak döndürecek. yani crc için olan son
        // byte daha put edilmemiş ve çöp değerken de bu arrayde bulunacak. ama crc'yi input
        // dizinin son elemanı hariç hesapladığımdan doğru bir şekilde hesaplar
        byteBuffer.flip();
        return byteBuffer;
    }

    public static boolean isInvalidOuchMessage(ByteBuffer buffer){
        if(buffer.get(0) != (byte) 0xAA){
            System.out.println("Invalid start byte...");
            return true;
        }
        if(buffer.getShort(1) + 4 != buffer.remaining()){
            System.out.println("Invalid message length...");
            return true;
        }
        if(COMMAND.isInvalidCommand(buffer.get(3))){
            System.out.println("Invalid command type...");
            return true;
        }
        return false;
    }
}
