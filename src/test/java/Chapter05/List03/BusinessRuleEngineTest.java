package Chapter05.List03;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BusinessRuleEngineTest {
    @Test
    public void shouldPerformAnActionWithFacts() {
        final Action mockedAction = mock(Action.class);
        final Facts mockedFacts = mock(Facts.class);
        final BusinessRulesEngine businessRulesEngine = new BusinessRulesEngine(mockedFacts);

        businessRulesEngine.addAction(mockedAction);
        businessRulesEngine.run();

        verify(mockedAction).execute(mockedFacts);
    }
}
