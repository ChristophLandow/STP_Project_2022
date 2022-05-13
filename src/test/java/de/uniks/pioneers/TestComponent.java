package de.uniks.pioneers;

import dagger.Component;

@Component( modules = {TestModule.class} )
public class TestComponent {

    @Component.Builder
    interface Builder extends MainComponent.Builder{}
}
