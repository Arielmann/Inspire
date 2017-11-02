package inspire.ariel.inspire.common.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.common.app.InspireApplication;

@Module
public class AppModule {

    private InspireApplication application;

    public AppModule(InspireApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton //Also being provided to other modules
    InspireApplication providesApplication() {
        return application;
    }

}
