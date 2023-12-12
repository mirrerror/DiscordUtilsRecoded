package md.mirrerror.discordutils.discord;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SecondFactorSession {

    private String ipAddress;
    private LocalDateTime start, end;

    public SecondFactorSession(String ipAddress, LocalDateTime end) {
        this.ipAddress = ipAddress;
        this.start = LocalDateTime.now();
        this.end = end;
    }

}
