package ai.timefold.solver.quarkus;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

import jakarta.inject.Inject;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.SimpleScore;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.api.solver.SolverJob;
import ai.timefold.solver.core.api.solver.SolverManager;
import ai.timefold.solver.core.impl.solver.DefaultSolutionManager;
import ai.timefold.solver.core.impl.solver.DefaultSolverFactory;
import ai.timefold.solver.core.impl.solver.DefaultSolverManager;
import ai.timefold.solver.quarkus.testdomain.gizmo.DummyConstraintProvider;
import ai.timefold.solver.quarkus.testdomain.gizmo.TestDataKitchenSinkEntity;
import ai.timefold.solver.quarkus.testdomain.gizmo.TestDataKitchenSinkSolution;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

class TimefoldProcessorGizmoKitchenSinkTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.timefold.solver.termination.best-score-limit", "0hard/0soft")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestDataKitchenSinkEntity.class,
                            TestDataKitchenSinkSolution.class,
                            DummyConstraintProvider.class));

    @Inject
    SolverFactory<TestDataKitchenSinkSolution> solverFactory;
    @Inject
    SolverManager<TestDataKitchenSinkSolution> solverManager;
    @Inject
    SolutionManager<TestDataKitchenSinkSolution, SimpleScore> solutionManager;

    @Test
    void singletonSolverFactory() {
        assertThat(solverFactory).isNotNull();
        // There is only one ScoreDirectorFactory instance
        assertThat(((DefaultSolutionManager<?, ?>) solutionManager).getScoreDirectorFactory())
                .isSameAs(((DefaultSolverFactory<?>) solverFactory).getScoreDirectorFactory());
        assertThat(solverManager).isNotNull();
        // There is only one SolverFactory instance
        assertThat(((DefaultSolverManager<TestDataKitchenSinkSolution>) solverManager).getSolverFactory())
                .isSameAs(solverFactory);
    }

    @Test
    void solve() throws ExecutionException, InterruptedException {
        TestDataKitchenSinkSolution problem = new TestDataKitchenSinkSolution(
                new TestDataKitchenSinkEntity(),
                Collections.emptyList(),
                "Test",
                Collections.emptyList(),
                HardSoftScore.ZERO);

        SolverJob<TestDataKitchenSinkSolution> solverJob = solverManager.solve(1L, problem);
        TestDataKitchenSinkSolution solution = solverJob.getFinalBestSolution();
        assertThat(solution).isNotNull();
        assertThat(solution.getPlanningEntityProperty().testGetIntVariable()).isEqualTo(1);
        assertThat(solution.getPlanningEntityProperty().testGetStringVariable()).isEqualTo("A");
    }

}
