package com.tradedoubler.rafty;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.NettyTransport;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author qinwa
 */
public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final Address serverAddress;
    private final CopycatServer copyCatServer;

    public Server(Address serverAddress, List<Address> members, String storageDir) {
        this.serverAddress = serverAddress;
        this.copyCatServer = CopycatServer.builder(this.serverAddress, members)
                .withTransport(new NettyTransport())
                .withStateMachine(new TrackingEventStateMachine())
                .withStorage(new Storage(storageDir, StorageLevel.MEMORY))
                .build();
    }

    public void start() throws InterruptedException {
        log.info("Starting Rafty server on " + serverAddress.toString() + "...");
        copyCatServer.open().join();
        log.info("Rafty server started as "+copyCatServer.state());
        copyCatServer.onStateChange(state -> {
            log.info("Rafty server state change to " + state);
        });
        while (copyCatServer.isOpen()) {
            Thread.sleep(1000);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        log.info("####################### Starting Rafty Server ########################");
        log.info("MembersList serverId");
        log.info("Example of args: localhost:8521,localhost:8522,localhost:8523, 0");
        log.info("######################################################################");

        List<Address> addresses = Utils.parseAddresses(args[0]);
        Integer serverId = Integer.parseInt(args[1]);
        Address currentServer = addresses.get(0);
        addresses.remove(0);
        Server server = new Server(currentServer, addresses, System.getProperty("user.dir") + "/logs/" + serverId);
        Runtime.getRuntime().addShutdownHook(new ShutdownThread(server));
        server.start();
    }

    private static class ShutdownThread extends Thread {
        private final Server server;

        public ShutdownThread(Server server) {
            this.server = server;
        }

        public void run() {
            server.copyCatServer.close();
            log.info("Stopped Rafty server.");
        }
    }
}
