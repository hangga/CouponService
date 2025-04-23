package id.web.hangga;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CouponService {

    private final Clock clock;
    private ZonedDateTime deadline;
    private ZonedDateTime nowUtc;

    public ZonedDateTime getNowUtc() {
        return nowUtc;
    }

    public ZonedDateTime getDeadline() {
        return deadline;
    }

    public CouponService(Clock clock) {
        this.clock = clock;
    }

    public boolean isCouponValid() {
        ZoneId zoneUtc = ZoneId.of("UTC");
        this.deadline = ZonedDateTime.of(2025, 4, 16, 23, 59, 59, 0, zoneUtc);
        this.nowUtc = ZonedDateTime.now(clock)
            .withZoneSameInstant(zoneUtc);

        return !nowUtc.isAfter(deadline);
    }
}
