import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
 class JobPortalSystem extends JFrame {
    // Simple model class
    static class JobApplication {
        String name, email, jobTitle, qualification;
        JobApplication(String n, String e, String j, String q) {
            name = n; email = e; jobTitle = j; qualification = q;
        }
    }

    private final JTextField tfName = new JTextField(18);
    private final JTextField tfEmail = new JTextField(18);
    private final JTextField tfJobTitle = new JTextField(18);
    private final JTextField tfQualification = new JTextField(18);
    private final DefaultTableModel model;
    private final JTable table;
    private final java.util.List<JobApplication> applications = new ArrayList<>();

    public JobPortalSystem() {
        setTitle("Job Portal System");
        setSize(980, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Top heading
        JLabel heading = new JLabel("Online Job Portal", SwingConstants.CENTER);
        heading.setOpaque(true);
        heading.setBackground(new Color(30, 41, 56));
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 26));
        heading.setBorder(new EmptyBorder(16, 12, 16, 12));
        add(heading, BorderLayout.NORTH);

        // Left form panel
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(12, 12, 12, 12));
        left.setBackground(new Color(245, 247, 250));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220,220,220)),
                "Apply for a Job"));
        formCard.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(new JLabel("Applicant Name:"), gbc);
        gbc.gridx = 1;
        formCard.add(tfName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formCard.add(tfEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formCard.add(new JLabel("Job Title:"), gbc);
        gbc.gridx = 1;
        formCard.add(tfJobTitle, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formCard.add(new JLabel("Qualification:"), gbc);
        gbc.gridx = 1;
        formCard.add(tfQualification, gbc);

        // Button row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(new Color(245, 247, 250));
        JButton btnApply = styledButton("Apply");
        JButton btnClear = styledButton("Clear");
        JButton btnDelete = styledButton("Delete Selected");
        JButton btnLoad = styledButton("Load Selected");
        JButton btnExport = styledButton("Export CSV");
        btnRow.add(btnApply); btnRow.add(btnClear); btnRow.add(btnLoad); btnRow.add(btnDelete); btnRow.add(btnExport);

        left.add(formCard);
        left.add(Box.createVerticalStrut(12));
        left.add(btnRow);

        add(left, BorderLayout.WEST);

        // Table in center
        model = new DefaultTableModel(new String[]{"Name", "Email", "Job Title", "Qualification"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make table read-only
            }
        };
        table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("SansSerif", Font.BOLD, 14));
        th.setBackground(new Color(33, 47, 61));
        th.setForeground(Color.WHITE);

        JScrollPane tablePane = new JScrollPane(table);
        tablePane.setBorder(new EmptyBorder(12,12,12,12));
        add(tablePane, BorderLayout.CENTER);

        // Sample data
        applications.add(new JobApplication("Aditya", "adi@mail.com", "Software Developer", "B.Tech"));
        applications.add(new JobApplication("Harsh", "harsh@mail.com", "Graphic Designer", "BCA"));
        applications.add(new JobApplication("Avantika","avantika@gmail.com","FullStack","MCA"));
        refreshTable();

        // Event wiring
        btnApply.addActionListener(e -> applyOrUpdate());
        btnClear.addActionListener(e -> clearForm());
        btnLoad.addActionListener(e -> loadSelectedToForm());
        btnDelete.addActionListener(e -> deleteSelected());
        btnExport.addActionListener(e -> exportCsv());

        // Double-click to load
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) loadSelectedToForm();
            }
        });
    }

    private JButton styledButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(41, 128, 185));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        return b;
    }

    private void applyOrUpdate() {
        String name = tfName.getText().trim();
        String email = tfEmail.getText().trim();
        String jobTitle = tfJobTitle.getText().trim();
        String qualification = tfQualification.getText().trim();

        if (name.isEmpty() || email.isEmpty() || jobTitle.isEmpty() || qualification.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // If a row is selected AND form matches selected row, treat as update
        int sel = table.getSelectedRow();
        if (sel >= 0) {
            JobApplication ja = applications.get(sel);
            ja.name = name; ja.email = email; ja.jobTitle = jobTitle; ja.qualification = qualification;
            refreshTable();
            table.getSelectionModel().setSelectionInterval(sel, sel);
            JOptionPane.showMessageDialog(this, "Record updated.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            applications.add(new JobApplication(name, email, jobTitle, qualification));
            refreshTable();
            JOptionPane.showMessageDialog(this, "Application added.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
        clearForm();
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (JobApplication j : applications) {
            model.addRow(new Object[]{j.name, j.email, j.jobTitle, j.qualification});
        }
    }

    private void clearForm() {
        tfName.setText("");
        tfEmail.setText("");
        tfJobTitle.setText("");
        tfQualification.setText("");
        table.clearSelection();
    }

    private void loadSelectedToForm() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JobApplication ja = applications.get(sel);
        tfName.setText(ja.name);
        tfEmail.setText(ja.email);
        tfJobTitle.setText(ja.jobTitle);
        tfQualification.setText(ja.qualification);
    }

    private void deleteSelected() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete selected application?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            applications.remove(sel);
            refreshTable();
            clearForm();
        }
    }

    private void exportCsv() {
        if (applications.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data to export.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter("applications_export.csv"))) {
            pw.println("Name,Email,Job Title,Qualification");
            for (JobApplication j : applications) {
                pw.printf("\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        j.name.replace("\"","\"\""),
                        j.email.replace("\"","\"\""),
                        j.jobTitle.replace("\"","\"\""),
                        j.qualification.replace("\"","\"\""));
            }
            JOptionPane.showMessageDialog(this, "Exported to applications_export.csv", "Export", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JobPortalSystem ui = new JobPortalSystem();
            ui.setVisible(true);
        });
    }
}
