package bist.demo.exchange.common.message;

public enum COMMAND {
    HEARTBEAT(0x48);

    private final int value;

    COMMAND(int value) {
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }

    public static boolean isInvalidCommand(int value){
        for(COMMAND command : COMMAND.values()){
            if(value == command.getValue()) return false;
        }
        return true;
    }
}
