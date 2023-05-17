package org.gridgain.demo;

import org.apache.ignite.Ignition;

public class Server {
    public static void main(String[] args) {
        Ignition.start("config/ignite-config-node.xml");
    }
}
