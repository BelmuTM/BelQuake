package com.belmu.quake.Core.Stats;

public enum StreaksMessages {

    FIVE(5, "§7%player% §bis on a §eKilling Spree"),
    TEN(10, "§7%player% §bis on a §cRampage"),
    FIFTEEN(15, "§7%player% §bis §dDominating"),
    TWENTY(20, "§7%player% §bis §4Unstoppable"),
    TWENTYFIVE(25, "§7%player% §bis §4§lG§c§lO§6§lD§e§lL§a§lI§b§lK§d§lE");

    private int amount;
    private String msg;

    private StreaksMessages(int amount, String msg) {
        this.amount = amount;
        this.msg = msg;
    }

    public static String getStreaksMsgByAmount(int killAmount) {

        for(StreaksMessages streaksMessage : StreaksMessages.values()) {
            if(streaksMessage.amount == killAmount) return streaksMessage.msg;
        }
        return null;
    }
}
