package com.dwellora.service.impl;

import com.dwellora.dto.ApartmentRequestDTO;
import com.dwellora.dto.ApartmentResponseDTO;
import com.dwellora.entity.Apartment;
import com.dwellora.enums.Status;
import com.dwellora.exception.ApartmentException;
import com.dwellora.repository.ApartmentRepository;
import com.dwellora.service.ApartmentService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ApartmentServiceImpl implements ApartmentService {

    private final ApartmentRepository apartmentRepository;

    public ApartmentServiceImpl(ApartmentRepository apartmentRepository) {
        this.apartmentRepository = apartmentRepository;
    }

    @Override
    public ApartmentResponseDTO addApartment(ApartmentRequestDTO dto) {
        Apartment apartment = mapToEntity(dto);
        apartment.setStatus(Status.ACTIVE);
        Apartment saved = apartmentRepository.save(apartment);
        return mapToResponse(saved);
    }

    @Override
    public List<ApartmentResponseDTO> getAllApartments() {
        return apartmentRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public ApartmentResponseDTO getApartmentById(Integer apartmentId) {
        Apartment apartment =
                apartmentRepository
                        .findById(apartmentId)
                        .orElseThrow(
                                () -> new ApartmentException("Apartment not found with id: " + apartmentId));
        return mapToResponse(apartment);
    }

    @Override
    public ApartmentResponseDTO updateApartment(Integer apartmentId, ApartmentRequestDTO dto) {
        Apartment existing =
                apartmentRepository
                        .findById(apartmentId)
                        .orElseThrow(
                                () -> new ApartmentException("Apartment not found with id: " + apartmentId));

        existing.setApartmentName(dto.getApartmentName());
        existing.setAddress(dto.getAddress());
        existing.setCity(dto.getCity());
        existing.setState(dto.getState());
        existing.setPincode(dto.getPincode());
        existing.setTotalBlocks(dto.getTotalBlocks());
        existing.setTotalUnits(dto.getTotalUnits());

        Apartment updated = apartmentRepository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void deleteApartment(Integer apartmentId) {
        if (!apartmentRepository.existsById(apartmentId)) {
            throw new ApartmentException("Cannot delete. Apartment not found with id: " + apartmentId);
        }
        apartmentRepository.deleteById(apartmentId);
    }

    private ApartmentResponseDTO mapToResponse(Apartment apartment) {
        return new ApartmentResponseDTO(
                apartment.getApartmentId(),
                apartment.getApartmentName(),
                apartment.getAddress(),
                apartment.getCity(),
                apartment.getState(),
                apartment.getPincode(),
                apartment.getTotalBlocks(),
                apartment.getTotalUnits(),
                apartment.getStatus());
    }

    private Apartment mapToEntity(ApartmentRequestDTO dto) {
        Apartment apartment = new Apartment();
        apartment.setApartmentName(dto.getApartmentName());
        apartment.setAddress(dto.getAddress());
        apartment.setCity(dto.getCity());
        apartment.setState(dto.getState());
        apartment.setPincode(dto.getPincode());
        apartment.setTotalBlocks(dto.getTotalBlocks());
        apartment.setTotalUnits(dto.getTotalUnits());
        return apartment;
    }
}