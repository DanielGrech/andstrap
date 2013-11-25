package {package_name}.modules;

import {package_name}.dao.ForecastDao;
import {package_name}.util.DaoUtils;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Provides access to our model DAO objects
 */
@Module(
        staticInjections = {
                DaoUtils.class
        }
)
public class DaoModule {

}
