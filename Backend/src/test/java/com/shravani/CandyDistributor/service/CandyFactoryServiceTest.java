package com.shravani.CandyDistributor.service;

import com.shravani.CandyDistributor.dto.CandyFactoryDTO;
import com.shravani.CandyDistributor.model.CandyFactory;
import com.shravani.CandyDistributor.repository.CandyFactoryRepository;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CandyFactoryServiceTest {

    private CandyFactoryRepository mockRepo;
    private CandyFactoryService service;

    @Before
    public void setUp() {
        mockRepo = mock(CandyFactoryRepository.class);
        service = new CandyFactoryService(mockRepo);
    }

    @Test
    public void testGetFactoryById_ReturnsCorrectDTO() {
        CandyFactory factory = new CandyFactory();
        factory.setFactory("SweetLand");
        factory.setLatitude(10.0);
        factory.setLongitude(20.0);

        when(mockRepo.findById("SweetLand")).thenReturn(Optional.of(factory));

        CandyFactoryDTO result = service.getFactoryById("SweetLand");

        assertNotNull(result);
        assertEquals("SweetLand", result.getFactory());
        assertEquals(10.0, result.getLatitude(), 0.01);
        assertEquals(20.0, result.getLongitude(), 0.01);
    }

    @Test(expected = RuntimeException.class)
    public void testGetFactoryById_ThrowsExceptionWhenNotFound() {
        when(mockRepo.findById("Unknown")).thenReturn(Optional.empty());

        service.getFactoryById("Unknown");
    }
}
