package Chapter05.List02;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BusinessRuleEngineTest {
    @Test
    public void shouldHaveNoRulesInitially() {
        final BusinessRulesEngine businessRulesEngine = new BusinessRulesEngine();
        assertEquals(0, businessRulesEngine.count());
    }

    @Test
    public void shouldAddTwoActions() {
        final BusinessRulesEngine businessRulesEngine = new BusinessRulesEngine();
        businessRulesEngine.addAction(() -> {});
        businessRulesEngine.addAction(() -> {});
        assertEquals(2, businessRulesEngine.count());
    }
}
