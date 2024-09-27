package net.minestom.server.network;

import java.nio.ByteBuffer;

public final class NioBufferExtractor {

  private NioBufferExtractor() {}

  public static ByteBuffer from(NetworkBuffer buffer) {
    return buffer.nioBuffer;
  }
}