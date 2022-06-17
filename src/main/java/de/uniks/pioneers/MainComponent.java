package de.uniks.pioneers;

import dagger.BindsInstance;
import dagger.Component;
import de.uniks.pioneers.controller.LoginScreenController;
import javax.inject.Singleton;

@Component(modules = MainModule.class)
@Singleton
public interface MainComponent {
    LoginScreenController loginController();

    @Component.Builder
    interface Builder{

        @BindsInstance
        Builder mainApp (App app);

        MainComponent build();
    }
}
