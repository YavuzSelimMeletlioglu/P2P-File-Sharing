import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class BroadcastingServer implements Runnable {

    public interface ActivePeersListener {
        void onActivePeersChanged(Set<String> activePeers);
    }

    private ActivePeersListener listener;
    protected DatagramSocket socket = null;
    protected boolean running;
    protected byte[] buf = new byte[256];
    protected Set<String> activePeers;

    public BroadcastingServer() throws IOException {
        socket = new DatagramSocket(null);
        socket.setReuseAddress(true);
        socket.bind(new InetSocketAddress(4445));
        this.activePeers = new HashSet<>();
    }

    public void setActivePeersListener(ActivePeersListener listener) {
        this.listener = listener;
    }

    private void notifyActivePeersChanged() {
        if (listener != null && this.activePeers != null) {
            listener.onActivePeersChanged(new HashSet<>(this.activePeers));
        }
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                String peerId = address.getHostAddress();

                String received = new String(packet.getData(), 0, packet.getLength());
                if (received.equals("I AM HERE")) {
                    this.activePeers.add(peerId);
                    notifyActivePeersChanged();
                } else if (received.equals("END")) {
                    this.activePeers.remove(peerId);
                    notifyActivePeersChanged();
                }

                String responseMessage = "HELLO " + this.activePeers.size();
                byte[] responseBytes = responseMessage.getBytes();

                DatagramPacket responsePacket = new DatagramPacket(
                        responseBytes,
                        responseBytes.length,
                        address,
                        packet.getPort());
                socket.send(responsePacket);

            } catch (IOException e) {
                e.printStackTrace();
                running = false;
            }
        }
        socket.close();
    }
}