# Scalable Key-Value Store

A Java-based distributed key-value store implementing the Paxos protocol (proposer/acceptor/learner) with simulated component failures and simple logging.

## Features

-   Paxos-based consensus for consistency across replicas
-   Roles: Proposer, Acceptor, Learner
-   Simulated random failures for proposer/acceptor/learner components
-   Client that connects to servers and issues key-value requests
-   Simple logging system under `LoggerPackage`

## Repository Layout

-   `src/Client.java` — client program that connects to servers
-   `src/Server.java` — server entrypoint implementing Paxos roles
-   `src/KeyValueStore.java` — key-value storage logic
-   `src/IDataStorage.java` / `src/IDataStorageImpl.java` — storage interface and implementation
-   `src/PaxosMessage.java`, `src/PaxosResponse.java` — message/response types used by Paxos
-   `src/LoggerPackage/` — logging utilities and configuration

## Prerequisites

-   Java 8 or later
-   An IDE such as IntelliJ IDEA (instructions below) or command-line Java toolchain

## Run (IntelliJ IDEA)

1. Open the project in IntelliJ IDEA.
2. Create run configurations for `Server` and `Client` (use the classes in `src/`).
3. For each `Server` run configuration: open "Modify options" and enable "Allow multiple instances".
4. Provide the server port or server number as the program argument when starting each server instance.
    - Start multiple server instances, each with a different port/ID.
5. For the `Client` run configuration: enter the list of server ports (all at once) as program arguments so the client can connect to the cluster.

Notes: the existing project uses program arguments to pass server numbers/ports. See the provided screenshots for IntelliJ run-configuration examples.

## Run (Command Line)

1. From the project root, compile the sources:

```bash
javac -d out src/*.java src/LoggerPackage/*.java
```

2. Run a server (example):

```bash
java -cp out Server <server-port-or-id>
```

3. Run the client (example):

```bash
java -cp out Client <port1> <port2> <port3> <port4> <port5>
```

Replace `<server-port-or-id>` and `<port1>..` with the actual ports or IDs you choose.

## Logging & Failure Simulation

-   The servers write logs that indicate when a component (Proposer, Acceptor, Learner) fails and recovers.
-   Random failures are simulated via the `simulateRandomFailure` method inside the server components; some operations may be delayed (1–2 seconds) to emulate timeouts.

## Behavior Notes

-   The client connects to a random server in the cluster.
-   Paxos messages coordinate promises and commits between proposers and acceptors to achieve consensus before learners update the store.

## Troubleshooting

-   If instances do not start, confirm the run configuration arguments and that ports are not already in use.
-   Check the log files (configured in `LoggerPackage`) for component-level failure messages.

## Next Steps / Improvements

-   Add a script to start multiple servers with predefined ports for easier local testing.
-   Provide unit and integration tests to validate Paxos behavior under different failure scenarios.

---

