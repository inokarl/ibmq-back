package com.mq.back.services;

import com.mq.back.dto.PartnerDto;
import com.mq.back.entities.Direction;
import com.mq.back.entities.Partner;
import com.mq.back.entities.ProcessedFlowType;
import com.mq.back.repositories.PartnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class PartnerServiceTest {

    @Mock
    private PartnerRepository partnerRepository;

    @InjectMocks
    private PartnerService partnerService;

    @Test
    void getPartners_ShouldReturnPageOfPartners() {
        // Arrange
        Pageable pageable = PageRequest.of(0, PartnerService.PAGE_SIZE);
        List<Partner> partners = List.of(new Partner(), new Partner());
        Page<Partner> partnerPage = new PageImpl<>(partners);
        Mockito.when(partnerRepository.findAll(pageable)).thenReturn(partnerPage);

        // Act
        Page<Partner> result = partnerService.getPartners(0);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        Mockito.verify(partnerRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    void deletePartnerById_ShouldDeleteExistingPartner() {
        // Arrange
        UUID partnerId = UUID.randomUUID();
        Partner partner = new Partner();
        partner.setId(partnerId);
        Mockito.when(partnerRepository.findById(partnerId)).thenReturn(Optional.of(partner));

        // Act
        partnerService.deletePartnerById(partnerId);

        // Assert
        Mockito.verify(partnerRepository, Mockito.times(1)).delete(partner);
    }

    @Test
    void addPartner_ShouldSaveAndReturnPartner() {
        // Arrange
        PartnerDto partnerDto = new PartnerDto(
                "Test Alias",
                "Test Type",
                Direction.INBOUND,
                "Test Application",
                ProcessedFlowType.MESSAGE,
                "Test Description",
                null
        );

        Partner savedPartner = new Partner();
        savedPartner.setId(UUID.randomUUID());
        savedPartner.setAlias(partnerDto.alias());
        savedPartner.setType(partnerDto.type());
        savedPartner.setDirection(partnerDto.direction());
        savedPartner.setApplication(partnerDto.application());
        savedPartner.setProcessedFlowType(partnerDto.processedFlowType());
        savedPartner.setDescription(partnerDto.description());
        savedPartner.setCreatedAt(LocalDateTime.now());

        Mockito.when(partnerRepository.save(Mockito.any(Partner.class))).thenReturn(savedPartner);

        // Act
        Partner result = partnerService.addPartner(partnerDto);

        // Assert
        assertNotNull(result);
        assertEquals(partnerDto.alias(), result.getAlias());
        assertEquals(partnerDto.type(), result.getType());
        Mockito.verify(partnerRepository, Mockito.times(1)).save(Mockito.any(Partner.class));
    }
}