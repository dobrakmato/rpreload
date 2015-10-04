package eu.matejkormuth.rpreload.resourcepack.sources;

import eu.matejkormuth.rpreload.resourcepack.Chain;
import org.bukkit.entity.Player;

public class GlobalSettingSource extends Chain<Player, String> {
    @Override
    public String process(Player input) {
        return next.process(input);
    }
}
