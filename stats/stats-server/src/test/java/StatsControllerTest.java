import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StatsControllerTest {
    @Mock
    private HitServiceImpl hitService;

    @InjectMocks
    private StatsController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private HitDto hitDto;

    private ViewStatsDto dto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        hitDto = new HitDto(
                1,
                "test-service",
                "test/1",
                "1.1.1.1",
                null);

        dto = new ViewStatsDto(
                "test-service",
                "test/1",
                1L);
    }

    @Test
    void postHit() throws Exception {
        when(hitService.postHit(any()))
                .thenReturn(hitDto);

        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(hitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(hitDto.getId()), Integer.class))
                .andExpect(jsonPath("$.app", is(hitDto.getApp())))
                .andExpect(jsonPath("$.uri", is(hitDto.getUri())))
                .andExpect(jsonPath("$.ip", is(hitDto.getIp())));
    }

    @Test
    void getStats() throws Exception {
        when(hitService.getStats(any(), any(), anyList(), anyBoolean()))
                .thenReturn(List.of(dto));

        mvc.perform(get("/stats?start=" + LocalDateTime.now().minusYears(1) +"&end=" + LocalDateTime.now().plusYears(1)
                        + "&uris=test/1&unique=false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hits", is(dto.getHits()), Long.class))
                .andExpect(jsonPath("$[0].app", is(dto.getApp())))
                .andExpect(jsonPath("$[0].uri", is(dto.getUri())));
    }
}