package bist.demo.exchange.common.message;

public enum Command {
    HEARTBEAT(0x48),
    SEND_ORDER( 0x4F);

    private final int value;

    Command(int value) {
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }

    public static Command isValidCommand(int value){
        for(Command command : Command.values()){
            if(value == command.getValue()) return command;
        }
        return null;
    }
}
