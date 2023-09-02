package club.crestmc.uhc.scenario;

import club.crestmc.uhc.UHC;
import club.crestmc.uhc.util.ReflectionUtil;

import java.util.HashSet;
import java.util.Set;

public class ScenarioManager {

    private final UHC plugin;

    private final Set<Scenario> scenarios;

    public ScenarioManager(UHC plugin) {
        this.plugin = plugin;
        this.scenarios = new HashSet<>();

        load();
    }

    private void load() {
        ReflectionUtil.consume("club.crestmc.uhc.scenario.defined", UHC.class.getClassLoader(), Scenario.class, scenario -> {
            scenario.onLoad();
            scenarios.add(scenario);
        }, true);
    }

    public void enable(Scenario scenario) {
        scenario.setEnabled(true);
    }

    public void disable(Scenario scenario) {
        scenario.setEnabled(false);
    }

    public Scenario getScenario(String name) {
        return scenarios.stream()
                .filter(scenario -> scenario.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Set<Scenario> getScenarios() {
        return scenarios;
    }

}
