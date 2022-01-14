package com.example.project.controller;

import com.example.project.dto.ClientDto;
import com.example.project.dto.PurchaseDto;
import com.example.project.exception.EntityNotFoundException;
import com.example.project.mapper.PurchaseMapper;
import com.example.project.model.Client;
import com.example.project.model.Purchase;
import com.example.project.service.PurchaseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseControllerTest {

    @Mock
    private PurchaseService purchaseService;

    @Spy
    private PurchaseMapper purchaseMapper;

    @InjectMocks
    private PurchaseController purchaseController;

    private Purchase expectedPurchase;
    private PurchaseDto expectedDto;

    @BeforeEach
    void setUp() {
        expectedPurchase = Purchase.builder()
                .id(1L)
                .price(12.123F)
                .time(LocalDateTime.now())
                .client(Client.builder().id(1L).build())
                //.products()
                .build();
        expectedDto = PurchaseDto.builder()
                .id(1L)
                .price(12.123F)
                .time(LocalDateTime.now())
                .clientDto(ClientDto.builder().id(1L).build())
                //.products()
                .build();
    }

    @Test
    @DisplayName("get all purchases - happy flow")
    public void test_findAll_happyFlow() {
        List<Purchase> purchaseList = new ArrayList<>();
        purchaseList.add(expectedPurchase);

        when(purchaseService.findAll()).thenReturn(purchaseList);
        ResponseEntity<List<PurchaseDto>> result = purchaseController.findAll();

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(purchaseMapper.toDto(purchaseList));

        verify(purchaseService).findAll();
        verify(purchaseMapper, times(2)).toDto(purchaseList);
    }

    @Test
    @DisplayName("get purchase by id - happy flow")
    void test_findPurchaseById_happyFlow() {
        Long id = expectedPurchase.getId();

        when(purchaseService.findById(id)).thenReturn(expectedPurchase);


        ResponseEntity<PurchaseDto> result = purchaseController.findById(id);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(purchaseMapper.toDto(expectedPurchase));

        verify(purchaseService).findById(id);
        verify(purchaseMapper, times(2)).toDto(expectedPurchase);
        verify(purchaseMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("get purchase by id - medicine does not exist in database")
    public void test_findPurchaseById_throwsEntityNotFoundException_whenPurchaseNotFound() {
        Long id = expectedPurchase.getId();

        when(purchaseService.findById(id)).thenThrow(new EntityNotFoundException(String.format("The purchase with id = %s does not exist in the database.",id.toString())));

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> purchaseController.findById(id));

        assertThat(ex.getMessage()).isEqualTo(String.format("The purchase with id = %s does not exist in the database.",id.toString()));

        verify(purchaseService).findById(id);
        verify(purchaseMapper, times(0)).toDto(expectedPurchase);
        verify(purchaseMapper, times(0)).toEntity(expectedDto);
    }

    @Test
    @DisplayName("find purchase by client id - happy flow")
    public void test_findByClient_happyFlow() {
        List<Purchase> purchaseList = new ArrayList<>();
        purchaseList.add(expectedPurchase);

        when(purchaseService.findByClient(expectedPurchase.getClient().getId())).thenReturn(purchaseList);

        ResponseEntity<List<PurchaseDto>> result = purchaseController.findByClient(expectedPurchase.getClient().getId());

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(purchaseMapper.toDto(purchaseList));

        verify(purchaseService).findByClient(expectedPurchase.getClient().getId());
        verify(purchaseMapper, times(2)).toDto(purchaseList);

    }

    @Test
    @DisplayName("add a purchase - happy flow")
    public void test_createPurchase_happyFlow() {
        PurchaseDto purchaseDto = PurchaseDto.builder()
                .price(12.123F)
                .time(LocalDateTime.now())
                .clientDto(ClientDto.builder().id(1L).build())
                //.products()
                .build();
        when(purchaseService.create(purchaseMapper.toEntity(purchaseDto))).thenReturn(expectedPurchase);

        ResponseEntity<PurchaseDto> result = purchaseController.createPurchase(purchaseDto);

        assertThat(result.getStatusCodeValue()).isEqualTo(201);
        assertThat(result.getBody()).isEqualTo(purchaseMapper.toDto(expectedPurchase));

        verify(purchaseService, times(1)).create(purchaseMapper.toEntity(purchaseDto));
        verify(purchaseMapper, times(2)).toDto(expectedPurchase);
        verify(purchaseMapper, times(3)).toEntity(purchaseDto);

    }

    @Test
    @DisplayName("delete purchase - happy flow")
    public void test_deletePurchase_happyFlow() {
        Long id = expectedPurchase.getId();

        doNothing().when(purchaseService).deleteById(id);

        ResponseEntity<Void> result = purchaseController.deletePurchase(id);

        assertThat(result.getStatusCodeValue()).isEqualTo(204);

        verify(purchaseService).deleteById(id);
        verify(purchaseMapper, times(0)).toDto(expectedPurchase);
        verify(purchaseMapper, times(0)).toEntity(expectedDto);
    }
}