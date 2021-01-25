package com.belmu.quake.Core.Packets.TabList;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public enum TabEnum {

    A(0, "§5§lQUAKECRAFT\n    §7Developed by §b@Belmu_", "§6Ha§eve §fFu§en!"),
    B(1, "§d§lQUAKECRAFT\n    §7Developed by §b@Belmu_", "§eHa§6ve §eFu§fn!"),
    C(2, "§f§lQUAKECRAFT\n    §7Developed by §b@Belmu_", "§fHa§eve §6Fu§en!"),
    D(3, "§d§lQUAKECRAFT\n    §7Developed by §b@Belmu_", "§eHa§fve §eFu§6n!"),
    E(4, "§5§lQUAKECRAFT\n    §7Developed by §b@Belmu_", "§6Ha§eve §fFu§en!");

    public int id;
    public String header, footer;

    private TabEnum(int id, String header, String footer) {
        this.id = id;
        this.header = header;
        this.footer = footer;
    }

}
