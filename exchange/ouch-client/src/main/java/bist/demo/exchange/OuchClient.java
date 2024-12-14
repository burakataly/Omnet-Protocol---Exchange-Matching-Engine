package bist.demo.exchange;

import bist.demo.exchange.common.Constants;
import bist.demo.exchange.common.message.Command;
import bist.demo.exchange.ouch.gateway.OuchGateway;

import java.nio.ByteBuffer;

public class OuchClient {
    private final OuchGateway ouchGateway;

    public OuchClient(OuchGateway ouchGateway) {
        this.ouchGateway = ouchGateway;
    }

    public void sendOrder(String commodity, byte side, int quantity, int price){

        if (side != Constants.SIDE_BUY && side != Constants.SIDE_SELL){
            throw new IllegalArgumentException("Invalid side. Must be 'B' or 'S'.");
        }
        if (commodity == null || commodity.length() > Constants.MAX_COMMODITY_LENGTH) {
            throw new IllegalArgumentException("Invalid commodity name. Must be non-null and up to 5 characters.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }

        ByteBuffer data = ByteBuffer.allocate(Constants.ORDER_DATA_LENGTH);

        data.put(side);

        StringBuilder fixedSizeCommodityName = getCommodityName(commodity);

        for (int i = 0; i < 5; i++) {
            data.put((byte) fixedSizeCommodityName.charAt(i));
        }

        data.putInt(quantity);
        data.putInt(price);

        ouchGateway.sendMessage(Command.SEND_ORDER, data);
    }

    private StringBuilder getCommodityName(String commodity) {
        StringBuilder fixedSizeCommodityName = new StringBuilder(commodity);
        while (fixedSizeCommodityName.length() < 5) {
            fixedSizeCommodityName.append(' ');
        }
        return fixedSizeCommodityName;
    }
}
