package com.cognitivedynamics.noisegen.graph;

import com.cognitivedynamics.noisegen.graph.nodes.source.ConstantNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for NoiseNode interface contract and ConstantNode implementation.
 */
class NoiseNodeTest {

    private NoiseGraph graph;

    @BeforeEach
    void setUp() {
        graph = NoiseGraph.create(1337);
    }

    @Nested
    @DisplayName("NoiseGraph factory tests")
    class NoiseGraphTests {

        @Test
        @DisplayName("create() should return a NoiseGraph with default seed")
        void createShouldReturnNoiseGraphWithDefaultSeed() {
            NoiseGraph g = NoiseGraph.create();
            assertEquals(1337, g.getSeed());
        }

        @Test
        @DisplayName("create(seed) should return a NoiseGraph with specified seed")
        void createWithSeedShouldReturnNoiseGraphWithSeed() {
            NoiseGraph g = NoiseGraph.create(42);
            assertEquals(42, g.getSeed());
        }

        @Test
        @DisplayName("constant() should create a ConstantNode")
        void constantShouldCreateConstantNode() {
            ConstantNode node = graph.constant(3.14);
            assertNotNull(node);
            assertEquals(3.14, node.getValue());
        }
    }

    @Nested
    @DisplayName("ConstantNode tests")
    class ConstantNodeTests {

        @Test
        @DisplayName("should return constant value for 2D evaluation")
        void shouldReturnConstantFor2D() {
            ConstantNode node = new ConstantNode(0.5);
            assertEquals(0.5, node.evaluate2D(1337, 0, 0));
            assertEquals(0.5, node.evaluate2D(1337, 100, 200));
            assertEquals(0.5, node.evaluate2D(42, -500, 500));
        }

        @Test
        @DisplayName("should return constant value for 3D evaluation")
        void shouldReturnConstantFor3D() {
            ConstantNode node = new ConstantNode(-0.75);
            assertEquals(-0.75, node.evaluate3D(1337, 0, 0, 0));
            assertEquals(-0.75, node.evaluate3D(1337, 100, 200, 300));
            assertEquals(-0.75, node.evaluate3D(42, -500, 500, -500));
        }

        @Test
        @DisplayName("should return constant value for 4D evaluation")
        void shouldReturnConstantFor4D() {
            ConstantNode node = new ConstantNode(1.0);
            assertEquals(1.0, node.evaluate4D(1337, 0, 0, 0, 0));
            assertEquals(1.0, node.evaluate4D(1337, 100, 200, 300, 400));
        }

        @Test
        @DisplayName("should support 4D")
        void shouldSupport4D() {
            ConstantNode node = new ConstantNode(0);
            assertTrue(node.supports4D());
        }

        @Test
        @DisplayName("should return correct node type")
        void shouldReturnCorrectNodeType() {
            ConstantNode node = new ConstantNode(0);
            assertEquals("Constant", node.getNodeType());
        }

        @ParameterizedTest
        @ValueSource(doubles = {-1.0, -0.5, 0.0, 0.5, 1.0, Double.MAX_VALUE, Double.MIN_VALUE})
        @DisplayName("should handle various constant values")
        void shouldHandleVariousValues(double value) {
            ConstantNode node = new ConstantNode(value);
            assertEquals(value, node.evaluate2D(0, 0, 0));
            assertEquals(value, node.getValue());
        }

        @Test
        @DisplayName("should have meaningful toString")
        void shouldHaveMeaningfulToString() {
            ConstantNode node = new ConstantNode(0.5);
            String str = node.toString();
            assertTrue(str.contains("Constant"));
            assertTrue(str.contains("0.5"));
        }
    }

    @Nested
    @DisplayName("NoiseNode interface contract tests")
    class InterfaceContractTests {

        @Test
        @DisplayName("nodes should be usable as NoiseNode interface")
        void nodesShouldBeUsableAsInterface() {
            NoiseNode node = graph.constant(1.0);
            assertNotNull(node.getNodeType());
            assertDoesNotThrow(() -> node.evaluate2D(0, 0, 0));
            assertDoesNotThrow(() -> node.evaluate3D(0, 0, 0, 0));
        }

        @Test
        @DisplayName("getNodeType should not be null or empty")
        void getNodeTypeShouldNotBeNullOrEmpty() {
            NoiseNode node = graph.constant(0);
            String type = node.getNodeType();
            assertNotNull(type);
            assertFalse(type.isEmpty());
        }
    }
}
