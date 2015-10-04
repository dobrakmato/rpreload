package eu.matejkormuth.rpreload.resourcepack.sources;

import eu.matejkormuth.rpreload.resourcepack.Chain;
import org.bukkit.entity.Player;

public class EmptySettingSource extends Chain<Player, String> {

    @Override
    public String process(Player input) {

        // This is last element, this must provide resource pack.
        return "";
    }
}
