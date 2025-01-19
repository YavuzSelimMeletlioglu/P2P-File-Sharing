import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.Socket;

public class FileClient extends Thread {

	private String IP;
	private static int PORT;
	private String path;

	public FileClient(String IP, String path) {
		this.IP = IP;
		this.path = path;
	}

	public static void getFiles(String IP, String name, int port) {
		PORT = port;
		new FileClient(IP, name).start();
	}

	@Override
	public void run() {
		try {
			Socket socket = new Socket(IP, PORT);

			DataInputStream dIS = new DataInputStream(socket.getInputStream());
			DataOutputStream dOS = new DataOutputStream(socket.getOutputStream());

			int fileNameLength = dIS.readInt();
			byte[] fileNameBytes = new byte[fileNameLength];
			dIS.readFully(fileNameBytes);
			String fileName = path + "/" + new String(fileNameBytes);

			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			RandomAccessFile rAF = new RandomAccessFile(file, "rw");

			int length = dIS.readInt();
			rAF.setLength(length);

			int chunkID;
			while ((chunkID = dIS.readInt()) != -1) {
				System.out.println("Reading chunk " + chunkID);
				rAF.seek(chunkID * 256000);

				int chunkLength = dIS.readInt();
				byte[] chunkData = new byte[chunkLength];
				dIS.readFully(chunkData);

				rAF.write(chunkData);

				dOS.writeInt(chunkID);
			}

			rAF.close();
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}