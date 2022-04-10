package Chapter03.List01;

import java.io.IOException;

public class MainApplication {
    public static void main(String[] args) throws IOException {
        final BankTransactionAnalyzer bankTransactionAnalyzer = new BankTransactionAnalyzer();
        final BankStatementParser bankStatementParser = new BankStatementCSVParser();
        bankTransactionAnalyzer.analyze(args[0], bankStatementParser);
    }
}
