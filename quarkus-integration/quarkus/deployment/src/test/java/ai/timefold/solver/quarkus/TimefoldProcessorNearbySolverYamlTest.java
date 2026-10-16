
package ai.timefold.solver.quarkus;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import jakarta.inject.Inject;

import ai.timefold.solver.core.api.score.SimpleScore;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.EnvironmentMode;
import ai.timefold.solver.core.config.solver.SolverConfig;
import ai.timefold.solver.quarkus.testdomain.dummy.DummyDistanceMeter;
import ai.timefold.solver.quarkus.testdomain.normal.TestdataQuarkusConstraintProvider;
import ai.timefold.solver.quarkus.testdomain.normal.TestdataQuarkusEntity;
import ai.timefold.solver.quarkus.testdomain.normal.TestdataQuarkusSolution;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

class TimefoldProcessorNearbySolverYamlTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class, TestdataQuarkusSolution.class,
                            TestdataQuarkusConstraintProvider.class, DummyDistanceMeter.class)
                    .addAsResource("ai/timefold/solver/quarkus/single-solver/application-nearby.yaml", "application.yaml"));

    @Inject
    SolverConfig solverConfig;
    @Inject
    SolverFactory<TestdataQuarkusSolution> solverFactory;

    @Test
    void solverProperties() {
        assertThat(solverConfig.getEnvironmentMode()).isEqualTo(EnvironmentMode.FULL_ASSERT);
        assertThat(solverConfig.getNearbyDistanceMeterClass()).isNotNull();
        assertThat(solverConfig.getDaemon()).isTrue();
        assertThat(solverConfig.getMoveThreadCount()).isEqualTo("2");

        assertThat(solverFactory).isNotNull();
    }

    @Test
    void terminationProperties() {
        assertThat(solverConfig.getTerminationConfig().getSpentLimit()).isEqualTo(Duration.ofHours(4));
        assertThat(solverConfig.getTerminationConfig().getUnimprovedSpentLimit()).isEqualTo(Duration.ofHours(5));
        assertThat(solverConfig.getTerminationConfig().getBestScoreLimit()).isEqualTo(SimpleScore.of(0).toString());
    }
}
