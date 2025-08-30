//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.json.JsonTest;
//import org.springframework.boot.test.json.JacksonTester;
//import org.springframework.boot.test.json.JsonContent;
//import org.springframework.test.context.ContextConfiguration;
//import ru.practicum.EwmStatsServiceServerApp;
//import ru.practicum.HitDto;
//
//import java.time.LocalDateTime;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@JsonTest
//@ContextConfiguration(classes = EwmStatsServiceServerApp.class)
//public class HitDtoJsonTest {
//    @Autowired
//    private JacksonTester<HitDto> json;
//
//    @Test
//    void testHitDto() throws Exception {
//        HitDto hitDto = new HitDto(
//                1,
//                "test-service",
//                "test/1",
//                "1.1.1.1", LocalDateTime.of(2000, 01, 01, 01, 01, 01));
//
//        JsonContent<HitDto> result = json.write(hitDto);
//
//        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
//        assertThat(result).extractingJsonPathStringValue("$.app").isEqualTo("test-service");
//        assertThat(result).extractingJsonPathStringValue("$.uri").isEqualTo("test/1");
//        assertThat(result).extractingJsonPathStringValue("$.ip").isEqualTo("1.1.1.1");
//        assertThat(result).extractingJsonPathStringValue("$.timestamp").isEqualTo("2000-01-01 01:01:01");
//
//    }
//}