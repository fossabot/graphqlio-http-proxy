package com.graphqlio.server.http.proxy.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
 

/// ToDo -- Timeout handling
/// ToDo -- Stop any blocking wait when server shuts down 
/// ToDo -- Cleanup 
/// ToDo -- proper exception handling

public class BlockingMap<K, V> {
	
    private Map<K, V> map = new ConcurrentHashMap<>();

    public V getOrWait(K key) {

        V value = null;
        synchronized (map) {
        	while(true) {
            if((value = map.get(key)) != null) {
            	map.remove(key);
            	return value;// work with value 
              } else {
                try {
					map.wait();
				} catch (InterruptedException e) {
					// ToDo -- Exception handling
					e.printStackTrace();
				} 
             }
           }
        }    	
    }
    
    
    public V putAndSignal (K key, V value) {   	
    	synchronized (map) {    	
	    	map.put(key,value);
	    	map.notifyAll(); // or notify().
    	}
    	return value;
    }
    
}

