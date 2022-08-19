// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: com/toutiao/melon/rpc/master.proto

package com.toutiao.melon.rpc;

public interface ProvideJarResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:com.toutiao.melon.rpc.ProvideJarResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Leading messages are jar_bytes then a message if needed
   * </pre>
   *
   * <code>bytes jar_bytes = 1;</code>
   * @return The jarBytes.
   */
  com.google.protobuf.ByteString getJarBytes();

  /**
   * <pre>
   * Error message, should only be set when error occurs
   * </pre>
   *
   * <code>string message = 2;</code>
   * @return The message.
   */
  String getMessage();
  /**
   * <pre>
   * Error message, should only be set when error occurs
   * </pre>
   *
   * <code>string message = 2;</code>
   * @return The bytes for message.
   */
  com.google.protobuf.ByteString
      getMessageBytes();

  public ProvideJarResponse.DataCase getDataCase();
}