package com.cloudnative.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cloudnative.DBManager;
import com.cloudnative.models.Rol;



public class RolDao {

    public List<Rol> getAll() {
        List<Rol> roles = new ArrayList<>();
        String query = "SELECT id, name FROM roles";

        try (ResultSet rs = DBManager.executeQuery(query)) {

            while (rs.next()) {
                Rol rol = new Rol();
                rol.setId(rs.getLong("id"));
                rol.setName(rs.getString("name"));
                roles.add(rol);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roles;
    }

    public Rol getById(Long id) {
        String query = "SELECT id, name FROM roles WHERE id = ?";
        try (ResultSet rs = DBManager.executeQuery(query, id)) {

            if (rs.next()) {
                Rol rol = new Rol();
                rol.setId(rs.getLong("id"));
                rol.setName(rs.getString("name"));
                return rol;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean create(Rol rol) {
        String query = "INSERT INTO roles (name) VALUES (?)";
        try {
            
            int rowsAffected = DBManager.executeUpdate(query, rol.getName());
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Rol rol) {
        String query = "UPDATE roles SET name = ? WHERE id = ?";
        
        try {
    
            int rowsAffected = DBManager.executeUpdate(query, rol.getName(), rol.getId());
    
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long id) {
        String query = "DELETE FROM roles WHERE id = ?";
        
        try {
            
            int rowsAffected = DBManager.executeUpdate(query, id);
    
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
