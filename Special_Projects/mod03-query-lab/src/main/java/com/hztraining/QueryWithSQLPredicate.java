package com.hztraining;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientUserCodeDeploymentConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hztraining.inv.Inventory;
import com.hztraining.inv.InventoryKey;
import com.hazelcast.query.SqlPredicate;
import com.hztraining.inv.InventoryTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class QueryWithSQLPredicate {

    private HazelcastInstance client;
    private static long start;

    public Collection<Inventory> queryNearbyStores(IMap<InventoryKey, Inventory> invmap, String item, String[] locations) {
        String locnKeys = String.join(", ", locations);
        // TODO: Query for items where
        //      SKU matches passed SKU;
        //      Location is in list of passed locations,
        //      and quantity is greater than zero.
        // If you need help with SQL syntax, see
        //      https://docs.hazelcast.org/docs/latest/manual/html-single/#supported-sql-syntax
        SqlPredicate predicate = new SqlPredicate("FIXME"); // TODO
        System.out.println("Query: " + predicate.toString());
        start = System.currentTimeMillis();
        Collection<Inventory> results = invmap.values(predicate);
        return results;
    }

    public static void main(String[] args) {

        String configname = ConfigUtil.findConfigNameInArgs(args);
        ClientConfig config = ConfigUtil.getClientConfigForCluster(configname);

        ClientUserCodeDeploymentConfig ucd = config.getUserCodeDeploymentConfig();
        ucd.setEnabled(true);
        ucd.addClass(Inventory.class);

        // Query using SQLPredicate
        QueryWithSQLPredicate main = new QueryWithSQLPredicate();
        main.client = HazelcastClient.newHazelcastClient(config);

        try {
            long start = System.currentTimeMillis();
            // We want to query whether an item is available at nearby stores
            // Items are in format Item + six digit item number, 0-999
            // Locations are 4 digit numeric, warehouses numbered 1-5, stores 101-150.
            String item = "Item000037";
            String[] stores = new String[]{"0121", "0132", "0106"};

            // Without index
            IMap<InventoryKey, Inventory> invmap = main.client.getMap("invmap");
            Collection<Inventory> nearby = main.queryNearbyStores(invmap, item, stores);
            for (Inventory i : nearby)
                System.out.println(i);

            // With index
            IMap<InventoryKey, Inventory> invmapi = main.client.getMap("invmap_indexed");
            nearby = main.queryNearbyStores(invmapi, item, stores);
            for (Inventory i : nearby)
                System.out.println(i);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           main.client.shutdown();
        }

    }
}