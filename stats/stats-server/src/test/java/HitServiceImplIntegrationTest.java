import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = EwmStatsServiceServerApp.class)
class HitServiceImplIntegrationTest {

    @Autowired
    private HitServiceImpl hitService;

    @Autowired
    private HitRepository hitRepository;

    private HitMapper hitMapper;

    private Hit hit;

    private HitDto hitDto;

    @BeforeEach
    void setUp() {
        hit = new Hit();
        hit.setApp("testapp");
        hit.setUri("test/uri");
        hit.setIp("1.1.1.1");
        hit = hitRepository.save(hit);
    }

    @Test
    void wordWithAllFieldsHit() {
        hitDto = HitMapper.mapToHitDto(hit);
        hit = hitRepository.findById(hit.getId()).orElseThrow();
        assertEquals(hit.getApp(), hitDto.getApp());
        assertEquals(hit.getUri(), hitDto.getUri());
        assertEquals(hit.getIp(), hitDto.getIp());
        assertEquals(hit.getId(), hitDto.getId());
    }

    @Test
    void updateUser_ShouldThrowExceptionWhenUserNotFound() {

        assertThrows(NullPointerException.class, () -> {
            hitService.postHit(null);
        });
    }
}