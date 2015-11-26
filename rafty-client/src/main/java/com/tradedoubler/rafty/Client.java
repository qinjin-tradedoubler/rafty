package com.tradedoubler.rafty;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.NettyTransport;
import io.atomix.copycat.client.CopycatClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author qinwa
 */
public class Client {
    private CopycatClient copycatClient;

    public Client(Address serverAddress) {
        copycatClient = CopycatClient.builder(serverAddress)
                .withTransport(new NettyTransport())
                .build();
    }

    public void start() {
        copycatClient.open().thenRun(() -> onStarted());
    }

    private void onStarted() {
        System.out.println("Successfully connected to the cluster!");

        try{
            TrackingEvent click = new Click("c-0",0,0,0, System.currentTimeMillis());
            TrackingEvent trackback = new Trackback("t-1",0,0,0, System.currentTimeMillis() );
            postEvent(click);
            postEvent(trackback);
            Thread.sleep(2000);
            TrackingEvent clickRead = getEvent(click.id);
            TrackingEvent trackbackRead = getEvent(trackback.id);
            System.out.println("Read event: "+ clickRead);
            System.out.println("Read event: "+ trackbackRead);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void postEvent(TrackingEvent event) {
        copycatClient.submit(new TrackingEventCommand(event)).thenAccept(result -> {
            System.out.println("Published event: " + result);
        });
    }

    public TrackingEvent getEvent(String trackingEventId) throws Exception {
        CompletableFuture<TrackingEvent> future = copycatClient.submit(new TrackingEventQuery(trackingEventId));
        return future.get(1, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws Exception {
        List<Address> addresses = Utils.parseAddresses(args[0]);
        Client client = new Client(addresses.get(0));
        Runtime.getRuntime().addShutdownHook(new ShutdownThread(client));
        client.start();
    }

    private static class ShutdownThread extends Thread{
        private final Client client;

        public ShutdownThread(Client client){
            this.client = client;
        }
        public void run() {
            client.copycatClient.close();
            System.out.println("Stopped Rafty client.");
        }
    }
}
