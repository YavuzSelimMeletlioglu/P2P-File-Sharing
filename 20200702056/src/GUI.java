import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

public class GUI implements ActionListener {

    private JFrame frame = new JFrame("P2P Shared Folder");
    private String root = "";
    private String destination = "";
    private JTextField rootField;
    private JTextField destinationField;
    protected Set<String> activePeers;
    protected Set<String> handledPeers;
    private JTextArea downloadLog;
    private JTextArea nameLog;

    private BroadcastingServer broadcastingServer;
    private BroadcastingClient broadcastClient;
    private FileServer fileServer;
    private FileClient fileClient;

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        JMenuItem connect = new JMenuItem("Connect");
        JMenuItem disconnect = new JMenuItem("Disconnect");
        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem developer = new JMenuItem("Developer");

        connect.addActionListener(this);
        disconnect.addActionListener(this);
        exit.addActionListener(this);
        developer.addActionListener(this);

        fileMenu.add(connect);
        fileMenu.add(disconnect);
        fileMenu.add(exit);
        helpMenu.add(developer);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private JComponent createInputPanel(String text, boolean isRoot) {
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);

        JTextField textField = new JTextField(30);
        JButton button = new JButton("Set");

        button.setActionCommand(isRoot ? "SET_ROOT" : "SET_DESTINATION");
        button.addActionListener(this);

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

        switch (actionCommand) {
            case "SET_ROOT" -> selectDirectory(true);
            case "SET_DESTINATION" -> selectDirectory(false);
            case "Connect" -> startBroadcasting();
            case "Disconnect" -> stopBroadcasting();
            case "Developer" -> showCoder();
            case "Exit" -> System.exit(0);
        }
    }

    private void showCoder() {
        JOptionPane.showMessageDialog(frame,
                "İsim: Yavuz Selim Meletlioğlu \n Numara: 20200702056",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void selectDirectory(boolean isRoot) {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = jfc.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            String selectedPath = jfc.getSelectedFile().getAbsolutePath();

            if (isRoot) {
                this.root = selectedPath;
                rootField.setText(root);
            } else {
                this.destination = selectedPath;
                destinationField.setText(destination);
            }
        }
    }

    private JPanel createLogPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        if (title.equals("Downloading files")) {
            this.downloadLog = textArea;
        } else {
            this.nameLog = textArea;
        }

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JButton searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(80, 25));
        JTextField searchField = new JTextField();

        panel.add(searchButton, BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);

        panel.setMaximumSize(new Dimension(500, 30));

        return panel;
    }

    private void startBroadcasting() {
        try {
            broadcastingServer = new BroadcastingServer();

            broadcastingServer.setActivePeersListener(updatedPeers -> {

                this.activePeers = updatedPeers;
                if (this.activePeers.size() > 0) {
                    for (String peer : this.activePeers) {
                        createPeerThread(peer);
                    }
                }
            });

            new Thread(broadcastingServer).start();

            broadcastClient = new BroadcastingClient();
            broadcastClient.sendMessage("I AM HERE");
            fileServer = new FileServer(root, 10000);

            new Thread(fileServer).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPeerThread(String peerIP) {
        fileClient = new FileClient(peerIP, destination, 10000);

        fileClient.setFileNameListener(this::updateNameLog); // Dosya isimleri logu
        fileClient.setFileDataListener(this::updateDownloadLog); // İndirme ilerleme logu

        new Thread(fileClient).start();
    }

    private void updateNameLog(String message) {
        SwingUtilities.invokeLater(() -> {
            nameLog.append(message + "\n");
        });
    }

    private void updateDownloadLog(String message) {
        SwingUtilities.invokeLater(() -> {
            downloadLog.append(message + "\n");
        });
    }

    private void stopBroadcasting() {
        if (broadcastingServer != null) {
            broadcastingServer.socket.close();
        }
        if (fileServer != null) {
            fileServer.closeSocket();
        }
        if (broadcastClient != null) {
            try {
                broadcastClient.sendMessage("END");
                broadcastClient.closeSocket();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void createAndShowGUI() {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
        GUI gui = new GUI();
        gui.createAndShowGUI();
    }
}