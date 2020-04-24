package com.graphqlio.server.http.proxy.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphqlio.wsf.converter.WsfConverter;
import com.graphqlio.wsf.domain.WsfFrame;
import com.graphqlio.wsf.domain.WsfFrameType;

/**
 * Class used to convert request-frames
 *
 */
public class WsfRequestConverter extends WsfConverter {

	private final Logger logger = LoggerFactory.getLogger(WsfConverter.class);
	
	
	public WsfRequestConverter() {
		super(WsfFrameType.GRAPHQLREQUEST);
	}
  
	@Override
	public String convert (WsfFrame message){
	  
	    if (message.getType() != frameType) {
	        logger.warn(
	            String.format(
	                "WsfConverter: Expected type (%s), got type (%s)", frameType, message.getType()));
	        /*
	         * not necessary to throw an exception here. converter is able to handle message
	         * type
	         *
	         */
	        // throw new WsfException();
	      }
	  
		final String asString = 
		        "["
		            + message.getFid()
		            + ","
		            + message.getRid()
		            + ","
		            + "\""
		            + message.getType()
		            + "\""
		            + ","
		            + "{"
		            + "\""
		            + "query"
		            + "\""
		            + ":"
		            + "\""
		            + message.getData()
		            + "\""
		            + "}"
		            + "]";

		return asString;
	}
	
}
