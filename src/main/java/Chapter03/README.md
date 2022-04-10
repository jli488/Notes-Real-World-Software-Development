## Open/Close Principle

Consider now if we want to add more functionalities to the `BankStatementProcessor`. For example a function of `findTransactionsGreaterThanEqual` compare to a given amount, or `findTransactionsInMonth` to search for transactions within a given month. A simple way of achieving this is to add two new methods in `BankStatementProcessor` like below:

```java
// Add a new method to find transactions within a given range of amount
public List<BankTransaction> findTransactionsGreaterThanEqual(final int amount) {
  final List<BankTransaction> result = new ArrayList<>();
  for (final BankTransaction bankTransaction : bankTransactions) {
    if (bankTransaction.getAmount() >= amount) {
      result.add(bankTransaction);
    }
  }
  return result;
}

// What if you want to have another method to search against a month?
// Notice how much duplicate we have now
public List<BankTransaction> findTransactionsInMonth(final Month month) {
  final List<BankTransaction> result = new ArrayList<>();
  for (final BankTransaction bankTransaction : bankTransactions) {
    if (bankTransaction.getDate().getMonth() == month) {
      result.add(bankTransaction);
    }
  }
  return result;
}
```

Now, what happens if we want to combine the criteria of *amount* and *month*? Clearly this approach exhibits several downsides:

- Your code will become **increasingly complicated** as you have to combine multiple properties of a bank transaction
- The **selection logic is coupled to the iteration logic**, making it harder to separate them out
- You keep on **duplicating code**

<u>This is where the Open/Closed principle comes in. It promotes the idea of being able to change the behavior of a method or class without having to modify the code</u>. In the above example, it would mean the ability to extend the behaviour of a `findTransactions()` method without having to duplicate the code or change it to introduce new parameter.

As we already know, interfaces is a useful tool to decouple concepts from one another. In this case, we can introduce a `BankTransactionFilter` interface that will be responsible for the selection logic.

```java
@FunctionalInterface
public interface BankTransactionFilter {
    boolean test(BankTransaction bankTransaction);
}
```

This interface models the concept of a selection criteria for a `BankTransaction`

```java
// Refactor the findTransactions method
// Flexible method using Open/Closed principle
public List<BankTransaction> findTransactions(final BankTransactionFilter filter) {
  final List<BankTransaction> result = new ArrayList<>();
  for (final BankTransaction bankTransaction : bankTransactions) {
    if (filter.test(bankTransaction)) {
      result.add(bankTransaction);
    }
  }
  return result;
}
```

This refactoring is very important because the iteration logic is decoupled from the business logic now (`Chapter03.List02`). Hence, it's now open for extension and closed for modification.

### Creating an Instance of a Functional Interface

One way of creating a filter instance is to implement the newly created interface

```java
class BankTransactionIsInFebruaryAndExpensive implements BankTransactionFilter {

    @Override
    public boolean test(final BankTransaction bankTransaction) {
        return bankTransaction.getDate().getMonth() == Month.FEBRUARY
               && bankTransaction.getAmount() >= 1_000);
    }
}
```

And then use it as below

```java
final List<BankTransaction> transactions
    = bankStatementProcessor.findTransactions(new BankTransactionIsInFebruaryAndExpensive());
```

### Lambda Expressions

However, you'd need to create special classes every time you have a new requirement. We can also use lambda

```java
bankStatementProcessor.findTransactions(bankTransaction -> {
  boolean monthMatch = bankTransaction.getDate().getMonth() == Month.FEBRUARY;
  boolean amountMatch = bankTransaction.getAmount() >= 1_000;
  return monthMatch && amountMatch;
});
```

To summarisze, the Open/Closed Principle is a useful principle to follow because it:

- Reduces fragility of code by not changing existing code
- Promotes reusability of existing code and as a result avoids code duplication
- Promotes decoupling, which leads to better code maintenance

## Interfaces Gotchas

### God Interface

Consider below interface, which contains all the operations that the bank transaction processor needs to implement

