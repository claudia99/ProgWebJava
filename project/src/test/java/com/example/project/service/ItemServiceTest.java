package com.example.project.service;

import com.example.project.exception.EntityNotFoundException;
import com.example.project.model.Inventory;
import com.example.project.model.Item;
import com.example.project.repository.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item expectedItem;

    @BeforeEach
    void setUp() {
        expectedItem = Item.builder()
                .id(1L)
                .orderedQuantity(200L)
                .inventory(Inventory.builder().id(2L).build())
                .build();
    }

    @Test
    @DisplayName("create an item - happy flow")
    public void create() {
        Item item = Item.builder()
                .orderedQuantity(200L)
                .inventory(Inventory.builder().id(2L).build())
                .build();

        when(itemRepository.save(item)).thenReturn(expectedItem);

        Item result = itemService.create(item);

        assertEquals(expectedItem.getId(),result.getId());
        assertEquals(expectedItem.getOrderedQuantity(),result.getOrderedQuantity());
        assertEquals(expectedItem.getInventory(),result.getInventory());

        verify(itemRepository, times(1)).save(item);

    }

    @Test
    @DisplayName("find items by purchase id - happy flow ")
    public void test_findByPurchaseId_happyFlow() {
        Long id = expectedItem.getId();
        List<Item> itemList = new ArrayList<>();
        itemList.add(expectedItem);
        when(itemRepository.findByPurchaseId(id)).thenReturn(itemList);

        List<Item> result = itemService.findByPurchaseId(id);

        assertEquals(itemList.size(), result.size());
        assertEquals(expectedItem.getId(), result.stream().findFirst().get().getId());
        assertEquals(expectedItem.getOrderedQuantity(), result.stream().findFirst().get().getOrderedQuantity());
        assertEquals(expectedItem.getInventory(), result.stream().findFirst().get().getInventory());

        verify(itemRepository).findByPurchaseId(id);

    }

    @Test
    @DisplayName("delete item by id - happy flow")
    public void test_deleteById_happyFlow() {
        Long id = expectedItem.getId();

        when(itemRepository.existsById(id)).thenReturn(true);
        doNothing().when(itemRepository).deleteById(id);

        itemService.deleteById(id);

        verify(itemRepository).existsById(id);
        verify(itemRepository).deleteById(id);
    }

    @Test
    @DisplayName("delete item by id -  item does not exist in the database")
    public void test_deleteById_throwsEntityNotFoundException_whenItemNotFound() {
        Long id = expectedItem.getId();

        when(itemRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () ->
                itemService.deleteById(id)
        );
        assertThat(ex.getMessage()).isEqualTo(String.format("The item with id = %s does not exist in the database.",id.toString()));

        verify(itemRepository).existsById(id);
        verify(itemRepository, times(0)).deleteById(id);
    }
}