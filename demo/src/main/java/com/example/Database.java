package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.function.Consumer;

public class Database {
    public static String url;
    public static String user;
    public static String password;

    public static Connection conn = null;

    public static int bandId = -1;

    public static boolean loggedIn() {
        return bandId >= 0;
    }

    public static void connect() throws SQLException {
        Dotenv dotenv = Dotenv.configure().directory("./demo/").load();
        url = dotenv.get("DATABASE_URL");
        user = dotenv.get("DATABASE_USER");
        password = dotenv.get("DATABASE_PASSWORD");
        conn = DriverManager.getConnection(url, user, password);
    }

    public static boolean loginBand(String email, String pass, Consumer<Boolean> callback) {
        String query = "SELECT * FROM login_band(?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, pass);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                if (id >= 0) {
                    bandId = id;
                    callback.accept(true);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        callback.accept(false);
        return false;
    }

    public static boolean registerBand(String name, String email, String pass) {
        String query = "SELECT * FROM register_band(?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, pass);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) >= 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<byte[]> loadImages(int bandId) {
        String sql = "SELECT * FROM get_band_images(?)";
        byte[][] slots = new byte[6][];
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bandId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int slot = rs.getInt("slot");
                byte[] data = rs.getBytes("data");
                if (slot >= 0 && slot < 6) {
                    slots[slot] = data;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Arrays.asList(slots); // preserves 6-slot structure
    }

    public static void saveImage(byte[] data, int bandId, int slot) {
        String sql = "SELECT save_band_image(?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bandId);
            stmt.setBytes(2, data);
            stmt.setInt(3, slot);
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteImage(int bandId, int slot) {
        String sql = "SELECT delete_band_image(?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bandId);
            stmt.setInt(2, slot);
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Kraj> getKraji() {
        ObservableList<Kraj> krajiList = FXCollections.observableArrayList();
        String query = "SELECT * FROM getKraji()";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                krajiList.add(new Kraj(
                        rs.getInt("id"),
                        rs.getString("ime"),
                        rs.getString("postna"),
                        rs.getString("vel_uporabnik")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return krajiList;
    }

    public static ObservableList<Izvajalec> getIzvajalci() {
        ObservableList<Izvajalec> izvajalciList = FXCollections.observableArrayList();
        String query = "SELECT * FROM getIzvajalci()";

        try (PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                izvajalciList.add(new Izvajalec(
                        rs.getInt("id"),
                        rs.getString("ime"),
                        rs.getString("opis"),
                        rs.getString("telefon"),
                        rs.getInt("st_dogodkov")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return izvajalciList;
    }

    public static ObservableList<Prostor> getProstori() {
        ObservableList<Prostor> prostoriList = FXCollections.observableArrayList();
        String query = "SELECT * FROM getProstori()";

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Prostor p = new Prostor(
                        rs.getInt("id"),
                        rs.getString("ime"),
                        rs.getString("opis"),
                        rs.getInt("kapaciteta"),
                        rs.getString("naslov"),
                        rs.getInt("kraj_id"),
                        rs.getInt("st_dogodkov"));
                prostoriList.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return prostoriList;
    }
}