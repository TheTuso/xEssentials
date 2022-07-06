package pl.tuso.essentials.nametag;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class NametagComparator implements Comparator<Nametag> {
    @Override
    public int compare(@NotNull Nametag o1, @NotNull Nametag o2) {
        if (o1.getPriority() < o2.getPriority()) return -1;
        if (o1.getPriority() > o2.getPriority()) return 1;
        return 0;
    }
}
