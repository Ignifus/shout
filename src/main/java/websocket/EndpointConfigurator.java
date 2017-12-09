package websocket;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class EndpointConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        if (request.getHeaders().containsKey("sec-websocket-protocol")) {
            sec.getUserProperties().put("wskey", request.getHeaders().get("sec-websocket-protocol").get(0));
        }
    }
}