```java
interface BankTransactionProcessor {
    double calculateTotalAmount();
    double calculateTotalInMonth(Month month);
    double calculateTotalInJanuary();
    double calculateAverageAmount();
    double calculateAverageAmountForCategory(Category category);
    List<BankTransaction> findTransactions(BankTransactionFilter bankTransactionFilter);
}
```

This appoach displays several downsides

1. This interface becomes **increasingly complex** as every single helper operation is an integral part of the explicit API definition
2. This interface **acts more like a "God Class"**

This approach actually introducing two forms of additional coupling

- An interface in Java defines a contract that every single implementation has to adhere by. The more operations you add, the more likely changes will happen, increasing the scope for potential problems down the line
- Concrete properties of a `BankTransaction` such as the month and the category have cropped up as part of method names (such as `calculateAverageForCategory()` and `calculateTotalInJanuary()`). This is more problematic with interfaces as they now depend on specific accessors of a domain object. If the internals of that domain object change, then this may cause changes to the interface as well and, as a consequence, to all its concrete implementations, too.

It is generally recommended to define smaller interfaces. The idea is to minimize dependency to multiple operations or internals of a domain object

### Too Granular

The other extreme view is to define one interface for each operation

```java
interface CalculateTotalAmount {
    double calculateTotalAmount();
}

interface CalculateAverage {
    double calculateAverage();
}

interface CalculateTotalInMonth {
    double calculateTotalInMonth(Month month);
}
```

This approach introduces "anti-cohesion". It becomes **harder to discover the operations of interest** as they are hiding in multiple separate interfaces. Part of promoting good maintenance is to help discoverability of common operations. Too granular interfaces also adds overal complexity.

## Explicit Versus Implicit API

- Explicit API such as `findTransactionsGreaterThanEqual()` is self-explanatory and easy to use. No need to worry about adding descriptive method names to help readability and comprehension of your API
- Implicit API such as `findTransactions()` is initially more difficult to use and it needs to be well documented. However, it provides a unified API for all cases where you need to look up transactions. (`Chapter03.List03.BankStatementProcessor`)

### Domain Class or Primitive Value?

While we kept the interface definition of `BankTransactionSummarizer` simple, it is often preferable to not return a primitive value like a `double` if you are looking at returning a result from an aggregation. This is because **it doesn't give you the flexibility to later return multiple results**.

A solution to this problem is to introduce a new domain class such as `Summary` that wraps the `double` value. This means that in the future you can add other fields and results to this class. This technique helps further decouple the various concepts in your domain and also helps minimize cascading changes when requirements change.

## Multiple Exporters

### Introducing a Domain Object

- A specialized domain object. You could introduce a new concept such as `SummaryStatistics` which represents summary information that the user is interested in exporting. A *domain object* is simply an instance of a class that is related to your domain. By introducing a domain object, you introduce a form of decoupling, an indirection layer

  ```java
  public class SummaryStatistics {
  
      private final double sum;
      private final double max;
      private final double min;
      private final double average;
  
      public SummaryStatistics(final double sum, final double max, final double min, final double average) {
          this.sum = sum;
          this.max = max;
          this.min = min;
          this.average = average;
      }
  
      public double getSum() {
          return sum;
      }
  
      public double getMax() {
          return max;
      }
  
      public double getMin() {
          return min;
      }
  
      public double getAverage() {
          return average;
      }
  }
  ```

- A more complex domain object. You could introduce a concept such as `Report` which is more generic and could contain different kinds of fields storing various results including collection of transactions. Whether you need this or not depends on the user requirements and whether you are expecting more complex information.

### Defining and Implementing the Appropriate Interface

You will need to define an interface called `Exporter` to let you decouple from multiple implementations of exporters. The first attempt might be like:

```java
public interface Exporter {
	void export(SummaryStatistics summaryStatistics);
}
```

However, this approach is to be avoided for several reasons:

- The return type `void` is not useful and is difficult to reason about. You don't know what is returned. The signature of the `export()` method implies that some state change is happening somewhere or that this method will log or print information back to the screen. We don't know.
- Returning `void` makes it very hard to test the result with assertions. What is the actual result to compare with the expected result? Unfortunately, you cannot get a result with void.

