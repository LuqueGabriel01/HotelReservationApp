package com.hotelreservation.catalog.exceptions;

/** Exception thrown when the images fail to upload. */
public class ImageUploadException extends RuntimeException {
  public ImageUploadException(String message) {
    super(message);
  }
}
