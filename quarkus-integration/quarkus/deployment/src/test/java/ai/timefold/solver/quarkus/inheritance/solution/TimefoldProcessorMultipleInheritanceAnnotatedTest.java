package ai.timefold.solver.quarkus.inheritance.solution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import ai.timefold.solver.core.testdomain.inheritance.solution.baseannotated.multiple.TestdataMultipleInheritanceBaseSolution;
import ai.timefold.solver.core.testdomain.inheritance.solution.baseannotated.multiple.TestdataMultipleInheritanceChildSolution;
import ai.timefold.solver.core.testdomain.inheritance.solution.baseannotated.multiple.TestdataMultipleInheritanceEntity;
import ai.timefold.solver.core.testdomain.inheritance.solution.baseannotated.multiple.TestdataMultipleInheritanceExtendedSolution;
import ai.timefold.solver.quarkus.testdomain.superclass.DummyConstraintProvider;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

class TimefoldProcessorMultipleInheritanceAnnotatedTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(DummyConstraintProvider.class, TestdataMultipleInheritanceExtendedSolution.class,
                            TestdataMultipleInheritanceChildSolution.class, TestdataMultipleInheritanceBaseSolution.class,
                            TestdataMultipleInheritanceEntity.class))
            .assertException(exception -> {
                assertThat(exception.getClass()).isEqualTo(IllegalStateException.class);
                assertThat(exception.getMessage().contains("Multiple classes")).isTrue();
                assertThat(exception.getMessage().contains("found in the classpath with a @PlanningSolution annotation."))
                        .isTrue();
            });

    /**
     * This test validates the behavior of the solver
     * when multiple inheritance is applied.
     */
    @Test
    void testMultipleInheritance() {
        fail("The build should fail");
    }
}
