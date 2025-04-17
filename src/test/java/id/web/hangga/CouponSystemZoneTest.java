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

//    @Test
//    void testCouponWithSystemZone() {
//        Clock systemClock = Clock.systemDefaultZone();
//        CouponService service = new CouponService(systemClock);
//
//        // Hasil test ini akan tergantung timezone default dari environment (CircleCI)
//        boolean valid = service.isCouponValid();
//
//        System.out.printf("Zone: %s | Now Jakarta: %s | Valid: %b\n",
//            ZoneId.systemDefault(),
//            ZonedDateTime.now(systemClock).withZoneSameInstant(ZoneId.of("Asia/Jakarta")),
//            valid
//        );
//    }

    @ParameterizedTest(name = "[{index}] {0} => should be valid? {1}")
    @CsvSource({
        // Format: localDateTime (tanpa zona), expectedIsValid
        "2025-04-16T22:00:00, true",
        "2025-04-16T23:59:59, true",
        "2025-04-16T16:59:59, true",
        "2025-04-17T00:59:59, false",
        "2025-04-16T18:59:59, true",
        "2025-04-16T17:59:59, true",
        "2025-04-16T12:59:59, true",
        "2025-04-16T09:59:59, true",
        "2025-04-17T00:00:01, false",
        "2025-04-16T17:00:00, true",
        "2025-04-17T01:00:00, false",
        "2025-04-17T02:00:00, false",
        "2025-04-16T19:00:01, true",
        "2025-04-16T13:00:00, true",
        "2025-04-16T10:00:00, true"
    })
    void testCouponValidInSystemTimezone(String localDateTimeStr, boolean expectedValid) {
        LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeStr);
        ZoneId systemZone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(systemZone).toInstant();
        Clock fixedClock = Clock.fixed(instant, systemZone);

        CouponService service = new CouponService(fixedClock);
        boolean actualValid = service.isCouponValid();

        ZonedDateTime now = ZonedDateTime.now(fixedClock);
        ZonedDateTime nowInJakarta = now.withZoneSameInstant(ZoneId.of("Asia/Jakarta"));
        ZonedDateTime deadline = ZonedDateTime.of(
            2025, 4, 16, 23, 59, 59, 0,
            ZoneId.of("Asia/Jakarta")
        );

        System.out.printf("""
                [TEST] System TZ: %s
                       Input LocalDateTime: %s
                       Interpreted Instant: %s
                       Jakarta Time:        %s
                       Deadline Jakarta:    %s
                       Expected Valid:      %s
                       Actual Valid:        %s
                """,
            systemZone,
            localDateTimeStr,
            instant,
            nowInJakarta,
            deadline,
            expectedValid,
            actualValid
        );

        assertEquals(expectedValid, actualValid);
    }
}