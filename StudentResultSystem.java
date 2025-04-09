import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentResultSystem extends JFrame implements ActionListener {

    JTextField idField, nameField, rollField, marksField;
    JButton addButton, viewButton, updateButton, deleteButton;

    public StudentResultSystem() {
        setTitle("Student Result Management System");
        setSize(450, 400);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel idLabel = new JLabel("ID:");
        idLabel.setBounds(30, 30, 100, 25);
        add(idLabel);

        idField = new JTextField();
        idField.setBounds(140, 30, 250, 25);
        add(idField);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(30, 70, 100, 25);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(140, 70, 250, 25);
        add(nameField);

        JLabel rollLabel = new JLabel("Roll No:");
        rollLabel.setBounds(30, 110, 100, 25);
        add(rollLabel);

        rollField = new JTextField();
        rollField.setBounds(140, 110, 250, 25);
        add(rollField);

        JLabel marksLabel = new JLabel("Marks:");
        marksLabel.setBounds(30, 150, 100, 25);
        add(marksLabel);

        marksField = new JTextField();
        marksField.setBounds(140, 150, 250, 25);
        add(marksField);

        addButton = new JButton("Add");
        addButton.setBounds(30, 200, 90, 30);
        add(addButton);
        addButton.addActionListener(this);

        updateButton = new JButton("Update");
        updateButton.setBounds(130, 200, 90, 30);
        add(updateButton);
        updateButton.addActionListener(this);

        deleteButton = new JButton("Delete");
        deleteButton.setBounds(230, 200, 90, 30);
        add(deleteButton);
        deleteButton.addActionListener(this);

        viewButton = new JButton("View All");
        viewButton.setBounds(330, 200, 90, 30);
        add(viewButton);
        viewButton.addActionListener(this);

        setVisible(true);
    }

    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/student_db"; // Change your DB details
            String user = "root";
            String password = "sahil"; // Replace with your password
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed!");
            return null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            insertStudent();
        } else if (e.getSource() == updateButton) {
            updateStudent();
        } else if (e.getSource() == deleteButton) {
            deleteStudent();
        } else if (e.getSource() == viewButton) {
            viewStudents();
        }
    }

    private void insertStudent() {
        String name = nameField.getText();
        String roll = rollField.getText();
        String marksText = marksField.getText();

        if (name.isEmpty() || roll.isEmpty() || marksText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields to insert.");
            return;
        }

        try {
            int marks = Integer.parseInt(marksText);
            Connection con = getConnection();
            String query = "INSERT INTO students(name, roll_no, marks) VALUES (?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, name);
            pst.setString(2, roll);
            pst.setInt(3, marks);
            pst.executeUpdate();
            con.close();
            JOptionPane.showMessageDialog(this, "Student inserted!");
            clearFields();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error inserting student.");
        }
    }

    private void updateStudent() {
        String idText = idField.getText();
        String name = nameField.getText();
        String roll = rollField.getText();
        String marksText = marksField.getText();

        if (idText.isEmpty() || name.isEmpty() || roll.isEmpty() || marksText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields to update.");
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            int marks = Integer.parseInt(marksText);
            Connection con = getConnection();
            String query = "UPDATE students SET name=?, roll_no=?, marks=? WHERE id=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, name);
            pst.setString(2, roll);
            pst.setInt(3, marks);
            pst.setInt(4, id);
            int rows = pst.executeUpdate();
            con.close();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Student updated!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Student not found!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating student.");
        }
    }

    private void deleteStudent() {
        String idText = idField.getText();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter ID to delete.");
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            Connection con = getConnection();
            String query = "DELETE FROM students WHERE id=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, id);
            int rows = pst.executeUpdate();
            con.close();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Student deleted!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Student not found!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting student.");
        }
    }

    private void viewStudents() {
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("id"))
                  .append(", Name: ").append(rs.getString("name"))
                  .append(", Roll: ").append(rs.getString("roll_no"))
                  .append(", Marks: ").append(rs.getInt("marks")).append("\n");
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 200));
            JOptionPane.showMessageDialog(this, scrollPane, "All Students", JOptionPane.INFORMATION_MESSAGE);
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching students.");
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        rollField.setText("");
        marksField.setText("");
    }

    public static void main(String[] args) {
        new StudentResultSystem();
    }
}
