package ai.timefold.solver.quarkus;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.inject.Inject;

import ai.timefold.solver.core.api.score.SimpleScore;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.api.solver.SolverJob;
import ai.timefold.solver.core.api.solver.SolverManager;
import ai.timefold.solver.core.impl.solver.DefaultSolutionManager;
import ai.timefold.solver.core.impl.solver.DefaultSolverFactory;
import ai.timefold.solver.core.impl.solver.DefaultSolverManager;
import ai.timefold.solver.quarkus.testdomain.normal.TestdataQuarkusConstraintProvider;
import ai.timefold.solver.quarkus.testdomain.normal.TestdataQuarkusEntity;
import ai.timefold.solver.quarkus.testdomain.normal.TestdataQuarkusSolution;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

class TimefoldProcessorSolveTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.timefold.solver.termination.best-score-limit", "0")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusEntity.class,
                            TestdataQuarkusSolution.class, TestdataQuarkusConstraintProvider.class));

    @Inject
    SolverFactory<TestdataQuarkusSolution> solverFactory;
    @Inject
    SolverManager<TestdataQuarkusSolution> solverManager;
    @Inject
    SolutionManager<TestdataQuarkusSolution, SimpleScore> solutionManager;

    @Test
    void singletonSolverFactory() {
        assertThat(solverFactory).isNotNull();
        assertThat(((DefaultSolutionManager<TestdataQuarkusSolution, SimpleScore>) solutionManager).getScoreDirectorFactory())
                .isSameAs(((DefaultSolverFactory<TestdataQuarkusSolution>) solverFactory).getScoreDirectorFactory());
        assertThat(solverManager).isNotNull();
        // There is only one SolverFactory instance
        assertThat(((DefaultSolverManager<TestdataQuarkusSolution>) solverManager).getSolverFactory()).isSameAs(solverFactory);
        assertThat(solutionManager).isNotNull();
    }

    @Test
    void solve() throws ExecutionException, InterruptedException {
        TestdataQuarkusSolution problem = new TestdataQuarkusSolution();
        problem.setValueList(IntStream.range(1, 3)
                .mapToObj(i -> "v" + i)
                .collect(Collectors.toList()));
        problem.setEntityList(IntStream.range(1, 3)
                .mapToObj(i -> new TestdataQuarkusEntity())
                .collect(Collectors.toList()));
        SolverJob<TestdataQuarkusSolution> solverJob = solverManager.solve(1L, problem);
        TestdataQuarkusSolution solution = solverJob.getFinalBestSolution();
        assertThat(solution).isNotNull();
        assertThat(solution.getScore().score() >= 0).isTrue();
    }

}
