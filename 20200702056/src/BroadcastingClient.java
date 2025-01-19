import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastingClient {
    private DatagramSocket socket;
    private InetAddress address;
    private int PORT;
    private byte[] buf;

    public BroadcastingClient() throws Exception {
        this.address = InetAddress.getByName("255.255.255.255");
        this.PORT = 4445;
        socket = new DatagramSocket();
        socket.setBroadcast(true);
    }

    public void sendMessage(String message) throws IOException {

        buf = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
        socket.send(packet);
        closeSocket();
    }

    public void closeSocket() {
        socket.close();
    }

}