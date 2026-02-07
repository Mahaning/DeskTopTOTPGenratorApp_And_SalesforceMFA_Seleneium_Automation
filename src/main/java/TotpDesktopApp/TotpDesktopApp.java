package TotpDesktopApp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;


public class TotpDesktopApp extends JFrame {

    private final JTextField[] otpBoxes = new JTextField[6];
    private final JLabel timerLabel = new JLabel("", SwingConstants.CENTER);
    private final UserSearchField userField;
    private JButton copyButton;
    private JLabel copyStatusLabel;


    // Constructor sets up the UI
    public TotpDesktopApp() {

        setTitle("OTP Verification");
        setSize(560, 600);                 // Bigger window
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // oft gradient background
        JPanel bg = new GradientPanel();
        bg.setLayout(new GridBagLayout());

        // Bigger card
        JPanel card = new RoundedShadowPanel(26);
//        card.setBackground(Color.WHITE);
        card.setBackground(new Color(204, 204, 255));
        card.setPreferredSize(new Dimension(420, 500)); //  Bigger card
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(28, 28, 28, 28));
        
        
        // UI Elements with better styling and spacing Key Icon
        JLabel icon = new JLabel("🔐", SwingConstants.CENTER);
        icon.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 44)); // ⬆
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title 
        JLabel title = new JLabel("OTP Verification");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24)); // ⬆
        title.setForeground(new Color(40, 40, 40));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Subtitle
        JLabel subtitle = new JLabel("Select user to generate code");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14)); //
        subtitle.setForeground(new Color(130, 130, 130));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        // User search field with suggestions
        List<String> lstUsers = new ArrayList<>(
                Arrays.asList(UserSecretStore.getUsers())
        );
        // Create the user search field and set its properties
        userField = new UserSearchField(lstUsers);
        userField.setMaximumSize(new Dimension(320, 44)); // ⬆ Wider & taller
        userField.setOnUserSelected(u -> generateOtp());
        // OTP boxes
        JPanel otpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        otpPanel.setOpaque(false);
        // Create 6 OTP boxes with better styling
        for (int i = 0; i < 6; i++) {
            otpBoxes[i] = new JTextField(1);
            otpBoxes[i].setFont(new Font("Segoe UI", Font.BOLD, 26)); // ⬆
            otpBoxes[i].setHorizontalAlignment(JTextField.CENTER);
            otpBoxes[i].setEditable(false);
            otpBoxes[i].setBorder(BorderFactory.createLineBorder(
                    new Color(220, 220, 220), 2, true));
            otpBoxes[i].setPreferredSize(new Dimension(52, 58)); // ⬆ Bigger boxes
            otpBoxes[i].setBackground(new Color(248, 250, 252));
            otpPanel.add(otpBoxes[i]);
        }
        // Timer label otp count down
        timerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // ⬆
        timerLabel.setForeground(new Color(150, 150, 150));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to card with better spacing
        card.add(icon);
        card.add(Box.createVerticalStrut(10));
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));
        card.add(userField);
        card.add(Box.createVerticalStrut(24));
        card.add(otpPanel);
        card.add(Box.createVerticalStrut(18));
        card.add(timerLabel);
        card.add(Box.createVerticalStrut(12));

        // Copy button
        copyButton = new JButton("📋 Copy OTP");
        copyButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        copyButton.setFocusPainted(false);
        copyButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        copyButton.setBackground(new Color(240, 240, 255));
        copyButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        copyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        copyButton.setEnabled(false);
        copyButton.addActionListener(e -> copyOtpToClipboard());
        copyStatusLabel = new JLabel(" ");
        copyStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyStatusLabel.setForeground(new Color(0, 150, 0));
        copyStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(copyButton);
        card.add(Box.createVerticalStrut(6));
        card.add(copyStatusLabel);
        bg.add(card);
        add(bg);

        new Timer(1000, e -> refreshTimer()).start();
    }
    
    // Generate OTP for selected user and display in boxes
    private void generateOtp() {
        String strUser = userField.getSelectedUser();
        if (strUser.isEmpty()) return;

        // Get secret for user and generate OTP
        String secret = UserSecretStore.getSecret(strUser);
        if (secret == null) return;
        // Generate OTP using RFC6238TOTP class and display in boxes
        String otp = RFC6238TOTP.generate(secret);
        for (int i = 0; i < 6; i++) {
            otpBoxes[i].setText(String.valueOf(otp.charAt(i)));
        }

        copyButton.setEnabled(true);
        copyStatusLabel.setText(" ");
    }

    // Refresh timer label and auto-regenerate OTP when it expires
    private void refreshTimer() {
        int remain = 30 - (int) (Instant.now().getEpochSecond() % 30);
        timerLabel.setText("Code refreshes in " + remain + " seconds");
        if (remain == 30) generateOtp();
    }

    // Main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new TotpDesktopApp().setVisible(true));
    }

    // Gradient background
    static class GradientPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(230, 240, 255),
                    0, getHeight(), new Color(245, 235, 255)
            );
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // Shadow card
    static class RoundedShadowPanel extends JPanel {
        private final int intRadius;

        RoundedShadowPanel(int intRadius) {
            this.intRadius = intRadius;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Shadow
            g2.setColor(new Color(0, 0, 0, 25));
            g2.fillRoundRect(6, 8, getWidth() - 12, getHeight() - 12,
                    intRadius, intRadius);

            // Card
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 12, getHeight() - 12,
                    intRadius, intRadius);
        }
    }
    // Copy OTP to clipboard and show status message
    private void copyOtpToClipboard() {
        StringBuilder otp = new StringBuilder();
        for (JTextField box : otpBoxes) {
            otp.append(box.getText());
        }

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(otp.toString()), null);

        copyStatusLabel.setText("✔ OTP copied to clipboard");

        // Auto-hide message
        new Timer(2000, e -> copyStatusLabel.setText(" ")).start();
    }

}
