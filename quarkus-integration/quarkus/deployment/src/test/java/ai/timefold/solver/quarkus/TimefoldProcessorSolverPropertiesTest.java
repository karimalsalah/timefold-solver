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

class TimefoldProcessorSolverPropertiesTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.timefold.solver.environment-mode", "FULL_ASSERT")
            .overrideConfigKey("quarkus.timefold.solver.daemon", "true")
            .overrideConfigKey("quarkus.timefold.solver.nearby-distance-meter-class",
                    "ai.timefold.solver.quarkus.testdomain.dummy.DummyDistanceMeter")
            .overrideConfigKey("quarkus.timefold.solver.move-thread-count", "2")
            .overrideConfigKey("quarkus.timefold.solver.domain-access-type", "REFLECTION")
            .overrideConfigKey("quarkus.timefold.solver.termination.spent-limit", "4h")
            .overrideConfigKey("quarkus.timefold.solver.termination.unimproved-spent-limit", "5h")
            .overrideConfigKey("quarkus.timefold.solver.termination.best-score-limit", "0")
            .overrideConfigKey("quarkus.timefold.solver.termination.diminished-returns.enabled", "true")
            .overrideConfigKey("quarkus.timefold.solver.termination.diminished-returns.sliding-window-duration", "6h")
            .overrideConfigKey("quarkus.timefold.solver.termination.diminished-returns.minimum-improvement-ratio", "0.5")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class, TestdataQuarkusSolution.class,
                            TestdataQuarkusConstraintProvider.class, DummyDistanceMeter.class));

    @Inject
    SolverConfig solverConfig;
    @Inject
    SolverFactory<TestdataQuarkusSolution> solverFactory;

    @Test
    void solverProperties() {
        assertThat(solverConfig.getEnvironmentMode()).isEqualTo(EnvironmentMode.FULL_ASSERT);
        assertThat(solverConfig.getDaemon()).isTrue();
        assertThat(solverConfig.getMoveThreadCount()).isEqualTo("2");
        assertThat(solverConfig.getNearbyDistanceMeterClass()).isNotNull();
        assertThat(solverFactory).isNotNull();
    }

    @Test
    void terminationProperties() {
        assertThat(solverConfig.getTerminationConfig().getSpentLimit()).isEqualTo(Duration.ofHours(4));
        assertThat(solverConfig.getTerminationConfig().getUnimprovedSpentLimit()).isEqualTo(Duration.ofHours(5));
        assertThat(solverConfig.getTerminationConfig().getBestScoreLimit()).isEqualTo(SimpleScore.of(0).toString());

        var terminationConfig = solverConfig.getTerminationConfig();
        assertThat(terminationConfig).isNotNull();
        assertThat(terminationConfig.getDiminishedReturnsConfig()).isNotNull();
        assertThat(terminationConfig.getDiminishedReturnsConfig().getSlidingWindowDuration()).isEqualTo(Duration.ofHours(6));
        assertThat(terminationConfig.getDiminishedReturnsConfig().getMinimumImprovementRatio()).isEqualTo(0.5);
    }
}
