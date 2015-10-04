/**
 * rpreload - Resource pack management made easy.
 * Copyright (c) 2015, Matej Kormuth <http://www.github.com/dobrakmato>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.matejkormuth.rpreload.resourcepack.sources;

import eu.matejkormuth.bmboot.facades.Container;
import eu.matejkormuth.rpreload.resourcepack.Chain;
import eu.matejkormuth.rpreload.resourcepack.ResourcePackModule;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class WorldSettingSource extends Chain<Player, String> {

    public static final String NO_RP = "#NO_RP#";

    @Override
    public String process(@Nonnull Player input) {

        ResourcePackModule rpm = Container.get(ResourcePackModule.class);

        // Name of resource pack for this world.
        String rpName = rpm.getWorldPacks().getString(input.getWorld().getName(), NO_RP);

        // If any.
        if (!NO_RP.equals(rpName)) {
            // Get URL of resource pack.
            return rpm.getResourcePacks().getString(rpName);
        }

        return next.process(input);
    }
}
