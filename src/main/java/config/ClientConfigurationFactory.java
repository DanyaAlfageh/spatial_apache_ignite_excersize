package config;

import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.sharedfs.TcpDiscoverySharedFsIpFinder;

/** This file was generated by Ignite Web Console (02/11/2021, 12:55) **/
public class ClientConfigurationFactory {
    /**
     * Configure grid.
     * 
     * @return Ignite configuration.
     * @throws Exception If failed to construct Ignite configuration instance.
     **/
    public static IgniteConfiguration createConfiguration() throws Exception {
        IgniteConfiguration cfg = new IgniteConfiguration();

        cfg.setClientMode(true);
        cfg.setIgniteInstanceName("Cluster-2");

        TcpDiscoverySpi discovery = new TcpDiscoverySpi();

        discovery.setIpFinder(new TcpDiscoverySharedFsIpFinder());

        cfg.setDiscoverySpi(discovery);

        DataStorageConfiguration dataStorageCfg = new DataStorageConfiguration();

        DataRegionConfiguration dataRegionCfg = new DataRegionConfiguration();

        dataRegionCfg.setInitialSize(1073741824L);
        dataRegionCfg.setMaxSize(21474836480L);
               


        dataStorageCfg.setDefaultDataRegionConfiguration(dataRegionCfg);
        cfg.setDataStorageConfiguration(dataStorageCfg);

        
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempDir2 = System.getProperty("java.io.tmpdir2");

        
        // Emulates that Ignite work directory has not been calculated,
        // and IgniteUtils#workDirectory resolved directory into "java.io.tmpdir"
        cfg.setWorkDirectory(tempDir);
        
        cfg.setIgniteHome(tempDir2);


        return cfg;
    }
}