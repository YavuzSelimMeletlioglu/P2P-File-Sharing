import java.io.*;
import java.net.*;
import java.util.*;

public class FileServer implements Runnable {

	private ServerSocket welcomeSocket = null;
	private File folder;

	public FileServer(String filePath, int serverPort) {
		try {
			folder = new File(filePath);
			welcomeSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket connectionSocket = welcomeSocket.accept();
				System.out.println("New connection from: " + connectionSocket.getInetAddress().getHostAddress());
				new Thread(new ClientHandler(connectionSocket, folder)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void closeSocket() {
		try {
			this.welcomeSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class ClientHandler implements Runnable {
		private Socket socket;
		private File folder;

		public ClientHandler(Socket socket, File folder) {
			this.socket = socket;
			this.folder = folder;
		}

		@Override
		public void run() {
			try {
				File[] listOfFiles = folder.listFiles();
				DataInputStream dIS = new DataInputStream(socket.getInputStream());
				DataOutputStream dOS = new DataOutputStream(socket.getOutputStream());

				for (File file : listOfFiles) {
					if (!file.isDirectory()) {
						dOS.writeUTF("START");
						dOS.writeUTF(file.getName());
						dOS.writeInt((int) file.length());

						RandomAccessFile rAF = new RandomAccessFile(file, "rw");
						int chunkCount = (int) Math.ceil(file.length() / 256000.0);
						int[] checkArray = new int[chunkCount];
						Random random = new Random();
						int loop = 0;

						while (loop < chunkCount) {
							int i = random.nextInt(chunkCount);
							if (checkArray[i] == 0) {
								rAF.seek(i * 256000);
								byte[] toSend = new byte[256000];
								int read = rAF.read(toSend);
								dOS.writeInt(i);
								dOS.writeInt(read);
								dOS.write(toSend, 0, read);
								dOS.flush();
								int ACK = dIS.readInt();
								if (i == ACK) {
									checkArray[i] = 1;
									loop++;
								}
							}
						}

						dOS.writeInt(-1);
						dOS.writeUTF("END");
						rAF.close();
					}
				}

				dOS.writeUTF("FINISH");
				dOS.close();
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}