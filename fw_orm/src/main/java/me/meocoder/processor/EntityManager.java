package me.meocoder.processor;

import me.meocoder.anotation.Column;
import me.meocoder.anotation.Id;
import me.meocoder.anotation.Table;
import me.meocoder.config.SimpleDataSource;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityManager {
    private final SimpleDataSource ds;
    private final TransactionManager txManager;

    public EntityManager(SimpleDataSource ds, TransactionManager txManager) {
        this.ds = ds;
        this.txManager = txManager;
    }

    private Connection getConnection() throws SQLException {
        Connection txConn = txManager.getConnection();
        if (txConn != null) return txConn;
        Connection conn = ds.getConnection();
        conn.setAutoCommit(false);
        return conn;
    }

    public <T> void persist(T entity) {
        Class<?> cl = entity.getClass();
        String tableName = getTableName(cl);
        Field idField = findIdField(cl);
        List<Field> cols = new ArrayList<>();

        for (Field f : cl.getDeclaredFields()) {
            if (f.equals(idField)) continue; // assume id auto-generated
            if (f.isSynthetic()) continue;
            cols.add(f);
        }

        String colNames = String.join(",", mapNames(cols));
        String placeHolder = String.join(",", Collections.nCopies(cols.size(), "?"));
        String sql = "INSERT INTO " + tableName + " (" + colNames + ") VALUES (" + placeHolder + ")";
        System.out.println("[sql] :: " + sql);
        Connection c = null;
        boolean isTx = txManager.getConnection() != null;
        PreparedStatement ps = null;
        try {
            c = getConnection();
            ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (!isTx) {
                // nếu không trong transaction thì tự commit & đóng connection
                c.commit();
                c.close();
            }

            int i = 1;
            for (Field f : cols) {
                f.setAccessible(true);
                ps.setObject(i++, f.get(entity));
            }
            ps.executeUpdate();

            if (idField != null) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    Object key = keys.getObject(1);
                    idField.setAccessible(true);
                    idField.set(entity, convertIfNeeded(key, idField.getType()));
                }
            }
        } catch (Exception e) {
            if (!isTx && c != null) {
                try {
                    c.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public <T> T find(Class<T> cl, Object id) {
        String table = getTableName(cl);
        Field idField = findIdField(cl);
        String idCol = getColumnName(idField);
        String sql = "SELECT * FROM " + table + " WHERE " + idCol + " = ?";
        System.out.println("[sql] :: " + sql);
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;
            T entity = cl.getDeclaredConstructor().newInstance();
            ResultSetMetaData md = rs.getMetaData();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                String col = md.getColumnName(i);
                Object val = rs.getObject(i);
                Field f = findFieldByColumn(cl, col);
                if (f != null) {
                    f.setAccessible(true);
                    f.set(entity, convertIfNeeded(val, f.getType()));
                }
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void merge(T entity) {
        Class<?> cl = entity.getClass();
        String table = getTableName(cl);
        Field idField = findIdField(cl);

        if (idField == null) throw new RuntimeException("No @Id for " + cl);
        List<Field> cols = new ArrayList<>();
        for (Field f : cl.getDeclaredFields()) {
            if (f.equals(idField) || f.isSynthetic()) continue;
            cols.add(f);
        }

        String setClause = String.join(",", mapNamesForSet(cols));
        String idCol = getColumnName(idField);
        String sql = "UPDATE " + table + " SET " + setClause + " WHERE " + idCol + " = ?";
        System.out.println("[sql] :: " + sql);

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            for (Field f : cols) {
                f.setAccessible(true);
                ps.setObject(i++, f.get(entity));
                idField.setAccessible(true);
                ps.setObject(i, idField.get(entity));
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void remove(T entity) {
        Class<?> cl = entity.getClass();
        String table = getTableName(cl);
        Field idField = findIdField(cl);
        String idCol = getColumnName(idField);
        String sql = "DELETE FROM " + table + " WHERE " + idCol + " = ?";
        System.out.println("[sql] :: " + sql);
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            idField.setAccessible(true);
            ps.setObject(1, idField.get(entity));
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> queryList(String sql, Class<T> entityClass, Object... params) {
        System.out.println("[sql] " + sql);
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            ResultSet rs = ps.executeQuery();
            List<T> list = new ArrayList<>();
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                T entity = entityClass.getDeclaredConstructor().newInstance();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    String col = md.getColumnLabel(i);
                    Field f = findFieldByColumn(entityClass, col);
                    if (f != null) {
                        f.setAccessible(true);
                        f.set(entity, convertIfNeeded(rs.getObject(i), f.getType()));
                    }
                }
                list.add(entity);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Field findIdField(Class<?> cl) {
        for (Field field : cl.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class))
                return field;
        }
        return null;
    }

    private String getTableName(Class<?> cl) {
        Table t = cl.getAnnotation(Table.class);
        if (t != null) return t.name();
        return cl.getSimpleName().toLowerCase();
    }

    private String getColumnName(Field cl) {
        Column c = cl.getAnnotation(Column.class);
        if (c != null && !c.name().isEmpty()) return c.name();
        return cl.getName();
    }

    private Field findFieldByColumn(Class<?> cl, String columnName) {
        for (Field field : cl.getDeclaredFields()) {
            if (getColumnName(field).equalsIgnoreCase(columnName) || field.getName().equalsIgnoreCase(columnName))
                return field;
        }
        return null;
    }

    private List<String> mapNames(List<Field> fields) {
        List<String> out = new ArrayList<>();
        for (Field f : fields) out.add(getColumnName(f));
        return out;
    }

    private List<String> mapNamesForSet(List<Field> fields) {
        List<String> out = new ArrayList<>();
        for (Field f : fields) out.add(getColumnName(f) + " = ?");
        return out;
    }

    private Object convertIfNeeded(Object val, Class<?> targetType) {
        if (val == null) return null;
        if (targetType.isAssignableFrom(val.getClass())) return val;
        if (targetType == Long.class || targetType == long.class) return ((Number) val).longValue();
        if (targetType == Integer.class || targetType == int.class) return ((Number) val).intValue();
        if (targetType == String.class) return val.toString();
        if (targetType == java.util.Date.class && val instanceof Timestamp)
            return new java.util.Date(((Timestamp) val).getTime());
        return val;
    }
}
