package id.web.hangga;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CouponSystemZoneTest {

    @Test
    void testCouponWithSystemZone() {
        Clock systemClock = Clock.systemDefaultZone();
        CouponService service = new CouponService(systemClock);

        // Hasil test ini akan tergantung timezone default dari environment (CircleCI)
        boolean valid = service.isCouponValid();

        System.out.printf("Zone: %s | Now Jakarta: %s | Valid: %b\n",
            ZoneId.systemDefault(),
            ZonedDateTime.now(systemClock).withZoneSameInstant(ZoneId.of("Asia/Jakarta")),
            valid
        );
    }

    @ParameterizedTest(name = "[{index}] {0} di zona {1} => harus valid? {2}")
    @CsvSource({
        // ✅ Valid case
        "2025-04-16T22:00:00, Asia/Jakarta, true",
        "2025-04-16T23:59:59, Asia/Jakarta, true",
        "2025-04-16T16:59:59, UTC, true",
        "2025-04-16T18:59:59, Europe/Paris, true",       // Sama dengan 23:59:59 Jakarta
        "2025-04-16T17:59:59, Europe/London, true",      // Sama dengan 23:59:59 Jakarta
        "2025-04-16T12:59:59, America/New_York, true",   // Sama dengan 23:59:59 Jakarta
        "2025-04-16T09:59:59, America/Los_Angeles, true",// Sama dengan 23:59:59 Jakarta

        // ❌ Expired case
        "2025-04-17T00:00:01, Asia/Jakarta, false",
        "2025-04-16T17:00:00, UTC, false",
        "2025-04-17T02:00:00, Asia/Kuala_Lumpur, false",
        "2025-04-16T19:00:01, Europe/Paris, false",
        "2025-04-16T13:00:00, America/New_York, false",
        "2025-04-16T10:00:00, America/Los_Angeles, false"
    })
    void testCouponVariousTimes(String dateTimeStr, String zoneIdStr, boolean expectedValid) {
        ZoneId zoneId = ZoneId.of(zoneIdStr);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr);
        Instant instant = localDateTime.atZone(zoneId).toInstant();
        Clock fixedClock = Clock.fixed(instant, zoneId);

        CouponService service = new CouponService(fixedClock);
        boolean result = service.isCouponValid();

        assertEquals(expectedValid, result,
            String.format("Datetime %s @ %s should be valid? %s", dateTimeStr, zoneIdStr, expectedValid));
    }
}