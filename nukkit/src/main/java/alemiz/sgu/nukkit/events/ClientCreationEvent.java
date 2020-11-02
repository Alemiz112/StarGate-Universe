/*
 * Copyright 2020 Alemiz
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package alemiz.sgu.nukkit.events;

import alemiz.sgu.nukkit.StarGateUniverse;
import alemiz.stargate.client.StarGateClient;
import cn.nukkit.event.HandlerList;

public class ClientCreationEvent extends ClientEvent {

    private static final HandlerList handlers = new HandlerList();

    public ClientCreationEvent(StarGateClient client, StarGateUniverse plugin) {
        super(client, plugin);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
