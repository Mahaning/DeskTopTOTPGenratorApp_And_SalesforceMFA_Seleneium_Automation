package TotpDesktopApp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class UserSearchField extends JPanel {

    private final JTextField field = new JTextField();
    private final JWindow suggestionWindow;
    private final JList<String> suggestionList = new JList<>();
    private final List<String> users;

    private Consumer<String> onUserSelected;

    public UserSearchField(List<String> users) {
        this.users = users;

        setLayout(new BorderLayout());
        setOpaque(false);

        // TEXT FIELD (soft + rounded)
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(new RoundedBorder(16, new Color(210, 210, 210)));
        field.setBackground(Color.WHITE);
        field.setCaretColor(new Color(60, 60, 60));
        field.setPreferredSize(new Dimension(260, 40));
        add(field, BorderLayout.CENTER);

        // SUGGESTION WINDOW
        suggestionWindow = new JWindow();
        suggestionWindow.setFocusableWindowState(false);
        setBorder(new RoundedBorder(16, new Color(210, 210, 210)));

        suggestionList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionList.setFixedCellHeight(32);
        suggestionList.setBackground(Color.WHITE);
        suggestionList.setSelectionBackground(new Color(0, 153, 204));
        suggestionList.setSelectionForeground(Color.WHITE);
        suggestionList.setBorder(null);

        JScrollPane scroll = new JScrollPane(suggestionList);
        scroll.setBorder(new RoundedBorder(12, new Color(220, 220, 220)));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        suggestionWindow.add(scroll);

        // FILTER WHILE TYPING
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateSuggestions(); }
            public void removeUpdate(DocumentEvent e) { updateSuggestions(); }
            public void changedUpdate(DocumentEvent e) {}
        });

        // MOUSE SELECTION
        suggestionList.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                selectUser();
            }
        });

        // KEYBOARD HANDLING (TEXT FIELD)
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                int size = suggestionList.getModel().getSize();
                int index = suggestionList.getSelectedIndex();

                switch (e.getKeyCode()) {

                    case KeyEvent.VK_DOWN:
                        showIfHidden();
                        if (size > 0) {
                            suggestionList.setSelectedIndex(
                                    Math.min(index + 1, size - 1)
                            );
                        }
                        e.consume();
                        break;

                    case KeyEvent.VK_UP:
                        if (size > 0) {
                            suggestionList.setSelectedIndex(
                                    Math.max(index - 1, 0)
                            );
                        }
                        e.consume();
                        break;

                    case KeyEvent.VK_ENTER:
                        showIfHidden();
                        if (size > 0) {
                            if (suggestionList.getSelectedIndex() == -1) {
                                suggestionList.setSelectedIndex(0);
                            }
                            selectUser();
                        }
                        e.consume();
                        break;

                    case KeyEvent.VK_ESCAPE:
                        hideSuggestions();
                        e.consume();
                        break;
                }
            }
        });

        // CLOSE ON FOCUS LOST
        field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> hideSuggestions());
            }
        });
    }

    // ================= LOGIC =================

    // Update suggestion list based on current text input
    private void updateSuggestions() {
        String text = field.getText().trim().toLowerCase();

        if (text.isEmpty()) {
            hideSuggestions();
            return;
        }

        List<String> matches = users.stream()
                .filter(u -> u.toLowerCase().contains(text))
                .collect(Collectors.toList());

        if (matches.isEmpty()) {
            hideSuggestions();
            return;
        }

        suggestionList.setListData(matches.toArray(new String[0]));
        suggestionList.setSelectedIndex(0);
        showSuggestions(matches.size());
    }

    // Show suggestion window if it's not already visible
    private void showIfHidden() {
        if (!suggestionWindow.isVisible()) {
            updateSuggestions();
        }
    }

    // Position and show the suggestion window below the text field
    private void showSuggestions(int count) {
        Point p = field.getLocationOnScreen();
        suggestionWindow.setBounds(
                p.x,
                p.y + field.getHeight() + 4,
                field.getWidth(),
                Math.min(180, count * 32)
        );
        suggestionWindow.setVisible(true);
    }

    // Hide the suggestion window
    private void hideSuggestions() {
        suggestionWindow.setVisible(false);
    }

    // Handle user selection from the suggestion list
    private void selectUser() {
        String user = suggestionList.getSelectedValue();
        if (user == null) return;

        field.setText(user);
        hideSuggestions();

        if (onUserSelected != null) {
            onUserSelected.accept(user);
        }
    }

    // ================= API =================

    // Set callback to be invoked when a user is selected
    public void setOnUserSelected(Consumer<String> callback) {
        this.onUserSelected = callback;
    }

    public String getSelectedUser() {
        return field.getText().trim();
    }

    // ================= ROUNDED BORDER =================

    // Custom rounded border for text field and suggestion window
    static class RoundedBorder extends AbstractBorder {

        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        // Paint the rounded border
        @Override
        public void paintBorder(
                Component c, Graphics g,
                int x, int y, int width, int height) {

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(
                    x, y,
                    width - 1, height - 1,
                    radius, radius
            );
        }

        // Define insets to ensure proper spacing for the rounded border
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(10, 14, 10, 14);
        }

        // Overloaded method to provide insets with custom values
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 14;
            insets.top = insets.bottom = 10;
            return insets;
        }
    }
}
