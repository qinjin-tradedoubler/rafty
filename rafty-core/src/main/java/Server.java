import com.tradedoubler.rafty.Utils;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.NettyTransport;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;

import java.util.List;

/**
 * @author qinwa
 */
public class Server {
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

    public void start() {
        System.out.println("Starting Rafty server on " + serverAddress.toString() + "...");
        copyCatServer.open().join();
    }

    public static void main(String[] args) {
        List<Address> addresses = Utils.parseAddresses(args[0]);
        Server server = new Server(addresses.get(0), addresses, "/var/log/rafty");
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
            System.out.println("Stopped Rafty server.");
        }
    }
}
