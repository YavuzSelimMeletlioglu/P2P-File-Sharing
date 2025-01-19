import java.io.*;
import java.net.*;
import java.util.*;

public class FileServer implements Runnable {

	private Socket socket;
	private File folder;
	private ServerSocket welcomeSocket = null;

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
		try {
			Socket connectionSocket;

			if (welcomeSocket != null) {
				connectionSocket = welcomeSocket.accept();
				socket = connectionSocket;
			}
			if (socket != null) {
				RandomAccessFile rAF = null;

				File[] listOfFiles = folder.listFiles();
				System.out.println(socket.getInetAddress());
				DataInputStream dIS = new DataInputStream(socket.getInputStream());
				DataOutputStream dOS = new DataOutputStream(socket.getOutputStream());

				FilenameFilter textFilter = new FilenameFilter() {
					public boolean accept(File dir, String name) {
						String lowercaseName = name.toLowerCase();
						if (lowercaseName.endsWith(".txt")) {
							return true;
						} else {
							return false;
						}
					}
				};

				for (File file : listOfFiles) {
					if (file.isFile()) {
						String fileName = file.getName();
						byte[] fileNameBytes = fileName.getBytes();
						dOS.writeInt(fileNameBytes.length);
						dOS.write(fileNameBytes);

						int length = (int) file.length();
						dOS.writeInt(length);

						rAF = new RandomAccessFile(file, "rw");
						int chunkCount = (int) Math.ceil(length / 256000.0);
						int[] checkArray = new int[chunkCount];
						Random random = new Random();
						int loop = 0;
						while (loop < chunkCount) {
							int i = random.nextInt(chunkCount);
							if (checkArray[i] == 0) {
								rAF.seek(i * 256000);
								byte[] toSend = new byte[256000];
								int read = rAF.read(toSend);
								dOS.writeInt(i); // Chunk indeksini gönder
								dOS.writeInt(read); // Okunan byte uzunluğunu gönder
								dOS.write(toSend, 0, read); // Chunk verisini gönder
								dOS.flush();
								int ACK = dIS.readInt(); // İstemciden onay (ACK) al
								if (i == ACK) {
									checkArray[i] = 1;
									loop++;
								}
							}
						}
					}
				}

				if (rAF != null) {
					System.out.println(">>> Sent all chunks to " + socket.getInetAddress().getHostAddress() + "...");
					rAF.close();
					dOS.writeInt(-1); // Transfer tamamlandı sinyali gönder
					dOS.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeServer() {
		try {
			welcomeSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
