package de.uniks.pioneers;

import dagger.Component;
import de.uniks.pioneers.controller.NewGameScreenLobbyController;

import javax.inject.Singleton;

@Component( modules = {TestModule.class} )
@Singleton
public interface TestComponent extends MainComponent{


    @Component.Builder
    interface Builder extends MainComponent.Builder{}
}
