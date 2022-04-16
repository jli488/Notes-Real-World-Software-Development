### The Hexagonal Architecture

The core of your application is the business logic that you are writing, and you want to keep different implementation choices separate from this core logic.

Whenever you have a technology-specific concern that you want to decouple from the core of your business logic, you introduce a *port*. Events from the outside world arrive at and depart from your business logic core through a port. An *adapter* is the technology-specific implementation code that plugs into the port.

One decision to make is how you separate what should be a port and what should be part of the core domain. A good principle to help you decide might be to think of anything that is critical to the business problem that you’re solving as living inside the core of the application and anything that is technology specific or involves communicating with the outside world as living outside the core application.

## Where to Begin

Software development needs just enough upfront design to avoid it collapsing into chaos, but architecture without coding enough bits to make it real can quickly become sterile and unrealistic.