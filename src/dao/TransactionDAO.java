package dao;

import config.DatabaseConfig;
import models.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    
    public void issueBook(Transaction transaction) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            String insertQuery = "INSERT INTO transactions (book_id, member_id, issue_date, status) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                stmt.setInt(1, transaction.getBookId());
                stmt.setInt(2, transaction.getMemberId());
                stmt.setDate(3, Date.valueOf(transaction.getIssueDate()));
                stmt.setString(4, transaction.getStatus());
                stmt.executeUpdate();
            }
            
            String updateQuery = "UPDATE books SET available = false WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setInt(1, transaction.getBookId());
                stmt.executeUpdate();
            }
            
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    public void returnBook(int transactionId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            int bookId = 0;
            String selectQuery = "SELECT book_id FROM transactions WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
                stmt.setInt(1, transactionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        bookId = rs.getInt("book_id");
                    }
                }
            }
            
            String updateTransQuery = "UPDATE transactions SET return_date = ?, status = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateTransQuery)) {
                stmt.setDate(1, Date.valueOf(LocalDate.now()));
                stmt.setString(2, "returned");
                stmt.setInt(3, transactionId);
                stmt.executeUpdate();
            }
            
            String updateBookQuery = "UPDATE books SET available = true WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateBookQuery)) {
                stmt.setInt(1, bookId);
                stmt.executeUpdate();
            }
            
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    public List<Transaction> getAllTransactions() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT t.*, b.title as book_title, m.name as member_name " +
                      "FROM transactions t " +
                      "JOIN books b ON t.book_id = b.id " +
                      "JOIN members m ON t.member_id = m.id " +
                      "ORDER BY t.id DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setBookId(rs.getInt("book_id"));
                transaction.setMemberId(rs.getInt("member_id"));
                transaction.setIssueDate(rs.getDate("issue_date").toLocalDate());
                
                Date returnDate = rs.getDate("return_date");
                if (returnDate != null) {
                    transaction.setReturnDate(returnDate.toLocalDate());
                }
                
                transaction.setStatus(rs.getString("status"));
                transaction.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                transaction.setBookTitle(rs.getString("book_title"));
                transaction.setMemberName(rs.getString("member_name"));
                transactions.add(transaction);
            }
        }
        
        return transactions;
    }
    
    public List<Transaction> getActiveTransactions() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT t.*, b.title as book_title, m.name as member_name " +
                      "FROM transactions t " +
                      "JOIN books b ON t.book_id = b.id " +
                      "JOIN members m ON t.member_id = m.id " +
                      "WHERE t.status = 'issued' " +
                      "ORDER BY t.id DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setBookId(rs.getInt("book_id"));
                transaction.setMemberId(rs.getInt("member_id"));
                transaction.setIssueDate(rs.getDate("issue_date").toLocalDate());
                transaction.setStatus(rs.getString("status"));
                transaction.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                transaction.setBookTitle(rs.getString("book_title"));
                transaction.setMemberName(rs.getString("member_name"));
                transactions.add(transaction);
            }
        }
        
        return transactions;
    }
}
