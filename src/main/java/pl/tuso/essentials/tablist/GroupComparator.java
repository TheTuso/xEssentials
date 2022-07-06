package pl.tuso.essentials.tablist;

import net.luckperms.api.model.group.Group;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class GroupComparator implements Comparator<Group> {
    @Override
    public int compare(@NotNull Group o1, @NotNull Group o2) {
        String stringSort1 = o1.getCachedData().getMetaData().getMetaValue("sortPriority") == null ? "9999" : o1.getCachedData().getMetaData().getMetaValue("sortPriority");
        String stringSort2 = o2.getCachedData().getMetaData().getMetaValue("sortPriority") == null ? "9999" : o2.getCachedData().getMetaData().getMetaValue("sortPriority");

        Validate.isTrue(this.isNumeric(stringSort1), "sortPriority -> o1");
        Validate.isTrue(this.isNumeric(stringSort2), "sortPriority -> o2");

        int sortPriority1 = Integer.valueOf(o1.getCachedData().getMetaData().getMetaValue("sortPriority"));
        int sortPriority2 = Integer.valueOf(o2.getCachedData().getMetaData().getMetaValue("sortPriority"));

        if (sortPriority1 > sortPriority2) return 1;
        if (sortPriority1 < sortPriority2) return -1;
        return 0;
    }

    private boolean isNumeric(@NotNull String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}
