package com.graphqlio.server.http.proxy.domain;

import com.graphqlio.wsf.converter.WsfConverter;
import com.graphqlio.wsf.domain.WsfFrameType;

/**
 * Class used to convert response-frames
 *
 */
public class WsfResponseConverter extends WsfConverter {

  public WsfResponseConverter() {
    super(WsfFrameType.GRAPHQLRESPONSE);
  }
}
