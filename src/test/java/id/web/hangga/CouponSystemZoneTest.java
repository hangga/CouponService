package id.web.hangga;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CouponSystemZoneTest {

    ZoneId systemZone = ZoneId.systemDefault();
    static int testIndex = 1;

    @BeforeAll
    static void printTableHeader() {
        System.out.println();
        System.out.println("### Timezone: `" + ZoneId.systemDefault() + "`");
        System.out.printf("| %-2s | %-24s | %-26s | %-26s | %-12s |\n",
            "#", "Input Local Time", "UTC Time", "Deadline UTC", "Valid?");
        System.out.println("|----|--------------------------|----------------------------|----------------------------|--------------|");
    }

    @ParameterizedTest(name = "[{index}] {0} => should be valid? {1}")
    @CsvSource({
        "2025-04-15T00:00:00",
        "2025-04-16T22:00:00",
        "2025-04-17T00:59:59",
        "2025-04-16T18:59:59",
        "2025-04-17T00:00:01",
        "2025-04-16T17:00:00",
        "2025-04-17T02:00:00",
        "2025-04-16T13:00:00"
    })
    void testCouponValidInSystemTimezone(String localDateTimeStr) {
        LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeStr);

        Instant instant = localDateTime.atZone(systemZone).toInstant();
        Clock fixedClock = Clock.fixed(instant, systemZone);

        CouponService service = new CouponService(fixedClock);
        boolean actualValid = service.isCouponValid();

        System.out.printf("| %-2d | %-24s | %-26s | %-26s | %-12s |\n",
            testIndex++,
            localDateTimeStr,
            service.getNowUtc(),
            service.getDeadline(),
            actualValid ? "✅ true" : "❌ false"
        );
    }
}