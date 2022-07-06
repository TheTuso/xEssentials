package pl.tuso.essentials.nametag;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface NametagProperties {
    void setValue(Component valuse);

    Component getValue();
    void setPriority(int priority);

    int getPriority();

    void setOffset(double offset);

    double getOffset();

    void teleport();

    void teleport(Player viewer);

    void sneak(boolean sneaking);

    void destroy();

    void destroy(Player viewer);

    void refresh();

    void spawn(Player viewer);

    void respawn(Player viewer);
}
