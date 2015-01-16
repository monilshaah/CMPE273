package edu.sjsu.cmpe.cache.client.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Client extends CosistentHash {

    public static void main(String[] args) throws Exception {
    	System.out.println("Starting Cache Client...");
    	
        Client client = new Client();
        CacheServiceInterface cache3000 = new DistributedCacheService("http://localhost:3000");
        CacheServiceInterface cache3001 = new DistributedCacheService("http://localhost:3001");
        CacheServiceInterface cache3002 = new DistributedCacheService("http://localhost:3002");
        
        List<CacheServiceInterface> cacheServeList = new ArrayList<CacheServiceInterface>();
        cacheServeList.add(cache3000);
        cacheServeList.add(cache3001);
        cacheServeList.add(cache3002);
        
        HashMap<Long, String> dataMap = new HashMap<Long, String>();
        dataMap.put((long) 1, "A");
        dataMap.put((long) 2, "B");
        dataMap.put((long) 3, "C");
        dataMap.put((long) 4, "D");
        dataMap.put((long) 5, "E");
        dataMap.put((long) 6, "F");
        dataMap.put((long) 7, "G");
        dataMap.put((long) 8, "H");
        dataMap.put((long) 9, "I");
        dataMap.put((long) 10, "J");
        
        for(long keyCnt = 1; keyCnt < 11; keyCnt++ ) {
         	int bucket = client.getConsistentHash(keyCnt, cacheServeList.size());
         	System.out.println("Inserting key "+keyCnt+" into bucket "+bucket);
        	cacheServeList.get(bucket).put(keyCnt, dataMap.get(keyCnt));
        }
        System.out.println("Retrieving data...");
        for(long keyCnt = 1; keyCnt < 11; keyCnt++ ) {
        	int bucket = client.getConsistentHash(keyCnt, cacheServeList.size());
        	String value = cacheServeList.get(bucket).get(keyCnt);
        	System.out.println(keyCnt+" => "+value);
        }
        
        System.out.println("Existing Cache Client...");
    }

}
