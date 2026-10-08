package ai.timefold.solver.quarkus.inheritance.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import ai.timefold.solver.core.testconstraint.DummyConstraintProvider;
import ai.timefold.solver.core.testdomain.inheritance.entity.single.basenot.interfaces.TestdataBaseNotAnnotatedInterfaceChildEntity;
import ai.timefold.solver.core.testdomain.inheritance.entity.single.basenot.interfaces.TestdataBaseNotAnnotatedInterfaceSolution;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

class TimefoldProcessorOnlyChildAnnotatedInterfaceTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(DummyConstraintProvider.class, TestdataBaseNotAnnotatedInterfaceSolution.class,
                            TestdataBaseNotAnnotatedInterfaceChildEntity.class))
            .assertException(exception -> {
                assertThat(exception.getClass()).isEqualTo(IllegalStateException.class);
                assertThat(exception.getMessage().contains(
                        "is not annotated with @PlanningEntity but defines genuine or shadow variables.")).isTrue();
            });

    /**
     * This test validates the behavior of the solver
     * when only the child class is annotated with {@code @PlanningEntity}
     * and the base entity is an interface.
     */
    @Test
    void testOnlyChildClassAnnotatedBaseIsInterface() {
        fail("The build should fail");
    }
}
