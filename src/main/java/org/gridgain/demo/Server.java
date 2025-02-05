package org.gridgain.demo;

import org.apache.ignite.Ignition;

public class Server {
    public static void main(String[] args) throws Exception {
    	DemoConfiguration cfg = new DemoConfiguration();
        Ignition.start(cfg);
    }
}
