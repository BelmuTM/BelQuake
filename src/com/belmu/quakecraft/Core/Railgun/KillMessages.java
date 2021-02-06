package com.belmu.quakecraft.Core.Railgun;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public enum KillMessages {

    DOUBLE(2, "§e§lDOUBLE KILL"),
    TRIPLE(3, "§2§lTRIPLE KILL"),
    QUADRUPLE(4, "§6§lQUADRUPLE KILL"),
    PENTA(5, "§4§lP§6§lE§e§lN§a§lT§2§lA §3§lK§b§lI§d§lL§5§lL");

    private int amount;
    private String msg;

    private KillMessages(int amount, String msg) {
        this.amount = amount;
        this.msg = msg;
    }

    public static String getKillMsgByAmount(int killAmount) {

        for(KillMessages killMessage : KillMessages.values()) {
            if(killMessage.amount == killAmount) return killMessage.msg;
        }
        return null;
    }
}
