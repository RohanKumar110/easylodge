package com.rohankumar.easylodge.controllers.admin.inventory;

import com.rohankumar.easylodge.dtos.inventory.InventoryRequest;
import com.rohankumar.easylodge.dtos.inventory.InventoryResponse;
import com.rohankumar.easylodge.dtos.wrapper.ApiResponse;
import com.rohankumar.easylodge.dtos.wrapper.PaginationResponse;
import com.rohankumar.easylodge.services.inventory.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponse<PaginationResponse<InventoryResponse>>> getAllInventoriesByRoom(
            @PathVariable UUID roomId,
            @Valid @ModelAttribute InventoryRequest inventoryRequest) {

        log.info("Attempting to get all inventories by room with id {}", roomId);
        PaginationResponse<InventoryResponse> paginationResponse = inventoryService
                .getAllInventoriesByRoom(roomId, inventoryRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), "Inventories Found Successfully", paginationResponse));
    }
}
