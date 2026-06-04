#!/bin/sh
# Run the JVM benchmark suite with JMH's GC profiler.
#
# Reports alloc/op alongside wall-time, which is what validates A1/A2:
# the buildString() in indexKey() is small enough that wall-time deltas may sit in
# noise, but the allocations/op delta will be unambiguous.
#
# Output columns of interest (look for these on the gc.* rows):
#   gc.alloc.rate.norm   bytes allocated per @Benchmark op (lower = better)
#   gc.count             GC events during the run
#
# kotlinx-benchmark Gradle plugin doesn't expose `-prof gc` via a config key, so we
# run JMH against the generated shadow jar directly.
#
# Run from projects/ root.

set -e

# 1. Build the JMH shadow jar (without running it).
./gradlew :core:benchmark:jvmBenchmarkJar

# 2. Locate the shadow jar (path layout is stable for kotlinx-benchmark 0.4.x).
JAR=$(find core/benchmark/build/benchmarks/jvm/jars -name '*.jar' | head -1)
if [ -z "$JAR" ]; then
  echo "No benchmark jar found. Adjust the find path for your kotlinx-benchmark version."
  exit 1
fi

# 3. Run JMH directly with the GC profiler. Default to JvmBenchmark.* — pass a regex
#    as the first arg to override (e.g. ./benchmark-gc.sh ".*startup_with_binds.*").
PATTERN="${1:-.*JvmBenchmark.*}"
java -jar "$JAR" -prof gc -f 1 -wi 5 -i 5 "$PATTERN"
