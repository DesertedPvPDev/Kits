package codes.matthewp.desertedpvp.kit;

import codes.matthewp.desertedpvp.user.User;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Interface rep of a kit object
 */
public class IKit {

    public String name;
    public int price;
    public List<String> lore;
    public Material iconMat;
    public String perm;

    public String intelID() {
        return null;
    }

    public String getName() {
        return name;
    }

    public String getPerm() {
        return perm;
    }

    public int getPrice() {
        return price;
    }

    public List<String> getLore() {
        return lore;
    }

    public void giveKit(Player p) {

    }

    public void gotKill(Player killer, Player killed) {

    }

    public void hasRightClicked(User p, ItemStack whatGotClicked) {

    }

    public boolean canKill(Player p) {
        return true;
    }

    public void load(ConfigurationSection sec) {
        name = sec.getString("name");
        price = sec.getInt("price");
        iconMat = Material.getMaterial(sec.getString("icon"));
        lore = sec.getStringList("lore");
        perm = sec.getString("perm");
    }

    public ItemStack icon() {
        ItemStack stack = new ItemStack(iconMat);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(color(getName()));
        meta.setLore(colorList(getLore()));
        stack.setItemMeta(meta);
        return stack;
    }

    public String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public List<String> colorList(List<String> list) {
        return list.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
    }

    public String stripColor(String str) {
        str = ChatColor.translateAlternateColorCodes('&', str);
        return ChatColor.stripColor(str);
    }
}
