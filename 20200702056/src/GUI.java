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

    private BroadcastingServer broadcastingServer;
    private BroadcastingClient broadcastClient;
    private FileServer fileServer;

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        JMenuItem connect = new JMenuItem("Connect");
        JMenuItem disconnect = new JMenuItem("Disconnect");
        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem developer = new JMenuItem("Developer");
        JMenuItem start = new JMenuItem("Start");

        connect.addActionListener(this);
        disconnect.addActionListener(this);
        exit.addActionListener(this);
        developer.addActionListener(this);
        start.addActionListener(this);

        fileMenu.add(connect);
        fileMenu.add(start);
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
            case "Disconnect" -> stopTransaction();
            case "Developer" -> showDeveloper();
            case "Exit" -> System.exit(0);
            case "Start" -> startTransaction();
        }
    }

    private void showDeveloper() {
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

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        return panel;
    }

    private void startBroadcasting() {
        try {
            broadcastingServer = new BroadcastingServer();

            new Thread(broadcastingServer).start();

            broadcastClient = new BroadcastingClient();
            broadcastClient.sendMessage("I AM HERE");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPeerThread(String peerIP) {
        Thread peerThread = new Thread(() -> {
            try {
                FileClient.getFiles(peerIP, destination, 10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        peerThread.start();

    }

    private void startTransaction() {
        fileServer = new FileServer(root, 10000);

        new Thread(fileServer).start();

        broadcastingServer.setActivePeersListener(updatedPeers -> {

            this.activePeers = updatedPeers;
            if (this.activePeers.size() > 0) {
                for (String peer : this.activePeers) {
                    createPeerThread(peer);
                }
            }
        });
    }

    private void stopTransaction() {
        if (broadcastingServer != null) {
            broadcastingServer.socket.close();
        }
        if (fileServer != null) {
            fileServer.closeSocket();
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

        frame.add(contentPanel);
        frame.setSize(500, 600);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.createAndShowGUI();
    }
}