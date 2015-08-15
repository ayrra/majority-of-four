import java.net.*; // for Socket, ServerSocket, and InetAddress
import java.io.*; // for IOException and Input/OutputStream
import java.util.Arrays;
import java.util.Random;

public class Server {

	// Start Transmission
	public static final byte STX = 0x01;
	// End Transmission
	public static final byte ETX = 0x02;

	public static void main(String[] args) throws IOException {
		
		boolean[] oBitArray;
		boolean[] bitArray;
		boolean[] bitArray2;
		boolean[] bitArray3;
		boolean[] bitArray4;
		byte byte1;
		byte byte2;
		byte byte3;
		byte byte4;
		String arrayString =  "";
		String arrayString2 = "";
		String arrayString3 = "";
		String arrayString4 = "";

		if (args.length != 1) // Test for correct # of args
			throw new IllegalArgumentException("Parameter(s): <Port>");

		int servPort = Integer.parseInt(args[0]);

		// Create a server socket to accept client connection requests
		ServerSocket servSock = new ServerSocket(servPort);

		while (true) { // Run forever, accepting and servicing connections
			Socket clntSock = servSock.accept(); // Get client connection

			SocketAddress clientAddress = clntSock.getRemoteSocketAddress();
			System.out.println("Handling client at " + clientAddress);
			InputStream in = clntSock.getInputStream();
			OutputStream out = clntSock.getOutputStream();
			//Whenever the client sends the Start transmission byte
			//Generate the random byte, duplicate it 4 times,
			//make up to two errors, pack that data
			//send it to the client
			while (in.read() == STX) {
				//Make some random bits.
				oBitArray = makeRandomBits(8);
				
				//Duplicate those random bits 4 times (or 3 depending on how you think)
				bitArray = Arrays.copyOf(oBitArray, 8);
				bitArray2 = Arrays.copyOf(oBitArray, 8);
				bitArray3 = Arrays.copyOf(oBitArray, 8);
				bitArray4 = Arrays.copyOf(oBitArray, 8);
				
				//Display the original byte
				String oArrayString = booleanArrayToString(oBitArray);
				System.out.print(oArrayString + " - ");
				//Make some random errors
				makeRandomErrors(bitArray);
				makeRandomErrors(bitArray2);
				makeRandomErrors(bitArray3);
				makeRandomErrors(bitArray4);
				//Pack the boolean representations into a byte array
				byte1 = packByte(bitArray);
				byte2 = packByte(bitArray2);
				byte3 = packByte(bitArray3);
				byte4 = packByte(bitArray4);
				//Print out the bytes you are going to send to the client (with errors)
				arrayString = booleanArrayToString(bitArray);
				arrayString2 = booleanArrayToString(bitArray2);
				arrayString3 = booleanArrayToString(bitArray3);
				arrayString4 = booleanArrayToString(bitArray4);
				System.out.print(arrayString + " ");
				System.out.print(arrayString2 + " ");
				System.out.print(arrayString3 + " ");
				System.out.println(arrayString4);
				//Send data to client
				out.write(byte1);
				out.write(byte2);
				out.write(byte3);
				out.write(byte4);
			}
			//We shouldn't close the client socket until the client asks us to.
			if (in.read() == ETX) 
			clntSock.close(); // Close the socket. We are done with this client!
			System.out.println("Client Done: " + clientAddress);
		}
		/* NOT REACHED */
	}

	//Make some random bits stored in a boolean array
	public static boolean[] makeRandomBits(int size) {
		boolean [] bitarray = new boolean[size];
		Random random = new Random();
		for (int i = 0; i < size; i++) {
			bitarray[i] = random.nextBoolean();
		}
		return bitarray;
	}


	//Generate are most 2 random errors and flip two random bits.
	public static void makeRandomErrors(boolean[] data) {
		for (int i=0; i<data.length;i++) {
			if (new java.util.Random().nextInt(20)==0) {	//CHANGE THE 20 TO INCREASE OR DECREASE ERRORS 1/X
				data[i] = data[i] ^ true;
			}
		}
	}
	
	//Pack a boolean array into a byte.
	//This should do the exact opposite of unpackByte(byte) function in the client.
	//Be careful of ordering!
	public static byte packByte(boolean[] data) {
		String s = "";
		for (int i = 0; i < data.length; i++) {
			if (data[i]) {
				s = s.concat("1");
			}
			else {
				s = s.concat("0");
			}
		}
		byte converted = (byte) Integer.parseInt(s,2);
		return converted;
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
	
}