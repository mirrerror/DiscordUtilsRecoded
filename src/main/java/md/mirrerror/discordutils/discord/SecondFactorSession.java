package md.mirrerror.discordutils.discord;

import java.time.LocalDateTime;

public class SecondFactorSession {
    private String ipAddress;
    private LocalDateTime start, end;

    public SecondFactorSession(String ipAddress, LocalDateTime start, LocalDateTime end) {
        this.ipAddress = ipAddress;
        this.start = start;
        this.end = end;
    }

    public SecondFactorSession(String ipAddress, LocalDateTime end) {
        this.ipAddress = ipAddress;
        this.start = LocalDateTime.now();
        this.end = end;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
