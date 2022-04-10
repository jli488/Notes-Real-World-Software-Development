## Code Maintainability and Anti-Patterns

- A wish list of properties about the code you write
  - It should be simple to locate code responsible for a particular feature
  - It should be simple to understand what the code does
  - It should be simple to add or remove a new feature
  - It should provide good encapsulation. Implementation details should be hidden from a user of your code so it is easier to understand and make changes
- If you keep on copy pasting the same code as new requirements come in, you will end up with the following issues, which are called *anti-patterns* because they are common ineffective solutions
  - Hard to understand code because you have one giant "*God Class*"
  - Code that is brittle and easily broken by changes because of *code duplication*

## Single Responsibility Principle

- Two complementary ways of thinking about SRP
  - A class has responsibility over a single functionality
  - There is only one reason for a class to change
- The code in `BankTransactionAnalyzerSimple` has multiple responsibilities that can be broken down individually
  - Reading input
  - Parsing the input in a given format
  - Processing the result
  - Reporting a summary of the result
- In `ListTwo`, the classes has been reorganized. The key benefit is that
  - The main application is no longer responsible for the implementation of the parsing logic
  - You can reuse the functionality encapsulated by the `BankStatementCSVParser` class
  - If you need to change the way the parsing algorithm works, you have just a single place that needs to change
  - It's good to follow the *principle of least surprise* when you implement methods. This means
    - Use self-documenting method names so it is immediately obvious what they do
    - Do not change the state of parameters as other parts of code may depende on it

## Cohesion

Cohesion is connected with **how related** things are. To be more precise, cohesion measures how strongly related responsibilities of a class or method are. High cohesion means that the code is easier for others to locate, understand, and use.

### Class-Level Cohesion

- Six common ways to group methods. If the methods you are grouping are weakly related, you have low cohesion.
  - Functional
  - Informational
  - Utility
  - Logical
  - Sequential
  - Temporal

- Functional

  In `Chaptor02.List02`, the `BankStatementCSVParser` was to **group the methods functionally**. Both two methods are solving a defined task: parse the lines in the CSV format.

  In fact, the method `parseLinesFrom()` uses the method `parseFrom()`. This is generallty a good way to achieve high cohesion because the methods are working together.

  The danger with functional cohesion is that it may be tempting to have a profusion of overly simplistic classes grouping only a single method.

- Informational

  Another reason to group methods is because they **work on the same data or domain object**. For example, if you needed a way to create, read, update, and delete `BankTransaction` objects (CRUD operations); you may wish to have a class dedicated for these operations, i.e. the DAO class.

  ```java
  public class BankTransactionDAO {
  
      public BankTransaction create(final LocalDate date, final double amount, final String description) {
          // ...
          throw new UnsupportedOperationException();
      }
  
      public BankTransaction read(final long id) {
          // ...
          throw new UnsupportedOperationException();
      }
  
      public BankTransaction update(final long id) {
          // ...
          throw new UnsupportedOperationException();
      }
  
      public void delete(final BankTransaction BankTransaction) {
          // ...
          throw new UnsupportedOperationException();
      }
  }
  ```

  This is a typical pattern you see often when interfacing with a database that maintains a table of a specific domain object. DAOs essentially abstract and encapsulate access to a data source, such as a persistent database or an in-memory database.

  The downside of this approach is that **this kind of cohesion can group multiple concerns together**, which introduces **additional dependencies** for a class that only uses and requires some of the operations.

- Utility

  When it is not obvious where the methods belong so you end up with a utility class that is a bit like a jack of all trades.

  This is generally to be avoided because you end up with low cohesion. In addition, utility classes exhibit a poor discoverability characteristic.

- Logical

  You may be tempted to group the methods responsible for parsing the different format (CSV, JSON, and XML) inside one class as the methods are logically categorized to do "parsing". However, it breaks SRP, and thus this approach is not recommended.

- Sequential

  Say you need to read a file, parse it, process it, and save the information. You may group all of the methods in one single class. This is called sequential cohesion, but this also breaks SRP. A better approach is to break down each responsibility inside individual, cohesive classes.

- Temporal

  A temporally cohesive class is one that performs several operations that are only related in time. A typical example is a class that declares some sort of initialisation and clean-up operations.

|       Levels of cohesion        |                       Pro                       |                          Cons                          |
| :-----------------------------: | :---------------------------------------------: | :----------------------------------------------------: |
|   Functional (high cohesion)    |               Easy to understand                |          Can lead to overly simplistic class           |
| Informational (medium cohesion) |                Easy to maintain                 |          Can lead to unnecessary dependencies          |
|  Sequential (medium cohesion)   |        Easy to locate related operations        |              Encourages violation of SRP               |
|    Logical (medium cohesion)    | Provides some form of high-level categorization |              Encourages violation of SRP               |
|     Utility (low cohesion)      |             Simple to put in place              | Harder to reason about the responsibility of the class |
|     Temporal (low cohesion)     |                       N/A                       |   Harder to understand and use individual operations   |

### Mehod-Level Cohesion

The more different functionalities a method performs, the harder it becomes to understand what that methods actually does. Methods that display low cohesion are also harder to test because they have multiple responsibilities within one method, which makes it difficult to tesst responsibilities individually. Typically, if you find yourself with a method that contains a series of if/else blocks that make modifications to many different fields of a class or parameters to the method, then it is a sign you should break down the methods in more cohesive parts.

## Coupling

Coupling is **how dependent** you are on other classes. Another way to think is how much knowledge (i.e., specific implementation) you reply on about certain classes. The more classes you rely on, the less flexible you become when introducing changes. In fact, *the class affected by a change may affect all the classes depending on it*.

Coupling is concerned with how dependent things are. For example, in `Chapter02.List03`, the `BankTransactionAnalyzer` relies on the class `BankStatementCSVParser`, which is annoying. What if you want to support JSON, XML, etc? You can *decouple different components by using an interface*, which is the tool of choice for providing flexibility for changing requirements. Check the `BankStatementParser` interface in `Chapter02.List04`, where we dcoupled the dependency between `BankTransactionAnalyzer` and `BankStatementCSVParser` using the interface of `BankStatementParser`

