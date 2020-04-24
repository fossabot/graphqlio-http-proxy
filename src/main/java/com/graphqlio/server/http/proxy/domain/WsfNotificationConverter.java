package com.graphqlio.server.http.proxy.domain;

import com.graphqlio.wsf.converter.WsfConverter;
import com.graphqlio.wsf.domain.WsfFrameType;

/**
 * Class used to convert notification-frames
 *
 */
public class WsfNotificationConverter extends WsfConverter {

  public WsfNotificationConverter() {
    super(WsfFrameType.GRAPHQLNOTIFIER);
  }
}
