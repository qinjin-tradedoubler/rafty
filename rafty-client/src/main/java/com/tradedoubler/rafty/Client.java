package com.tradedoubler.rafty;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.NettyTransport;
import io.atomix.copycat.client.CopycatClient;

/**
 * @author qinwa
 */
public class Client {
    private CopycatClient client;

    public Client(String serverAddress, int serverPort) {
        client = CopycatClient.builder(new Address(serverAddress, serverPort))
                .withTransport(new NettyTransport())
                .build();
    }

    public void start(){
        client.open().thenRun(() -> System.out.println("Successfully connected to the cluster!"));
    }

    //TODO
    public void postEvent(TrackingEvent event){
        client.submit(new TrackingEventCommand(event)).thenAccept(result -> {
            System.out.println("Result is " + result);
        });
    }

    //TODO
    public TrackingEvent readEvent(int id, TrackingEvent.Type type){
        return null;
    }
}
