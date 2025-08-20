import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.EwmStatsServiceServerApp;
import ru.practicum.ViewStatsDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = EwmStatsServiceServerApp.class)
public class ViewStatsDtoTest {
    @Autowired
    private JacksonTester<ViewStatsDto> json;

    @Test
    void testViewStatsDto() throws Exception {
        ViewStatsDto dto = new ViewStatsDto(
                "test-service",
                "test/1",
                1L);

        JsonContent<ViewStatsDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.hits").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.app").isEqualTo("test-service");
        assertThat(result).extractingJsonPathStringValue("$.uri").isEqualTo("test/1");
    }
}