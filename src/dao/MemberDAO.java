package dao;

import config.DatabaseConfig;
import models.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {
    
    public void addMember(Member member) throws SQLException {
        String query = "INSERT INTO members (name, email, phone) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getPhone());
            stmt.executeUpdate();
        }
    }
    
    public List<Member> getAllMembers() throws SQLException {
        List<Member> members = new ArrayList<>();
        String query = "SELECT * FROM members ORDER BY id DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Member member = new Member();
                member.setId(rs.getInt("id"));
                member.setName(rs.getString("name"));
                member.setEmail(rs.getString("email"));
                member.setPhone(rs.getString("phone"));
                member.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                members.add(member);
            }
        }
        
        return members;
    }
    
    public void updateMember(Member member) throws SQLException {
        String query = "UPDATE members SET name = ?, email = ?, phone = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getPhone());
            stmt.setInt(4, member.getId());
            stmt.executeUpdate();
        }
    }
    
    public void deleteMember(int id) throws SQLException {
        String query = "DELETE FROM members WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public List<Member> searchMembers(String keyword) throws SQLException {
        List<Member> members = new ArrayList<>();
        String query = "SELECT * FROM members WHERE name ILIKE ? OR email ILIKE ? OR phone ILIKE ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getInt("id"));
                    member.setName(rs.getString("name"));
                    member.setEmail(rs.getString("email"));
                    member.setPhone(rs.getString("phone"));
                    member.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    members.add(member);
                }
            }
        }
        
        return members;
    }
}
