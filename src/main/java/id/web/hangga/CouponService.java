package id.web.hangga;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CouponService {

    private final Clock clock;
    private ZonedDateTime deadline;
    private ZonedDateTime nowInJkt;

    public ZonedDateTime getNowInJkt() {
        return nowInJkt;
    }

    public ZonedDateTime getDeadline() {
        return deadline;
    }

    public CouponService(Clock clock) {
        this.clock = clock;
    }

    public boolean isCouponValid() {
        this.deadline = ZonedDateTime.of(2025, 4, 16, 23, 59, 59, 0, ZoneId.of("Asia/Jakarta"));
        this.nowInJkt = ZonedDateTime.now(clock)
            .withZoneSameInstant(ZoneId.of("Asia/Jakarta"));

        return !nowInJkt.isAfter(deadline);
    }
}
