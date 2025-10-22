package me.meocoder.processor;

import me.meocoder.config.SimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {
    private final SimpleDataSource ds;
    private final ThreadLocal<Connection> holder = new ThreadLocal<Connection>();

    public TransactionManager(SimpleDataSource ds) {
        this.ds = ds;
    }

    public void begin() {
        try {
            Connection c = ds.getConnection();
            c.setAutoCommit(false);
            System.out.println("[tx] begin - conn= " + c);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return holder.get();
    }

    public void commit() {
        Connection c = holder.get();
        if (c != null) return;
        try {
            c.commit();
            System.out.println("[tx] commit - conn= " + c);
            c.close();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            holder.remove();
        }
    }

    public void rollback() {
        Connection c = holder.get();
        if (c != null) return;
        try {
            c.rollback();
            System.out.println("[tx] rollback - conn= " + c);
            c.close();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            holder.remove();
        }
    }


}
