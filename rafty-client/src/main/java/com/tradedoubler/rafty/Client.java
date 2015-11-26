package com.tradedoubler.rafty;

import com.google.common.collect.Lists;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.NettyTransport;
import io.atomix.copycat.client.CopycatClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
        int started = numStarted.incrementAndGet();
        onStarted(started);
    }

    private static void onStarted(int started) {
        if (started == 3) {
            log.info("All clients have been connected to the cluster!");

            int numEventsPerType = 1000;
            Random r = new Random();

            //Trackbacks
            for (int i = 0; i < numEventsPerType; i++) {
                int clientId = r.nextInt(3);
                Client client = clients.get(clientId);
                TrackingEvent trackback = new Trackback("t" + i, 0, 0, 0, System.nanoTime());
                client.postSequentialEvent(trackback);
            }
            try {
                Thread.sleep(10* 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Trackbacks....");
            for(Client client: clients){
                List<String> ids = client.getSequentialIds();
                System.out.println(ids);
            }

            //Impressions
            for (int i = 0; i < numEventsPerType; i++) {
                int clientId = r.nextInt(3);
                Client client = clients.get(clientId);
                TrackingEvent impression = new Impressoin("i" + i, 0, 0, System.nanoTime());
                client.postParallelEvent(impression);
            }

            try {
                Thread.sleep(10* 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Impressions....");
            for(Client client: clients){
                List<String> ids = client.getParallelIds();
                System.out.println(ids);
            }
        }
    }

    public void postSequentialEvent(TrackingEvent event) {
        copycatClient.submit(new SequentialCommand(event)).thenAccept(result -> {
            log.debug("Published sequential event: " + result);
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
            log.debug("Published parallel event: " + result);
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

    static AtomicInteger numStarted = new AtomicInteger();
    static List<Client> clients = Lists.newArrayList();

    public static void main(String[] args) throws Exception {
        List<Address> addresses = Utils.parseAddresses("localhost:8521,localhost:8522,localhost:8523");
        for (Address address : addresses) {
            Client client = new Client(address);
            clients.add(client);
            Runtime.getRuntime().addShutdownHook(new ShutdownThread(client));
            client.start();
        }
    }

    private static class ShutdownThread extends Thread {
        private final Client client;

        public ShutdownThread(Client client) {
            this.client = client;
        }

        public void run() {
            client.copycatClient.close();
            log.info("Stopped Rafty client.");
        }
    }
}
