// This program creates the example TopicHive database (db) from scratch.
// If the TopicHive db already exists, it is first deleted, and then created
// afresh. The assumption before running the program is that the
// H2 database server is already running. To do so, execute
//       java -cp h2*.jar org.h2.tools.Server -tcp -pg
// in the directory where the h2.jar is located.

// java.sql has many objects like Statement, Connection, ResultSet
// that will be useful for accessing the database from java.

import java.sql.*;

public class MakeTopicHiveDatabase {

    static Statement statement;

    public static void main (String[] argv)
    {
        try {
            // The first step is to load the driver and use it to open
            // a connection to the H2 server (that should be running).
            Class.forName ("org.h2.Driver");
            Connection conn = DriverManager.getConnection (
                    "jdbc:h2:~/Desktop/myservers/databases/topichive",
                    "sa",
                    ""
            );

            // If the connection worked, we'll reach here (otherwise an
            // exception is thrown.

            // Now make a statement, which is the object used to issue
            // queries.
            statement = conn.createStatement ();

            // The users table:
            makeUsersTable();
            printTable ("USERS", 7);

            makeChatroomsTable();
            printTable ("CHATROOMS", 3);

            makeMessagesTable();
            printTable ("MESSAGES", 6);

            makeUserToChatroomTable();
            printTable ("USERTOCHATROOM", 2);


            // Close the connection, and we're done.
            conn.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void printTable (String tableName, int numColumns)
            throws SQLException
    {
        // Build the SELECT query string:
        String sql = "SELECT * FROM " + tableName;

        // Execute at the database, which returns rows that are
        // placed into the ResultSet object.
        ResultSet rs = statement.executeQuery (sql);

        // Now extract the results from ResultSet
        System.out.println ("\nRows from " + tableName + ":");
        while (rs.next()) {
            String row = "Row: ";
            for (int i=1; i<=numColumns; i++) {
                String s = rs.getString (i);
                // One could get an int column into an int variable.
                row += " " + s;
            }
            System.out.println (row);
        }
    }


    static void makeUsersTable()
            throws SQLException
    {
        // Get rid of any existing table by this name.
        String sql = "DROP TABLE IF EXISTS USERS";
        statement.executeUpdate (sql);

        // Now make a fresh (but empty) table.
        sql = "CREATE TABLE USERS (EMAIL VARCHAR(50) PRIMARY KEY, DISPLAYNAME VARCHAR(25), PASSWORD VARCHAR(12), FIRSTNAME VARCHAR(25), LASTNAME VARCHAR(25), IS_BLOCKED BOOLEAN DEFAULT FALSE, IS_ADMIN BOOLEAN DEFAULT FALSE)";

        statement.executeUpdate (sql);

        // Insert rows one by one.
        sql = "INSERT INTO USERS VALUES ('admin1@topichive.com', 'admin1', 'password', 'Big', 'Guy', FALSE, TRUE)";
        statement.executeUpdate (sql);
        sql = "INSERT INTO USERS VALUES ('admin2@topichive.com', 'admin2', 'password', 'Sohil', 'Khan', FALSE, TRUE)";
        statement.executeUpdate (sql);
        sql = "INSERT INTO USERS VALUES ('user1@gmail.com', 'Goku', '1234', 'Shyama', 'Arunachalam', FALSE, FALSE)";
        statement.executeUpdate (sql);
        sql = "INSERT INTO USERS VALUES ('user2@gmail.com', 'Vegeta', '787878', 'Liki', 'Singh', FALSE, FALSE)";
        statement.executeUpdate (sql);
        sql = "INSERT INTO USERS VALUES ('user3@gmail.com', 'Gohan', '787878', 'Shejal', 'Pradhan', FALSE, FALSE)";
        statement.executeUpdate (sql);
        sql = "INSERT INTO USERS VALUES ('user4@gmail.com', 'Krillin', '12345', 'Shaival', 'Prakash', FALSE, FALSE)";
        statement.executeUpdate (sql);
        sql = "INSERT INTO USERS VALUES ('user5@gmail.com', 'Picolo', '12345', 'Dhanush', 'Reddy', TRUE, FALSE)";
        statement.executeUpdate (sql);
        sql = "INSERT INTO USERS VALUES ('user6@gmail.com', 'Naruto', '12345', 'Abhi', 'Gowda', FALSE, FALSE)";
        statement.executeUpdate (sql);

    }

    static void makeChatroomsTable ()
            throws SQLException
    {
        String sql = "DROP TABLE IF EXISTS CHATROOMS";
        statement.executeUpdate (sql);
        sql = "CREATE TABLE CHATROOMS (ID INT AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR(100), DESCRIPTION VARCHAR(300))";
        statement.executeUpdate (sql);

        sql = "INSERT INTO CHATROOMS (NAME, DESCRIPTION) VALUES ('PETROLHEADS', 'Cause life is too short to drive slow!')";
        statement.executeUpdate (sql);

        sql = "INSERT INTO CHATROOMS (NAME, DESCRIPTION) VALUES ('PETCARE', 'Bow-bow Meow-meow')";
        statement.executeUpdate (sql);

        sql = "INSERT INTO CHATROOMS (NAME, DESCRIPTION) VALUES ('CAREER ADVICE', 'For all them $500k FAANG offers')";
        statement.executeUpdate (sql);

        sql = "INSERT INTO CHATROOMS (NAME, DESCRIPTION) VALUES ('RUNNING GEEKS', 'Run fast, run far. Run like Forrest!')";
        statement.executeUpdate (sql);

        sql = "INSERT INTO CHATROOMS (NAME, DESCRIPTION) VALUES ('WEIGHTLIFTERS', 'Light weight Baby!')";
        statement.executeUpdate (sql);

        sql = "INSERT INTO CHATROOMS (NAME, DESCRIPTION) VALUES ('SCIENCE & TECH', 'Get your nerd hat on!!')";
        statement.executeUpdate (sql);

        sql = "INSERT INTO CHATROOMS (NAME, DESCRIPTION) VALUES ('HOME BUYING & SELLING', 'Fancy villas and beachside... er, 1 bed condos')";
        statement.executeUpdate (sql);

    }

    static void makeMessagesTable ()
            throws SQLException
    {
        String sql = "DROP TABLE IF EXISTS MESSAGES";
        statement.executeUpdate (sql);
        sql = "CREATE TABLE MESSAGES (MESSAGE_ID INT AUTO_INCREMENT PRIMARY KEY, MESSAGE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP, MESSAGE_TEXT VARCHAR(300), CHATROOM_ID INT, EMAIL VARCHAR(50), IS_DELETED BOOLEAN DEFAULT FALSE, FOREIGN KEY (CHATROOM_ID) REFERENCES CHATROOMS(ID) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY (EMAIL) REFERENCES users(EMAIL) ON DELETE CASCADE ON UPDATE CASCADE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 10:00:00', 'Hey, heard Jaguar just rebranded. What do you think of their new logo. Looks sick!', 1, 'user3@gmail.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 10:15:00', 'Does anyone have tips for training a new puppy?', 2, 'user2@gmail.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 11:15:00', 'Jaguar just rebranded. Loving the sleek new logo!', 1, 'admin2@topichive.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 11:30:00', 'Tesla is releasing a Cybertruck update next week!', 1, 'user1@gmail.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 11:40:00', 'My cat loves this new brand of treats. Any recommendations for toys?', 2, 'user3@gmail.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 13:01:00', 'How do you prepare for a technical interview at Google?', 3, 'user5@gmail.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 13:10:00', 'What’s the best pre-workout supplement for strength training?', 5, 'user2@gmail.com', FALSE)";

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 15:00:00', 'What are the best mortgage rates currently available?', 7, 'user4@gmail.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 15:20:00', 'How do you decide between renting and buying?', 7, 'admin2@topichive.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 16:00:00', 'What’s your favorite marathon training plan?', 4, 'user5@gmail.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 16:15:00', 'Any advice for a fresh grad entering the job market in 2024?', 3, 'user2@gmail.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 16:30:00', 'Are certifications like AWS worth pursuing for a mid-level developer?', 3, 'user3@gmail.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 17:20:00', 'How do you avoid shin splints when running daily?', 4, 'user4@gmail.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 17:40:00', 'What’s the best running shoe for flat feet?', 4, 'user1@gmail.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 18:20:00', 'Does anyone have tips for improving bench press form?', 5, 'admin2@topichive.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 18:40:00', 'How often should you train legs in a week?', 5, 'user3@gmail.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 19:00:00', 'Quantum computing seems to be making progress. Thoughts?', 6, 'user1@gmail.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 19:40:00', 'What’s the best way to keep rabbits cool during summer?', 2, 'user2@gmail.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 20:00:00', 'Is AI the biggest disruption since the internet?', 6, 'user5@gmail.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 20:01:00', 'What’s the best way to stage a home for a quick sale?', 7, 'user2@gmail.com', FALSE)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO MESSAGES (MESSAGE_TIME, MESSAGE_TEXT, CHATROOM_ID, EMAIL, IS_DELETED) VALUES ('2024-11-30 20:10:00', 'How do you feel about the ethical concerns around CRISPR?', 6, 'user4@gmail.com', FALSE)";
        statement.executeUpdate (sql);

    }



    static void makeUserToChatroomTable ()
            throws SQLException
    {
        String sql = "DROP TABLE IF EXISTS USERTOCHATROOM";
        statement.executeUpdate (sql);
        sql = "CREATE TABLE USERTOCHATROOM (USER_EMAIL VARCHAR(50), CHATROOM_ID INT, PRIMARY KEY (USER_EMAIL, CHATROOM_ID), FOREIGN KEY (USER_EMAIL) REFERENCES USERS(EMAIL) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY (CHATROOM_ID) REFERENCES chatrooms(ID) ON DELETE CASCADE ON UPDATE CASCADE)";
        statement.executeUpdate (sql);

        // Add admin1 to all chatrooms
        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('admin1@topichive.com', 1)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('admin1@topichive.com', 2)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('admin1@topichive.com', 3)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('admin1@topichive.com', 4)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('admin1@topichive.com', 5)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('admin1@topichive.com', 6)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('admin1@topichive.com', 7)";
        statement.executeUpdate (sql);

        //Add admin2 to all chatrooms
        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('admin2@topichive.com', 1)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('admin2@topichive.com', 2)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('admin2@topichive.com', 3)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('admin2@topichive.com', 4)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('admin2@topichive.com', 5)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('admin2@topichive.com', 6)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('admin2@topichive.com', 7)";
        statement.executeUpdate (sql);

        //Add user1 to some chatrooms
        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('user1@gmail.com', 1)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('user1@gmail.com', 4)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('user1@gmail.com', 6)";
        statement.executeUpdate (sql);

        //Add user2 to some chatrooms
        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('user2@gmail.com', 2)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('user2@gmail.com', 3)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('user2@gmail.com', 5)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('user2@gmail.com', 7)";
        statement.executeUpdate (sql);

        //Add user3 to some chatrooms
        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('user3@gmail.com', 1)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('user3@gmail.com', 3)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('user3@gmail.com', 5)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('user3@gmail.com', 2)";
        statement.executeUpdate (sql);

        //Add user4 to some chatrooms
        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('user4@gmail.com', 7)";
        statement.executeUpdate (sql);
        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('user4@gmail.com', 6)";
        statement.executeUpdate (sql);
        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('user4@gmail.com', 4)";
        statement.executeUpdate (sql);

        //Add user5 to some chatrooms
        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('user5@gmail.com', 4)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('user5@gmail.com', 6)";
        statement.executeUpdate (sql);

        sql = "INSERT INTO USERTOCHATROOM (USER_EMAIL, CHATROOM_ID) VALUES ('user5@gmail.com', 3)";
        statement.executeUpdate (sql);


    }


}