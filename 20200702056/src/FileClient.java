import java.io.*;
import java.net.Socket;

public class FileClient implements Runnable {

	public interface DownloadListener {
		void onDownloadOccured(String message);
	}

	private DownloadListener dataListener = null;
	private DownloadListener nameListener = null;
	private String IP;
	private int PORT;
	private String path;

	public FileClient(String IP, String path, int port) {
		this.IP = IP;
		this.path = path;
		PORT = port;
	}

	public void setFileDataListener(DownloadListener listener) {
		this.dataListener = listener;
	}

	public void setFileNameListener(DownloadListener listener) {
		this.nameListener = listener;
	}

	private void notifyDownloadOccured(String data, boolean isName) {
		if (isName && nameListener != null) {
			nameListener.onDownloadOccured(data);
		} else if (!isName && dataListener != null) {
			dataListener.onDownloadOccured(data);
		}
	}

	@Override
	public void run() {
		try (Socket socket = new Socket(IP, PORT);
				DataInputStream dIS = new DataInputStream(socket.getInputStream());
				DataOutputStream dOS = new DataOutputStream(socket.getOutputStream())) {

			while (true) {
				String signal = dIS.readUTF();
				if (signal.equals("FILENAMES")) {
					int fileCount = dIS.readInt();
					for (int i = 0; i < fileCount; i++) {
						String fileName = dIS.readUTF();
						notifyDownloadOccured(fileName, true);
					}
				} else if (signal.equals("FILESDATAS")) {
					String fileName = dIS.readUTF();
					String filePath = path + "/" + fileName;
					notifyDownloadOccured("File downloading: " + fileName, false);
					File file = new File(filePath);
					if (!file.exists()) {
						file.createNewFile();
					}
					RandomAccessFile rAF = new RandomAccessFile(file, "rw");
					int length = dIS.readInt();
					rAF.setLength(length);
					int totalBytesRead = 0;
					int lastPercent = 0;
					int chunkID;
					while ((chunkID = dIS.readInt()) != -1) {
						rAF.seek(chunkID * 256000);

						int chunkLength = dIS.readInt();
						byte[] chunkData = new byte[chunkLength];
						dIS.readFully(chunkData);

						rAF.write(chunkData);
						totalBytesRead += chunkLength;
						int percent = (int) ((totalBytesRead / (float) length) * 100);
						if (percent > lastPercent) {
							lastPercent = percent;
							notifyDownloadOccured("Progress: " + percent + "%", false);
						}
						dOS.writeInt(chunkID);
					}

					rAF.close();
					notifyDownloadOccured("File downloaded: " + fileName, false);
				} else if (signal.equals("FINISH")) {
					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}