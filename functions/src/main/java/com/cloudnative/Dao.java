package com.cloudnative;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Dao {

    public String getAll() {
        String roles = "";
        String query = "SELECT id, name FROM roles";

        try (
            Connection conn = DBManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
        ) {

            while (rs.next()) {
                roles += "id=" + rs.getString("id") + ",";
                roles += "name=" + rs.getString("name") + ",";
                roles += ";";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roles;
    }
}
