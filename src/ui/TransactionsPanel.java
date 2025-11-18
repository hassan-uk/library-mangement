package ui;

import dao.BookDAO;
import dao.MemberDAO;
import dao.TransactionDAO;
import models.Book;
import models.Member;
import models.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TransactionsPanel extends JPanel {
    private JTable transactionsTable;
    private DefaultTableModel tableModel;
    private TransactionDAO transactionDAO;
    private BookDAO bookDAO;
    private MemberDAO memberDAO;
    
    public TransactionsPanel() {
        transactionDAO = new TransactionDAO();
        bookDAO = new BookDAO();
        memberDAO = new MemberDAO();
        initializeUI();
        loadTransactions();
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
        
        JLabel titleLabel = new JLabel("Transactions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(15, 23, 42));
        header.add(titleLabel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);
        
        JButton issueButton = createStyledButton("Issue Book", new Color(16, 185, 129));
        issueButton.addActionListener(e -> showIssueDialog());
        
        JButton returnButton = createStyledButton("Return Book", new Color(59, 130, 246));
        returnButton.addActionListener(e -> returnBook());
        
        rightPanel.add(issueButton);
        rightPanel.add(returnButton);
        
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        String[] columns = {"ID", "Book", "Member", "Issue Date", "Return Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        transactionsTable = new JTable(tableModel);
        transactionsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        transactionsTable.setRowHeight(35);
        transactionsTable.setShowGrid(false);
        transactionsTable.setIntercellSpacing(new Dimension(0, 0));
        transactionsTable.setSelectionBackground(new Color(239, 246, 255));
        transactionsTable.setSelectionForeground(new Color(15, 23, 42));
        transactionsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        transactionsTable.getTableHeader().setBackground(new Color(248, 250, 252));
        transactionsTable.getTableHeader().setForeground(new Color(51, 65, 85));
        transactionsTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        
        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
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
        button.setPreferredSize(new Dimension(120, 38));
        
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
    
    private void loadTransactions() {
        try {
            List<Transaction> transactions = transactionDAO.getAllTransactions();
            tableModel.setRowCount(0);
            
            for (Transaction transaction : transactions) {
                Object[] row = {
                    transaction.getId(),
                    transaction.getBookTitle(),
                    transaction.getMemberName(),
                    transaction.getIssueDate(),
                    transaction.getReturnDate() != null ? transaction.getReturnDate() : "-",
                    transaction.getStatus()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showIssueDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Issue Book", true);
        dialog.setLayout(new BorderLayout(20, 20));
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        formPanel.setBackground(Color.WHITE);
        
        JComboBox<String> bookCombo = new JComboBox<>();
        JComboBox<String> memberCombo = new JComboBox<>();
        
        try {
            List<Book> books = bookDAO.getAllBooks();
            for (Book book : books) {
                if (book.isAvailable()) {
                    bookCombo.addItem(book.getId() + " - " + book.getTitle());
                }
            }
            
            List<Member> members = memberDAO.getAllMembers();
            for (Member member : members) {
                memberCombo.addItem(member.getId() + " - " + member.getName());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Error loading data: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        bookCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        memberCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTextField dateField = createFormField();
        dateField.setText(LocalDate.now().toString());
        
        formPanel.add(createLabel("Book"));
        formPanel.add(bookCombo);
        formPanel.add(createLabel("Member"));
        formPanel.add(memberCombo);
        formPanel.add(createLabel("Issue Date"));
        formPanel.add(dateField);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton saveButton = createStyledButton("Issue", new Color(16, 185, 129));
        saveButton.addActionListener(e -> {
            try {
                String bookSelection = (String) bookCombo.getSelectedItem();
                String memberSelection = (String) memberCombo.getSelectedItem();
                
                if (bookSelection == null || memberSelection == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select both book and member");
                    return;
                }
                
                int bookId = Integer.parseInt(bookSelection.split(" - ")[0]);
                int memberId = Integer.parseInt(memberSelection.split(" - ")[0]);
                LocalDate issueDate = LocalDate.parse(dateField.getText().trim());
                
                Transaction transaction = new Transaction(bookId, memberId, issueDate);
                transactionDAO.issueBook(transaction);
                
                loadTransactions();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Book issued successfully");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error issuing book: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use YYYY-MM-DD",
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
    
    private void returnBook() {
        int selectedRow = transactionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction");
            return;
        }
        
        String status = (String) tableModel.getValueAt(selectedRow, 5);
        if (status.equals("returned")) {
            JOptionPane.showMessageDialog(this, "This book has already been returned");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to mark this book as returned?",
            "Confirm Return", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int transactionId = (int) tableModel.getValueAt(selectedRow, 0);
                transactionDAO.returnBook(transactionId);
                loadTransactions();
                JOptionPane.showMessageDialog(this, "Book returned successfully");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error returning book: " + e.getMessage(),
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
        loadTransactions();
    }
}
