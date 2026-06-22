package com.hotelreservation.catalog.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hotelreservation.catalog.constants.ErrorConstants;
import com.hotelreservation.catalog.exceptions.ImageUploadException;
import com.hotelreservation.catalog.exceptions.NotFoundException;
import com.hotelreservation.catalog.mappers.ImageMapper;
import com.hotelreservation.catalog.models.dto.response.image.ImageHotelResponseDto;
import com.hotelreservation.catalog.models.entities.Hotel;
import com.hotelreservation.catalog.models.entities.ImageHotel;
import com.hotelreservation.catalog.repositories.HotelRepository;
import com.hotelreservation.catalog.repositories.ImageHotelRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service responsible for managing hotel image assets, handling uploads, deletions, and showcase
 * updates via Cloudinary cloud storage integration.
 */
@Service
@RequiredArgsConstructor
public class ImageService {

  private final HotelRepository hotelRepository;
  private final ImageHotelRepository imageHotelRepository;
  private final ImageMapper imageMapper;
  private final Cloudinary cloudinary;

  /**
   * Uploads a batch of multipart files to Cloudinary, maps the successful secure asset metadata,
   * and links the resulting image records to a target hotel.
   *
   * @param hotelId The unique identifier of the destination hotel.
   * @param files A collection of file payloads to process and upload.
   * @return A list of mapped response DTOs for the successfully registered images.
   * @throws NotFoundException If the target hotel does not exist.
   * @throws ImageUploadException If an infrastructural error or timeout occurs during the
   *     Cloudinary transport layer.
   * @throws IOException If raw file binary stream extraction fails.
   */
  @Transactional
  public List<ImageHotelResponseDto> uploadImages(UUID hotelId, List<MultipartFile> files)
      throws IOException {

    Hotel hotel =
        hotelRepository
            .findById(hotelId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ErrorConstants.Error.HOTEL_NOT_FOUND_PREFIX
                            + hotelId
                            + ErrorConstants.Error.NOT_FOUND_SUFFIX));

    List<ImageHotel> images = new ArrayList<>();

    for (MultipartFile multipartFile : files) {
      try {

        Map uploadResult =
            cloudinary
                .uploader()
                .upload(multipartFile.getBytes(), ObjectUtils.asMap("folder", "hotels/" + hotelId));

        String url = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        images.add(new ImageHotel(hotel, url, publicId, false));
      } catch (IOException e) {
        throw new ImageUploadException("Failed to upload images to Cloudinary");
      }
    }

    List<ImageHotel> saved = imageHotelRepository.saveAll(images);

    return saved.stream().map(imageMapper::toResponseDto).toList();
  }

  /**
   * Removes an image from both the local repository ecosystem and the Cloudinary storage bucket.
   * Verifies that the targeted image belongs to the specified hotel before deletion.
   *
   * @param hotelId The unique identifier of the parent hotel.
   * @param imageId The unique identifier of the target image to remove.
   * @throws NotFoundException If the image record does not exist or does not belong to the given
   *     hotel.
   * @throws ImageUploadException If Cloudinary remote asset destruction fails.
   */
  @Transactional
  public void deleteImage(UUID hotelId, UUID imageId) {

    ImageHotel imageHotel =
        imageHotelRepository
            .findById(imageId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ErrorConstants.Error.IMAGE_NOT_FOUND_PREFIX
                            + imageId
                            + ErrorConstants.Error.NOT_FOUND_SUFFIX));

    if (!imageHotel.getHotel().getId().equals(hotelId)) {
      throw new NotFoundException(
          ErrorConstants.Error.IMAGE_NOT_FOUND_PREFIX
              + imageId
              + ErrorConstants.Error.NOT_FOUND_SUFFIX_IN_
              + " hotel with id "
              + hotelId);
    }

    try {
      cloudinary.uploader().destroy(imageHotel.getPublicId(), ObjectUtils.emptyMap());
    } catch (IOException e) {
      throw new ImageUploadException("Failed to delete image from Cloudinary");
    }
    imageHotelRepository.delete(imageHotel);
  }

  /**
   * Designates a specific image as the primary showcase graphic for a hotel. Automatically unmarks
   * any pre-existing main image assigned to that hotel beforehand.
   *
   * @param hotelId The unique identifier of the parent hotel.
   * @param imageId The unique identifier of the image to promote.
   * @return The updated and mapped image response profile.
   * @throws NotFoundException If the hotel or image do not exist, or if the target image is not
   *     linked to the hotel.
   */
  @Transactional
  public ImageHotelResponseDto setMainImage(UUID hotelId, UUID imageId) {

    if (!hotelRepository.existsById(hotelId)) {
      throw new NotFoundException(
          ErrorConstants.Error.HOTEL_NOT_FOUND_PREFIX
              + hotelId
              + ErrorConstants.Error.NOT_FOUND_SUFFIX);
    }

    imageHotelRepository
        .findMainImageByHotelId(hotelId)
        .ifPresent(
            current -> {
              current.unmarkAsMain();
              imageHotelRepository.save(current);
            });

    ImageHotel newMain =
        imageHotelRepository
            .findById(imageId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ErrorConstants.Error.IMAGE_NOT_FOUND_PREFIX
                            + imageId
                            + ErrorConstants.Error.NOT_FOUND_SUFFIX));

    if (!newMain.getHotel().getId().equals(hotelId)) {
      throw new NotFoundException(
          ErrorConstants.Error.IMAGE_NOT_FOUND_PREFIX
              + imageId
              + ErrorConstants.Error.NOT_FOUND_SUFFIX_IN_
              + " hotel with id "
              + hotelId);
    }

    newMain.markAsMain();
    return imageMapper.toResponseDto(imageHotelRepository.save(newMain));
  }
}
