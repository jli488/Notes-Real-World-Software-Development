package Chapter06;

import java.util.Optional;

public class Twootr {
    Optional<SenderEndPoint> onLogon(String userId, ReceiverEndPoint receiver) {
        return Optional.of(new SenderEndPoint());
    }
}
