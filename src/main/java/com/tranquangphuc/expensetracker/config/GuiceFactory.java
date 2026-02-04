package com.tranquangphuc.expensetracker.config;

import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tranquangphuc.expensetracker.repository.ExpenseJsonRepository;
import com.tranquangphuc.expensetracker.repository.ExpenseRepository;
import com.tranquangphuc.expensetracker.service.ExpenseService;
import com.tranquangphuc.expensetracker.service.ExpenseServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;
import tools.jackson.databind.ObjectMapper;

public class GuiceFactory implements IFactory {
    private final Injector injector = Guice.createInjector(new AppModule());

    @Override
    public <K> K create(Class<K> aClass) throws Exception {
        try {
            return injector.getInstance(aClass);
        } catch (ConfigurationException ex) { // no implementation found in Guice configuration
            return CommandLine.defaultFactory().create(aClass); // fallback if missing
        }
    }

    static class AppModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(ObjectMapper.class).toInstance(new ObjectMapper());
            bind(ExpenseRepository.class).to(ExpenseJsonRepository.class);
            bind(ExpenseService.class).to(ExpenseServiceImpl.class);
        }
    }
}
