package ui;

import dao.MemberDAO;
import models.Member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MembersPanel extends JPanel {
    private JTable membersTable;
    private DefaultTableModel tableModel;
    private MemberDAO memberDAO;
    private JTextField searchField;
    
    public MembersPanel() {
        memberDAO = new MemberDAO();
        initializeUI();
        loadMembers();
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
        
        JLabel titleLabel = new JLabel("Members");
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
        searchButton.addActionListener(e -> searchMembers());
        
        JButton addButton = createStyledButton("Add Member", new Color(16, 185, 129));
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
        
        String[] columns = {"ID", "Name", "Email", "Phone"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        membersTable = new JTable(tableModel);
        membersTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        membersTable.setRowHeight(35);
        membersTable.setShowGrid(false);
        membersTable.setIntercellSpacing(new Dimension(0, 0));
        membersTable.setSelectionBackground(new Color(239, 246, 255));
        membersTable.setSelectionForeground(new Color(15, 23, 42));
        membersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        membersTable.getTableHeader().setBackground(new Color(248, 250, 252));
        membersTable.getTableHeader().setForeground(new Color(51, 65, 85));
        membersTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        
        JScrollPane scrollPane = new JScrollPane(membersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton editButton = createStyledButton("Edit", new Color(59, 130, 246));
        editButton.addActionListener(e -> editMember());
        
        JButton deleteButton = createStyledButton("Delete", new Color(239, 68, 68));
        deleteButton.addActionListener(e -> deleteMember());
        
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
    
    private void loadMembers() {
        try {
            List<Member> members = memberDAO.getAllMembers();
            tableModel.setRowCount(0);
            
            for (Member member : members) {
                Object[] row = {
                    member.getId(),
                    member.getName(),
                    member.getEmail(),
                    member.getPhone()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading members: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchMembers() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadMembers();
            return;
        }
        
        try {
            List<Member> members = memberDAO.searchMembers(keyword);
            tableModel.setRowCount(0);
            
            for (Member member : members) {
                Object[] row = {
                    member.getId(),
                    member.getName(),
                    member.getEmail(),
                    member.getPhone()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching members: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Member", true);
        dialog.setLayout(new BorderLayout(20, 20));
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        formPanel.setBackground(Color.WHITE);
        
        JTextField nameField = createFormField();
        JTextField emailField = createFormField();
        JTextField phoneField = createFormField();
        
        formPanel.add(createLabel("Name"));
        formPanel.add(nameField);
        formPanel.add(createLabel("Email"));
        formPanel.add(emailField);
        formPanel.add(createLabel("Phone"));
        formPanel.add(phoneField);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton saveButton = createStyledButton("Save", new Color(16, 185, 129));
        saveButton.addActionListener(e -> {
            try {
                Member member = new Member(
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim()
                );
                
                memberDAO.addMember(member);
                loadMembers();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Member added successfully");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding member: " + ex.getMessage(),
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
    
    private void editMember() {
        int selectedRow = membersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member to edit");
            return;
        }
        
        int memberId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentEmail = (String) tableModel.getValueAt(selectedRow, 2);
        String currentPhone = (String) tableModel.getValueAt(selectedRow, 3);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Member", true);
        dialog.setLayout(new BorderLayout(20, 20));
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        formPanel.setBackground(Color.WHITE);
        
        JTextField nameField = createFormField();
        nameField.setText(currentName);
        JTextField emailField = createFormField();
        emailField.setText(currentEmail);
        JTextField phoneField = createFormField();
        phoneField.setText(currentPhone);
        
        formPanel.add(createLabel("Name"));
        formPanel.add(nameField);
        formPanel.add(createLabel("Email"));
        formPanel.add(emailField);
        formPanel.add(createLabel("Phone"));
        formPanel.add(phoneField);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton saveButton = createStyledButton("Update", new Color(16, 185, 129));
        saveButton.addActionListener(e -> {
            try {
                Member member = new Member();
                member.setId(memberId);
                member.setName(nameField.getText().trim());
                member.setEmail(emailField.getText().trim());
                member.setPhone(phoneField.getText().trim());
                
                memberDAO.updateMember(member);
                loadMembers();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Member updated successfully");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating member: " + ex.getMessage(),
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
    
    private void deleteMember() {
        int selectedRow = membersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member to delete");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this member?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int memberId = (int) tableModel.getValueAt(selectedRow, 0);
                memberDAO.deleteMember(memberId);
                loadMembers();
                JOptionPane.showMessageDialog(this, "Member deleted successfully");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting member: " + e.getMessage(),
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
        loadMembers();
    }
}
