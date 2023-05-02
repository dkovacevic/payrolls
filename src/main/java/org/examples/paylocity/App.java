package org.examples.paylocity;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jdbi3.JdbiHealthCheck;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.examples.paylocity.resources.BenefitsResource;
import org.examples.paylocity.resources.DependantResource;
import org.examples.paylocity.resources.EmployeeBenefitsResource;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;

import java.util.Optional;

public class App extends Application<Config> {
    public static MetricRegistry metrics;
    public static Config config;

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<Config> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
        bootstrap.addBundle(new SwaggerBundle<>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(Config configuration) {
                return configuration.swagger;
            }
        });
    }

    public void run(Config configuration, Environment environment) {
        App.config = configuration;
        App.metrics = environment.metrics();

        Jdbi jdbi = new JdbiFactory().build(environment, configuration.database, "paylocity");

        Flyway flyway = Flyway
                .configure()
                .dataSource(configuration.database.build(metrics, "paylocity"))
                .load();
        flyway.migrate();

        environment.jersey().register(new BenefitsResource(jdbi));
        environment.jersey().register(new DependantResource(jdbi));
        environment.jersey().register(new EmployeeBenefitsResource(jdbi));

        CollectorRegistry.defaultRegistry.register(new DropwizardExports(metrics));
        environment.getApplicationContext().addServlet(MetricsServlet.class, "/metrics");

        environment.healthChecks().register("DB Test", new JdbiHealthCheck(jdbi, Optional.empty()));
    }
}