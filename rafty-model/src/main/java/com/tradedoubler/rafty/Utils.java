package com.tradedoubler.rafty;

import com.google.common.collect.Lists;
import io.atomix.catalyst.transport.Address;

import java.util.List;

/**
 * @author qinwa
 */
public class Utils {
    /**
     * Addresses are comma separated hostname:port. Such as:
     * 192.168.0.10:8521,192.168.0.11:8521,192.168.0.12:8521
     * Note that the first address is the current host.
     */
    public static List<Address> parseAddresses(String arg) {
        if (arg == null || arg.isEmpty()) {
            throw new RuntimeException("Addresses must be specified in args!");
        }
        List<Address> addresses = Lists.newArrayList();
        String[] splited = arg.split(",");
        for (String addr : splited) {
            String[] hostAndPort = addr.split(":");
            Address address = new Address(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
            addresses.add(address);
        }

        return addresses;
    }
}
