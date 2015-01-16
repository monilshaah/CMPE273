package edu.sjsu.cmpe273.CRDTClient;

public class Client {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Cache Client...");
        
        CRDTClient crdtClient = new CRDTClient();
        boolean requestStatus = crdtClient.put(1, "a");
        if (requestStatus) {
        	// succeessful and get
        	System.out.println("First write completed...Thread sleeping for 30 secs...");
        	Thread.sleep(30000);
        	requestStatus = crdtClient.put(1, "b");
        	if (requestStatus) {
        		System.out.println("Second write completed...Thread sleeping for 30 secs...");
            	Thread.sleep(30000);
            	String value = crdtClient.get(1);
            	System.out.println("GET value "+value);
        	} else {
            	System.out.println("Second write failed...");
        	}
        } else {
        	// failed
        	System.out.println("First write failed...");
        }	
        System.out.println("Existing Cache Client...");
        
    }

}
