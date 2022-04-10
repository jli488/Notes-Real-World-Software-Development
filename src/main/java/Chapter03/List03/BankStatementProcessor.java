package Chapter03.List03;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class BankStatementProcessor {
    private final List<BankTransaction> bankTransactions;

    public BankStatementProcessor(final List<BankTransaction> bankTransactions) {
        this.bankTransactions = bankTransactions;
    }

    public List<BankTransaction> findTransactions(final BankTransactionFilter filter) {
        final List<BankTransaction> result = new ArrayList<>();
        for (final BankTransaction bankTransaction : bankTransactions) {
            if (filter.test(bankTransaction)) {
                result.add(bankTransaction);
            }
        }
        return result;
    }

    public double summarizeTransactions(final BankTransactionSummarizer summarizer) {
        double result = 0;
        for (BankTransaction bankTransaction : bankTransactions) {
            result = summarizer.summarize(result, bankTransaction);
        }
        return result;
    }

    public double calculateTotalAmount() {
        return summarizeTransactions(((accumulator, bankTransaction) ->
                accumulator + bankTransaction.getAmount()
        ));
    }

    public double calculateTotalInMonth(final Month month) {
        return summarizeTransactions(((accumulator, bankTransaction) ->
                bankTransaction.getDate().getMonth() == month ? accumulator + bankTransaction.getAmount() : accumulator
        ));
    }

    public double calculateTotalForCategory(final String category) {
        return summarizeTransactions(((accumulator, bankTransaction) ->
                bankTransaction.getDescription().equalsIgnoreCase(category) ?
                        accumulator + bankTransaction.getAmount() : accumulator
        ));
    }
}
