/**
 * rpreload - Resource pack management made easy.
 * Copyright (c) 2015, Matej Kormuth <http://www.github.com/dobrakmato>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.matejkormuth.bmboot.internal;

import com.google.common.base.Joiner;
import eu.matejkormuth.bmboot.JarUtils;
import eu.matejkormuth.bmboot.ModJson;
import eu.matejkormuth.bmboot.commands.BMBootCommandExecutor;
import eu.matejkormuth.bmboot.facades.Container;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class BukkitPlugin extends JavaPlugin {

    // Logger.
    private static final Logger log = LoggerFactory.getLogger(BukkitPlugin.class);

    // Names.
    private static final String modFile = "mod.json";
    private static final String bmbootFile = "bmboot.json";
    private static final String licenseFile = "LICENSE.txt";

    /**
     * Application container.
     */
    private AppContainer container = new AppContainer();

    /**
     * List of enabled modules that should be disabled when plugin is disabled.
     */
    private List<Module> enabledModules = new ArrayList<>();
    /**
     * List of all modules (enabled, disabled and errored).
     */
    private List<Module> allModules;

    private String bootstrapName;
    private String bootstrapVersion;

    @Override
    public void onEnable() {
        log.info("BMBoot initializing...");
        JSONObject json = new JSONObject(JarUtils.readFile(bmbootFile));
        bootstrapName = json.getString("name");
        bootstrapVersion = json.getString("version");
        log.info("Bootstrap: " + bootstrapName + " - " + bootstrapVersion);

        // Create Container.
        container = new AppContainer();
        Container.swap(this.container);

        // Insert some useful values into container.
        Container.put(JavaPlugin.class, this);
        Container.put(Plugin.class, this);
        Container.put(Server.class, getServer());
        Container.put(BukkitScheduler.class, getServer().getScheduler());

        // Register command.
        getCommand("bmboot").setExecutor(new BMBootCommandExecutor(this));

        // Display license.
        displayLicense();

        // Read mod.json.
        ModJson modJson = loadModJson();

        // Load modules.
        loadModules(modJson);
    }

    /**
     * Loads mod.json files for this mod to start booting things up.
     *
     * @return loaded mod.json object
     */
    private ModJson loadModJson() {
        ModJson modJson = new ModJson(JarUtils.readFile(modFile));

        log.info("Loading {} v. {} by {}...", modJson.getName(), modJson.getVersion(), modJson.getAuthor());
        return modJson;
    }

    /**
     * Displays license information about this plugin to console.
     */
    private void displayLicense() {
        log.info("By using this plugin you agree to it's license.");
        String[] lines = JarUtils.readFile(licenseFile).split("\n");

        for (String line : lines) {
            log.info(line);
        }
    }

    /**
     * Loads and enables all modules.
     *
     * @param modJson mod.json object
     */
    private void loadModules(ModJson modJson) {

        log.info("Creating instances of modules...");

        // TODO: Refactor this big mess.

        // Create classes from string list.
        List<Class<?>> moduleClasses = new ArrayList<>();

        for (String clazzName : modJson.getModules()) {
            try {
                Class<?> clazz = Class.forName(clazzName);
                moduleClasses.add(clazz);
            } catch (ClassNotFoundException e) {
                log.error("Can't find class " + clazzName + " on classpath!", e);
            }
        }

        // Create instances from classes.
        List<Module> instances = new ArrayList<>();
        for (Class<?> clazz : moduleClasses) {
            try {
                Constructor<?> ctr = clazz.getConstructor();
                Object instance = ctr.newInstance();

                if (instance instanceof Module) {
                    instances.add((Module) instance);
                } else {
                    log.error("Specified class {} is not sub class of Module!", clazz.getName());
                }

            } catch (NoSuchMethodException e) {
                log.error("Can't find public no-arg constructor on class " + clazz.getName() + "!", e);
            } catch (InvocationTargetException | IllegalAccessException e) {
                log.error("Can't invoke constructor of class " + clazz.getName() + "!", e);
            } catch (InstantiationException e) {
                log.error("Class" + clazz.getName() + " cannot be instantiated!", e);
            }
        }

        log.info("Determining dependencies...");

        // Find all dependencies.
        instances.stream().forEach(Module::findInjectedDependencies);

        // Make clone of all modules from instances list, which will be later
        // modified.
        this.allModules = new ArrayList<>(instances);

        // Enable all.
        List<Module> erroredModules = new ArrayList<>();

        // Represents number of enabling iteration.
        int iterationCount = 1;
        int maxIterationCount = 50;

        // Loop while there are disabled modules.
        while (instances.size() > 0 && iterationCount < maxIterationCount) {
            for (Iterator<Module> itr = instances.iterator(); itr.hasNext(); ) {
                // Get next disabled module.
                Module m = itr.next();

                // Check if all dependencies of M are enabled.
                boolean dependenciesSatisfied = true;
                for (Class<? extends Module> dependencyModuleClass : m.getDependencies()) {
                    Module dependencyModule = container.get(dependencyModuleClass);

                    if (dependencyModule == null) {
                        log.error("Module {} declares reference to non-existing (not registered) module {}!",
                                m, dependencyModuleClass);
                        itr.remove();

                        // Add to errored modules.
                        erroredModules.add(m);

                        // Skip check of other dependencies, as this module will never load.
                        break;
                    }

                    dependenciesSatisfied &= dependencyModule.enabled;
                }

                // If the dependencies for M were satisfied, enable M.
                if (dependenciesSatisfied) {
                    try {
                        // Set container in this module.
                        m.container = container;
                        // Inject all dependencies.
                        m.injectDependencies();
                        // Enable module.
                        m.onEnable();
                        m.enabled = true;
                        log.info("Enabled module {}!", m);
                        // Add M to list of enabled plugins, so it will be disabled in onDisable().
                        enabledModules.add(m);
                        // Remove M from disabledModules list.
                        itr.remove();
                    } catch (Exception e) {
                        log.error("Can't enable module " + m.toString() + "! Exception: ", e);
                        erroredModules.add(m);
                        // Remove M from disabled as this module will never load.
                        itr.remove();
                    }
                }
            }
            iterationCount++;
        }

        // Display error message.
        if (instances.size() > 0) {
            log.error("Not all modules could be enabled!");
            log.error("There are still {} disabled modules after {} enable iterations.",
                    instances.size(), iterationCount);

            log.error("These modules are still disabled: {}", Joiner.on(", ").join(instances));
        }

        if (erroredModules.size() > 0) {
            log.error("There are also modules that produced errors while they were enabled!");
            log.error("Errored modules (check log for concrete errors): {}", Joiner.on(", ").join(erroredModules));
        }

        log.info("Initialization done!");
    }

    @Override
    public void onDisable() {
        // Disable modules.
        log.info("Disabling modules.");
        // Disable all modules.
        for (Module m : enabledModules) {
            try {
                m.onDisable();
                log.info("Disabled module {}!", m);
            } catch (Exception e) {
                log.error("Can't disable module {}! Exception: {}", m, e);
            }
        }
        log.info("Disabled all modules.");

        // Disable plugin.
        enabledModules.clear();
        allModules.clear();

        // Cancel all possible non canceled tasks.
        getServer().getScheduler().cancelTasks(this);

        Container.swap(null);
        this.container.clear();
        log.info("BMBoot disabled!");
    }

    public List<Module> getAllModules() {
        return Collections.unmodifiableList(allModules);
    }

    public List<Module> getEnabledModules() {
        return Collections.unmodifiableList(enabledModules);
    }

    public AppContainer getContainer() {
        return container;
    }

    public String getBootstrapName() {
        return bootstrapName;
    }

    public String getBootstrapVersion() {
        return bootstrapVersion;
    }
}
