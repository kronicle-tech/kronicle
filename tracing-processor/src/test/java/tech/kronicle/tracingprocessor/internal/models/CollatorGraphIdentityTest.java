package tech.kronicle.tracingprocessor.internal.models;

import org.junit.jupiter.api.Test;
import tech.kronicle.sdk.models.GraphNode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.kronicle.tracingprocessor.internal.testutils.CollatorGraphEdgeIdentityUtils.createCollatorEdgeIdentity;
import static tech.kronicle.tracingprocessor.internal.testutils.CollatorGraphEdgeUtils.createCollatorEdge;
import static tech.kronicle.tracingprocessor.internal.testutils.GraphNodeUtils.createNode;

public class CollatorGraphIdentityTest {

    @Test
    public void fromCollatorGraphShouldReturnAllNodesFromTheSuppliedGraph() {
        // Given
        GraphNode node1 = createNode(1);
        GraphNode node2 = createNode(2);
        GraphNode node3 = createNode(3);
        CollatorGraph graph = new CollatorGraph(
                List.of(
                        node1,
                        node2,
                        node3
                ),
                List.of(
                        createCollatorEdge(1)
                ),
                1
        );

        // When
        CollatorGraphIdentity returnValue = CollatorGraphIdentity.fromCollatorGraph(graph);

        // Then
        assertThat(returnValue).isEqualTo(
                new CollatorGraphIdentity(
                        List.of(
                                node1,
                                node2,
                                node3
                        ),
                        List.of(
                                createCollatorEdgeIdentity(1)
                        )
                )
        );
    }

    @Test
    public void fromCollatorGraphShouldReturnAllIdentitiesForTheEdgesFromTheSuppliedGraph() {
        // Given
        GraphNode node1 = createNode(1);
        CollatorGraph graph = new CollatorGraph(
                List.of(
                        node1
                ),
                List.of(
                        createCollatorEdge(1),
                        createCollatorEdge(2),
                        createCollatorEdge(3)
                ),
                1
        );

        // When
        CollatorGraphIdentity returnValue = CollatorGraphIdentity.fromCollatorGraph(graph);

        // Then
        assertThat(returnValue).isEqualTo(
                new CollatorGraphIdentity(
                        List.of(
                                node1
                        ),
                        List.of(
                                createCollatorEdgeIdentity(1),
                                createCollatorEdgeIdentity(2),
                                createCollatorEdgeIdentity(3)
                        )
                )
        );
    }

    @Test
    public void fromCollatorGraphShouldDedupTheEdgeIdentities() {
        // Given
        GraphNode node1 = createNode(1);
        CollatorGraph graph = new CollatorGraph(
                List.of(node1),
                List.of(
                        createCollatorEdge(1,11),
                        createCollatorEdge(1, 12),
                        createCollatorEdge(2, 13),
                        createCollatorEdge(2, 14)
                ),
                1
        );

        // When
        CollatorGraphIdentity returnValue = CollatorGraphIdentity.fromCollatorGraph(graph);

        // Then
        assertThat(returnValue).isEqualTo(
                new CollatorGraphIdentity(
                        List.of(node1),
                        List.of(
                                createCollatorEdgeIdentity(1),
                                createCollatorEdgeIdentity(2)
                        )
                )
        );
    }
}
