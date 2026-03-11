# fastnoisenouveau Architecture Boundary Ratification Review

Date: 2026-03-11

## 1. Intent and Scope

fastnoisenouveau should be a deterministic procedural utility subsystem focused on noise/field generation primitives, not a world-generation authority layer.

fastnoisenouveau should own:
- noise generation
- procedural math utilities
- fractal and domain-warp composition
- deterministic seeded generation
- caller-consumed scalar/vector field generation

fastnoisenouveau should not own:
- world generation policy
- terrain orchestration
- simulation authority
- rendering authority
- runtime orchestration
- content-system ownership

## 2. Repo Overview (Grounded)

Repository layout (`fastnoiselitenouveau/pom.xml`) is a 3-module Maven build:
- `noisegen-lib`: core noise library (`com.cognitivedynamics.noisegen.*`)
- `preview-tool`: JavaFX visualization app consuming the library
- `samples`: JavaFX sample apps consuming the library

Major public API/runtime surfaces in `noisegen-lib`:
- Core facade/config: `FastNoiseLite`, `NoiseConfig`, `NoiseTypes`, `NoiseUtils`, `Vector2/Vector3`
- Noise generators: simplex/perlin/value/cellular/wavelet/simplex4D (`generators` package)
- Composition stages: `fractal.FractalProcessor`, `warp.DomainWarpProcessor`, `transforms.*`
- Higher-scale utilities: `spatial.*` (`ChunkedNoise`, `DoublePrecisionNoise`, `LODNoise`, `HierarchicalNoise`, etc.)
- Node graph API: `graph.NoiseGraph`, `graph.NoiseNode`, node families (`source`, `combiner`, `modifier`, `fractal`, `warp`), `graph.util.BulkEvaluator`
- Derivatives support: `derivatives.NoiseDerivatives`, `SimplexDerivatives`

Deterministic/seeded surfaces:
- Seed-first evaluation APIs are widespread (for example `NoiseNode.evaluate* (int seed, ...)`).
- `NoiseGraph` carries default seed convenience while evaluation remains explicit.
- Thread-safety/determinism intent is reinforced by graph/thread-safety tests (`graph/integration/ThreadSafetyTest`).

Runtime/global-state surfaces:
- `FastNoiseLite` holds mutable per-instance `NoiseConfig`.
- No repository evidence of global mutable singleton state governing engine/runtime behavior.

## 3. Strict Ownership Statement

fastnoisenouveau should exclusively own:
- procedural noise generation algorithms
- deterministic seeded field evaluation
- fractal composition logic
- domain warping logic
- sampling/transformation utilities over noise fields
- reusable noise-node graph composition/evaluation
- derivative/gradient utility calculations for caller use

## 4. Explicit Non-Ownership

fastnoisenouveau must not own:
- world orchestration
- terrain/content assembly policy
- authoritative world-state mutation
- ECS authority
- physics authority
- rendering/GPU authority
- scene ownership
- persistence/session authority
- runtime orchestration

fastnoisenouveau must not become a hidden procedural world-engine.

## 5. Dependency Rules

Allowed dependency patterns:
- core math/hash/value/vector utilities
- deterministic seed/random utilities embedded in noise algorithms
- caller-provided configuration objects (seed/frequency/fractal settings)
- optional visualization/demo dependencies in non-core modules (`preview-tool`, `samples`)

Forbidden dependency patterns:
- direct dependencies on WorldEngine/ECS/Physics/Collision/SceneGraph/LightEngine/GPU subsystems
- runtime orchestration dependencies
- persistence/session ownership dependencies
- hidden global mutable state driving engine behavior

Repo-grounded observations:
- `noisegen-lib` dependency surface is minimal and self-contained (test-time JUnit only in `noisegen-lib/pom.xml`).
- No direct dependencies on Dynamis runtime authority subsystems were found.
- JavaFX dependencies are isolated to `preview-tool` and `samples`, not the core library authority boundary.

## 6. Public vs Internal Boundary Assessment

Public utility boundary is mostly clean for a library-style subsystem, with moderate API breadth.

Findings:
- Core API is clear and utility-oriented (`FastNoiseLite`, graph API, processors, transforms).
- Implementation packages are also directly consumable (`generators`, `spatial`, many graph node classes), so API surface is broad.
- Configuration is bounded to instance-level mutable objects (`NoiseConfig`) rather than global mutable runtime singletons.

Assessment: boundary is functionally clean as a utility library, but the public surface is wider than strictly necessary and could be tightened with a narrower API-first package strategy.

## 7. Authority Leakage or Overlap

No major authority leakage into broader engine ownership was found.

Checks against overlap targets:
- WorldEngine/Terrain orchestration: no orchestration ownership surfaces found in core library.
- ECS: no ECS ownership/dependency found.
- Rendering systems: no render/GPU authority in core library (UI preview/sample rendering is demo-only).
- Persistence/config systems: no persistence/session authority found.
- Simulation/runtime ownership: no simulation stepping or world mutation authority found.

State/determinism concerns:
- No hidden global singleton state was found.
- Instance-local mutable configuration exists (`FastNoiseLite` + `NoiseConfig`), which is acceptable for a utility library but should remain instance-scoped and explicit.
- Seeded APIs and thread-safety tests support deterministic utility semantics.

## 8. Relationship Clarification

- Terrain:
  - fastnoisenouveau should provide procedural scalar/vector fields.
  - fastnoisenouveau should not own biome/terrain assembly policy.

- World/content generation systems:
  - fastnoisenouveau should be consumed as a sampling engine.
  - Callers should own composition policy, runtime scheduling, and application of generated data.

- AssetPipeline:
  - fastnoisenouveau may support offline/procedural data generation inputs.
  - It should not own asset build orchestration.

- Rendering systems:
  - fastnoisenouveau should provide data that rendering systems consume.
  - It should not own render planning or GPU execution.

- ECS:
  - fastnoisenouveau should be input-only utility for systems using ECS data.
  - It should not own ECS state or lifecycle.

- WorldEngine:
  - fastnoisenouveau should be called by world/content systems under WorldEngine orchestration.
  - It should not directly orchestrate world lifecycle/runtime behavior.

## 9. Ratification Result

**Boundary ratified with minor tightening recommended**.

Justification:
- The repository is strongly aligned with deterministic procedural utility ownership (noise generation, fractals, warping, graph composition) and shows no direct overlap into world/simulation/render authority.
- Minor tightening is recommended due to broad public implementation exposure and mutable instance config patterns that should stay clearly bounded and non-authoritative.

## 10. Boundary Rules Going Forward

- fastnoisenouveau must remain a deterministic procedural utility layer.
- Callers must own terrain/world/content-generation policy and orchestration.
- fastnoisenouveau must not accumulate world/runtime orchestration responsibilities.
- fastnoisenouveau must not own authoritative world-state mutation.
- Public APIs should stay centered on sampling/generation primitives, not higher-level engine behavior.
- Mutable configuration must remain instance-scoped and explicit; no hidden global mutable control state.
- Demo/preview modules must remain non-authoritative consumers and must not redefine core library boundaries.
