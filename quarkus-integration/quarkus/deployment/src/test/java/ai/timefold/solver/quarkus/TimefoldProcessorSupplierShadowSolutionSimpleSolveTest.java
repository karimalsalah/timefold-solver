package ai.timefold.solver.quarkus;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.ExecutionException;

import jakarta.inject.Inject;

import ai.timefold.solver.core.api.solver.SolverManager;
import ai.timefold.solver.quarkus.testdomain.declarative.simple.TestdataQuarkusSupplierVariableSimpleConstraintProvider;
import ai.timefold.solver.quarkus.testdomain.declarative.simple.TestdataQuarkusSupplierVariableSimpleEntity;
import ai.timefold.solver.quarkus.testdomain.declarative.simple.TestdataQuarkusSupplierVariableSimpleSolution;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

class TimefoldProcessorSupplierShadowSolutionSimpleSolveTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.timefold.solver.termination.best-score-limit", "0")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestdataQuarkusSupplierVariableSimpleSolution.class,
                            TestdataQuarkusSupplierVariableSimpleEntity.class,
                            TestdataQuarkusSupplierVariableSimpleConstraintProvider.class));
    @Inject
    SolverManager<TestdataQuarkusSupplierVariableSimpleSolution> solverManager;

    @Test
    void solve() throws ExecutionException, InterruptedException {
        var shadowEntity =
                new TestdataQuarkusSupplierVariableSimpleEntity();
        var problem = new TestdataQuarkusSupplierVariableSimpleSolution();
        problem.setEntityList(List.of(shadowEntity));
        problem.setValueList(List.of("a", "b"));
        var solverJob = solverManager.solve(1L, problem);
        var solution = solverJob.getFinalBestSolution();
        assertThat(solution).isNotNull();
        assertThat(problem).isNotSameAs(solution);
        assertThat(solution.getScore().score()).isEqualTo(0);
        assertThat(problem.getEntityList().get(0)).isNotSameAs(solution.getEntityList().get(0));
    }

}
