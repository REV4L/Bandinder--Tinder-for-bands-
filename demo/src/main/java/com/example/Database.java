package com.example;

// import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import io.github.cdimascio.dotenv.Dotenv;
// import io.github.cdimascio.dotenv.internal.*;

public class Database {
    public static String url;
    public static String user;
    public static String password; // pls dont hack me

    public static String font = "TrebuchetMS";
    public static String color = "#000000";

    public static Connection conn = null;

    public static int organizatorId = -1;
    public static Organizator organizator = null;

    public static boolean loggedIn() {
        return organizator != null;
    }

    public static void connect() throws SQLException {
        // Dotenv dotenv = Dotenv.load();
        Dotenv dotenv = Dotenv.configure().directory("./demo/").load();
        // dotenv = Dotenv.configure().directory(".").load();
        // Dotenv dotenv = Dotenv.configure().filename(".env").load();
        // Dotenv dotenv = Dotenv.configure().directory("src/main/resources").load();
        // Dotenv dotenv =
        // Dotenv.configure().filename("D:/SCV/UPB/M3.1/demo/src/main/resources/.env").load();
        // Dotenv dotenv = Dotenv.configure()
        // .filename("D:\\SCV\\UPB\\M3.1\\demo\\src\\main\\resources\\.env")
        // .load();

        // Dotenv dotenv = Dotenv.configure()
        // .filename("src/main/resources/.env") // Relative path
        // .load();

        url = dotenv.get("DATABASE_URL");
        user = dotenv.get("DATABASE_USER");
        password = dotenv.get("DATABASE_PASSWORD");

        conn = DriverManager.getConnection(url, user, password);

        // String sql = "INSERT INTO dogodek_log(cas, data) VALUES(NOW(), ?)";
        // PreparedStatement s = conn.prepareStatement("INSERT INTO dogodek_log(cas,
        // data) VALUES(NOW(), ?)");
        // s.setString(1, "John Doe");

        // // int rowsAffected = s.executeUpdate();
        // ScheduledExecutorService executorService =
        // Executors.newSingleThreadScheduledExecutor();
        // Runnable discardPlansTask = new Runnable() {
        // @Override
        // public void run() {
        // try (Statement stmt = conn.createStatement()) {
        // // Execute the DISCARD PLANS command
        // stmt.execute("DISCARD PLANS");
        // System.out.println("Plans discarded successfully.");
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // };

        // // Schedule the task to run every 100 milliseconds
        // executorService.scheduleAtFixedRate(discardPlansTask, 0, 100,
        // TimeUnit.MILLISECONDS);
    }

    public static boolean login(String email, String password) {
        String loginFunction = "{? = call login(?, ?)}"; // CallableStatement for the login function
        int userId = -1; // Default to -1 (invalid login)

        try {
            // Ensure the connection is established
            if (conn == null || conn.isClosed()) {
                connect();
            }

            // Create a CallableStatement to call the login function
            try (CallableStatement stmt = conn.prepareCall(loginFunction)) {
                // Set input parameters
                stmt.setString(2, email);
                stmt.setString(3, password);

                // Register the output parameter (userid)
                stmt.registerOutParameter(1, Types.INTEGER);

                // Execute the function
                stmt.execute();

                // Retrieve the result (userid)
                userId = stmt.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error during login query execution.");
        }

        organizatorId = userId;
        organizator = getOrganizator(userId);
        return userId >= 0;
    }

    public static void logOut() {
        organizatorId = -1;
        organizator = null;
    }

    public static void getOrganizator() {
        if (organizatorId >= 0) {
            organizator = getOrganizator(organizatorId);

            String query = "SELECT * FROM get_organizator_settings(?)";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, organizatorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        font = rs.getString("font");
                        color = rs.getString("color");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Organizator getOrganizator(int userId) {
        Organizator organizator = null;
        String query = "SELECT * FROM getOrganizers(?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    organizator = new Organizator(
                            rs.getInt("id"),
                            rs.getString("ime"),
                            rs.getString("opis"),
                            rs.getString("email"),
                            rs.getString("pasw"),
                            rs.getString("telefon"),
                            rs.getString("naslov"),
                            rs.getInt("kraj_id"),
                            rs.getInt("settings_id"),
                            rs.getInt("st_dogodkov"));
                }
            }

            stmt.clearBatch();
            stmt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return organizator;
    }

