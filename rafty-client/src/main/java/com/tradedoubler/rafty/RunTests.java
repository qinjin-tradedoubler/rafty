package com.tradedoubler.rafty;

import com.google.common.collect.Lists;
import io.atomix.catalyst.transport.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by qinjin on 26/11/15.
 */
public class RunTests {
    private static final Logger log = LoggerFactory.getLogger(Client.class);

    private static int NUM_EVENT_PER_TYPE = 100;

    private static void runTests(List<Client> clients) {
        log.info("All clients have been connected to the cluster!");
        int numClients = clients.size();
        //Trackbacks
        List<ClientWorker> clientWorkers = Lists.newArrayList();
        for (int i = 0; i < clients.size(); i++) {
            ClientWorker worker = new ClientWorker(clients.get(i), numClients, TrackingEvent.Type.Trackback);
            clientWorkers.add(worker);
        }

        for (ClientWorker worker : clientWorkers) {
            worker.start();
        }

        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Impressions
        clientWorkers.clear();
        for (int i = 0; i < clients.size(); i++) {
            ClientWorker worker = new ClientWorker(clients.get(i), numClients, TrackingEvent.Type.Impression);
            clientWorkers.add(worker);
        }

        for (ClientWorker worker : clientWorkers) {
            worker.start();
        }

        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Trackbacks....");
        for (Client client : clients) {
            List<String> ids = client.getSequentialIds();
            System.out.println("Client "+client.getId() +" "+ ids);
        }

        System.out.println("Impressions....");
        for (Client client : clients) {
            List<String> ids = client.getParallelIds();
            System.out.println(ids);
        }
    }

    public static void main(String[] args) throws Exception {
        log.info("####################### Starting Rafty Test ########################");
        log.info("MembersList NumberOfEventPerType");
        log.info("Example of args: localhost:8521,localhost:8522,localhost:8523, 1000");
        log.info("######################################################################");
        //Each client connect to one raft server.
        List<Address> addresses = Utils.parseAddresses(args[0]);
        NUM_EVENT_PER_TYPE = Integer.parseInt(args[1]);
        List<Client> clients = Lists.newArrayList();
        for (int i = 0; i < addresses.size(); i++) {
            Client client = new Client(i, addresses.get(i));
            clients.add(client);
            Runtime.getRuntime().addShutdownHook(new ShutdownThread(client));
            client.start();
        }

        runTests(clients);
    }

    private static class ShutdownThread extends Thread {
        private final Client client;

        public ShutdownThread(Client client) {
            this.client = client;
        }

        public void run() {
            client.getCopycatClient().close();
            log.info("Stopped Rafty client " + client.getId());
        }
    }

    private static class ClientWorker extends Thread {
        private final Client client;
        private final int numClients;
        private final TrackingEvent.Type eventType;

        public ClientWorker(Client client, int numClients, TrackingEvent.Type eventType) {
            this.client = client;
            this.numClients = numClients;
            this.eventType = eventType;
        }

        public void run() {
            for (int i = 0; i < NUM_EVENT_PER_TYPE; i++) {
                if (i % numClients == client.getId()) {
                    if (eventType == TrackingEvent.Type.Trackback) {
                        TrackingEvent trackback = new Trackback("t" + i, 0, 0, 0, System.nanoTime());
                        client.postSequentialEvent(trackback);
                    } else if (eventType == TrackingEvent.Type.Click) {
                        TrackingEvent click = new Click("c" + i, 0, 0, 0, System.nanoTime());
                        client.postSequentialEvent(click);
                    } else {
                        TrackingEvent impression = new Impressoin("i" + i, 0, 0, System.nanoTime());
                        client.postParallelEvent(impression);
                    }
                }
            }
        }
    }
}
