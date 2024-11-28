package com.mq.back.services;

import com.mq.back.dto.PartnerDto;
import com.mq.back.entities.Partner;
import com.mq.back.exceptions.custom.EntityNotFoundException;
import com.mq.back.repositories.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PartnerService {

    public static final int PAGE_SIZE = 10;

    private final PartnerRepository partnerRepository;

    @Autowired
    public PartnerService(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    public Page<Partner> getPartners(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        return partnerRepository.findAll(pageable);
    }

    public void deletePartnerById(UUID partnerId) {
        Partner partner = findPartnerById(partnerId);
        partnerRepository.delete(partner);
    }

    public Partner addPartner(PartnerDto partnerDto) {
        Partner partner = new Partner();
        partner.setAlias(partnerDto.alias());
        partner.setType(partnerDto.type());
        partner.setDirection(partnerDto.direction());
        partner.setApplication(partnerDto.application());
        partner.setProcessedFlowType(partnerDto.processedFlowType());
        partner.setDescription(partnerDto.description());
        partner.setCreatedAt(partnerDto.createdAt() != null ? partnerDto.createdAt() : LocalDateTime.now());
        return partnerRepository.save(partner);
    }

    private Partner findPartnerById(UUID partnerId) {
        return partnerRepository
                .findById(partnerId)
                .orElseThrow(() -> new EntityNotFoundException(Partner.class, "ID"));
    }
}
