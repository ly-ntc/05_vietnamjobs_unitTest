package ViewPostingApplied;

import com.demo.dtos.PostingDTO;
import com.demo.entities.Postings;
import com.demo.repositories.PostingRepository;
import com.demo.services.PostingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class PostingServiceImpl_FindAllLimitTest {
    @Mock
    private PostingRepository postingRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PostingServiceImpl postingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void TC58_findAllLimit_WithValidLimit_ReturnsDTOList() {
        // Arrange
        int limit = 2;
        Postings post1 = new Postings(); post1.setId(1);
        Postings post2 = new Postings(); post2.setId(2);
        List<Postings> entityList = Arrays.asList(post1, post2);

        PostingDTO dto1 = new PostingDTO(); dto1.setId(1);
        PostingDTO dto2 = new PostingDTO(); dto2.setId(2);
        List<PostingDTO> dtoList = Arrays.asList(dto1, dto2);

        when(postingRepository.findAllLimit(limit)).thenReturn(entityList);
        when(modelMapper.map(eq(entityList), any(Type.class))).thenReturn(dtoList);

        // Act
        List<PostingDTO> result = postingService.findAllLimit(limit);

        // Assert
        System.out.println("✅ Mong muốn: Kích thước = 2, ID = [1, 2]");
        System.out.print("📤 Thực tế: Kích thước = " + result.size() + ", ID = [");
        result.forEach(p -> System.out.print(p.getId() + " "));
        System.out.println("]");

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
    }

    @Test
    void TC59_findAllLimit_WithZeroLimit_ReturnsEmptyList() {
        // Arrange
        int limit = 0;
        List<Postings> entityList = List.of();
        List<PostingDTO> dtoList = List.of();

        when(postingRepository.findAllLimit(limit)).thenReturn(entityList);
        when(modelMapper.map(eq(entityList), any(Type.class))).thenReturn(dtoList);

        // Act
        List<PostingDTO> result = postingService.findAllLimit(limit);

        // Assert
        System.out.println("✅ Mong muốn: Danh sách rỗng");
        System.out.println("📤 Thực tế: Kích thước = " + result.size());

        assertTrue(result.isEmpty());
    }

    @Test
    void TC60_findAllLimit_RepositoryReturnsNull_ReturnsNull() {
        // Arrange
        int limit = 5;
        when(postingRepository.findAllLimit(limit)).thenReturn(null);
        when(modelMapper.map(isNull(), any(Type.class))).thenReturn(null);

        // Act
        List<PostingDTO> result = postingService.findAllLimit(limit);

        // Assert
        System.out.println("✅ Mong muốn: Trả về null");
        System.out.println("📤 Thực tế: " + result);

        assertNull(result);
    }
    @Test
    void TC61_findAllLimit_MapperThrowsException_ThrowsRuntime() {
        // Arrange
        int limit = 3;
        List<Postings> entityList = List.of(new Postings());
        when(postingRepository.findAllLimit(limit)).thenReturn(entityList);
        when(modelMapper.map(eq(entityList), any(Type.class))).thenThrow(new RuntimeException("Mapping failed"));

        // Act & Assert
        System.out.println("✅ Mong muốn: Ném ra RuntimeException với thông báo \"Mapping failed\"");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            postingService.findAllLimit(limit);
        });

        System.out.println("📤 Thực tế: " + ex.getMessage());
        assertEquals("Mapping failed", ex.getMessage());
    }

}
