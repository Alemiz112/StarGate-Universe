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

package alemiz.sgu;

import alemiz.sgu.events.ClientAuthenticatedEvent;
import alemiz.sgu.events.ClientConnectedEvent;
import alemiz.sgu.events.ClientDisconnectedEvent;
import alemiz.sgu.handler.PacketHandler;
import alemiz.stargate.client.ClientSession;

import java.net.InetSocketAddress;

public class StarGateClientListener extends alemiz.stargate.client.StarGateClientListener {

    private final StarGateUniverse loader;

    public StarGateClientListener(StarGateUniverse loader){
        this.loader = loader;
    }

    @Override
    public void onSessionCreated(InetSocketAddress address, ClientSession session) {
        ClientConnectedEvent event = new ClientConnectedEvent(session.getClient(), this.loader);
        this.loader.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Here we change default session handle to extended and modified for proxy.
     * @param session authenticated session instance
     */
    @Override
    public void onSessionAuthenticated(ClientSession session) {
        session.setPacketHandler(new PacketHandler(session, this.loader));

        ClientAuthenticatedEvent event = new ClientAuthenticatedEvent(session.getClient(), this.loader);
        this.loader.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()){
            session.disconnect(event.getCancelMessage());
        }
    }

    @Override
    public void onSessionDisconnected(ClientSession session) {
        ClientDisconnectedEvent event = new ClientDisconnectedEvent(session.getClient(), this.loader);
        this.loader.getServer().getPluginManager().callEvent(event);
    }
}
