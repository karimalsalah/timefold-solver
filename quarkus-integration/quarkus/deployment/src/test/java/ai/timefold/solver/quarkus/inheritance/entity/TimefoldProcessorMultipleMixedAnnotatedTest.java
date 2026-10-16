package ai.timefold.solver.quarkus.inheritance.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import ai.timefold.solver.core.testdomain.inheritance.entity.multiple.baseannotated.classes.mixed.TestMultipleMixedConstraintProvider;
import ai.timefold.solver.core.testdomain.inheritance.entity.multiple.baseannotated.classes.mixed.TestdataMultipleMixedBaseEntity;
import ai.timefold.solver.core.testdomain.inheritance.entity.multiple.baseannotated.classes.mixed.TestdataMultipleMixedChildEntity;
import ai.timefold.solver.core.testdomain.inheritance.entity.multiple.baseannotated.classes.mixed.TestdataMultipleMixedSolution;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

class TimefoldProcessorMultipleMixedAnnotatedTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestMultipleMixedConstraintProvider.class, TestdataMultipleMixedSolution.class,
                            TestdataMultipleMixedChildEntity.class, TestdataMultipleMixedBaseEntity.class))
            .assertException(exception -> {
                assertThat(exception.getClass()).isEqualTo(IllegalStateException.class);
                assertThat(exception.getMessage().contains(
                        "Mixed inheritance is not permitted.")).isTrue();
            });

    /**
     * This test validates the behavior of the solver
     * when multiple inheritance is used, the child is annotated with {@code @PlanningEntity}
     * and it inherits from class and interface.
     */
    @Test
    void testMultipleBothClassesAnnotatedMixedPattern() {
        fail("The build should fail");
    }
}