With this in mind, a good `Exporter` interface might look like:

```java
public interface Exporter {
    String export(SummaryStatistics summaryStatistics);
}
```

One thing to remember here: **think about how you can test it**

## Exception Handling

### Why Use Exceptions

- Documentation: The language supports exceptions as part of method signatures
- Type safety: The type system figures out whether you are handling exceptional flow
- Separation of concern: Business logic and exception recovery are separated out with a try/catch block

In Java, the class `Exception` typically represents checked exceptions, and a program should be able to recover from it. `Error` and `RuntimeException` are unchecked exceptions, and you shouldn't expect to catch and recover from them.

### Patterns and Anti-Patterns with Exceptions

There are two separate concerns when thinking about parsing the CSV file:

- Parsing the right syntax (e.g., CSV, JSON)
- Validation of the data

You will focus on the syntax error first and then the validation of the data

- **Deciding between unchecked and checked**

  Part of the benefit of supporting exceptions in your code is to provide a clearer diagnosis to the user of your API in the event that a problem arises.

  ```java
  final String[] columns = line.split(",");
  
  if (columns.length < EXPECTED_ATTRIBUTES_LENGTH) {
  	throw new CSVSyntaxException();
  }
  ```

  Should `CSVSyntaxException` be a checked or an unchecked exception? To answer this question you need to ask **whether you require the user of your API to take a compulsory recovery action**. Typically, **errors due to business logic validation (e.g., wrong format or arithmetic) should be unchecked exceptions**, as they would add a lot of try/catch clutter in your code. In addition, system errors (e.g., disk ran out of space) should also be unchecked exceptions as there is nothing the client can do.

  In a nutshell, the recommendation is to use unchecked exceptions and only use checked exceptions sparingly to avoid significant clutter in the code.

Two anti-patterns with using exceptions for validation

- **Overly specific**

  It is recommended creating a dedicated `Validator` class for validation logic, for several reasons:

  - You don't have to duplicate the validation logic when you need to reuse it
  - You get confidence that different parts of your system validate the same way
  - You can easily unit test this logic separately
  - If follows SRP, which leads to simpler maintenance and program comrehension

  But, here is the first anti-pattern, an overly specific approach

  ```java
  public class OverlySpecificBankStatementValidator {
    private String description;
    private String date;
    private String amount;
    
    public OverlySpecificBankStatementValidator(final String description,
                                                final String date,
                                                final String amount): {
      this.description = Objects.requireNonNull(description);
      this.date = Objects.requireNonNull(description);
      this.amount = Objects.requireNonNull(description);
    }
    
    public boolean validate() throws DescriptionTooLongException, InvalidDateFormat, DateInTheFutureException, InvalidAmountException {
      if(this.description.length() > 100) {
        throw new DescriptionTooLongException();
      }
  
      final LocalDate parsedDate;
      try {
        parsedDate = LocalDate.parse(this.date);
      }
      catch (DateTimeParseException e) {
        throw new InvalidDateFormat();
      }
      
      if (parsedDate.isAfter(LocalDate.now())) {
        throw new DateInTheFutureException();
      }
  
      try {
        Double.parseDouble(this.amount);
      }
      catch (NumberFormatException e) {
        throw new InvalidAmountException();
      }
      return true;
    }
  }
  ```

  The above design forces user to explicitly deal with each of the exceptions, which is not heling user understand and simply use this API.

- **Overly apathetic**

  The other spectrum is making everything an unchecked exception

  ```java
  public boolean validate() {
      if(this.description.length() > 100) {
          throw new IllegalArgumentException("The description is too long");
      }
  
      final LocalDate parsedDate;
      try {
          parsedDate = LocalDate.parse(this.date);
      }
      catch (DateTimeParseException e) {
          throw new IllegalArgumentException("Invalid format for date", e);
      }
    
      if (parsedDate.isAfter(LocalDate.now())) {
        throw new IllegalArgumentException("date cannot be in the future");
      }
  
      try {
          Double.parseDouble(this.amount);
      }
      catch (NumberFormatException e) {
          throw new IllegalArgumentException("Invalid format for amount", e);
      }
      return true;
  }
  ```

