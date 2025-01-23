import java.io.*;
import java.net.Socket;

public class FileClient extends Thread {

	private String IP;
	private static int PORT;
	private String path;

	public FileClient(String IP, String path) {
		this.IP = IP;
		this.path = path;
	}

	public static void getFiles(String IP, String path, int port) {
		PORT = port;
		new FileClient(IP, path).start();
	}

	@Override
	public void run() {
		Socket socket = null;
		while (socket == null) {
			try {
				socket = new Socket(IP, PORT);
			} catch (IOException e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}

		try {
			DataInputStream dIS = new DataInputStream(socket.getInputStream());
			DataOutputStream dOS = new DataOutputStream(socket.getOutputStream());

			while (true) {
				String signal = dIS.readUTF();
				if (signal.equals("START")) {
					String fileName = path + "/" + dIS.readUTF();

					File file = new File(fileName);
					if (!file.exists()) {
						file.createNewFile();
					}

					RandomAccessFile rAF = new RandomAccessFile(file, "rw");
					int length = dIS.readInt();
					rAF.setLength(length);

					int chunkID;
					while ((chunkID = dIS.readInt()) != -1) {
						rAF.seek(chunkID * 256000);
						int chunkLength = dIS.readInt();
						byte[] chunkData = new byte[chunkLength];
						dIS.readFully(chunkData);
						rAF.write(chunkData);
						dOS.writeInt(chunkID);
					}

					rAF.close();
					System.out.println("Received file: " + fileName);
				} else if (signal.equals("END")) {
					System.out.println("File transfer complete.");
				} else if (signal.equals("FINISH")) {
					break;
				}
			}

			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}