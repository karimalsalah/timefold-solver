package ai.timefold.solver.quarkus.inheritance.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import ai.timefold.solver.core.testdomain.inheritance.entity.multiple.baseannotated.classes.childnot.TestMultipleChildNotAnnotatedConstraintProvider;
import ai.timefold.solver.core.testdomain.inheritance.entity.multiple.baseannotated.classes.childnot.TestdataMultipleChildNotAnnotatedBaseEntity;
import ai.timefold.solver.core.testdomain.inheritance.entity.multiple.baseannotated.classes.childnot.TestdataMultipleChildNotAnnotatedChildEntity;
import ai.timefold.solver.core.testdomain.inheritance.entity.multiple.baseannotated.classes.childnot.TestdataMultipleChildNotAnnotatedSecondChildEntity;
import ai.timefold.solver.core.testdomain.inheritance.entity.multiple.baseannotated.classes.childnot.TestdataMultipleChildNotAnnotatedSolution;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

class TimefoldProcessorMultipleOnlyBaseAnnotatedTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClasses(TestMultipleChildNotAnnotatedConstraintProvider.class,
                            TestdataMultipleChildNotAnnotatedSolution.class,
                            TestdataMultipleChildNotAnnotatedChildEntity.class,
                            TestdataMultipleChildNotAnnotatedSecondChildEntity.class,
                            TestdataMultipleChildNotAnnotatedBaseEntity.class))
            .assertException(exception -> {
                assertThat(exception.getClass()).isEqualTo(IllegalStateException.class);
                assertThat(exception.getMessage().contains(
                        "Remove either the entity classes or entity interfaces from the inheritance chain to create a single-level inheritance structure"))
                        .isTrue();
            });

    /**
     * This test validates the behavior of the solver
     * when only the base class is annotated with {@code @PlanningEntity} and multiple inheritance is used.
     */
    @Test
    void testMultipleOnlyBaseClassAnnotated() {
        fail("The build should fail");
    }
}