    public static void updateOrganizator(int id, String ime, String opis, String email, String telefon,
            String naslov, int krajId) {
        String query = "SELECT update_organizator(?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id); // p_id
            stmt.setString(2, ime); // p_ime
            stmt.setString(3, opis); // p_ime
            stmt.setString(4, email); // p_email
            stmt.setString(5, telefon); // p_telefon
            stmt.setString(6, naslov); // p_naslov
            stmt.setInt(7, krajId); // p_naslov

            // Execute the update
            stmt.executeUpdate();

            stmt.clearBatch();
            stmt.close();
            // conn.commit();
            System.out.println("Organizator updated");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to get a single izvajalec by ID
    public static Izvajalec getIzvajalec(int izvajalecId) {
        Izvajalec izvajalec = null;
        String query = "SELECT * FROM get_izvajalec(?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, izvajalecId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    izvajalec = new Izvajalec(
                            rs.getInt("id"),
                            rs.getString("ime"),
                            rs.getString("opis"),
                            rs.getString("telefon"),
                            rs.getInt("st_dogodkov") // Auto-counted
                    );
                }
            }

            stmt.clearBatch();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return izvajalec;
    }

    // Method to get all izvajalci
    public static List<Izvajalec> getAllIzvajalci() {
        List<Izvajalec> izvajalciList = new ArrayList<>();
        String query = "SELECT * FROM get_all_izvajalci()";

        try (PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Izvajalec izvajalec = new Izvajalec(
                        rs.getInt("id"),
                        rs.getString("ime"),
                        rs.getString("opis"),
                        rs.getString("telefon"),
                        rs.getInt("st_dogodkov"));
                izvajalciList.add(izvajalec);
            }

            stmt.clearBatch();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return izvajalciList;
    }

    public static int insertIzvajalec(String ime, String opis, String telefon) {
        int newId = -1; // Default value in case of error
        String query = "SELECT insert_izvajalec(?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, ime);
            stmt.setString(2, opis);
            stmt.setString(3, telefon);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                newId = rs.getInt(1); // Retrieve the inserted ID
            }

            stmt.clearBatch();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newId;
    }

    public static void updateIzvajalec(int id, String ime, String opis, String telefon) {
        String query = "SELECT update_izvajalec(?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.setString(2, ime);
            stmt.setString(3, opis);
            stmt.setString(4, telefon);

            stmt.executeUpdate();
            stmt.clearBatch();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteIzvajalec(int id) throws SQLException {
        String query = "SELECT del_izvajalec(?)";

        // try (
        PreparedStatement stmt = conn.prepareStatement(query);
        // ) {
        stmt.setInt(1, id);

        stmt.executeQuery();
        stmt.clearBatch();
        stmt.close();

        // return true;

        // } catch (SQLException e) {
        // e.printStackTrace();

        // return false;
        // }

    }

    // Method to update the settings for an organizator using the SQL function
    public static void updateSettings(int organizatorId, String font, String color) {
        String query = "SELECT update_organizator_settings(?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Set the parameters for the query
            stmt.setInt(1, organizatorId); // Set the organizatorId
            stmt.setString(2, font); // Set the font
            stmt.setString(3, color); // Set the color

            // Execute the function call
            stmt.execute();

            System.out.println("Settings updated successfully for organizator ID: " + organizatorId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteProstor(int id) throws SQLException {
        String query = "SELECT delProstor(?)";

        // try (
        PreparedStatement stmt = conn.prepareStatement(query);
        // ) {

        stmt.setInt(1, id);
        stmt.executeQuery();

        // return true;
        // } catch (SQLException e) {
        // e.printStackTrace();
        // return false;
        // }

    }

    public static int insertProstor(String ime, String opis, int kapaciteta, String naslov, int kraj_id) {
        String query = "SELECT insertProstor(?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, ime);
            stmt.setString(2, opis);
            stmt.setInt(3, kapaciteta);
            stmt.setString(4, naslov);
            stmt.setInt(5, kraj_id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // Return the newly inserted id
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 in case of failure
    }

    // Method to get a single Prostor by ID
    public static Prostor getProstor(int id) {
        Prostor prostor = null;
        String query = "SELECT * FROM getProstor(?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    prostor = new Prostor(
                            rs.getInt("id"),
                            rs.getString("ime"),
                            rs.getString("opis"),
                            rs.getInt("kapaciteta"),
                            rs.getString("naslov"),
                            rs.getInt("kraj_id"),
                            rs.getInt("st_dogodkov"));
                }
            }

            stmt.clearBatch();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return prostor;
    }

    // Method to update a Prostor by ID
    public static void updateProstor(int id, String ime, String opis, int kapaciteta, String naslov, int kraj_id) {
        String query = "SELECT updateProstor(?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id); // p_id
            stmt.setString(2, ime); // p_ime
            stmt.setString(3, opis); // p_opis
            stmt.setInt(4, kapaciteta); // p_kapaciteta
            stmt.setString(5, naslov); // p_naslov
            stmt.setInt(6, kraj_id); // p_kraj_id

            // Execute the update
            stmt.executeUpdate();

            stmt.clearBatch();
            stmt.close();
            System.out.println("Prostor updated");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int insertDogodek(int organizatorId, int prostorId, int izvajalecId, float cenaVstopnice,
            Timestamp cas, String ime, String opis) {
        String query = "SELECT insert_dogodek(?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, organizatorId);
            ps.setInt(2, prostorId);
            ps.setInt(3, izvajalecId);
            ps.setFloat(4, cenaVstopnice);
            ps.setTimestamp(5, cas);
            ps.setString(6, ime);
            ps.setString(7, opis);

            // Execute the query and get the result set
            ResultSet rs = ps.executeQuery();

            // Check if the query returned a result and get the new_id
            if (rs.next()) {
                return rs.getInt(1); // The first column is the returned new_id
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return -1 or some error value if the insert was unsuccessful
        return -1;
    }

    public static void updateDogodek(int id, int organizatorId, int prostorId, int izvajalecId, float cenaVstopnice,
            Timestamp cas, String ime, String opis) {
        String query = "SELECT update_dogodek(?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            ps.setInt(2, organizatorId);
            ps.setInt(3, prostorId);
            ps.setInt(4, izvajalecId);
            ps.setFloat(5, cenaVstopnice);
            ps.setTimestamp(6, cas);
            ps.setString(7, ime);
            ps.setString(8, opis);

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDogodek(int id) throws SQLException {
        String query = "SELECT delete_dogodek(?)";
        // try (
        PreparedStatement ps = conn.prepareStatement(query);
        // ) {

        ps.setInt(1, id);
        ps.executeQuery();
        // return true;
        // } catch (SQLException e) {
        // e.printStackTrace();
        // return false;
        // }

    }

    public static Dogodek getDogodek(int id) {
        String query = "SELECT * FROM get_dogodek(?)";
        Dogodek dogodek = null;

        try (PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dogodek = new Dogodek(
                            rs.getInt("id"),
                            rs.getString("ime"),
                            rs.getString("opis"),
                            rs.getInt("organizator_id"), // Changed to fetch organizator_id
                            rs.getInt("prostor_id"), // Changed to fetch prostor_id
                            rs.getInt("izvajalec_id"), // Changed to fetch izvajalec_id
                            rs.getFloat("cena_vstopnice"),
                            rs.getTimestamp("cas"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dogodek;
    }

    public static List<Dogodek> getAllDogodki(int organizatorId) {
        // Adjusted query to use organizator_id parameter in the function call
        String query = "SELECT * FROM get_all_dogodki(?)";
        List<Dogodek> dogodki = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(query)) {

            // Set the organizatorId parameter in the query
            ps.setInt(1, organizatorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Dogodek dogodek = new Dogodek(
                            rs.getInt("id"),
                            rs.getString("ime"),
                            rs.getString("opis"),
                            rs.getInt("organizator_id"),
                            rs.getInt("prostor_id"),
                            rs.getInt("izvajalec_id"),
                            rs.getFloat("cena_vstopnice"),
                            rs.getTimestamp("cas"));
                    dogodki.add(dogodek);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dogodki;
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
        String query = "SELECT * FROM getIzvajalci()"; // Using the SQL function for all izvajalci

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

            // stmt.clearBatch();
            // stmt.close();

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