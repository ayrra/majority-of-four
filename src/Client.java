import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Client {

	// Start Transmission
	public static final byte STX = 0x01;
	// End Transmission
	public static final byte ETX = 0x02;
	
	public static void main(String[] args) throws IOException {

		if (args.length != 3) // Test for correct # of args
			throw new IllegalArgumentException("Parameter(s): <Server> <Port> <RunCount>");

		String server = args[0]; // Server name or IP address
		int servPort = Integer.parseInt(args[1]);
		int numToRun = Integer.parseInt(args[2]);
		// Create socket that is connected to server on specified port
		Socket socket = new Socket(server, servPort);
		System.out.println("Connected to server...");

		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		byte[] bytes = new byte[4];
		int[] numbers = new int[4];
		//Loop the number of times you want to run
		for(int i=0; i<numToRun;i++){
			//Ask server to start transmitting
			out.write(STX);
			for (int x=0;x<4;x++) {
				numbers[x] = in.read();
				bytes[x] = (byte) numbers[x];
			}
			int x = majority(bytes);
			if (x != -1) {
				byte temp = (byte) x;
				boolean[]maj = unpackByte(temp);
				String m = booleanArrayToString(maj);
				System.out.println(m);
			}
			else {
			System.out.println("Unknowable");
			}
		}
		//Tell the server you are done.
		out.write(ETX);
		System.out.println("Done");
		socket.close(); // Close the socket and its streams
	}
	
	//Unpack a byte into a boolean array
	//This should do the exact opposite of packByte(boolean[]) in the server.
	//Be careful!
	public static boolean[] unpackByte(byte b){
		boolean [] temp = new boolean[8];
		temp[0] = ((b & 0x80) != 0);
	    temp[1] = ((b & 0x40) != 0);
	    temp[2] = ((b & 0x20) != 0);
	    temp[3] = ((b & 0x10) != 0);
	    temp[4] = ((b & 0x08) != 0);
	    temp[5] = ((b & 0x04) != 0);
	    temp[6] = ((b & 0x02) != 0);
	    temp[7] = ((b & 0x01) != 0);
		return temp;
	}
	//Converts a boolean array to a string of ones and zeros.
	//This function will let you print your boolean[] version of your bytes
	public static String booleanArrayToString(boolean[] data){
		String s = "";
		for (int i = 0; i < data.length; i++) {
			if (data[i]) {
				s = s.concat("1");
			}
			else {
				s = s.concat("0");
			}
		}
		return s;
	}
	
	//This function will return the index of one of the majority
	//Or it should return an error value if it cannot determine 
	//which bytes are the majority.
	public static int majority(byte[] members){
		Arrays.sort(members);
		int value = members[0];
		int count = 1;
		int tempCount = 1;
		
		for (int i=1; i<members.length;i++) {
			int previous = members[i - 1];
			int current = members[i];
			
			if (current == previous) {
				tempCount++;
			}
			if (current != previous || i == members.length - 1) {
				if (tempCount > count) {
					value = previous;
					count = tempCount;
				}
			}
		}
		if(count < 3) 
			return -1;
		return value;
	}
}