import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI implements ActionListener {

    private JFrame frame = new JFrame("P2P Shared Folder");
    private String root = "";
    private String destination = "";
    private JTextField rootField;
    private JTextField destinationField;

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        JMenuItem connect = new JMenuItem("Connect");
        JMenuItem disconnect = new JMenuItem("Disconnect");
        JMenuItem exit = new JMenuItem("Exit");

        fileMenu.add(connect);
        fileMenu.add(disconnect);
        fileMenu.add(exit);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private JComponent createInputPanel(String text, boolean isRoot) {
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        JTextField textField = new JTextField(30);
        JButton button = new JButton("Set");

        // Assign specific action commands to distinguish between root and destination
        button.setActionCommand(isRoot ? "SET_ROOT" : "SET_DESTINATION");

        // Register the overall ActionListener
        button.addActionListener(this);

        // Store the text fields for later access
        if (isRoot) {
            rootField = textField;
        } else {
            destinationField = textField;
        }

        panel.setBorder(BorderFactory.createTitledBorder(text));

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(textField)
                                .addComponent(button, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)));

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addComponent(button, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)));

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        if (actionCommand.equals("SET_ROOT")) {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = jfc.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {
                root = jfc.getSelectedFile().getAbsolutePath();
                rootField.setText(root);
            }
        } else if (actionCommand.equals("SET_DESTINATION")) {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = jfc.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {
                destination = jfc.getSelectedFile().getAbsolutePath();
                destinationField.setText(destination);
            }
        }
    }

    private JPanel createLogPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JButton searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(80, 25)); // Set smaller button size
        JTextField searchField = new JTextField();

        panel.add(searchButton, BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);

        panel.setMaximumSize(new Dimension(500, 30));

        return panel;
    }

    public void createAndShowGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(createMenuBar());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        contentPanel.add(createInputPanel("Root of the P2P shared folder", true));
        contentPanel.add(createInputPanel("Destination folder", false));
        contentPanel.add(createLogPanel("Downloading files"));
        contentPanel.add(createLogPanel("Found files"));
        contentPanel.add(createSearchPanel());

        frame.add(contentPanel);
        frame.setSize(500, 600);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI().createAndShowGUI());
    }
}