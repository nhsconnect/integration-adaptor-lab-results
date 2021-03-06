package uk.nhs.digital.nhsconnect.lab.results.mesh.token;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import uk.nhs.digital.nhsconnect.lab.results.utils.TimestampService;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
class TokenTimestamp {
    private static final String TIMESTAMP_FORMAT = "yyyyMMddHHmm";

    @NonNull
    private final Instant datetime;

    public String getValue() {
        return DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT).withZone(TimestampService.UK_ZONE).format(datetime);
    }
}