- **Notification Pattern**

  The solution is to introduce a domain class to collect errors.

  The first thing you need is a `Notification` class whose responsibility is to collect errors

  ```java
  public class Notification {
      private final List<String> errors = new ArrayList<>();
      
      public void addError(final String error) {
          errors.add(error);
      }
      
      public boolean hasErrors() {
          return !errors.isEmpty();
      }
      
      public String errorMessage() {
          return errors.toString();
      }
      
      public List<String> getErrors() {
          return this.errors;
      }
  }
  ```

  The benefit of introducing such a class is that you can now declare a validator that is able to collect multiple errors in one pass.

  ```java
  public Notification validate() {  
    final Notification notification = new Notification();
  
    if(this.description.length() > 100) {
      notification.addError("The description is too long");
    }
  
    final LocalDate parsedDate;
    try {
      parsedDate = LocalDate.parse(this.date);
      if (parsedDate.isAfter(LocalDate.now())) {
        notification.addError("date cannot be in the future");
      }
    }
    catch (DateTimeParseException e) {
      notification.addError("Invalid format for date");
    }
  
    final double amount;
    try {
      amount = Double.parseDouble(this.amount);
    }
    catch (NumberFormatException e) {
      notification.addError("Invalid format for amount");
    }
    return notification;
  }
  ```

### Guidelines for Using Exceptions

- Do not ignore an exception

- Do not catch the generic Exception

- Document exceptions

- Watch out for implementation-specific exceptions

  Do not throw implementation specific exceptions as it breaks encapsulation of your API

- Exceptions versus control flow

  Do not use exceptions for control flow, for example:

  ```java
  try {
    while (true) {  // This while loop relies on an exception to exit
      System.out.println(source.read());
    }
  }
  catch(NoDataException e) {
  }
  ```

### Alternatives to Exceptions

- Using null

  Instead of throwing a specific exception, you may want to just return `null`. This is absolutely to be avoided. `null` provides no useful information and also need to be checked on the caller side.

- The Null Object pattern

  Instead of returning a `null`, you return an object that implements the expected interface but whose method bodies are empty. This pattern can also be problematic because you may hide potential issues in the data with an object that simply ignores the real problem, and as a result make troubleshooting more difficult.

- Optional\<T\>

  Java 8 introduced a built-in data type `java.util.Optional<T>`, which is dedicated to representing the presence or absence of a value. The `Optional<T>` comes with a set of methods to explicitly deal with the absence of a value, which is useful to reduce the scope for bugs.

- Try\<T\>

  There’s another data type called `Try<T>`, which represents an operation that may succeed or fail. In a way it is analogous to `Optional<T>`, but instead of values you work with operations. In other words, the `Try<T>` data type brings similar code composability benefits and also helps reduce the scope for errors in your code.

## Using a Build Tool

### Why Use a Build Tool?

You can think of a build tool as an assistant that can automate the representitive tasks in the software development life cycle, including building, testing, and deploying your application

### Maven

Maven allows you to describe the build process for your software together with its dependencies

- pom.xml file

  - `project`

    This is the top-level element in all pom.xml files

  - `groupId`

    This element indicates the unique identifier of the **organization** that created the project

  - `artifactId`

    This element specifies a unique base name for the artifact generated by the build process

  - `packaging`

    This element indicates the package type to be used by this artifact. The default is JAR.

  - `version`

    The version of the artifact generated by the project

  - `build`

    This element specifies various configurations to guide the build process such as plug-ins an d resources

  - `dependencies`

    This element specifies a dependency list for the project

- Maven commands

  - `mvn clean`

    Cleans up any previously generated artifacts from a prior build

  - `mvn compile`

    Compiles the source code of the project (by default in a generated target folder)

  - `mvn test`

    Tests the compiled source code

  - `mvn package`

    Packages the compiled code in a suitable format such as JAR

  
