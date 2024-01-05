package md.mirrerror.discordutils.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class UserBatchUpdateEntry {

    private UUID uuid;
    private long userId;
    private boolean isSecondFactorEnabled;

}
