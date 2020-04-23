package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class Database {
    private Connection conn;
    private Statement stmt;
    private PreparedStatement pstmt;
    private ResultSet rs;

    private static int N = 10;

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:database.db");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void fill() {
        try {
            stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS goods "
                    + "(id INTEGER PRIMARY KEY, "
                    + "prodid INTEGER NOT NULL UNIQUE, "
                    + "title TEXT NOT NULL UNIQUE, "
                    + "cost DOUBLE NOT NULL);");

            String sql = "DELETE FROM goods";
            stmt.execute(sql);

            String query = "INSERT INTO goods VALUES (NULL, ?, ?, ?)";
            pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= N; i++) {
                pstmt.setInt(1, i);
                pstmt.setString(2,"item" + i);
                pstmt.setDouble(3, i * 10);
                pstmt.executeUpdate();
            }
            stmt.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addItem(int prodid, String title, double cost) throws MyException {
        String sql = "INSERT INTO goods VALUES (NULL, ?, ?, ?)";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, prodid);
            pstmt.setString(2, title);
            pstmt.setDouble(3, cost);
            try {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                throw new MyException("ERROR: prodid and title must be unique");
            }
            pstmt.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void deleteItem(String title) throws MyException {
        String query = "SELECT id FROM goods WHERE title = ?";
        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, title);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String sql = "DELETE FROM goods WHERE title = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, title);
                pstmt.executeUpdate();
            } else {
                throw new MyException("ERROR: no such item");
            }
            pstmt.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public ObservableList<Goods> showAll() {
        ObservableList<Goods> goodsData = FXCollections.observableArrayList();
        try {
            stmt = conn.createStatement();
            String query = "SELECT id, prodid, title, cost FROM goods";
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                goodsData.add(new Goods(rs.getInt("id"),
                        rs.getInt("prodid"),
                        rs.getString("title"),
                        rs.getDouble("cost")));
            }
            stmt.close();
            rs.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return goodsData;
    }

    public double price(String title) throws MyException {
        String query = "SELECT cost FROM goods WHERE title = ?";
        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, title);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("cost");
            } else {
                throw new MyException("ERROR: no such item");
            }
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return -1;
    }

    public void changePrice(String title, double price) throws MyException {
        String query = "SELECT id FROM goods WHERE title = ?";
        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, title);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String sql = "UPDATE goods "
                        + "SET cost = ? "
                        + "WHERE title = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setDouble(1, price);
                pstmt.setString(2, title);
                pstmt.executeUpdate();
            } else {
                throw new MyException("ERROR: no such item");
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ObservableList<Goods> filterByPrice(double bound1, double bound2) {
        ObservableList<Goods> goodsData = FXCollections.observableArrayList();
        try {
            stmt = conn.createStatement();
            String query = "SELECT id, prodid, title, cost"
                    + " FROM goods"
                    + " WHERE cost BETWEEN " + bound1 + " AND " + bound2;
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                goodsData.add(new Goods(rs.getInt("id"),
                        rs.getInt("prodid"),
                        rs.getString("title"),
                        rs.getDouble("cost")));
            }
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return goodsData;
    }

    public void disconnect() {
        try {
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

