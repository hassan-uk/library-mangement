package dao;

import config.DatabaseConfig;
import models.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    
    public void addBook(Book book) throws SQLException {
        String query = "INSERT INTO books (title, author, isbn, available) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setBoolean(4, book.isAvailable());
            stmt.executeUpdate();
        }
    }
    
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books ORDER BY id DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setAvailable(rs.getBoolean("available"));
                book.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                books.add(book);
            }
        }
        
        return books;
    }
    
    public void updateBook(Book book) throws SQLException {
        String query = "UPDATE books SET title = ?, author = ?, isbn = ?, available = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setBoolean(4, book.isAvailable());
            stmt.setInt(5, book.getId());
            stmt.executeUpdate();
        }
    }
    
    public void deleteBook(int id) throws SQLException {
        String query = "DELETE FROM books WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public List<Book> searchBooks(String keyword) throws SQLException {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books WHERE title ILIKE ? OR author ILIKE ? OR isbn ILIKE ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    book.setId(rs.getInt("id"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    book.setIsbn(rs.getString("isbn"));
                    book.setAvailable(rs.getBoolean("available"));
                    book.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    books.add(book);
                }
            }
        }
        
        return books;
    }
}
