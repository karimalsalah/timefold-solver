package ai.timefold.solver.quarkus;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import jakarta.inject.Inject;

import ai.timefold.solver.core.api.score.SimpleScore;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.api.solver.SolverManager;
import ai.timefold.solver.core.impl.solver.DefaultSolutionManager;
import ai.timefold.solver.core.impl.solver.DefaultSolverFactory;
import ai.timefold.solver.core.impl.solver.DefaultSolverManager;
import ai.timefold.solver.quarkus.testdomain.inheritance.solution.TestdataExtendedQuarkusSolution;
import ai.timefold.solver.quarkus.testdomain.normal.TestdataQuarkusConstraintProvider;
import ai.timefold.solver.quarkus.testdomain.normal.TestdataQuarkusEntity;
import ai.timefold.solver.quarkus.testdomain.normal.TestdataQuarkusSolution;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

class TimefoldProcessorExtendedSolutionSolveTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.timefold.solver.termination.best-score-limit", "0")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class,
                            TestdataQuarkusSolution.class, TestdataExtendedQuarkusSolution.class,
                            TestdataQuarkusConstraintProvider.class));

    @Inject
    SolverFactory<TestdataQuarkusSolution> solverFactory;
    @Inject
    SolverManager<TestdataQuarkusSolution> solverManager;
    @Inject
    SolutionManager<TestdataQuarkusSolution, SimpleScore> solutionManager;

    @Test
    void singletonSolverFactory() {
        assertThat(solverFactory).isNotNull();
        // There is only one ScoreDirectorFactory instance
        assertThat(((DefaultSolutionManager<TestdataQuarkusSolution, SimpleScore>) solutionManager).getScoreDirectorFactory())
                .isSameAs(((DefaultSolverFactory<TestdataQuarkusSolution>) solverFactory).getScoreDirectorFactory());
        assertThat(solverManager).isNotNull();
        // There is only one SolverFactory instance
        assertThat(((DefaultSolverManager<TestdataQuarkusSolution>) solverManager).getSolverFactory()).isSameAs(solverFactory);
    }

    @Test
    void solve() throws ExecutionException, InterruptedException {
        var problem = new TestdataExtendedQuarkusSolution("Extra Data");
        problem.setValueList(IntStream.range(1, 3)
                .mapToObj(i -> "v" + i)
                .toList());
        problem.setEntityList(IntStream.range(1, 3)
                .mapToObj(i -> new TestdataQuarkusEntity())
                .toList());
        var solverJob = solverManager.solve(1L, problem);
        var solution = (TestdataExtendedQuarkusSolution) solverJob.getFinalBestSolution();
        assertThat(solution).isNotNull();
        assertThat(solution.getScore().score() >= 0).isTrue();
        assertThat(solution.getExtraData()).isEqualTo("Extra Data");
    }

}
