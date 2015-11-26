package com.tradedoubler.rafty;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.NettyTransport;
import io.atomix.copycat.client.CopycatClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author qinwa
 */
public class Client {
    private static final Logger log = LoggerFactory.getLogger(Client.class);
    
    private CopycatClient copycatClient;

    public Client(Address serverAddress) {
        copycatClient = CopycatClient.builder(serverAddress)
                .withTransport(new NettyTransport())
                .build();
    }

    public void start() {
        copycatClient.open().join();
        onStarted();
//        copycatClient.open().thenRun(() -> log.info("Successfully connected to the cluster!"));
    }

    private void onStarted() {
        log.debug("Successfully connected to the cluster!");

        try{
            TrackingEvent click = new Click("c-0",0,0,0, System.currentTimeMillis());
            TrackingEvent trackback = new Trackback("t-1",0,0,0, System.currentTimeMillis() );
            postEvent(click);
            postEvent(trackback);
            Thread.sleep(2000);
            TrackingEvent clickRead = getEvent(click.id);
            TrackingEvent trackbackRead = getEvent(trackback.id);
            log.info("Read event: "+ clickRead);
            log.info("Read event: "+ trackbackRead);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void postEvent(TrackingEvent event) {
        copycatClient.submit(new TrackingEventCommand(event)).thenAccept(result -> {
            log.info("Published event: " + result);
        });
    }

    public TrackingEvent getEvent(String trackingEventId) {
        CompletableFuture<TrackingEvent> future = copycatClient.submit(new TrackingEventQuery(trackingEventId));
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
            log.info("Stopped Rafty client.");
        }
    }
}
