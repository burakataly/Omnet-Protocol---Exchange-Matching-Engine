package bist.demo.exchange.common.message;

import bist.demo.exchange.common.Constants;

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

    public static ByteBuffer createOuchMessage(byte command, byte[] data){
        int dataLength = data.length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(dataLength + 1 + Constants.MESSAGE_LENGTH_EXCEPT_DATA_AND_COMMAND);
        byteBuffer.put((byte) 0xAA);
        byteBuffer.putShort((short) (1 + dataLength));
        byteBuffer.put(command);
        byteBuffer.put(data);
        byteBuffer.put((byte) calculateCRC(byteBuffer.array())); //byteBuffer.array() tüm
        // allocate edilen bufferı array olarak döndürecek. yani crc için olan son
        // byte daha put edilmemiş ve çöp değerken de bu arrayde bulunacak. ama crc'yi input
        // dizinin son elemanı hariç hesapladığımdan doğru bir şekilde hesaplar
        byteBuffer.flip();
        return byteBuffer;
    }

}
