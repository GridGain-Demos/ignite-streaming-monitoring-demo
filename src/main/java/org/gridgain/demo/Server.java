package org.gridgain.demo;

import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;

public class Server {
    public static void main(String[] args) {
        Ignition.start(new IgniteConfiguration().setPeerClassLoadingEnabled(true));
    }
}
