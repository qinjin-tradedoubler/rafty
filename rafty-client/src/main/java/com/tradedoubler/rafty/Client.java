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

    private final CopycatClient copycatClient;
    private final int id;

    public Client(int id, Address serverAddress) {
        this.id = id;
        copycatClient = CopycatClient.builder(serverAddress)
                .withTransport(new NettyTransport())
                .build();
    }

    public void start() {
        copycatClient.open().join();
        log.info("Client "+id+" started.");
    }

    public int getId() {
        return id;
    }

    public CopycatClient getCopycatClient() {
        return copycatClient;
    }

    public void postSequentialEvent(TrackingEvent event) {
        copycatClient.submit(new SequentialCommand(event)).thenAccept(result -> {
            log.debug("Client "+getId()+" Published sequential event: " + result);
        });
    }

    public TrackingEvent getSequentialEvent(String trackingEventId) {
        CompletableFuture<TrackingEvent> future = copycatClient.submit(new SequentialEventQuery(trackingEventId));
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<String> getSequentialIds() {
        CompletableFuture<List<String>> future = copycatClient.submit(new SequentialIdsQuery());
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void postParallelEvent(TrackingEvent event) {
        copycatClient.submit(new ParallelEventCommand(event)).thenAccept(result -> {
            log.debug("Client "+getId()+" Published parallel event: " + result);
        });
    }

    public TrackingEvent getParallelEvent(String trackingEventId) {
        CompletableFuture<TrackingEvent> future = copycatClient.submit(new ParallelEventQuery(trackingEventId));
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getParallelIds() {
        CompletableFuture<List<String>> future = copycatClient.submit(new ParallelIdsQuery());
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
