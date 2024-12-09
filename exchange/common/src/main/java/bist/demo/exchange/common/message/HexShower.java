package bist.demo.exchange.common.message;

public class HexShower {

    public static String convertToHexString(byte[] array) {

        if (array == null || array.length == 0) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append('[');
        for (int i = 0; i < array.length - 1; i++) {
            stringBuilder.append(String.format("%02X ", array[i]));
        }
        stringBuilder.append(String.format("%02X]", array[array.length - 1]));

        return stringBuilder.toString();
    }
}
