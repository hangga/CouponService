package id.web.hangga;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CouponSystemZoneTest {

    ZoneId systemZone = ZoneId.systemDefault();
    static int testIndex = 1;

    StringBuilder htmlTable = new StringBuilder();

    @BeforeAll
    void setupHtmlTable() {
        htmlTable.append("<html><head><title>Coupon Timezone Report</title>")
            .append("<style>")
            .append("table { border-collapse: collapse; width: 100%; }")
            .append("th, td { border: 1px solid #ddd; padding: 8px; }")
            .append("th { background-color: #f2f2f2; }")
            .append("</style></head><body>\n");

        htmlTable.append("<h2>Timezone: ").append(systemZone).append("</h2>\n");
        htmlTable.append("<table>\n")
            .append("<tr>")
            .append("<th>#</th><th>Input Local Time</th><th>UTC Time</th><th>Deadline UTC</th><th>Valid?</th>")
            .append("</tr>\n");
    }

    @ParameterizedTest(name = "[{index}] {0}")
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

        htmlTable.append("<tr>")
            .append("<td>").append(testIndex++).append("</td>")
            .append("<td>").append(localDateTimeStr).append("</td>")
            .append("<td>").append(service.getNowUtc()).append("</td>")
            .append("<td>").append(service.getDeadline()).append("</td>")
            .append("<td>").append(actualValid ? "✅ true" : "❌ false").append("</td>")
            .append("</tr>\n");
    }

    @AfterAll
    void writeHtmlFile() {
        htmlTable.append("</table></body></html>");

        Path junitHtmlDir = Paths.get("build", "reports", "tests", "test");
        Path outputPath = junitHtmlDir.resolve("coupon-timezone-report.html");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
            writer.write(htmlTable.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("✅ HTML report saved to: " + outputPath.toAbsolutePath());
    }
}