package codes.matthewp.hypepvp.kit;

import codes.matthewp.hypepvp.kit.kits.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KitManager {

    private static HashMap<Material, IKit> matToKit;
    private static List<IKit> kits;

    public static void addKit(IKit kit) {
        checkNull();
        kits.add(kit);
    }

    public static IKit getKit(String intelID) {
        for(IKit kit : kits) {
            if(kit.intelID().equals(intelID)) {
                return kit;
            }
        }
        return null;
    }


    public static IKit getKitFromMat(Material mat) {
        return matToKit.get(mat);
    }

    public static void lazyLoad() {
        addKit(new KnightKit());
        addKit(new TosserKit());
        addKit(new ArcherKit());
        addKit(new ManiacKit());
        addKit(new JuggernautKit());
    }

    public static void loadKit(String key, ConfigurationSection section) {
        for(IKit kit : kits) {
            if(kit.intelID().equals(key)) {
                kit.load(section);
                matToKit.put(kit.iconMat, kit);
            }
        }
    }

    public static List<IKit> getPlayerKits(Player p) {
        checkNull();
        List<IKit> pKits = new ArrayList<>();

        if (p.isOp() || p.hasPermission("hypepvp.kit.*")) {
            pKits = kits;
        } else {
            for (IKit kit : kits) {
                if (p.hasPermission(kit.getPerm())) {
                    pKits.add(kit);
                }
            }
        }
        return pKits;
    }


    private static void checkNull() {
        if (matToKit == null) {
            matToKit = new HashMap<>();
        }
        if (kits == null) {
            kits = new ArrayList<>();
        }
    }
}