package com.dwellora.service.impl;

import com.dwellora.client.ApartmentClient;
import com.dwellora.dto.AmenityRequestDTO;
import com.dwellora.dto.AmenityResponseDTO;
import com.dwellora.entity.Amenity;
import com.dwellora.exception.AmenityException;
import com.dwellora.repository.AmenityRepository;
import com.dwellora.service.AmenityService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;
    private final ApartmentClient apartmentClient;

    public AmenityServiceImpl(
            AmenityRepository amenityRepository, ApartmentClient apartmentClient) {
        this.amenityRepository = amenityRepository;
        this.apartmentClient = apartmentClient;
    }

    @Override
    public AmenityResponseDTO addAmenity(AmenityRequestDTO request) {
        validateApartment(request.getApartmentId());

        if (amenityRepository.existsByApartmentIdAndAmenityName(
                request.getApartmentId(), request.getAmenityName())) {
            throw new AmenityException(
                    "Amenity already exists with name: " + request.getAmenityName());
        }

        validateTimes(request);

        Amenity amenity = mapToEntity(request);
        Amenity saved = amenityRepository.save(amenity);

        return mapToResponse(saved);
    }

    @Override
    public List<AmenityResponseDTO> getAllAmenities() {
        return amenityRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public AmenityResponseDTO getAmenityById(Integer amenityId) {
        Amenity amenity =
                amenityRepository
                        .findById(amenityId)
                        .orElseThrow(
                                () -> new AmenityException("Amenity not found with id: " + amenityId));
        return mapToResponse(amenity);
    }

    @Override
    public AmenityResponseDTO updateAmenity(Integer amenityId, AmenityRequestDTO request) {
        Amenity existing =
                amenityRepository
                        .findById(amenityId)
                        .orElseThrow(
                                () -> new AmenityException("Amenity not found with id: " + amenityId));

        validateApartment(request.getApartmentId());
        validateTimes(request);

        existing.setApartmentId(request.getApartmentId());
        existing.setAmenityName(request.getAmenityName());
        existing.setAmenityType(request.getAmenityType());
        existing.setCapacity(request.getCapacity());
        existing.setAvailable(request.getAvailable());
        existing.setOpeningTime(request.getOpeningTime());
        existing.setClosingTime(request.getClosingTime());
        existing.setBookingPolicy(request.getBookingPolicy());
        existing.setSlotDurationMinutes(request.getSlotDurationMinutes());
        existing.setMaxBookingsPerDay(request.getMaxBookingsPerDay());
        existing.setMaxBookingsPerMonth(request.getMaxBookingsPerMonth());

        Amenity updated = amenityRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void deleteAmenity(Integer amenityId) {
        if (!amenityRepository.existsById(amenityId)) {
            throw new AmenityException("Amenity not found with id: " + amenityId);
        }
        amenityRepository.deleteById(amenityId);
    }

    @Override
    public List<AmenityResponseDTO> getAmenitiesByApartment(Integer apartmentId) {
        return amenityRepository.findByApartmentId(apartmentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void validateApartment(Integer apartmentId) {
        try {
            Object apartment = apartmentClient.getApartmentById(apartmentId);
            if (apartment == null) {
                throw new AmenityException("Apartment not found with id: " + apartmentId);
            }
        } catch (Exception ex) {
            throw new AmenityException("Apartment not found or Apartment Service is unreachable");
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

    private Amenity mapToEntity(AmenityRequestDTO dto) {
        Amenity amenity = new Amenity();
        amenity.setApartmentId(dto.getApartmentId());
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