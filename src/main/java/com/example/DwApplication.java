package com.example;

import com.example.conf.DwConfiguration;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DwApplication extends Application<DwConfiguration> {
	public static void main(String[] args) throws Exception {
		new DwApplication().run(args);
	}

	@Override
	public void initialize(Bootstrap<DwConfiguration> bootstrap) {
		bootstrap.addBundle(new MultiPartBundle());
		bootstrap.addBundle(new AssetsBundle("/META-INF/resources/webjars/", "/webjars", "index.html", "webjars"));
		bootstrap.addBundle(new AssetsBundle("/dw/", "/dw", "index.html", "dw"));
	}

	@Override
	public void run(DwConfiguration conf, Environment env) throws Exception {
		DwSpringConfig.run(conf, env);
	}
}
