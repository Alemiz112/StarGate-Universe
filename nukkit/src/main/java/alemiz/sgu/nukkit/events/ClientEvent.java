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
import alemiz.stargate.client.ClientSession;
import alemiz.stargate.client.StarGateClient;
import cn.nukkit.event.Event;

public class ClientEvent extends Event {

    private final StarGateUniverse plugin;
    private final StarGateClient client;

    public ClientEvent(StarGateClient client, StarGateUniverse plugin){
        this.client = client;
        this.plugin = plugin;
    }

    public StarGateClient getClient() {
        return this.client;
    }

    public ClientSession getSession() {
        return this.client.getSession();
    }

    public StarGateUniverse getPlugin() {
        return this.plugin;
    }
}
