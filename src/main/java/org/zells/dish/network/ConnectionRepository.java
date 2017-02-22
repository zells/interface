package org.zells.dish.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionRepository {

    private List<ConnectionFactory> factories = new ArrayList<ConnectionFactory>();
    private Map<String, Connection> connections = new HashMap<String, Connection>();

    public void add(ConnectionFactory factory) {
        factories.add(factory);
    }

    public Connection getConnectionOf(String description) {
        if (connections.containsKey(description)) {
            return connections.get(description);
        }

        for (ConnectionFactory factory : factories) {
            if (factory.canBuild(description)) {
                Connection connection = factory.build(description);
                connections.put(description, connection);
                return connection;
            }
        }

        throw new RuntimeException("cannot build connection from: " + description);
    }
}
