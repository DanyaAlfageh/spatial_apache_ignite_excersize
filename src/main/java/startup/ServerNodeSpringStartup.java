package startup;

import org.apache.ignite.Ignition;

/** This file was generated by Ignite Web Console (02/11/2021, 12:55) **/
public class ServerNodeSpringStartup {
    /**
     * Start up node with specified configuration.
     * 
     * @param args Command line arguments, none required.
     * @throws Exception If failed.
     **/
    public static void main(String[] args) throws Exception {
        Ignition.start("Cluster-2-server.xml");
    }
}