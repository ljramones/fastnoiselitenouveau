# FastNoiseLite Nouveau

A modular Java refactoring of [FastNoiseLite](https://github.com/Auburn/FastNoiseLite) with significant extensions for procedural content generation at astronomical scales.
The original was a 2600-line library with little in code documentation, and this refactoring aims to improve maintainability, performance, and usability. It also includes a comprehensive guide for using the library. I needed additional noise generators and algorithms for my own projects.
I still have to push this to Maven Central, but you can use it directly from GitHub.

## Background

This library was developed for the **TRIPS (Terran Republic Interstellar Plotting System)** application, a 3D interstellar visualization and exploration tool. The original FastNoiseLite provided an excellent foundation, but TRIPS required additional capabilities for:

- **Procedural planet generation** - Creating realistic terrain, atmospheres, and surface features for explorable worlds
- **Interstellar-scale nebulae** - Rendering gas clouds spanning light-years with proper curl-based particle motion
- **Astronomical coordinate precision** - Working with coordinates at scales of billions of units without floating-point degradation
- **Real-time LOD systems** - Dynamically adjusting detail levels as users navigate from galaxy-scale views down to planetary surfaces

These requirements led to the extensions documented below, which add 4D noise, spatial utilities for infinite worlds, analytical derivatives for lighting, and advanced algorithms like curl noise for fluid-like motion.

## Roadmap

Planned features for future releases:

- [ ] **Noise Preview Tool** - Interactive visualizer to explore noise generator combinations, tweak parameters in real-time, and see the resulting patterns (heightmaps, textures, 3D volumes)
- [x] **Performance Benchmarks** - Comprehensive benchmarking suite similar to the original FastNoiseLite, comparing noise types, fractal modes, and extension algorithms across different scenarios

## Benchmarks

Run benchmarks with:
```bash
mvn compile test-compile exec:java
```

Example results (Apple M4 Max, 128GB unified memory, Java 17):

| 2D Noise Type | M points/s | 3D Noise Type | M points/s |
|---------------|------------|---------------|------------|
| Value | 1112.88 | Value | 157.18 |
| Perlin | 373.37 | Perlin | 86.42 |
| OpenSimplex2 | 275.76 | OpenSimplex2 | 73.91 |
| OpenSimplex2S | 210.47 | OpenSimplex2S | 64.06 |
| Cellular | 116.75 | Cellular | 18.98 |

| Fractal (3D, 4 oct) | M points/s | Spatial Utility | M points/s |
|---------------------|------------|-----------------|------------|
| FBm | 14.19 | ChunkedNoise | 199.17 |
| Ridged | 13.74 | DoublePrecision | 239.57 |
| HybridMulti [EXT] | 14.77 | TiledNoise | 68.59 |

**4D Simplex [EXT]:** 62.64 M points/s

## Features

### Original FastNoiseLite Features
- Multiple noise types (OpenSimplex2, Perlin, Cellular/Voronoi, Value)
- Fractal noise combining (FBm, Ridged, PingPong)
- Domain warping for organic distortion
- Deterministic output (same seed = same results)
- Fast performance suitable for real-time applications

### Extensions for TRIPS

| Feature | Description | Use Case |
|---------|-------------|----------|
| **4D Simplex Noise** | Full 4D noise with fractal support | Animated volumetrics, looping effects |
| **Double Precision** | Coordinates at astronomical scales | Interstellar distances (10^15 units) |
| **Chunked Noise** | Infinite worlds without precision loss | Seamless galaxy-scale exploration |
| **LOD Noise** | Distance-based octave reduction | Performance optimization for distant objects |
| **Tiled Noise** | Seamlessly tileable textures | Planetary surface textures |
| **Curl Noise** | Divergence-free flow fields | Nebula particle systems, atmospheres |
| **Noise Derivatives** | Analytical gradients and normal maps | Terrain lighting, bump mapping |
| **Billow/HybridMulti** | Additional fractal types | Clouds, eroded terrain |
| **Noise Transforms** | Post-processing pipeline | Ridge enhancement, terracing, quantization |
| **Wavelet Noise** | Band-limited, mipmap-safe noise | Texture synthesis without aliasing |

## Quick Start

### Maven Dependency

```xml
<dependency>
    <groupId>com.cognitivedynamics</groupId>
    <artifactId>fastnoiselitenouveau</artifactId>
    <version>1.1.1</version>
</dependency>
```

### Basic Usage

```java
import com.cognitivedynamics.noisegen.FastNoiseLite;

// Create noise generator with seed
FastNoiseLite noise = new FastNoiseLite(1337);

// Configure for terrain
noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
noise.SetFractalType(FastNoiseLite.FractalType.FBm);
noise.SetFractalOctaves(6);
noise.SetFrequency(0.005f);

// Generate noise
float value2D = noise.GetNoise(x, y);
float value3D = noise.GetNoise(x, y, z);
float value4D = noise.GetNoise(x, y, z, time);  // [EXT] 4D support
```

### Astronomical-Scale Coordinates

```java
import com.cognitivedynamics.noisegen.spatial.DoublePrecisionNoise;

FastNoiseLite base = new FastNoiseLite(1337);
DoublePrecisionNoise precise = new DoublePrecisionNoise(base);

// Works at interstellar distances
double x = 1_000_000_000_000.5;  // 1 trillion + 0.5
double y = 2_500_000_000_000.3;
float value = precise.getNoise(x, y);  // Precise to the decimal
```

### Nebula Particle Motion with Curl Noise

```java
import com.cognitivedynamics.noisegen.spatial.TurbulenceNoise;

FastNoiseLite base = new FastNoiseLite(1337);
TurbulenceNoise turbulence = new TurbulenceNoise(base);
turbulence.setFrequency(0.001f);  // Large-scale swirls

// Get divergence-free velocity for particle advection
float[] velocity = turbulence.curl3D(x, y, z);
// Particles following this field won't bunch up or disperse
```

## Build

```bash
mvn clean compile    # Compile
mvn test             # Run all tests (499 tests)
mvn package          # Build JAR
```

Requires Java 17+.

---

## Comprehensive Guide

### Table of Contents

1. [Noise Types](#noise-types)
2. [Fractal Noise](#fractal-noise)
3. [Domain Warp](#domain-warp)
4. [4D Noise](#4d-noise)
5. [Noise Transforms](#noise-transforms)
6. [Spatial Utilities](#spatial-utilities)
7. [Advanced Algorithms](#advanced-algorithms)
8. [Noise Derivatives](#noise-derivatives)
9. [Configuration Reference](#configuration-reference)
10. [Package Structure](#package-structure)

---

## Cheat Sheet: What To Use When

| Goal | Primary Tool / Class | Key Setting / Method |
|------|---------------------|----------------------|
| **Smooth terrain / clouds** | OpenSimplex2 + FBm | `SetFractalOctaves(5-8)` |
| **Sharp ridges / mountains** | Ridged or Billow | `SetFractalType(Ridged)` |
| **Soft puffy clouds** | Billow fractal | `SetFractalType(Billow)` |
| **Eroded terrain** | HybridMulti | `SetFractalType(HybridMulti)` |
| **Mesa / plateau steps** | TerraceTransform | `TerraceTransform.contours(8)` |
| **Animated volumetrics** | 4D noise | `GetNoise(x, y, z, time)` |
| **Looping animations** | TiledNoise (4D torus) | `tiled.getNoise(x, y, z, time)` |
| **Infinite world, huge coords** | ChunkedNoise | `new ChunkedNoise(base, 1024)` |
| **Astronomical distances** | DoublePrecisionNoise | `precise.getNoise(1e12, 2e12)` |
| **Distant detail optimization** | LODNoise | `lod.getNoise(x, y, distance)` |
| **Tileable planetary textures** | TiledNoise | `tiled.getSeamlessImage(1024, 1024)` |
| **Realistic lighting / bumps** | NoiseDerivatives | `deriv.computeNormal2D(x, y, scale)` |
| **Normal map textures** | NoiseDerivatives | `deriv.generateNormalMapRGB(...)` |
| **Nebula filaments** | Ridged + curl noise | `turbulence.curl3D(x, y, z)` |
| **Fluid / smoke motion** | TurbulenceNoise | `turbulence.curlFBm3D(...)` |
| **Voronoi cells / cracks** | Cellular noise | `SetNoiseType(Cellular)` |
| **Organic biome boundaries** | Cellular + Domain Warp | `DomainWarp()` → `Cellular` |

---

## Noise Types

### OpenSimplex2 (Default)

The recommended general-purpose noise. Produces smooth, natural-looking patterns.

```java
noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
```

**Best for:** Terrain, clouds, general procedural textures

### OpenSimplex2S

A smoother variant with slightly different characteristics.

```java
noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
```

### Perlin

Classic Perlin noise with a slightly different visual character.

```java
noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
```

### Cellular (Voronoi)

Creates cell-like patterns based on distance to random points.

```java
noise.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
noise.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Euclidean);
noise.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance);
noise.SetCellularJitter(1.0f);
```

**Best for:** Stone textures, biological patterns, cracked surfaces

### Value / ValueCubic

Simple value noise using interpolated random values at grid points.

```java
noise.SetNoiseType(FastNoiseLite.NoiseType.Value);
noise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);  // Smoother
```

---

## Fractal Noise

Fractal noise combines multiple octaves at different frequencies for more natural patterns.

### FBm (Fractional Brownian Motion)

```java
noise.SetFractalType(FastNoiseLite.FractalType.FBm);
noise.SetFractalOctaves(4);
noise.SetFractalLacunarity(2.0f);
noise.SetFractalGain(0.5f);
```

### Ridged

Creates ridge-like patterns for mountains and veins.

```java
noise.SetFractalType(FastNoiseLite.FractalType.Ridged);
```

### PingPong

Creates banded, terraced patterns.

```java
noise.SetFractalType(FastNoiseLite.FractalType.PingPong);
noise.SetFractalPingPongStrength(2.0f);
```

### Billow *(Extension)*

Soft, cloud-like patterns - the inverse of ridged.

```java
noise.SetFractalType(FastNoiseLite.FractalType.Billow);
```

### Hybrid Multifractal *(Extension)*

Multiplicative/additive blend where flat areas stay flat but detailed areas get more detail.

```java
noise.SetFractalType(FastNoiseLite.FractalType.HybridMulti);
```

**Best for:** Realistic terrain, erosion-like detail distribution

---

## Domain Warp

Domain warping distorts input coordinates before sampling, creating organic patterns.

```java
FastNoiseLite noise = new FastNoiseLite(1337);
noise.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2);
noise.SetDomainWarpAmp(30.0f);

Vector2 coord = new Vector2(x, y);
noise.DomainWarp(coord);
float value = noise.GetNoise(coord.x, coord.y);
```

---

## 4D Noise

*(Extension)* - 4D noise adds a fourth dimension, typically used for time-based animations.

```java
// Animated 3D noise
float value = noise.GetNoise(x, y, z, time * 0.5f);

// Looping animation (circular path through 4D)
float angle = progress * 2.0f * (float) Math.PI;
float w = (float) Math.sin(angle) * loopRadius;
float extraDim = (float) Math.cos(angle) * loopRadius;
float looping = noise.GetNoise(x, y, z + extraDim, w);
```

---

## Noise Transforms

*(Extension)* - Post-processing transforms for noise values.

```java
import com.cognitivedynamics.noisegen.transforms.*;

// Range remapping
NoiseTransform normalize = new RangeTransform(-1f, 1f, 0f, 1f);

// Power curves
NoiseTransform sharp = new PowerTransform(2.0f);

// Ridge patterns
NoiseTransform ridges = new RidgeTransform();

// Terracing
NoiseTransform terrace = new TerraceTransform(8);

// Chain multiple transforms
ChainedTransform pipeline = new ChainedTransform(
    new RidgeTransform(),
    new PowerTransform(2.0f),
    new RangeTransform(0f, 1f, 0f, 255f)
);
float result = pipeline.apply(noiseValue);
```

---

## Spatial Utilities

*(Extension)* - Utilities for large-scale or specialized coordinate systems.

### ChunkedNoise

Handles infinite worlds without float precision degradation.

```java
ChunkedNoise chunked = new ChunkedNoise(baseNoise, 1000.0);
float value = chunked.getNoise(1_000_000_000.0, 2_500_000_000.0);
```

### LODNoise

Automatically reduces octaves based on distance.

```java
LODNoise lod = new LODNoise(baseNoise, 8);
float nearValue = lod.getNoise(x, y, 0.0f);    // Full detail
float farValue = lod.getNoise(x, y, 500.0f);   // Reduced detail
```

### TiledNoise

Creates seamlessly tileable noise for textures.

```java
TiledNoise tiled = new TiledNoise(baseNoise, 256, 256);
float v1 = tiled.getNoise(0, 128);    // Left edge
float v2 = tiled.getNoise(256, 128);  // Right edge (equals v1)

// Generate seamless image
byte[] grayscale = tiled.getSeamlessImage(256, 256);
byte[] terrain = tiled.getSeamlessImageRGB(256, 256, TiledNoise.TERRAIN_GRADIENT);
```

### DoublePrecisionNoise

Double-precision coordinates for astronomical scales.

```java
DoublePrecisionNoise precise = new DoublePrecisionNoise(baseNoise);
float value = precise.getNoise(1_000_000_000_000.5, 2_500_000_000_000.3);
```

---

## Advanced Algorithms

*(Extension)* - Specialized algorithms for demanding use cases.

### TurbulenceNoise (Curl Noise)

Divergence-free flow fields for fluid-like motion.

```java
TurbulenceNoise turbulence = new TurbulenceNoise(baseNoise);

// Curl noise for particle advection
float[] velocity = turbulence.curl3D(x, y, z);

// Multi-octave curl
float[] curlFBm = turbulence.curlFBm3D(x, y, z, 4);
```

**Best for:** Nebula visualization, smoke/fire effects, atmospheric particles

### WaveletNoiseGen

Band-limited noise for clean mipmapping.

```java
WaveletNoiseGen wavelet = new WaveletNoiseGen(1337, 128);
float fbm = wavelet.sampleFBm2D(x, y, 4, 2.0f, 0.5f);
```

### HierarchicalNoise

Quadtree/octree-based adaptive sampling.

```java
HierarchicalNoise hier = new HierarchicalNoise(baseNoise, 8);
float coarse = hier.sampleLevel(x, y, 0);  // Continents
float fine = hier.sampleLevel(x, y, 7);    // Pebbles
float adaptive = hier.sampleAdaptive(x, y, viewScale);
```

### SparseConvolutionNoise

Memory-efficient noise with constant memory regardless of world size.

```java
SparseConvolutionNoise sparse = new SparseConvolutionNoise(1337);
float value = sparse.getNoise(x, y);
```

---

## Noise Derivatives

*(Extension)* - Analytical gradients for lighting and normal maps.

```java
import com.cognitivedynamics.noisegen.derivatives.NoiseDerivatives;

NoiseDerivatives deriv = new NoiseDerivatives(noise);

// Get noise value and gradient together
NoiseDerivatives.NoiseWithGradient2D result = deriv.getNoiseWithGradient2D(x, y);
float value = result.value;
float dx = result.dx;
float dy = result.dy;

// Compute surface normal for terrain lighting
float[] normal = deriv.computeNormal2D(x, y, heightScale);

// Generate normal map texture
byte[] normalMap = deriv.generateNormalMapRGB(width, height, worldSize, heightScale);
```

---

## Configuration Reference

### Frequency Guidelines

| Scale | Frequency | Use Case |
|-------|-----------|----------|
| Continent | 0.001 - 0.003 | Large landmasses |
| Region | 0.003 - 0.01 | Mountain ranges, biomes |
| Local | 0.01 - 0.05 | Hills, forests |
| Detail | 0.05 - 0.2 | Rocks, grass |
| Fine | 0.2 - 1.0 | Textures, small details |

---

## Package Structure

```
com.cognitivedynamics.noisegen/
├── FastNoiseLite.java       # Main facade (use this!)
├── NoiseConfig.java         # Configuration holder
├── NoiseTypes.java          # Enum definitions
├── Vector2.java, Vector3.java
├── generators/
│   ├── NoiseGenerator.java      # Interface
│   ├── SimplexNoiseGen.java     # OpenSimplex2/2S
│   ├── Simplex4DNoiseGen.java   # [EXT] 4D Simplex
│   ├── WaveletNoiseGen.java     # [EXT] Band-limited
│   ├── CellularNoiseGen.java    # Voronoi
│   ├── PerlinNoiseGen.java      # Classic Perlin
│   └── ValueNoiseGen.java       # Value/ValueCubic
├── fractal/
│   └── FractalProcessor.java    # FBm, Ridged, PingPong, Billow, HybridMulti
├── transforms/                   # [EXT]
│   ├── NoiseTransform.java, RangeTransform.java, PowerTransform.java
│   ├── RidgeTransform.java, TurbulenceTransform.java, ClampTransform.java
│   ├── InvertTransform.java, ChainedTransform.java
│   ├── TerraceTransform.java, QuantizeTransform.java
├── spatial/                      # [EXT]
│   ├── ChunkedNoise.java, LODNoise.java, TiledNoise.java
│   ├── DoublePrecisionNoise.java, SparseConvolutionNoise.java
│   ├── HierarchicalNoise.java, TurbulenceNoise.java
├── derivatives/                  # [EXT]
│   ├── NoiseDerivatives.java, SimplexDerivatives.java
└── warp/
    └── DomainWarpProcessor.java
```

---

## License

MIT License - See source files for full license text.

Based on [FastNoiseLite](https://github.com/Auburn/FastNoiseLite) by Jordan Peck.
