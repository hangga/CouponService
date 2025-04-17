package id.web.hangga;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CouponService {

    private final Clock clock;

    public CouponService(Clock clock) {
        this.clock = clock;
    }

    public boolean isCouponValid() {
        ZonedDateTime deadline = ZonedDateTime.of(
            2025, 4, 16, 23, 59, 59, 0,
            ZoneId.of("Asia/Jakarta")
        );

        ZonedDateTime now = ZonedDateTime.now(clock)
            .withZoneSameInstant(ZoneId.of("Asia/Jakarta"));

        boolean isValid = !now.isAfter(deadline);

        // Logging lebih informatif
        System.out.printf("""
                [CouponService] Now      : %s (%s)
                                Deadline : %s (Asia/Jakarta)
                                Valid?   : %s
                """,
            now,
            ZoneId.systemDefault(),
            deadline,
            isValid
        );

        return isValid;
    }
}
