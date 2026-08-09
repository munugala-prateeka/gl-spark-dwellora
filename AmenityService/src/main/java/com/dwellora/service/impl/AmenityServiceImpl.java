package com.dwellora.service.impl;

import com.dwellora.dto.AmenityRequestDTO;
import com.dwellora.dto.AmenityResponseDTO;
import com.dwellora.entity.Amenity;
import com.dwellora.exception.AmenityException;
import com.dwellora.repository.AmenityRepository;
import com.dwellora.service.AmenityService;
import java.util.List;
import org.springframework.stereotype.Service;

/** Implementation of AmenityService handling business logic and persistence. */
@Service
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;

    public AmenityServiceImpl(AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }

    /** Creates a new amenity after validating uniqueness and operating hours. */
    @Override
    public AmenityResponseDTO addAmenity(Long apartmentId, AmenityRequestDTO request) {
        if (amenityRepository.existsByApartmentIdAndAmenityName(
                apartmentId, request.getAmenityName())) {
            throw new AmenityException(
                    "Amenity already exists with name: " + request.getAmenityName());
        }

        validateTimes(request);

        Amenity amenity = mapToEntity(apartmentId, request);
        Amenity saved = amenityRepository.save(amenity);

        return mapToResponse(saved);
    }

    /** Retrieves all amenities across all apartments. */
    @Override
    public List<AmenityResponseDTO> getAllAmenities() {
        return amenityRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    /** Retrieves an amenity by ID after verifying apartment ownership. */
    @Override
    public AmenityResponseDTO getAmenityById(Long amenityId, Long apartmentId) {
        Amenity amenity =
                amenityRepository
                        .findById(amenityId)
                        .orElseThrow(
                                () -> new AmenityException("Amenity not found with id: " + amenityId));

        validateOwnership(amenity, apartmentId);

        return mapToResponse(amenity);
    }

    /** Updates an existing amenity after validating ownership and opening/closing times. */
    @Override
    public AmenityResponseDTO updateAmenity(
            Long apartmentId, Long amenityId, AmenityRequestDTO request) {
        Amenity existing =
                amenityRepository
                        .findById(amenityId)
                        .orElseThrow(
                                () -> new AmenityException("Amenity not found with id: " + amenityId));

        validateOwnership(existing, apartmentId);
        validateTimes(request);

        existing.setAmenityName(request.getAmenityName());
        existing.setAmenityType(request.getAmenityType());
        existing.setCapacity(request.getCapacity());
        existing.setAvailable(
                request.getAvailable() != null ? request.getAvailable() : true);
        existing.setOpeningTime(request.getOpeningTime());
        existing.setClosingTime(request.getClosingTime());
        existing.setBookingPolicy(request.getBookingPolicy());
        existing.setSlotDurationMinutes(request.getSlotDurationMinutes());
        existing.setMaxBookingsPerDay(request.getMaxBookingsPerDay());
        existing.setMaxBookingsPerMonth(request.getMaxBookingsPerMonth());

        Amenity updated = amenityRepository.save(existing);

        return mapToResponse(updated);
    }

    /** Deletes an amenity after validating apartment ownership. */
    @Override
    public void deleteAmenity(Long amenityId, Long apartmentId) {
        Amenity existing =
                amenityRepository
                        .findById(amenityId)
                        .orElseThrow(
                                () -> new AmenityException("Amenity not found with id: " + amenityId));

        validateOwnership(existing, apartmentId);

        amenityRepository.delete(existing);
    }

    /** Retrieves all amenities belonging to a given apartment ID. */
    @Override
    public List<AmenityResponseDTO> getAmenitiesByApartment(Long apartmentId) {
        return amenityRepository.findByApartmentId(apartmentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void validateOwnership(Amenity amenity, Long apartmentId) {
        if (!amenity.getApartmentId().equals(apartmentId)) {
            throw new AmenityException("Amenity does not belong to your apartment.");
        }
    }

    private void validateTimes(AmenityRequestDTO request) {
        if (request.getOpeningTime() != null && request.getClosingTime() != null) {
            if (request.getOpeningTime().isAfter(request.getClosingTime())
                    || request.getOpeningTime().equals(request.getClosingTime())) {
                throw new AmenityException("Opening time must be strictly before closing time.");
            }
        }
    }

    private Amenity mapToEntity(Long apartmentId, AmenityRequestDTO dto) {
        Amenity amenity = new Amenity();
        amenity.setApartmentId(apartmentId);
        amenity.setAmenityName(dto.getAmenityName());
        amenity.setAmenityType(dto.getAmenityType());
        amenity.setCapacity(dto.getCapacity());
        amenity.setAvailable(dto.getAvailable() != null ? dto.getAvailable() : true);
        amenity.setOpeningTime(dto.getOpeningTime());
        amenity.setClosingTime(dto.getClosingTime());
        amenity.setBookingPolicy(dto.getBookingPolicy());
        amenity.setSlotDurationMinutes(dto.getSlotDurationMinutes());
        amenity.setMaxBookingsPerDay(dto.getMaxBookingsPerDay());
        amenity.setMaxBookingsPerMonth(dto.getMaxBookingsPerMonth());
        return amenity;
    }

    private AmenityResponseDTO mapToResponse(Amenity amenity) {
        return new AmenityResponseDTO(
                amenity.getAmenityId(),
                amenity.getApartmentId(),
                amenity.getAmenityName(),
                amenity.getAmenityType(),
                amenity.getCapacity(),
                amenity.getAvailable(),
                amenity.getOpeningTime(),
                amenity.getClosingTime(),
                amenity.getBookingPolicy(),
                amenity.getSlotDurationMinutes(),
                amenity.getMaxBookingsPerDay(),
                amenity.getMaxBookingsPerMonth());
    }
}