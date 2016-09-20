package tdm5Ex2Client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TransfertFileClient {


	static String host;
	static int port;
	static String filePath;
	//static String newFile = "C:\Users\B03073\Documents\NE449\src\tdm5ex1Yo\test_copy.txt";

	public static void main(String[] args) throws IOException {
		if(args.length != 3) {
			System.out.println("Usage: <hostname> <port_server> <file_path>");
			System.exit(1);
		}

		host = args[0];
		port = Integer.parseInt(args[1]);
		filePath = args[2];

		ClientTCP client = new ClientTCP(host,port);
		client.open();

		client.send(filePath);
		byte[] buf = new byte[2048];
		buf = client.receive(2048);
		if(buf[0] == 0x18) {
			System.out.println("File doesn't exist on server");
		}
		else {
			System.out.println("Start transfer");
			System.out.println(filePath);
			try {
				StringBuffer filename = new StringBuffer();
				boolean done = false;
				int i = filePath.length()-1;
				while(!done) {
					if(filePath.charAt(i)=='/') {
						done = true;
					}
					else {
						filename.append(filePath.charAt(i));
					}
					i--;
				}
				filename.reverse();
				System.out.println(filename);
				FileOutputStream file = new FileOutputStream(filename.toString());
				while(buf[0] != 0x4) {
					System.out.println(client.getNbBytes());
					byte[] tmp = new byte[client.getNbBytes()];
					file.write(tmp);
					buf = client.receive(2048);
				}
				file.close();
			}
			catch(FileNotFoundException f) {
				System.out.println("Failed to open file");
			}
		}

		client.close();
	}
}