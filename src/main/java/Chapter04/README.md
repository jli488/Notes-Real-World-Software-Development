## The Liskov Substitution Principle (LSP)

Informally you can think of LSP as meaning that child classes should maintain the behavior they inherit from their parents.

LSP: Let $q(x)$ be a property provable about object $x$ of type $T$. Then $q(y)$ should be true for objects $y$ of type $S$ where $S$ is a subtype of $T$.

- *Preconditions cannot be strengthened in a subtype*

  A precondition establishes the conditions under which some code will work. You can't just assume what you've written will work anyway, anyhow, anywhere.

  **LSP means that you can't require any more restrictive preconditions than your parent required.**

- *Postconditions cannot be weakened in a subtype*

  Postconditions are things that have to be true after some code has run. If the parent has some kind of side effect or returns some value, then the child must do so as well.

- *Invariants of the supertype must be preserved in a subtype*

  An invariant is something that never changes. In the context of inheritance, we want to make sure that any invariants that are expected to be maintained by the parent class should also be maintained by the children.

- *The History Rule*

  In essence, the child class shouldn't allow state changes that your parent disallowed. For example, we have an immutable `Document` class. You shouldn't subclass this `Document` and create a mutable `Document` class. Any user of the parent class would expect certain behavior in response to calling methods on the `Document` class. If the child were mutable, it could violate callers' expectations about what calling those methods does.

## Extending and Reusing Code

In order to reuse the code we need to actually have it live in some class. You have essentially three options to consider, each with pros and cons:

- Use a *utility* classs

  Utility class is not a real `Thing`, which actually violate the expectation that class maps a mental model of your domain onto your code. As a result, utility class often ends up turning into bundles of procedural code with no single responsibility or concept. Over time, this can often lead to the apearance of a God Class in the codebase, i.e a single class that ends up hogging a lot of responsibilities

- Use *inheritance*

  In practice, inheritance is often a poor choice when the inheritance fails to model some real-world relationship. Inheritance relationships tend to be brittle. As a rule of thumb, it's a bad idea to introduce an inheritance relationship purely to enable code reuse.

- Use a *domain class*

  To use this approach, we would model some underlying concept. Try to find a model for the functionality that could be reused.

## Test Hygiene

Test hygiene means to keep your test code clean and ensure that it is **maintained and improved along with your codebase** under test.

### Test Naming

The first thing to think about when it comes to tests is their naming. The key driving principles when it comes to test naming are <u>readability, maintainability, and acting as *executable documentation*</u>.

The names should act as statements that document what functionality works and what does not. Test names should describe the behavior under test, not a concept. One naming anti-pattern is to simply name the test after a method that is invoked during testing, rather than the behavior.

Three rules of thumb of good test naming:

- Use domain terminology
- Use natural language
- Be descriptive

### Behavior Not Implementation

If you are writing a test for a class, a componentm or even a system, then you should *only be testing the public behavior of whatever is being tested*. Tests should only invoke the public API methods and not try to inspect the internal state of the objects or the design. This is one of the key mistakes made by developers that leads to hard-to-maintain tests. Relying on specific implementation details results in brittle tests because if you change the implementation detail in question, the test can start to fail even if the behavior is still working. Relying on the behavior of a class relies on using the public API.

A common anti-pattern in this regard is exposing otherwise private state through a getter or setter in order to make testing easier. This makes yur test brittle, and is sometimes a good indication that you need to refactor out a new class that can be more easily and effectively tested

### Don't Repeat Yourself

Sadly, developers often don't bother to remove duplication from tests in the same way as they would for application code.

```java
@Test
public void shouldImportImageAttributes() throws Exception
{
  system.importFile(XRAY);
  final Document document = onlyDocument();

	assertAttributeEquals(document, WIDTH, "320");
	assertAttributeEquals(document, HEIGHT, "179");
	assertTypeIs("IMAGE", document);
}
```

In the above code, normally you would have to look up the attribute name for every attribute and assert that it is equal to an expected value. This is a common enough operation that a common method, `assertAttributeEquals`, was extracted with this logic.

```java
private void assertAttributeEquals(
  final Document document,
  final String attributeName,
  final String expectedValue) {
  assertEquals(
    "Document has the wrong value for " + attributeName,
    expectedValue,
    document.getAttribute(attributeName));
}
```

### Good Diagnostics

When writing tests, the best thing to do is to **optimize for failure**. Optimize here means ensure that it is written in a way that makes understanding why and how it failed as easy as possible.

By diagnostics we mean the <u>message and information that gets printed out when a test fails</u>. The clearer this message is about what has failed, the easier it is to debug the test failure. 

### Testing Error Cases

One of the absolute worst and most common mistakes to make when writing software is only to test the happy path. Examples of testing `Exception` using junit

```java
@Test(expected = FileNotFoundException.class)
public void shouldNotImportMissingFile() throws Exception {
  system.importFile("gobbledygook.txt");
}

@Test(expected = UnknownFileTypeException.class)
public void shouldNotImportUnknownFile() throws Exception {
  system.importFile(RESOURCES + "unknown.txt");
}
```

### Constants

It’s a good idea when it comes to constants that have some kind of nonobvious meaning to give them a proper name that can be used within tests. We do that extensively through the `DocumentManagementSystemTest`, and in fact, have a block at the top dedicated to declaring constants

```java
private static final String RESOURCES =
  "src" + File.separator + "test" + File.separator + "resources" + File.separator;
private static final String LETTER = RESOURCES + "patient.letter";
private static final String REPORT = RESOURCES + "patient.report";
private static final String XRAY = RESOURCES + "xray.jpg";
private static final String INVOICE = RESOURCES + "patient.invoice";
private static final String JOE_BLOGGS = "Joe Bloggs";
```

