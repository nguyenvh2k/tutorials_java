package me.meocoder.processor;

import me.meocoder.config.SimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleConnectionPool {
    private final BlockingQueue<Connection> pool;

    public SimpleConnectionPool(int size, SimpleDataSource ds) throws SQLException {
        pool = new LinkedBlockingQueue<>(size);
        for (int i = 0; i < size; i++) {
            pool.add(ds.getConnection());
        }
    }

    public Connection borrowConnection() throws InterruptedException {
        return pool.take();
    }

    public void returnConnection(Connection c) {
        if (c != null) pool.offer(c);
    }

    public void closeAll() {
        pool.forEach(c -> { try { c.close(); } catch (Exception ignored) {} });
    }
}
