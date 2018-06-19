package org.koin.standalone;

import org.koin.core.parameter.ParameterList;

import kotlin.Lazy;

/**
 * @author @fredy-mederos
 */
public class DataFetcher {

    //From properties
    private Lazy<String> PREFIX = KoinJavaComponent.property("PrefixProp");
    private String SEPARATOR = KoinJavaComponent.getProperty("SeparatorProp");

    //From components
    private Lazy<DataSource> localDb_lazy = KoinJavaComponent.inject(DataSource.class, "db");
    private DataSource remoteApi = KoinJavaComponent.get(DataSource.class, "api");
    private DataConverter dataConverter = KoinJavaComponent.get(DataConverter.class, "", null, () -> new ParameterList(SEPARATOR));

    public DataFetcher() {
        //Use this constructor only for test cases.
        //In production the DataFetcher is created in some hidden factory.
        //That is why you can not pass any argument in the constructor.
    }

    public String getAllDataConverted() {
        return PREFIX.getValue() + dataConverter.convert(localDb_lazy.getValue().getData(), remoteApi.getData());
    }
}
