# FiveM Booster — ExitLag-style Route Optimizer

Final Project ASD. A Java Swing desktop application that demonstrates how a
service like [ExitLag](https://www.exitlag.com/) reduces ping for
**FiveM (GTA V multiplayer)** sessions by routing traffic through an optimized
network of relay servers instead of the default ISP path.

> **Disclaimer.** This is a simulation for educational purposes — it does
> **not** intercept or tunnel real FiveM / GTA network traffic. It implements
> and visualizes the *algorithm* (A\* over a weighted relay graph) that an
> ExitLag-style router uses to pick the best path to a FiveM server.

## Features

- 16 real-world FiveM servers (NoPixel, GTA World, Eclipse RP, ProdigyRP,
  DOJ RP, Indo RP, OCRP, UK Roleplay, EuropaRP …) mapped to their hosting
  regions.
- 24 relay PoPs across SEA, East Asia, Oceania, North America and Europe.
- A\* search that minimises `base_link_latency + relay_load_penalty` to find
  the lowest-ping route.
- Live simulation: relay load jitters every 1.5 s so the route adapts
  dynamically (the "dynamic re-routing" ExitLag advertises).
- Side-by-side comparison of **Direct ISP route** vs **Optimized route** with
  ping / jitter / packet-loss deltas — typically a **30–60 % ping reduction**.

## Quick start

### Option 1 — run the fat JAR (any OS)

```bash
./build.sh                       # Linux / macOS
java -jar dist/FiveMBooster.jar
```

```bat
build-exe.bat                    :: Windows - also builds the .exe
run.bat                          :: or just double-click run.bat
```

Requires JDK 17+ on `PATH` (JDK 21 recommended).

### Option 2 — build a native Windows `.exe`

On a Windows machine with JDK 21 installed:

```bat
build-exe.bat              :: produces dist\FiveM Booster\FiveM Booster.exe
build-exe.bat installer    :: produces dist\FiveM Booster-1.0.0.exe (installer)
```

The app-image build uses `jpackage --type app-image` and needs no extra
tooling. The installer build (`.exe` installer) additionally requires the
free [WiX Toolset 3.x](https://wixtoolset.org/).

### Option 3 — download the pre-built `.exe` from GitHub Actions

Every push to `main` or a `claude/*` branch triggers
`.github/workflows/build-windows-exe.yml`, which runs on a Windows runner and
uploads two artifacts:

- `FiveMBooster-windows` — zipped app-image with `FiveM Booster.exe` inside
- `FiveMBooster-jar` — the portable fat JAR

Grab them from the workflow run's *Artifacts* section on GitHub.

## How it works

1. `NetworkFactory` builds a weighted graph of relay PoPs (Jakarta, Singapore,
   Tokyo, Seoul, Sydney, Los Angeles, Dallas, New York, London, Frankfurt, …).
   Each edge carries a base latency, jitter and packet-loss figure.
2. `NetworkSimulator` jitters the load on every relay every 1.5 s.
3. On **Connect**, two routes are computed:
   - **Direct** — `directRoute()` returns a straight ISP-style ping inflated
     by a congestion factor that grows with distance. This is the baseline.
   - **Optimized** — `optimalRoute()` runs A\* over the relay graph, expanding
     nodes in order of `g + h` (where `g` = summed edge cost + load penalty,
     `h` = admissible geographic estimate to the destination).
4. The UI redraws the chosen hops on the map and updates the live metrics.

## Project layout

```
src/exitlag/
  Main.java                     entry point
  model/
    RelayNode.java              relay PoP with location and load
    GameServer.java             FiveM server + region (-> destination relay)
    Route.java                  hops + ping/jitter/loss summary
  core/
    NetworkGraph.java           A* optimizer + direct-route baseline
    NetworkSimulator.java       per-tick load jitter
  data/
    NetworkFactory.java         seeded relay graph + FiveM server list
  ui/
    MainWindow.java             Swing window
    RouteCanvas.java            map renderer for the chosen route

build.sh                        cross-platform fat JAR build
build-exe.bat                   Windows: fat JAR + jpackage -> .exe
run.bat                         Windows: double-click launcher
.github/workflows/build-windows-exe.yml
                                CI: builds the .exe on Windows runners
```

## Why A\*?

A\* is the canonical shortest-path algorithm for graphs with a meaningful
heuristic. It expands the frontier in order of `g(n) + h(n)` where `g(n)` is
the accumulated ping so far and `h(n)` is an admissible geographic estimate to
the destination. With an admissible heuristic A\* is **optimal** — it always
returns the lowest-ping path — while exploring far fewer nodes than plain
Dijkstra. Real route-optimization products use the same family of algorithms.
