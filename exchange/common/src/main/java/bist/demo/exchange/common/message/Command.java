package bist.demo.exchange.common.message;

public enum Command {
    HEARTBEAT((byte) 0x48),
    SEND_ORDER((byte) 0x4F);

    private final byte value;

    Command(byte value) {
        this.value = value;
    }

    public byte getValue(){
        return this.value;
    }

    public static Command isValidCommand(byte value){
        for(Command command : Command.values()){
            if(value == command.getValue()) return command;
        }
        return null;
    }
}
