package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private BooksPanel booksPanel;
    private MembersPanel membersPanel;
    private TransactionsPanel transactionsPanel;
    
    public MainFrame() {
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());
        
        add(createSidebar(), BorderLayout.WEST);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.WHITE);
        
        booksPanel = new BooksPanel();
        membersPanel = new MembersPanel();
        transactionsPanel = new TransactionsPanel();
        
        mainPanel.add(booksPanel, "books");
        mainPanel.add(membersPanel, "members");
        mainPanel.add(transactionsPanel, "transactions");
        
        add(mainPanel, BorderLayout.CENTER);
        
        cardLayout.show(mainPanel, "books");
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(15, 23, 42));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Library System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        sidebar.add(titleLabel);
        
        sidebar.add(createNavButton("Books", "books"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Members", "members"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Transactions", "transactions"));
        
        sidebar.add(Box.createVerticalGlue());
        
        return sidebar;
    }
    
    private JButton createNavButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(180, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(new Color(226, 232, 240));
        button.setBackground(new Color(30, 41, 59));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(51, 65, 85));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 41, 59));
            }
        });
        
        button.addActionListener(e -> {
            cardLayout.show(mainPanel, panelName);
            if (panelName.equals("books")) {
                booksPanel.refreshTable();
            } else if (panelName.equals("members")) {
                membersPanel.refreshTable();
            } else if (panelName.equals("transactions")) {
                transactionsPanel.refreshTable();
            }
        });
        
        return button;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
