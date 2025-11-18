package ui;

import dao.BookDAO;
import models.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class BooksPanel extends JPanel {
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private BookDAO bookDAO;
    private JTextField searchField;
    
    public BooksPanel() {
        bookDAO = new BookDAO();
        initializeUI();
        loadBooks();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        add(createHeader(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Books");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(15, 23, 42));
        header.add(titleLabel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(250, 38));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JButton searchButton = createStyledButton("Search", new Color(59, 130, 246));
        searchButton.addActionListener(e -> searchBooks());
        
        JButton addButton = createStyledButton("Add Book", new Color(16, 185, 129));
        addButton.addActionListener(e -> showAddDialog());
        
        rightPanel.add(searchField);
        rightPanel.add(searchButton);
        rightPanel.add(addButton);
        
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        String[] columns = {"ID", "Title", "Author", "ISBN", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        booksTable = new JTable(tableModel);
        booksTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        booksTable.setRowHeight(35);
        booksTable.setShowGrid(false);
        booksTable.setIntercellSpacing(new Dimension(0, 0));
        booksTable.setSelectionBackground(new Color(239, 246, 255));
        booksTable.setSelectionForeground(new Color(15, 23, 42));
        booksTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        booksTable.getTableHeader().setBackground(new Color(248, 250, 252));
        booksTable.getTableHeader().setForeground(new Color(51, 65, 85));
        booksTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        
        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton editButton = createStyledButton("Edit", new Color(59, 130, 246));
        editButton.addActionListener(e -> editBook());
        
        JButton deleteButton = createStyledButton("Delete", new Color(239, 68, 68));
        deleteButton.addActionListener(e -> deleteBook());
        
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 38));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void loadBooks() {
        try {
            List<Book> books = bookDAO.getAllBooks();
            tableModel.setRowCount(0);
            
            for (Book book : books) {
                Object[] row = {
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    book.isAvailable() ? "Available" : "Issued"
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchBooks() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadBooks();
            return;
        }
        
        try {
            List<Book> books = bookDAO.searchBooks(keyword);
            tableModel.setRowCount(0);
            
            for (Book book : books) {
                Object[] row = {
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    book.isAvailable() ? "Available" : "Issued"
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching books: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Book", true);
        dialog.setLayout(new BorderLayout(20, 20));
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        formPanel.setBackground(Color.WHITE);
        
        JTextField titleField = createFormField();
        JTextField authorField = createFormField();
        JTextField isbnField = createFormField();
        
        formPanel.add(createLabel("Title"));
        formPanel.add(titleField);
        formPanel.add(createLabel("Author"));
        formPanel.add(authorField);
        formPanel.add(createLabel("ISBN"));
        formPanel.add(isbnField);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton saveButton = createStyledButton("Save", new Color(16, 185, 129));
        saveButton.addActionListener(e -> {
            try {
                Book book = new Book(
                    titleField.getText().trim(),
                    authorField.getText().trim(),
                    isbnField.getText().trim()
                );
                
                bookDAO.addBook(book);
                loadBooks();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Book added successfully");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding book: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = createStyledButton("Cancel", new Color(100, 116, 139));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void editBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit");
            return;
        }
        
        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentTitle = (String) tableModel.getValueAt(selectedRow, 1);
        String currentAuthor = (String) tableModel.getValueAt(selectedRow, 2);
        String currentIsbn = (String) tableModel.getValueAt(selectedRow, 3);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Book", true);
        dialog.setLayout(new BorderLayout(20, 20));
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        formPanel.setBackground(Color.WHITE);
        
        JTextField titleField = createFormField();
        titleField.setText(currentTitle);
        JTextField authorField = createFormField();
        authorField.setText(currentAuthor);
        JTextField isbnField = createFormField();
        isbnField.setText(currentIsbn);
        
        formPanel.add(createLabel("Title"));
        formPanel.add(titleField);
        formPanel.add(createLabel("Author"));
        formPanel.add(authorField);
        formPanel.add(createLabel("ISBN"));
        formPanel.add(isbnField);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton saveButton = createStyledButton("Update", new Color(16, 185, 129));
        saveButton.addActionListener(e -> {
            try {
                Book book = new Book();
                book.setId(bookId);
                book.setTitle(titleField.getText().trim());
                book.setAuthor(authorField.getText().trim());
                book.setIsbn(isbnField.getText().trim());
                book.setAvailable(((String) tableModel.getValueAt(selectedRow, 4)).equals("Available"));
                
                bookDAO.updateBook(book);
                loadBooks();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Book updated successfully");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating book: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = createStyledButton("Cancel", new Color(100, 116, 139));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void deleteBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this book?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int bookId = (int) tableModel.getValueAt(selectedRow, 0);
                bookDAO.deleteBook(bookId);
                loadBooks();
                JOptionPane.showMessageDialog(this, "Book deleted successfully");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting book: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(51, 65, 85));
        return label;
    }
    
    private JTextField createFormField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    public void refreshTable() {
        loadBooks();
    }
}
