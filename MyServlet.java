// Handle the query server-side

import java.sql.*;
import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import com.google.gson.*;


class DataInfo {
    String infoClass1;
    String infoClass2;
    String action;
}


public class MyServlet extends HttpServlet {

    static Connection conn;
    static Statement statement;

    public MyServlet ()
    {
        // NOTE: this is where we set up a connection to the dbase.
        // There's a single connection for the life of this servlet,
        // which is opened when the MyBCServlet class instance
        // is first created.
        try {
            Class.forName ("org.h2.Driver");
            conn = DriverManager.getConnection (
                    "jdbc:h2:~/Desktop/myservers/databases/topichive",
                    "sa",
                    ""
            );
            statement = conn.createStatement();
            System.out.println ("MyBCServlet: successful connection to H2 dbase");
        }
        catch (Exception e) {
            // Bad news if we reach here.
            e.printStackTrace ();
        }
    }

    public void doPost (HttpServletRequest req, HttpServletResponse resp)
    {
        // We'll print to terminal to know whether the browser used post or get.
        System.out.println ("MyBCServlet: doPost()");
        handleRequest (req, resp);
    }


    public void doGet (HttpServletRequest req, HttpServletResponse resp)
    {
        System.out.println ("MyBCServlet: doGet()");
        handleRequest (req, resp);
    }



