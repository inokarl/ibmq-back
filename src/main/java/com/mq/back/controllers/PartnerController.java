package com.mq.back.controllers;


import com.mq.back.dto.PartnerDto;
import com.mq.back.entities.Partner;
import com.mq.back.services.PartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/partners")
public class PartnerController {

    private final PartnerService partnerService;

    @Autowired
    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }


    /**
     * Retrieve a list of all partners.
     *
     * @param page The page number to retrieve (default is 0).
     * @return A page of partners.
     */
    @Operation(summary = "Get all partners", description = "Retrieve a list of all partners.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Partners retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @GetMapping("/all")
    public Page<Partner> getPartners(@RequestParam(defaultValue = "0") int page) {
        return partnerService.getPartners(page);
    }


    /**
     * Add a new partner.
     *
     * @param partnerDto Data Transfer Object (DTO) containing partner details.
     * @return The created partner.
     */
    @Operation(summary = "Add a new partner", description = "Create a new partner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Partner created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid partner data")
    })
    @PostMapping("/add")
    public ResponseEntity<Partner> addPartner(@Valid @RequestBody PartnerDto partnerDto) {
        Partner partner = partnerService.addPartner(partnerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(partner);
    }

    /**
     * Delete a partner by ID.
     *
     * @param partnerId The ID of the partner to be deleted.
     * @return HTTP status 204 (No Content) if the partner was successfully deleted.
     */
    @Operation(summary = "Delete a partner", description = "Delete a partner by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Partner deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Partner not found")
    })
    @DeleteMapping("/delete/{partnerId}")
    public ResponseEntity<Void> deletePartner(@PathVariable UUID partnerId) {
        partnerService.deletePartnerById(partnerId);
        return ResponseEntity.noContent().build();
    }
}
