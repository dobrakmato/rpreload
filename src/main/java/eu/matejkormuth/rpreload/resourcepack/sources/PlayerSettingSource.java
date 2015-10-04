package eu.matejkormuth.rpreload.resourcepack.sources;

import eu.matejkormuth.rpreload.resourcepack.Chain;
import org.bukkit.entity.Player;

public class PlayerSettingSource extends Chain<Player, String> {

    @Override
    public String process(Player input) {


        // If can't resolve.
        return next.process(input);
    }
}
