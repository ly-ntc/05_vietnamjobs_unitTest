package ApplyCVTest;
import com.demo.dtos.AccountDTO;
import com.demo.entities.Account;
import com.demo.repositories.AccountRepository;
import com.demo.services.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImpl_findByUsernameTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void testFindByUsername_ValidUsername_ReturnsAccountDTO() {
        String username = "testuser";
        Account mockAccount = new Account();
        mockAccount.setUsername(username);

        AccountDTO expectedDTO = new AccountDTO();
        expectedDTO.setUsername(username);

        when(accountRepository.findbyUsername(username)).thenReturn(mockAccount);
        when(modelMapper.map(mockAccount, AccountDTO.class)).thenReturn(expectedDTO);

        AccountDTO result = accountService.findByUsername(username);

        System.out.println("✅ [ValidUsername] Expected: " + expectedDTO.getUsername() + ", Actual: " + result.getUsername());

        assertNotNull(result);
        assertEquals(expectedDTO.getUsername(), result.getUsername());
    }

    @Test
    void testFindByUsername_UsernameIsNull_ReturnsNull() {
        AccountDTO result = accountService.findByUsername(null);

        System.out.println("✅ [NullUsername] Expected: null, Actual: " + result);

        assertNull(result);
    }

    @Test
    void testFindByUsername_UsernameIsEmpty_ReturnsNull() {
        AccountDTO result = accountService.findByUsername("");

        System.out.println("✅ [EmptyUsername] Expected: null, Actual: " + result);

        assertNull(result);
    }

    @Test
    void testFindByUsername_UsernameNotFound_ReturnsNull() {
        String username = "nonexistent";

        when(accountRepository.findbyUsername(username)).thenReturn(null);

        AccountDTO result = accountService.findByUsername(username);

        System.out.println("✅ [UsernameNotFound] Expected: null, Actual: " + result);

        assertNull(result);
    }

    @Test
    void testFindByUsername_MappingError_ThrowsException() {
        String username = "user1";
        Account account = new Account();
        account.setUsername(username);

        when(accountRepository.findbyUsername(username)).thenReturn(account);
        when(modelMapper.map(any(), eq(AccountDTO.class))).thenThrow(new RuntimeException("Mapping failed"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            accountService.findByUsername(username);
        });

        System.out.println("✅ [MappingError] Expected: Mapping failed, Actual: " + thrown.getMessage());
    }
}