    public void handleRequest (HttpServletRequest req, HttpServletResponse resp)
    {
        try {
            // We are going to extract the string line by line
            StringBuffer sbuf = null;
            BufferedReader bufReader = null;
            String inputStr = null;

            bufReader = req.getReader ();
            sbuf = new StringBuffer ();
            while ((inputStr = bufReader.readLine()) != null) {
                sbuf.append (inputStr);
            }

            // What's in the buffer is the entire JSON string.
            String jStr = sbuf.toString();
            System.out.println("Received: " + jStr);


            // Parse out the username and password from JSON:
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            DataInfo d = gson.fromJson (jStr, DataInfo.class);
            System.out.println ("Received: dataInfo=" + d);
            String action = d.action;

            // Set the content type:
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            Writer writer = resp.getWriter ();
            String outputJson = "";

            if ("login".equals(action)){
                String userExists = confirmUser(d.infoClass1, d.infoClass2);
                if ("exists".equalsIgnoreCase(userExists)) {
                    outputJson = "{\"email\":\"" + d.infoClass1 + "\"}";
                } else if("blocked".equalsIgnoreCase(userExists)) {
                    outputJson = "{\"email\":\"" + userExists + "\"}";
                } else {
                    outputJson = "{\"email\":" + null + "}";
                }
            } else if ("getChatrooms".equals(action)) {
                outputJson = getUserChatrooms(d.infoClass1);
            } else if ("retrieveChats".equals(action)) {
                int chatroomId = Integer.parseInt(d.infoClass1); // Chatroom ID passed in infoClass1
                outputJson = retrieveChats(chatroomId);
            } else if ("sendMessage".equals(action)) {
                JsonObject messageData = new JsonParser().parse(d.infoClass2).getAsJsonObject();
//                JsonObject messageData = JsonParser.parseString(d.infoClass2).getAsJsonObject();
                String text = messageData.get("text").getAsString();
                String email = messageData.get("email").getAsString();
                int chatroomId = Integer.parseInt(d.infoClass1);

                boolean success = saveMessage(chatroomId, text, email);
                outputJson = "{\"success\":" + success + "}";
            } else if ("browseChatrooms".equals(action)) {
                outputJson = browseChatrooms(d.infoClass1); // Pass user email
            } else if ("joinChatroom".equals(action)) {
                int chatroomId = Integer.parseInt(d.infoClass1);
                String email = d.infoClass2; // User email
                boolean success = joinChatroom(chatroomId, email);
                outputJson = "{\"success\":" + success + "}";
            } else if ("checkAdmin".equals(action)) {
                String email = d.infoClass1; // User email
                boolean isAdmin = checkIfAdmin(email);
                outputJson = "{\"isAdmin\":" + isAdmin + "}";
            } else if ("getChatroomDetails".equals(action)) {
                int chatroomId = Integer.parseInt(d.infoClass1); // Chatroom ID passed in infoClass1
                outputJson = getChatroomDetails(chatroomId);
            } else if ("leaveChatroom".equals(action)) {
                int chatroomId = Integer.parseInt(d.infoClass1); // Chatroom ID
                String email = d.infoClass2; // User email
                boolean success = removeUserFromChatroom(chatroomId, email);
                outputJson = "{\"success\":" + success + "}";
            } else if ("signup".equals(action)) {
                JsonObject userJson = new JsonParser().parse(d.infoClass1).getAsJsonObject();
                String email = userJson.get("email").getAsString();
                String password = userJson.get("password").getAsString();
                String displayName = userJson.get("displayname").getAsString();
                String firstName = userJson.get("firstname").getAsString();
                String lastName = userJson.get("lastname").getAsString();

                boolean success = registerUser(email, password, displayName, firstName, lastName);
                if (success) {
                    outputJson = "{\"success\": true}";
                } else {
                    outputJson = "{\"success\": false, \"message\": \"User already exists or an error occurred.\"}";
                }
            } else if ("getAllUsers".equals(action)) {
                outputJson = getAllUsers();
            } else if ("banUser".equals(action)) {
                String email = d.infoClass1; // User email to be banned
                boolean success = banUser(email);
                outputJson = "{\"success\":" + success + "}";
            } else if ("removeBan".equals(action)) {
                String email = d.infoClass1; // User email to remove ban
                boolean success = removeBan(email);
                outputJson = "{\"success\":" + success + "}";
            } else if ("upgradeToAdmin".equals(action)) {
                String email = d.infoClass1; // User email to upgrade to admin
                boolean success = upgradeToAdmin(email);
                if (success) {
                    addToAllChatrooms(email); // Add user to all chatrooms
                }
                outputJson = "{\"success\":" + success + "}";
            } else if ("getAllChatrooms".equals(action)) {
                outputJson = getAllChatrooms();
            } else if ("createChatroom".equals(action)) {
                JsonObject chatroomData = new JsonParser().parse(d.infoClass1).getAsJsonObject();
                String name = chatroomData.get("name").getAsString();
                String description = chatroomData.get("description").getAsString();

                boolean success = createChatroom(name, description);
                outputJson = "{\"success\":" + success + "}";
            } else if ("deleteChatroom".equals(action)) {
                int chatroomId = Integer.parseInt(d.infoClass1); // Chatroom ID
                boolean success = deleteChatroom(chatroomId);
                outputJson = "{\"success\":" + success + "}";
            }
            writer.write(outputJson);
            writer.flush();

            // Debugging:
            System.out.println (outputJson);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteChatroom(int chatroomId) {
        try {
            String query = "DELETE FROM CHATROOMS WHERE ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, chatroomId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean createChatroom(String name, String description) {
        try {
            // Insert new chatroom
            String insertChatroomQuery = "INSERT INTO CHATROOMS (NAME, DESCRIPTION) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertChatroomQuery, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Get the ID of the newly created chatroom
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newChatroomId = generatedKeys.getInt(1);
                    // Add all admin users to the new chatroom
                    addAdminsToChatroom(newChatroomId);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void addAdminsToChatroom(int chatroomId) {
        try {
            // Fetch all admin users
            String fetchAdminsQuery = "SELECT EMAIL FROM USERS WHERE IS_ADMIN = TRUE";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(fetchAdminsQuery);

            // Insert each admin into the new chatroom
            String insertUserToChatroomQuery = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertUserToChatroomQuery);

            while (rs.next()) {
                String adminEmail = rs.getString("EMAIL");
                pstmt.setString(1, adminEmail);
                pstmt.setInt(2, chatroomId);
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getAllChatrooms() {
        try {
            String query = "SELECT ID, NAME, DESCRIPTION FROM CHATROOMS";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            List<Map<String, String>> chatrooms = new ArrayList<>();
            while (rs.next()) {
                Map<String, String> chatroom = new HashMap<>();
                chatroom.put("id", String.valueOf(rs.getInt("ID")));
                chatroom.put("name", rs.getString("NAME"));
                chatroom.put("description", rs.getString("DESCRIPTION"));
                chatrooms.add(chatroom);
            }

            Gson gson = new Gson();
            return gson.toJson(chatrooms);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Unable to retrieve chatrooms\"}";
        }
    }

    private boolean banUser(String email) {
        try {
            String query = "UPDATE USERS SET IS_BLOCKED = TRUE WHERE EMAIL = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean removeBan(String email) {
        try {
            String query = "UPDATE USERS SET IS_BLOCKED = FALSE WHERE EMAIL = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean upgradeToAdmin(String email) {
        try {
            String query = "UPDATE USERS SET IS_ADMIN = TRUE WHERE EMAIL = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void addToAllChatrooms(String email) {
        try {
            String query = "SELECT ID FROM CHATROOMS";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            String mergeQuery = "MERGE INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) KEY (USER_EMAIL, CHATROOM_ID) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(mergeQuery);

            while (rs.next()) {
                int chatroomId = rs.getInt("ID");
                pstmt.setString(1, email);
                pstmt.setInt(2, chatroomId);
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getAllUsers() {
        try {
            String query = "SELECT EMAIL, DISPLAYNAME, FIRSTNAME, LASTNAME, IS_BLOCKED, IS_ADMIN FROM USERS";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            List<Map<String, Object>> users = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("email", rs.getString("EMAIL"));
                user.put("displayname", rs.getString("DISPLAYNAME"));
                user.put("firstname", rs.getString("FIRSTNAME"));
                user.put("lastname", rs.getString("LASTNAME"));
                user.put("isBlocked", rs.getBoolean("IS_BLOCKED"));
                user.put("isAdmin", rs.getBoolean("IS_ADMIN"));
                users.add(user);
            }

            Gson gson = new Gson();
            return gson.toJson(users);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Unable to retrieve users\"}";
        }
    }


    private boolean registerUser(String email, String password, String displayName, String firstName, String lastName) {
        try {
            String query = "INSERT INTO USERS (EMAIL, PASSWORD, DISPLAYNAME, FIRSTNAME, LASTNAME) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, displayName);
            pstmt.setString(4, firstName);
            pstmt.setString(5, lastName);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean removeUserFromChatroom(int chatroomId, String email) {
        try {
            String query = "DELETE FROM USERTOCHATROOM WHERE CHATROOM_ID = ? AND USER_EMAIL = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, chatroomId);
            pstmt.setString(2, email);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private String getChatroomDetails(int chatroomId) {
        try {
            String query = "SELECT NAME FROM CHATROOMS WHERE ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, chatroomId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return "{\"name\":\"" + rs.getString("NAME") + "\"}";
            } else {
                return "{\"error\":\"Chatroom not found\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Unable to retrieve chatroom details\"}";
        }
    }

    private boolean checkIfAdmin(String email) {
        try {
            String query = "SELECT IS_ADMIN FROM USERS WHERE EMAIL = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("IS_ADMIN");
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean joinChatroom(int chatroomId, String email) {
        try {
            String query = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setInt(2, chatroomId);

            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String browseChatrooms(String email) {
        try {
            String query = "SELECT c.ID, c.NAME, c.DESCRIPTION " +
                    "FROM CHATROOMS c " +
                    "WHERE c.ID NOT IN (SELECT CHATROOM_ID FROM USERTOCHATROOM WHERE USER_EMAIL = ?)";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);

            ResultSet rs = pstmt.executeQuery();
            List<Map<String, String>> chatrooms = new ArrayList<>();

            while (rs.next()) {
                Map<String, String> chatroom = new HashMap<>();
                chatroom.put("id", rs.getString("ID"));
                chatroom.put("name", rs.getString("NAME"));
                chatroom.put("description", rs.getString("DESCRIPTION"));
                chatrooms.add(chatroom);
            }

            Gson gson = new Gson();
            return gson.toJson(chatrooms);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Unable to retrieve chatrooms\"}";
        }
    }

    String confirmUser (String email, String password)
    {
        try {
            String sql = "SELECT * FROM USERS WHERE EMAIL = '" + email + "' AND PASSWORD = '" + password + "' AND IS_BLOCKED = FALSE";
            ResultSet rs = statement.executeQuery (sql);
            if (rs.next()) {
                return "exists";
            }
            sql = "SELECT * FROM USERS WHERE EMAIL = '" + email + "' AND PASSWORD = '" + password + "' AND IS_BLOCKED = TRUE";
            rs = statement.executeQuery (sql);
            if (rs.next()) {
                return "blocked";
            }
            return "does not exist";
        }
        catch (Exception e) {
            e.printStackTrace ();
            return "does not exist";
        }
    }

    private String getUserChatrooms(String email) {
        try {
            String query = "SELECT c.ID, c.NAME, c.DESCRIPTION " +
                    "FROM CHATROOMS c " +
                    "JOIN USERTOCHATROOM u ON c.ID = u.CHATROOM_ID " +
                    "WHERE u.USER_EMAIL = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);

            ResultSet rs = pstmt.executeQuery();
            List<Map<String, String>> chatrooms = new ArrayList<>();

            while (rs.next()) {
                Map<String, String> chatroom = new HashMap<>();
                chatroom.put("id", rs.getString("ID"));
                chatroom.put("name", rs.getString("NAME"));
                chatroom.put("description", rs.getString("DESCRIPTION"));
                chatrooms.add(chatroom);
            }

            Gson gson = new Gson();
            return gson.toJson(chatrooms);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Unable to retrieve chatrooms\"}";
        }
    }

    private String retrieveChats(int chatroomId) {
        try {
            String query = "SELECT m.MESSAGE_ID, m.MESSAGE_TIME, m.MESSAGE_TEXT, m.EMAIL, u.DISPLAYNAME " +
                    "FROM MESSAGES m JOIN USERS u ON m.EMAIL = u.EMAIL " +
                    "WHERE m.CHATROOM_ID = ? " +
                    "ORDER BY m.MESSAGE_TIME ASC";


            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, chatroomId);

            ResultSet rs = pstmt.executeQuery();
            List<Map<String, String>> messages = new ArrayList<>();

            while (rs.next()) {
                Map<String, String> message = new HashMap<>();
                message.put("messageId", rs.getString("MESSAGE_ID"));
                message.put("time", rs.getString("MESSAGE_TIME"));
                message.put("text", rs.getString("MESSAGE_TEXT"));
                message.put("email", rs.getString("EMAIL"));
                message.put("displayname", rs.getString("DISPLAYNAME"));
                messages.add(message);
            }

            Gson gson = new Gson();
            return gson.toJson(messages);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Unable to retrieve messages\"}";
        }
    }

    private boolean saveMessage(int chatroomId, String text, String email) {
        try {
            String query = "INSERT INTO MESSAGES (MESSAGE_TEXT, CHATROOM_ID, EMAIL) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, text);
            pstmt.setInt(2, chatroomId);
            pstmt.setString(3, email);

            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



}
