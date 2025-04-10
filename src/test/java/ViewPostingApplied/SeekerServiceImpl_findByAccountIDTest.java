package ViewPostingApplied;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.demo.dtos.SeekerDTO;
import com.demo.entities.Seeker;
import com.demo.repositories.SeekerRepository;

import com.demo.services.SeekerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
public class SeekerServiceImpl_findByAccountIDTest {
    @Mock
    private SeekerRepository seekerRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private SeekerServiceImpl seekerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ TC52: Tìm thấy seeker theo account_id
    @Test
    void TC52_findByAccountID_ReturnsSeekerDTO_WhenSeekerExists() {
        int accId = 1;
        Seeker seeker = new Seeker();
        seeker.setId(1);

        SeekerDTO expectedDTO = new SeekerDTO();
        expectedDTO.setId(1);

        when(seekerRepository.findByAccountID(accId)).thenReturn(seeker);
        when(mapper.map(seeker, SeekerDTO.class)).thenReturn(expectedDTO);

        SeekerDTO result = seekerService.findByAccountID(accId);

        System.out.println("\u2705 Mong muốn: ID = 1");
        System.out.println("\ud83d\udce4 Thực tế: ID = " + result.getId());

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    // ✅ TC53: Không tìm thấy seeker, trả về null → gây NullPointerException
//    @Test
//    void TC53_findByAccountID_ThrowsException_WhenSeekerIsNull() {
//        int accId = 2;
//        when(seekerRepository.findByAccountID(accId)).thenReturn(null);
//
//        System.out.println("\u2705 Mong muốn: Ném ra NullPointerException");
//
//        assertThrows(NullPointerException.class, () -> {
//            seekerService.findByAccountID(accId);
//        });
//    }

    // ✅ TC54: Mapper trả về null
    @Test
    void TC54_findByAccountID_ReturnsNull_WhenMapperFails() {
        int accId = 3;
        Seeker seeker = new Seeker();
        seeker.setId(3);

        when(seekerRepository.findByAccountID(accId)).thenReturn(seeker);
        when(mapper.map(seeker, SeekerDTO.class)).thenReturn(null);

        SeekerDTO result = seekerService.findByAccountID(accId);

        System.out.println("\u2705 Mong muốn: null");
        System.out.println("\ud83d\udce4 Thực tế: " + result);

        assertNull(result);
    }
}
