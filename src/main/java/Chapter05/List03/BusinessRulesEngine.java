package Chapter05.List03;

import java.util.ArrayList;
import java.util.List;

public class BusinessRulesEngine {
    private final List<Action> actions;
    private final Facts facts;

    public BusinessRulesEngine(final Facts facts) {
        this.actions = new ArrayList<>();
        this.facts = facts;
    }

    public void addAction(final Action action) {
        this.actions.add(action);
    }

    public int count() {
        return this.actions.size();
    }

    public void run() {
        this.actions.forEach(action -> action.execute(facts));
    }
}
