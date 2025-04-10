package MangerApplyTest;

import com.demo.dtos.ApplicationHistoryDTO;
import com.demo.entities.ApplicationHistory;
import com.demo.repositories.ApplicationHistoryRepository;
import com.demo.services.ApplicationHistoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class ApplicationHistoryServiceImpl_FindByIDTest {

    @Mock
    private ApplicationHistoryRepository applicationHistoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ApplicationHistoryServiceImpl service;

    @Test
    void testFindByID_WithValidId_ReturnsDTO() {
        int id = 1;

        ApplicationHistory entity = new ApplicationHistory();
        entity.setId(id);
        entity.setResult(0);

        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setId(id);
        dto.setResult(0);

        // Mock đúng tên repository
        when(applicationHistoryRepository.findById(id)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, ApplicationHistoryDTO.class)).thenReturn(dto);

        ApplicationHistoryDTO result = service.findByID(id);

        System.out.println("✅ [testFindByID_WithValidId_ReturnsDTO]");
        System.out.println("Mong muốn: ID = " + id + ", Result = 0");
        System.out.println("Thực tế  : ID = " + result.getId() + ", Result = " + result.getResult());

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(0, result.getResult());
    }

    @Test
    void testFindByID_WithInvalidId_ThrowsException() {
        int invalidId = 999;
        when(applicationHistoryRepository.findById(invalidId)).thenReturn(Optional.empty());

        // ✅ In ra log thử
        System.out.println("✅ [testFindByID_WithInvalidId_ThrowsException]");
        System.out.println("Mong muốn: Ném NoSuchElementException");

        Exception exception = assertThrows(
                java.util.NoSuchElementException.class,
                () -> service.findByID(invalidId)
        );

        System.out.println("Thực tế  : " + exception.getClass().getSimpleName() + " - " + exception.getMessage());
    }

    @Test
    void testFindByID_EntityMappedToDTOFields() {
        int id = 2;
        ApplicationHistory entity = new ApplicationHistory();
        entity.setId(id);
        entity.setResult(1);

        ApplicationHistoryDTO dto = new ApplicationHistoryDTO();
        dto.setId(id);
        dto.setResult(1);

        when(applicationHistoryRepository.findById(id)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, ApplicationHistoryDTO.class)).thenReturn(dto);

        ApplicationHistoryDTO result = service.findByID(id);

        // ✅ In kết quả kiểm tra map
        System.out.println("✅ [testFindByID_EntityMappedToDTOFields]");
        System.out.println("Mong muốn: DTO ID = " + dto.getId() + ", Result = " + dto.getResult());
        System.out.println("Thực tế  : DTO ID = " + result.getId() + ", Result = " + result.getResult());

        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getResult(), result.getResult());
    }
}
