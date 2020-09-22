package com.creativesourceapps.android.cupboard.di;

import com.creativesourceapps.android.cupboard.ui.view.RecipeFragment;
import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules={AppModule.class, NetModule.class})
public interface AppComponent {
    void inject(RecipeFragment fragment);
}