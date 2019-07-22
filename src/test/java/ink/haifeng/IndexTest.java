package ink.haifeng;

import ink.haifeng.Application;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.VertexLabel;
import org.janusgraph.core.schema.*;
import org.janusgraph.graphdb.database.management.ManagementSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;

import static org.janusgraph.core.attribute.Text.textFuzzy;

/**
 * @Author: haifeng
 * @Date: 2019-07-20 17:33
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class IndexTest {

    @Autowired
    private JanusGraph graph;

    @Autowired
    private GraphTraversalSource g;

    private String label = "Index_Label";

    private String compositeIndex = "composite-index";

    private String mixIndex = "mix-index";

    @Test
    public void createLabel() {
        JanusGraphManagement management = graph.openManagement();
        PropertyKey vertex_name = management.makePropertyKey("vertex_name").cardinality(Cardinality.SINGLE).dataType(String.class).make();
        PropertyKey group = management.makePropertyKey("group").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        VertexLabel label = management.makeVertexLabel(this.label).make();
        management.addProperties(label, vertex_name, group);
        management.commit();
    }


    @Test
    public void createCompositeIndex() throws InterruptedException, ExecutionException {
        graph.tx().rollback();
        JanusGraphManagement management = graph.openManagement();
        PropertyKey vertexName = management.getPropertyKey("vertex_name");
        PropertyKey group = management.getPropertyKey("group");
        management.buildIndex(compositeIndex, Vertex.class).addKey(vertexName).addKey(group).unique().buildCompositeIndex();
        management.commit();
        ManagementSystem.awaitGraphIndexStatus(graph, compositeIndex).call();
        //Reindex the existing data
        management = graph.openManagement();
        management.updateIndex(management.getGraphIndex(compositeIndex), SchemaAction.REINDEX).get();
        management.commit();
    }

    @Test
    public void createMixIndex() throws InterruptedException, ExecutionException {
        graph.tx().rollback();
        JanusGraphManagement management = graph.openManagement();
        PropertyKey vertexName = management.getPropertyKey("vertex_name");
        PropertyKey group = management.getPropertyKey("group");
        management.buildIndex(mixIndex, Vertex.class)
                .addKey(vertexName, Mapping.TEXTSTRING.asParameter())
                .addKey(group).buildMixedIndex("search");
        management.commit();


        ManagementSystem.awaitGraphIndexStatus(graph, mixIndex).call();
        //Reindex the existing data
        management = graph.openManagement();
        management.updateIndex(management.getGraphIndex(mixIndex), SchemaAction.REINDEX).get();
        management.commit();
    }


    @Test
    public void reindexComposite() throws ExecutionException, InterruptedException {
        //ManagementSystem.awaitGraphIndexStatus(graph, compositeIndex).call();
        JanusGraphManagement management = graph.openManagement();
        management.updateIndex(management.getGraphIndex(compositeIndex), SchemaAction.REGISTER_INDEX).get();
        management.commit();
    }


    @Test
    public void printSchema() {
        JanusGraphManagement management = graph.openManagement();
        System.out.println("****************************************************************");
        String s = management.printSchema();
        System.out.println(s);

        System.out.println("****************************************************************");
        String s2 = management.printVertexLabels();
        System.out.println(s2);
        System.out.println("****************************************************************");
        for (VertexLabel label : management.getVertexLabels()) {
            label.mappedProperties().forEach(p -> {
                System.out.println(p.cardinality() + "\t" + p.dataType() + "\t" + label.name() + "\t" + p.name());
            });
        }
        System.out.println("****************************************************************");
    }


    @Test
    public void addV() {
        for (int i = 0; i < 5; i++) {
            g.addV(label).property("vertex_name", "haifeng" + i).property("group", i).next();
        }
    }

    @Test
    public void query() {
        System.out.println(graph.traversal().V().has("vertex_name", textFuzzy("haifeng")).has("group", P.neq(1)).valueMap().toList());
    }
}
