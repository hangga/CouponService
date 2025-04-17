package id.web.hangga;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CouponSystemZoneTest {

    ZoneId systemZone = ZoneId.systemDefault();

    @ParameterizedTest(name = "[{index}] {0} => should be valid? {1}")
    @CsvSource({
        "2025-04-16T22:00:00, true",
        "2025-04-17T00:59:59, false",
        "2025-04-16T18:59:59, true",
        "2025-04-17T00:00:01, false",
        "2025-04-16T17:00:00, true",
        "2025-04-17T02:00:00, false",
        "2025-04-16T13:00:00, true"
    })
    void testCouponValidInSystemTimezone(String localDateTimeStr) {
        LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeStr);

        Instant instant = localDateTime.atZone(systemZone).toInstant();
        Clock fixedClock = Clock.fixed(instant, systemZone);

        CouponService service = new CouponService(fixedClock);
        boolean actualValid = service.isCouponValid();

        System.out.printf("""
                [TEST] System TZ: %s
                       Input LocalDateTime : %s
                       Interpreted Instant : %s
                       Jakarta Time        : %s
                       Deadline Jakarta    : %s
                       Valid               : %s
                """,
            systemZone,
            localDateTimeStr,
            instant,
            service.getNowInJkt(),
            service.getDeadline(),
            actualValid
        );
    }
}