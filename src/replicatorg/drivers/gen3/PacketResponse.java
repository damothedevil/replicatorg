package replicatorg.drivers.gen3;

import java.util.logging.Level;

import replicatorg.app.Base;

/**
 * Packet response wrapper, with convenience functions for reading off values in
 * sequence and retrieving the response code.
 */
public class PacketResponse {

	/** The response codes at the start of every response packet. */
	enum ResponseCode {
		GENERIC_ERROR("Generic Error"), 
		OK("OK"), 
		BUFFER_OVERFLOW("Buffer full"), 
		CRC_MISMATCH("CRC mismatch"), 
		QUERY_OVERFLOW("Query overflow"), 
		UNSUPPORTED("Unsupported command"),
		UNKNOWN("Unknown code")
		;
		
		private final String message;
		
		ResponseCode(String message) {
			this.message = message;
		}
		
		public String getMessage() { return message; }
		
		public static ResponseCode fromInt(int value) {
			switch(value) {
			case 0:
				return GENERIC_ERROR;
			case 1:
				return OK;
			case 2:
				return BUFFER_OVERFLOW;
			case 3:
				return CRC_MISMATCH;
			case 4:
				return QUERY_OVERFLOW;
			case 5:
				return UNSUPPORTED;
			}
			return UNKNOWN;
		}
	};


	byte[] payload;

	int readPoint = 1;

	public PacketResponse() {
	}

	public PacketResponse(byte[] p) {
		payload = p;
	}

	/**
	 * Prints a debug message with the packet response code decoded, along wiith
	 * the packet's contents in hex.
	 */
	public void printDebug() {
		ResponseCode code = getResponseCode(); 
		String msg = code.getMessage();

		// only print certain messages
		Level level = Level.FINER;
		if (code != ResponseCode.OK && code != ResponseCode.BUFFER_OVERFLOW) level = Level.WARNING;
		
		if (Base.logger.isLoggable(level)) {
			Base.logger.log(level,"Packet response code: " + msg);
			StringBuffer buf = new StringBuffer("Packet payload: ");
			for (int i = 1; i < payload.length; i++) {
				buf.append(Integer.toHexString(payload[i] & 0xff));
				buf.append(" ");
			}
			Base.logger.log(level,buf.toString());
		}
	}

	/**
	 * Retrieve the packet payload.
	 * 
	 * @return an array of bytes representing the payload.
	 */
	public byte[] getPayload() {
		return payload;
	}

	/**
	 * Get the next 8-bit value from the packet payload.
	 */
	int get8() {
		if (payload.length > readPoint)
			return ((int) payload[readPoint++]) & 0xff;
		else {
			System.out.println("Error: payload not big enough.");
			return 0;
		}
	}

	/**
	 * Get the next 16-bit value from the packet payload.
	 */
	int get16() {
		return get8() + (get8() << 8);
	}

	/**
	 * Get the next 32-bit value from the packet payload.
	 */
	int get32() {
		return get16() + (get16() << 16);
	}

	/**
	 * Does the response code indicate that the command was successful?
	 */
	public boolean isOK() {
		return getResponseCode() == ResponseCode.OK;
	}

	public ResponseCode getResponseCode() {
		return ResponseCode.fromInt(payload[0]);
	}

}
