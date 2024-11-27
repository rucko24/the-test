package org.example.service;

/*
* @author rubn
 */
public class ProcessContext implements ProcessStrategy {

    private final ProcessStrategy processStrategy;

    public ProcessContext(ProcessStrategy processStrategy) {
        this.processStrategy = processStrategy;
    }

    @Override
    public void execute() {
        this.processStrategy.execute();
    }

}
