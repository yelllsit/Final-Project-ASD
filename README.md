# ExitLag Clone — Route Optimizer

Final Project ASD. A Java Swing application that demonstrates how a service
like [ExitLag](https://www.exitlag.com/) reduces gaming ping by routing traffic
through an optimized network of relay servers instead of the default ISP path.

> **Note.** This is a simulation for learning purposes — it does **not**
> intercept or tunnel any real network traffic. It shows the *algorithm* an
> ExitLag-style router runs to pick the best relay path.

## How it works

1. The network is modelled as a weighted undirected graph of relay nodes
   (Jakarta, Singapore, Hong Kong, Tokyo, Seoul, Sydney, …). Every edge carries
   a base latency, jitter and packet-loss figure.
2. A background simulator jitters the load on every relay every 1.5 s so the
   graph behaves like a live network.
3. When the user clicks **Connect**, two routes are computed:
   - **Direct** — a straight ISP-style path, inflated with a congestion
     penalty that grows with distance. This is the baseline.
   - **Optimized** — an A\* search over the relay graph that minimises
     total effective latency (base link cost + relay-load penalty).
4. The UI shows ping, jitter, packet loss and hop count for the optimized
   route, plus the delta vs. the direct baseline — typically a 30–60 %
   reduction, in line with ExitLag's advertised figures.

## Running

```bash
javac -d out $(find src -name '*.java')
java -cp out exitlag.Main
```

Requires JDK 8+.

## Project layout

```
src/exitlag/
  Main.java                  entry point
  model/
    RelayNode.java           a relay server with location and load
    GameServer.java          a game + region (maps onto a destination relay)
    Route.java               hops + ping / jitter / loss summary
  core/
    NetworkGraph.java        graph + A* optimizer + direct-route baseline
    NetworkSimulator.java    updates relay load each tick
  data/
    NetworkFactory.java      seeds the default Asia-Pacific relay network
  ui/
    MainWindow.java          the Swing window
    RouteCanvas.java         draws the map + highlighted route
```

## Why A\*?

A\* is the same family of algorithms real routing optimizers use: it expands
the open set in order of `g(n) + h(n)`, where `g(n)` is the accumulated ping
and `h(n)` is an admissible geographic estimate to the destination. This lets
the optimizer skip dead-end paths without missing the global best route.

The existing `graphlinked.GraphIUP` class in this repo is the same idea
applied to a Jakarta traffic graph — the ExitLag clone extends the concept to
the "ping graph" used for gaming.
