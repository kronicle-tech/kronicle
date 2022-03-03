package tech.kronicle.pluginguice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.pf4j.PluginWrapper;
import tech.kronicle.common.ValidatorService;
import tech.kronicle.pluginapi.KroniclePlugin;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static tech.kronicle.common.ValidatorServiceFactory.createValidatorService;

public abstract class KronicleGuicePlugin<C> extends KroniclePlugin<C> {

    private final ValidatorService validatorService;
    private Injector guiceInjector;

    public KronicleGuicePlugin(PluginWrapper wrapper) {
        super(wrapper);
        this.validatorService = createValidatorService();
    }

    protected abstract List<Module> getGuiceModules();

    @Override
    public void initialize(Object config) {
        List<Module> guiceModules = getModifiableGuiceModules();
        if (nonNull(config)) {
            validatorService.validate(config);
            guiceModules.add(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(getConfigType()).toInstance((C) config);
                }
            });
        }
        guiceInjector = Guice.createInjector(guiceModules);
    }

    private List<Module> getModifiableGuiceModules() {
        List<Module> guiceModules = getGuiceModules();
        requireNonNull(guiceModules, "guiceModules");
        return new ArrayList<>(guiceModules);
    }

    public Injector getGuiceInjector() {
        return guiceInjector;
    }
}
