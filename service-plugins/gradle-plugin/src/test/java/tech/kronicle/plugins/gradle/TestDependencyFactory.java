package tech.kronicle.plugins.gradle;

import com.google.inject.Guice;
import com.google.inject.Injector;
import tech.kronicle.plugins.gradle.guice.TestGuiceModule;
import tech.kronicle.plugins.gradle.internal.groovyscriptvisitors.BuildGradleVisitor;

public final class TestDependencyFactory {

    public static GradleScanner createGradleScanner(Class<?> testClass) {
        return createInjector(testClass).getInstance(GradleScanner.class);
    }

    public static BuildGradleVisitor createBuildGradleVisitor(Class<?> testClass) {
        return createInjector(testClass).getInstance(BuildGradleVisitor.class);
    }

    private static Injector createInjector(Class<?> testClass) {
        return Guice.createInjector(new TestGuiceModule(testClass));
    }

    private TestDependencyFactory() {
    }
}
