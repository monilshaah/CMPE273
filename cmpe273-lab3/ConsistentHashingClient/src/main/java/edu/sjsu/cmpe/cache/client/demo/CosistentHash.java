package edu.sjsu.cmpe.cache.client.demo;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CosistentHash {
	private final String hashingAlgo = "MD5";
	private final ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
	
	public int getConsistentHash(long key, int serverSize) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance(hashingAlgo);
		buffer.clear();
		buffer.putLong(key);
		byte[] digestBytes = md5.digest(buffer.array());
		BigInteger digetstInt = new BigInteger(1,digestBytes);
		BigInteger cosistentHash = digetstInt.mod(new BigInteger(String.valueOf(serverSize)));
		//System.out.println("***Key: "+key+"\ndigestInt: "+digetstInt+"\nhash: "+cosistentHash.intValue());
		return cosistentHash.intValue();
	}
}
