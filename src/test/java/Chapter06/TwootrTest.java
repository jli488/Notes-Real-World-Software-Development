package Chapter06;

import org.junit.Test;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.*;

public class TwootrTest {
    private final ReceiverEndPoint receiverEndPoint = mock(ReceiverEndPoint.class);

    @Test
    public void shouldBeAbleToAuthenticateUser() {
        // receive logon message for valid user
        Twootr twootr = new Twootr();
        // logon method returns new endpoint
        Optional<SenderEndPoint> senderEndPoint = twootr.onLogon("UserId", receiverEndPoint);
        // assert that endpoint is valid
        assertTrue(senderEndPoint.isPresent());
    }
}
