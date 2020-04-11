import java.sql.*;
import java.util.Scanner;

public class Database {
    private Connection conn;
    private Statement stmt;
    private PreparedStatement pstmt;
    private ResultSet rs;

    private static int N = 10;

    public void connect()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:D:\\SQLite\\database.db");
            System.out.println("Connected");
        }
        catch (ClassNotFoundException | SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public void fill()
    {
        try
        {
            stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS goods "
                    + "(id INTEGER PRIMARY KEY, "
                    + "prodid INTEGER NOT NULL UNIQUE, "
                    + "title TEXT NOT NULL UNIQUE, "
                    + "cost DOUBLE NOT NULL);");

            String sql = "DELETE FROM goods";
            stmt.execute(sql);

            sql = "INSERT INTO goods (id, prodid, title, cost) VALUES (NULL, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            for (int i = 1; i <= N; i++)
            {
                pstmt.setInt(1, i + 5);
                pstmt.setString(2,"товар" + i);
                pstmt.setDouble(3, i * 10);
                pstmt.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public void addItem(int prodid, String title, double cost)
    {
        try
        {
            String sql = "INSERT INTO goods VALUES (NULL, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, prodid);
            pstmt.setString(2, title);
            pstmt.setDouble(3, cost);
            try {
                pstmt.executeUpdate();
                System.out.println("Товар добавлен");
            }
            catch (SQLException e)
            {
                System.out.println("ОШИБКА: артикул и имя товара должны быть уникальными");
            }
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public void deleteItem(String title)
    {
        try
        {
            String query = "SELECT id FROM goods "
                    + "WHERE title = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, title);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                String sql = "DELETE FROM goods "
                        + "WHERE title = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, title);
                pstmt.executeUpdate();
                System.out.println(title + " удален");
            }
            else
            {
                System.out.println("ОШИБКА: такого товара нет");
            }
            rs.close();
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public void showAll()
    {
        try
        {
            stmt = conn.createStatement();
            String query = "SELECT id, prodid, title, cost "
                    + "FROM goods";
            rs = stmt.executeQuery(query);
            System.out.println("id\t\tprodid\ttitle\t\tcost");
            while (rs.next())
            {
                System.out.println(rs.getInt("id") + "\t\t" +
                        rs.getInt("prodid") + "\t\t" +
                        rs.getString("title") + "\t\t" +
                        rs.getDouble("cost"));
            }
            rs.close();
            stmt.close();
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }

    public void price(String title)
    {
        try
        {
            String query = "SELECT cost "
                    + "FROM goods "
                    + "WHERE title = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, title);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                double cost = rs.getDouble("cost");
                System.out.println("Цена: " + cost);
            }
            else
            {
                System.out.println("ОШИБКА: такого товара нет");
            }
            rs.close();
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public void changePrice(String title, double price)
    {
        try
        {
            String query = "SELECT id FROM goods "
                    + "WHERE title = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, title);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                String sql = "UPDATE goods "
                        + "SET cost = ? "
                        + "WHERE title = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setDouble(1, price);
                pstmt.setString(2, title);
                pstmt.executeUpdate();
                System.out.println("Цена изменена");
            }
            else
            {
                System.out.println("ОШИБКА: такого товара нет");
            }
            rs.close();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void filterByPrice(double bound1, double bound2)
    {
        try
        {
            stmt = conn.createStatement();
            String query = "SELECT id, prodid, title, cost"
                    + " FROM goods"
                    + " WHERE cost BETWEEN " + bound1 + " AND " + bound2;
            rs = stmt.executeQuery(query);
            while (rs.next())
            {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getInt("prodid") + "\t" +
                        rs.getString("title") + "\t" +
                        rs.getDouble("cost"));
            }
            rs.close();
            stmt.close();
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public void disconnect()
    {
        try
        {
            conn.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}

