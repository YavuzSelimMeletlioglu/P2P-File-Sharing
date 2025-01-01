import javax.swing.*;
import java.awt.*;

public class GUI {

    public JFrame frame = new JFrame("P2P Shared Folder");

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private JComponent createInputPanel(String text) {
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        JTextField textField = new JTextField(30);
        JButton button = new JButton("Set");
        panel.setBorder(BorderFactory.createTitledBorder(text));

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(textField)
                                .addComponent(button, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)));

        // Define the vertical group
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addComponent(button, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)));

        return panel;
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

        // Create the smaller components
        JButton searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(80, 25)); // Set smaller button size
        JTextField searchField = new JTextField();

        // Add components to the panel
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

        contentPanel.add(createInputPanel("Root of the P2P shared folder"));
        contentPanel.add(createInputPanel("Destination folder"));

        contentPanel.add(createLogPanel("Downloading files"));
        contentPanel.add(createLogPanel("Found files"));
        contentPanel.add(createSearchPanel());

        frame.add(contentPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setVisible(true);
    }
}